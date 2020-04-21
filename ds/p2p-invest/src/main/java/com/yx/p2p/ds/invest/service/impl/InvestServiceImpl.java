package com.yx.p2p.ds.invest.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.yx.p2p.ds.constant.SysConstant;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.enums.SystemSourceEnum;
import com.yx.p2p.ds.enums.invest.InvestBizStateEnum;
import com.yx.p2p.ds.enums.investproduct.InvestTypeEnum;
import com.yx.p2p.ds.enums.lending.LendingTypeEnum;
import com.yx.p2p.ds.helper.BeanHelper;
import com.yx.p2p.ds.invest.mapper.InvestMapper;
import com.yx.p2p.ds.model.*;
import com.yx.p2p.ds.mq.InvestMQVo;
import com.yx.p2p.ds.server.CrmServer;
import com.yx.p2p.ds.server.PaymentServer;
import com.yx.p2p.ds.service.InvestProductService;
import com.yx.p2p.ds.service.InvestService;
import com.yx.p2p.ds.service.LendingService;
import com.yx.p2p.ds.util.BigDecimalUtil;
import com.yx.p2p.ds.util.DateUtil;
import com.yx.p2p.ds.util.LoggerUtil;
import com.yx.p2p.ds.util.OrderUtil;
import com.yx.p2p.ds.vo.InvestVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Reference
    private PaymentServer paymentServer;

    @Reference
    private CrmServer crmServer;

    @Autowired
    private LendingService lendingService;

    //投资充值
    public Result rechargeInvest(InvestVo investVo){
        //添加新投资
        Result result = this.addNewInvest(investVo);
        if(Result.checkStatus(result)){
            //构建投资人支付信息
            Payment payment = this.buildAddPayment(investVo);
            //网关支付
            result = paymentServer.gateway(payment);
        }else{
            logger.error("生成投资失败【customerId=】" +  investVo.getCustomerId());
        }
        return result;
    }

    //补偿网关支付（只打开了网关支付页面，并未支付）
    public Result compensateGateway(InvestVo investVo){
        Payment payment = this.buildCompensateGateway(investVo);
        //网关支付
        Result result = paymentServer.gateway(payment);
        return result;
    }

    private Payment buildCompensateGateway(InvestVo investVo) {
        Payment payment = new Payment();
        //封装Payment
        String sysSourceName = SystemSourceEnum.INVEST.getName();
        payment.setSystemSource(sysSourceName);
        payment.setOrderSn(OrderUtil.genOrderSn(sysSourceName));
        payment.setCustomerId(investVo.getCustomerId());
        payment.setBizId(String.valueOf(investVo.getId()));
        payment.setAmount(investVo.getInvestAmt());
        //查询客户表数据库
        Customer customer = crmServer.getCustomerById(investVo.getCustomerId());
        payment.setCustomerName(customer.getName());
        payment.setIdCard(customer.getIdCard());
        //查询客户银行
        CustomerBank customerBank = paymentServer.getCustomerBankById(investVo.getCustomerBankId());
        payment.setBankAccount(customerBank.getBankAccount());
        payment.setBankCode(customerBank.getBankCode());
        payment.setPhone(customerBank.getPhone());
        return payment;
    }

    //1.更新投资状态：投资成功
    //2.插入新出借单
    //3.发送撮合系统：新出借（MQ）
    //注意：1和2在同一事务
    @Transactional
    public Result receivePayResult(InvestMQVo investMQVo){
        Integer investId = Integer.parseInt(investMQVo.getBizId());
        //1.更新投资状态：投资成功
        Result result = this.updateInvestBizState(investId, InvestBizStateEnum.INVEST_SUC);
        if(Result.checkStatus(result)){
            //2.插入新出借单
            result = lendingService.addNewLending(investMQVo);
        }
        if(Result.checkStatus(result)){
            //3.发送撮合系统：新出借（MQ）

        }
        return result;
    }

    @Override
    public Result updateInvestBizState(Integer investId, InvestBizStateEnum investBizStateEnum) {
        logger.debug("【更新投资状态】investId=" + investId + ",state=" + investBizStateEnum);
        Result result = Result.error();
        try{
            Invest invest = this.buildUpdateInvestState(investId,investBizStateEnum);
            int count = investMapper.updateByPrimaryKeySelective(invest);
            if(count == 1){
                result = Result.success();
            }
            logger.debug("更新数据库：投资状态【investId=】" + investId + ",【investState=】"
                    + investBizStateEnum.getState() + "count=" + count);
        }catch(Exception e){
            result = LoggerUtil.addExceptionLog(e,logger);
        }
        return result;
    }

    private Invest buildUpdateInvestState(Integer investId, InvestBizStateEnum investBizStateEnum) {
        Invest invest = new Invest();
        invest.setId(investId);
        invest.setBizState(investBizStateEnum.getState());
        return invest;
    }

    private Payment buildAddPayment(InvestVo investVo) {
        //查询客户表数据库
        Customer customer = crmServer.getCustomerById(investVo.getCustomerId());
        //封装Payment
        String sysSourceName = SystemSourceEnum.INVEST.getName();
        Payment payment = new Payment();
        payment.setCustomerId(investVo.getCustomerId());
        payment.setSystemSource(sysSourceName);
        payment.setBizId(String.valueOf(investVo.getId()));
        payment.setOrderSn(OrderUtil.genOrderSn(sysSourceName));
        payment.setCustomerName(customer.getName());
        payment.setIdCard(customer.getIdCard());
        payment.setBankAccount(investVo.getBankAccount());
        payment.setBankCode(investVo.getBankCode());
        payment.setPhone(investVo.getPhone());
        payment.setAmount(investVo.getInvestAmt());
        logger.debug("【buildAddPayment from investVo to payment=】" + payment);
        return payment;
    }

    //添加新投资
    private Result addNewInvest(Invest invest){
        Result result = Result.success();
        try{
            //1.查询投资产品
            InvestProduct investProduct =
                    investProductService.getInvestProductById(invest.getInvestProductId());
            //2.封装新投资
            result = this.buildNewInvest(investProduct,invest);
            //3.插入数据库
            result = this.addInvestInDB(invest);
        }catch(Exception e){
            result = LoggerUtil.addExceptionLog(e,logger);
        }
        return result;
    }

    private Result addInvestInDB(Invest invest){
        Result result = Result.success();
        try{
            //1.设置时间，操作人，状态
            BeanHelper.setAddDefaultField(invest);
            investMapper.insert(invest);
            logger.debug("【invest.insert.db.invest=】" + invest);
        }catch (Exception e){
            result = LoggerUtil.addExceptionLog(e,logger);
        }
        return result;
    }

    private Result buildNewInvest(InvestProduct investProduct,Invest invest) {
        Result result = Result.success();
        invest.setStartDate(new Date());//开始时间
        result = this.buildEndDate(investProduct,invest);//结束时间
        result = this.buildProfit(investProduct,invest);//计算收益
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
        * @throws:
        */
    private Result buildProfit(InvestProduct investProduct, Invest invest) {
        Result result = Result.success();
        //日收益
        BigDecimal dayProfit = invest.getInvestAmt().multiply(investProduct.getYearIrr())
                .divide(new BigDecimal("365"), SysConstant.BIGDECIMAL_DIVIDE_ROUNDMODE,BigDecimal.ROUND_HALF_UP);
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
         * @description:
         * 设置投资结束日期endDate
         * 投资类型1-固定期限：非null，当前日期+InvestProduct.dayCount
         *  投资类型2-非固定期限：null
        * @author:  YX
        * @date:    2020/04/10 15:30
        * @param: null
        * @return:
        * @throws:
        */
    private Result buildEndDate(InvestProduct investProduct,Invest invest) {
        Result result = Result.success();
        try{
            if(investProduct.getInvestType().equals(InvestTypeEnum.FIXED.getState())){
                invest.setEndDate(DateUtil.add(invest.getStartDate(),investProduct.getDayCount()+1));
            }else{
                invest.setEndDate(null);
            }
        }catch (Exception e){
            result = LoggerUtil.addExceptionLog(e,logger);
        }
        return result;
    }

    public List<InvestVo> getInvestVoList(InvestVo investVo){
        logger.debug("【investVo】" + investVo);
        List<InvestVo> investVoList = this.queryDBnvestVoList(investVo);
        return investVoList;
    }

    //查询数据库
    public List<InvestVo> queryDBnvestVoList(InvestVo investVo){
        Integer count = investMapper.queryInvestVoCount(investVo);
        List<InvestVo> investVoList = investMapper.queryInvestVoListByPagination(investVo,0,count);
        logger.debug("【查询数据库：queryDBInvestListByCustomerId】investList=" + investVoList);
        return investVoList;
    }
}
