package com.yx.p2p.ds.invest.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.enums.SystemSourceEnum;
import com.yx.p2p.ds.enums.invest.InvestBizStateEnum;
import com.yx.p2p.ds.enums.investproduct.InvestTypeEnum;
import com.yx.p2p.ds.enums.match.InvestMatchReqLevelEnum;
import com.yx.p2p.ds.enums.match.MatchRemarkEnum;
import com.yx.p2p.ds.enums.mq.MQStatusEnum;
import com.yx.p2p.ds.enums.payment.PaymentTypeEnum;
import com.yx.p2p.ds.helper.BeanHelper;
import com.yx.p2p.ds.invest.mapper.InvestClaimHistoryMapper;
import com.yx.p2p.ds.invest.mapper.InvestClaimMapper;
import com.yx.p2p.ds.invest.mapper.InvestMapper;
import com.yx.p2p.ds.model.crm.Customer;
import com.yx.p2p.ds.model.invest.Invest;
import com.yx.p2p.ds.model.invest.InvestClaim;
import com.yx.p2p.ds.model.invest.InvestClaimHistory;
import com.yx.p2p.ds.model.invest.InvestProduct;
import com.yx.p2p.ds.model.match.FinanceMatchRes;
import com.yx.p2p.ds.model.match.InvestMatchReq;
import com.yx.p2p.ds.model.payment.CustomerBank;
import com.yx.p2p.ds.model.payment.Payment;
import com.yx.p2p.ds.mq.InvestMQVo;
import com.yx.p2p.ds.server.CrmServer;
import com.yx.p2p.ds.server.FinanceMatchReqServer;
import com.yx.p2p.ds.server.PaymentServer;
import com.yx.p2p.ds.service.InvestClaimHistoryService;
import com.yx.p2p.ds.service.InvestProductService;
import com.yx.p2p.ds.service.InvestService;
import com.yx.p2p.ds.service.LendingService;
import com.yx.p2p.ds.util.BigDecimalUtil;
import com.yx.p2p.ds.util.DateUtil;
import com.yx.p2p.ds.util.LoggerUtil;
import com.yx.p2p.ds.util.OrderUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/06/18:03
 */
@Service
public class InvestServiceImpl implements InvestService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private InvestMapper investMapper;

    @Autowired
    private InvestProductService investProductService;

    @Autowired
    private LendingService lendingService;

    @Autowired
    private InvestClaimMapper investClaimMapper;

    @Autowired
    private InvestClaimHistoryMapper investClaimHistoryMapper;

    @Reference
    private PaymentServer paymentServer;

    @Reference
    private CrmServer crmServer;

    @Reference
    private FinanceMatchReqServer financeMatchReqServer;


    @Autowired
    private InvestClaimHistoryService investClaimHistoryService;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Value("${mq.match.invest.producer.group}")
    private String matchInvestProducerGroup;

    @Value("${mq.match.invest.topic}")
    private String matchInvestTopic;

    @Value("${mq.match.invest.tag}")
    private String matchInvestTag;


    //投资充值
    public Result rechargeInvest(Invest invest){
        //添加新投资
        Result result = this.addNewInvest(invest);
        if(Result.checkStatus(result)){
            //构建投资人支付信息
            Payment payment = this.buildAddPayment(invest);
            //网关支付
            result = paymentServer.gateway(payment);
        }else{
            logger.error("生成投资失败【customerId=】" +  invest.getCustomerId());
        }
        return result;
    }

    //补偿网关支付（只打开了网关支付页面，并未支付）
    public Result compensateGateway(Invest invest){
        Invest dbInvest = this.getInvestByInvestId(invest.getId());
        Payment payment = this.buildAddPayment(dbInvest);
        //网关支付
        Result result = paymentServer.gateway(payment);
        return result;
    }

    //接收支付成功结果：
   //分为支付成功和支付失败两种处理方式。
    public Result receivePayResult(InvestMQVo investMQVo){
        logger.debug("【解析MQ支付结果】investMQVo=" + investMQVo);
        Result result = null;
        if(investMQVo.getStatus().equals(MQStatusEnum.OK.getStatus())){//支付成功
            result = this.receivePaySucResult(investMQVo);
        }else{//支付失败
            result = this.receivePayFailResult(investMQVo);
        }
        return result;
    }

    //接收支付成功结果：
    //1.幂等性验证
    //2.更新投资状态：投资成功
    //3.插入新出借单
    //4.发送撮合系统：新出借（MQ）
    //注意：1和2在同一事务
    @Transactional
    private Result receivePaySucResult(InvestMQVo investMQVo){
        logger.debug("【投资支付成功处理：】");
        //1.幂等性验证：查看出借单是否生成
        Result result =  lendingService.checkNoLendingByOrderSn(investMQVo.getOrderSn());
        if(Result.checkStatus(result)){
            //2.更新投资状态：投资成功
            result = this.updateInvestBizState(Integer.parseInt(investMQVo.getBizId()),
                    InvestBizStateEnum.INVEST_SUC);
            if(Result.checkStatus(result)){
                //3.插入新出借单
                result = lendingService.addNewLending(investMQVo);
            }
            if(Result.checkStatus(result)){
                Integer lendingId = (Integer)result.getTarget();
                //4.发送新投资撮合MQ
                result = this.dealInvestMatchReqMQ(lendingId,investMQVo);
            }
        }

        return result;
    }
    //发送新投资撮合MQ
    private Result dealInvestMatchReqMQ(Integer lendingId,InvestMQVo investMQVo) {
        Result result = Result.error();
        InvestMatchReq investMatchReq = this.buildInvestMatchReq(lendingId,investMQVo);
        this.sendInvestMatchReqMQ(investMatchReq);
        return result;
    }


    private void sendInvestMatchReqMQ(InvestMatchReq investMatchReq) {
        String mqVoStr = JSON.toJSONString(investMatchReq);
        DefaultMQProducer producer = null;
        try {
            Message message = new Message(matchInvestTopic,matchInvestTag,
                    investMatchReq.getInvestOrderSn(),mqVoStr.getBytes());
            producer = rocketMQTemplate.getProducer();
            producer.setProducerGroup(matchInvestProducerGroup);
            SendResult sendResult = producer.send(message);
            logger.debug("【发送投资撮合新投资MQ】，producer=" + producer.getProducerGroup() +
                    "，topic="+ matchInvestTopic + ",tag=" + matchInvestTag + ",message=" + message);
            logger.debug("【发送投资撮合新投资结果MQ】，sendResult" + sendResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Invest getInvestByInvestId(Integer investId){
        return this.queryDBInvestByInvestId(investId);
    }

    private InvestMatchReq buildInvestMatchReq(Integer lendingId, InvestMQVo investMQVo) {
        String investId = investMQVo.getBizId();//invest.id
        Invest invest = this.getInvestByInvestId(Integer.parseInt(investId));
        InvestMatchReq investMatchReq = new InvestMatchReq();
        //投资产品信息
        investMatchReq.setProductId(invest.getInvestProductId());
        investMatchReq.setProductName(invest.getInvestProductName());
        investMatchReq.setYearIrr(invest.getInvestYearIrr());
        //客户信息
        investMatchReq.setInvestCustomerId(investMQVo.getCustomerId());
        investMatchReq.setInvestCustomerName(invest.getCustomerName());
        //投资信息
        investMatchReq.setInvestBizId(investId);
        investMatchReq.setInvestAmt(investMQVo.getAmount());
        investMatchReq.setInvestOrderSn(String.valueOf(lendingId));//lending.id
        investMatchReq.setLevel(InvestMatchReqLevelEnum.NEW_INVEST.getLevel());
        investMatchReq.setRemark(MatchRemarkEnum.NEW_INVEST.getDesc());
        investMatchReq.setWaitAmt(investMatchReq.getInvestAmt());
        logger.debug("【发送新投资撮合前对象】InvestMatchReq=" + investMQVo);
        return investMatchReq;
    }

    //接收支付失败结果：
    //1.幂等性验证
    //2.更新投资状态：投资撤销
    public Result receivePayFailResult(InvestMQVo investMQVo){
        logger.debug("【投资支付失败处理：】");
        Integer investId = Integer.parseInt(investMQVo.getBizId());
        //1.幂等性验证（检查投资业务状态为非撤销）
        Result result = this.checkNoInvestBizCanelByInvestId(investId);
        if(Result.checkStatus(result)){//非撤销更新为撤销
            //2.更新投资状态：投资撤销
            result = this.updateInvestBizState(investId, InvestBizStateEnum.INVEST_CANCEL);
        }
        return result;
    }

    //检查投资业务状态为非撤销
    //Result.status=ok为非撤销，Result.status.error为撤销
    private Result checkNoInvestBizCanelByInvestId(Integer investId) {
        logger.debug("【检查投资业务状态为非撤销】investId=" + investId);
        Result result = Result.success();
        Invest invest = queryDBInvestByInvestId(investId);
        if (invest != null && invest.getBizState().equals(InvestBizStateEnum.INVEST_CANCEL.getState())) {
            result = Result.error();
        }
        logger.debug("【检查投资业务状态为非撤销】result=" + result);
        return result;
    }

    private Invest queryDBInvestByInvestId(Integer investId) {
        Invest invest = investMapper.selectByPrimaryKey(investId);
        return invest;
    }

    @Override
    public Result updateInvestBizState(Integer investId, InvestBizStateEnum investBizStateEnum) {
        logger.debug("【更新投资状态】investId=" + investId + ",state=" + investBizStateEnum);
        Result result = Result.error();
            Invest invest = this.buildUpdateInvestState(investId,investBizStateEnum);
            int count = investMapper.updateByPrimaryKeySelective(invest);
            if(count == 1){
                result = Result.success();
            }
            logger.debug("更新数据库：投资状态【investId=】" + investId + ",【investState=】"
                    + investBizStateEnum.getState() + "count=" + count);
        return result;
    }

    private Invest buildUpdateInvestState(Integer investId, InvestBizStateEnum investBizStateEnum) {
        Invest invest = new Invest();
        invest.setId(investId);
        invest.setBizState(investBizStateEnum.getState());
        return invest;
    }

    private Payment buildAddPayment(Invest invest) {
        logger.debug("【构建添加支付对象，入参】invest=" + invest);
        //封装Payment
        String sysSourceName = SystemSourceEnum.INVEST.getName();
        Payment payment = new Payment();
        payment.setCustomerId(invest.getCustomerId());
        payment.setSystemSource(sysSourceName);
        payment.setBizId(String.valueOf(invest.getId()));
        payment.setOrderSn(OrderUtil.genOrderSn(sysSourceName));
        payment.setCustomerName(invest.getCustomerName());
        payment.setIdCard(invest.getCustomerIdCard());
        payment.setBankAccount(invest.getBankAccount());
        payment.setBaseBankName(invest.getBaseBankName());
        payment.setBankCode(invest.getBankCode());
        payment.setPhone(invest.getPhone());
        payment.setAmount(invest.getInvestAmt());
        payment.setType(PaymentTypeEnum.INVEST_RECHARGE.getCode());
        payment.setRemark(PaymentTypeEnum.INVEST_RECHARGE.getCodeDesc());
        logger.debug("【构建添加支付对象】payment=" + payment);
        return payment;
    }

    //添加新投资
    private Result addNewInvest(Invest invest){
        Result result = Result.success();
        try{
            //1.封装新投资
            result = this.buildNewInvest(invest);
            //3.插入数据库
            result = this.addInvest2DB(invest);
        }catch(Exception e){
            result = LoggerUtil.addExceptionLog(e,logger);
        }
        return result;
    }

    private Result addInvest2DB(Invest invest){
        Result result = Result.success();
        try{
            //设置时间，操作人，状态
            BeanHelper.setAddDefaultField(invest);
            investMapper.insert(invest);
            logger.debug("【插入数据库.投资数据=】" + invest);
        }catch (Exception e){
            result = LoggerUtil.addExceptionLog(e,logger);
        }
        return result;
    }

    private Result buildNewInvest(Invest invest) {
        Result result = Result.error();
        //投资产品
        InvestProduct investProduct =
                investProductService.getInvestProductById(invest.getInvestProductId());
        invest.setInvestProductName(investProduct.getName());
        invest.setInvestYearIrr(investProduct.getYearIrr());
        invest.setInvestType(investProduct.getInvestType());
        invest.setInvestDayCount(investProduct.getDayCount());

        //客户银行卡信息
        CustomerBank customerBank = paymentServer.getCustomerBankById(invest.getCustomerBankId());
        invest.setBankAccount(customerBank.getBankAccount());
        invest.setBankCode(customerBank.getBankCode());
        invest.setBaseBankName(customerBank.getBaseBankName());
        invest.setPhone(customerBank.getPhone());

        //客户信息
        Customer customer = crmServer.getCustomerById(invest.getCustomerId());
        invest.setCustomerIdCard(customer.getIdCard());
        invest.setCustomerName(customer.getName());

        invest.setStartDate(new Date());//开始时间
        result = this.buildEndDate(investProduct,invest);//结束时间
        result = this.buildProfit(investProduct,invest);//计算收益
        result = Result.success();
        logger.debug("【buildNewInvest.invest=】" + invest);
        return result;
    }

    /**
        * @description:
     * 前台页面已经计算过，后台也计算一次，验证收益是否相同
     *  投资类型1-固定期限：保留2位四舍五入
     *  收益=round(日收益*投资天数,2)，日收益=round(投资金额*年化收益率/365,2)
     *  invest.profit= round(
     *      round(invest.investAmt*investProduct.yearIrr/365,2)*investProduct.dayCount
     *  ,2)
     *  投资类型2-非固定期限：先按“投资类型1-固定期限”计算，实际按赎回日计算
        * @author:  YX
        * @date:    2020/04/10 15:29
        * @param: investProduct
        * @param: invest
        * @return: void
        */
    private Result buildProfit(InvestProduct investProduct, Invest invest) {
        Result result = Result.success();
        //日收益（保留2位四舍五入）：投资金额*年收益率/365
        BigDecimal dayProfit =
                BigDecimalUtil.divide2(invest.getInvestAmt().multiply(investProduct.getYearIrr()),
                        new BigDecimal("365"));
        //收益
        BigDecimal profit = BigDecimalUtil.round2In45(
                dayProfit.multiply(new BigDecimal(investProduct.getDayCount()))
        );//保留2位四舍五入
        BigDecimal beforeProfit = invest.getProfit();
        if(profit.compareTo(beforeProfit) != 0){
            String msg = "前后台收益不同：前台计算收益："+ beforeProfit + "，后台计算收益：" + profit;
            result = Result.error(msg);
            logger.debug(msg);
        }else{
            String msg = "前后台收益相同：前台计算收益："+ beforeProfit + "后台计算收益：" + profit;
            result = Result.error(msg);
            logger.debug(msg);
        }
        return result;
    }

        /**
         * @description: 设置投资结束日期endDate
         * 投资类型1-固定期限：非null，当前日期+InvestProduct.dayCount
         *  投资类型2-非固定期限：null
        * @author:  YX
        * @date:    2020/04/10 15:30
        */
    private Result buildEndDate(InvestProduct investProduct,Invest invest) {
        Result result = Result.success();
        try{
            if(investProduct.getInvestType().equals(InvestTypeEnum.FIXED.getState())){
                invest.setEndDate(DateUtil.addDay(invest.getStartDate(),investProduct.getDayCount()+1));
            }else{
                invest.setEndDate(null);
            }
        }catch (Exception e){
            result = LoggerUtil.addExceptionLog(e,logger);
        }
        return result;
    }

    public List<Invest> getInvestVoList(Invest invest){
        logger.debug("【invest】" + invest);
        List<Invest> investVoList = this.queryDBnvestVoList(invest);
        return investVoList;
    }

    //查询数据库
    public List<Invest> queryDBnvestVoList(Invest invest){
        Integer count = investMapper.queryInvestCount(invest);
        List<Invest> investVoList = investMapper.queryInvestListByPagination(invest,0,count);
        logger.debug("【查询数据库：queryDBInvestListByCustomerId】investList=" + investVoList);
        return investVoList;
    }

    //放款通知
    //查询借款撮合结果
    //添加投资债权明细和历史
    public Result loanNotice(HashMap<String, String> loanMap){
        logger.debug("【放款，入参：loadMap=】" + loanMap);
        Result result = Result.error();
        String borrowId = loanMap.get("orderSn");//借款编号
        String financeCustomerId = loanMap.get("customerId");//融资客户
        String status = loanMap.get("status");
        Integer borrowIdInt = Integer.parseInt(borrowId);
        //1.验证是否已经插入过放款数据(借款人债务子账户流水)
        result = this.checkNoLoanNotice(borrowIdInt);
        if(Result.checkStatus(result)) {
            Integer financeCustomerIdInt = Integer.valueOf(financeCustomerId);
            //查询借款撮合数据
            List<FinanceMatchRes> borrowMatchResList = financeMatchReqServer.getBorrowMatchResList(
                    financeCustomerIdInt , borrowId);
            result = this.dealLoanNoticeData(financeCustomerIdInt,borrowIdInt,borrowMatchResList);
        }
        result = Result.success();
        logger.debug("【放款，结果：result=】" + result);
        return result;
    }

    private Result dealLoanNoticeData(Integer financeCustomerId, Integer borrowId, List<FinanceMatchRes> borrowMatchResList) {
        logger.debug("【批量插入投资债权明细】入参：financeCustomerId="+ financeCustomerId
                + ",borrowId=" + borrowId+ ",borrowMatchResList=" + borrowMatchResList);
        Result result = Result.error();
        if(!borrowMatchResList.isEmpty()){
            List<InvestClaim> investClaimList = this.buildAddInvestClaimList(borrowId,borrowMatchResList);
            //检查并处理出借单满额
            result = lendingService.checkAndDealFullAmt(investClaimList);
            //批量插入投资债权明细
            investClaimMapper.insertList(investClaimList);
            //批量插入投资债权明细历史
            result = investClaimHistoryService.genInvestClaimHistoryList(investClaimList);
        }
        result = Result.success();
        return result;
    }

    private List<InvestClaim> buildAddInvestClaimList(Integer borrowId,List<FinanceMatchRes> borrowMatchResList) {
        List<InvestClaim> investClaimList = new ArrayList<>();
        for(FinanceMatchRes financeMatchRes : borrowMatchResList){
            Integer investId = Integer.valueOf(financeMatchRes.getInvestBizId());
            InvestClaim investClaim = new InvestClaim();
            investClaim.setParentId(0);//父投资债权编号：新借款为0/转让为父编号
            investClaim.setBorrowId(borrowId);
            investClaim.setCustomerId(financeMatchRes.getFinanceCustomerId());
            investClaim.setCustomerName(financeMatchRes.getFinanceCustomerName());
            investClaim.setBorrowProductId(financeMatchRes.getBorrowProductId());
            investClaim.setBorrowProductName(financeMatchRes.getBorrowProductName());
            investClaim.setBorrowYearRate(financeMatchRes.getBorrowYearRate());
            investClaim.setBuyAmt(financeMatchRes.getTradeAmt());//买入金额：不会变化
            investClaim.setClaimAmt(investClaim.getBuyAmt());//债权金额：会增值变化
            investClaim.setHoldShare(financeMatchRes.getMatchShare());
            investClaim.setInvestId(investId);
            investClaim.setLendingId(Integer.valueOf(financeMatchRes.getInvestOrderSn()));
            BeanHelper.setAddDefaultField(investClaim);
            investClaimList.add(investClaim);
        }
        return investClaimList;
    }

    //验证是否已经插入过放款数据(投资债权明细)
    private Result checkNoLoanNotice(Integer borrowId) {
        logger.debug("【检查没有放款数据】入参borrowId=" + borrowId);
        Result result = Result.error();
        InvestClaim investClaim = new InvestClaim();
        investClaim.setBorrowId(borrowId);
        List<InvestClaim> investClaims = this.queryDBInvestClaminList(investClaim);
        if(investClaims.isEmpty()){
            result = Result.success();
        }
        logger.debug("【检查没有投资债权数据】result.status=error说明已经执行操作；" +
                "result.status=ok说明没有投资债权数据，继续执行。result=" + result);
        return result;
   }

   public List<InvestClaim> getInvestClaimList(InvestClaim investClaim){
        return this.queryDBInvestClaminList(investClaim);
   }

    private List<InvestClaim> queryDBInvestClaminList(InvestClaim investClaim) {
        logger.debug("【查询数据库：投资债权明细集合】入参：investClaim=" + investClaim);
        List<InvestClaim> investClaimList = investClaimMapper.select(investClaim);
        logger.debug("【查询数据库：投资债权明细集合】结果：investClaims=" + investClaimList);
        return investClaimList;
    }


}
