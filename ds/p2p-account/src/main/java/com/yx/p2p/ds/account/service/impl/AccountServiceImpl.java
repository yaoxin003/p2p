package com.yx.p2p.ds.account.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.qos.command.impl.Ls;
import com.yx.p2p.ds.account.mapper.*;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.helper.BeanHelper;
import com.yx.p2p.ds.model.account.*;
import com.yx.p2p.ds.model.match.FinanceMatchRes;
import com.yx.p2p.ds.mq.InvestMQVo;
import com.yx.p2p.ds.mq.MasterAccMQVo;
import com.yx.p2p.ds.server.FinanceMatchReqServer;
import com.yx.p2p.ds.service.account.AccountCoreService;
import com.yx.p2p.ds.service.account.AccountService;
import com.yx.p2p.ds.util.LoggerUtil;
import com.yx.p2p.ds.util.OrderUtil;
import io.netty.handler.codec.redis.ArrayRedisMessage;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @description:和业务相关的AccountService
 * @author: yx
 * @date: 2020/04/22/17:07
 */
@Service
public class AccountServiceImpl implements AccountService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MasterAccMapper masterAccMapper;

    @Autowired
    private CurrentSubAccMapper currentSubAccMapper;

    @Autowired
    private CurrentSubAccFlowMapper currentSubAccFlowMapper;

    @Autowired
    private DebtSubAccMapper debtSubAccMapper;

    @Autowired
    private DebtSubAccFlowMapper debtSubAccFlowMapper;

    @Autowired
    private ProfitSubAccMapper profitSubAccMapper;

    @Autowired
    private ClaimSubAccFlowMapper claimSubAccFlowMapper;

    @Autowired
    private AccountCoreService accountCoreService;

    @Reference
    private FinanceMatchReqServer financeMatchReqServer;

    //开户：主账户
    @Transactional
    public Result openAccount(MasterAccMQVo masterAccMQVo){
        logger.debug("【开户】masterAccMQVo=" + masterAccMQVo);
        Result result = Result.error();
        try {
            //1.验证是否开户
            result = this.checkNoOpenMasterAcc(masterAccMQVo.getCustomerId());
            if(Result.checkStatus(result)){//未开户
                //2.未开户，则添加主账户
                result = this.addMasterAcc(masterAccMQVo);
            }else{
                logger.debug("【已开户】masterAccMQVo=" + masterAccMQVo);
            }
        }catch (Exception e){
            result = LoggerUtil.addExceptionLog(e,logger);
            throw new RuntimeException("开户异常");
        }
        return result;
    }

    //添加主账户
    private Result addMasterAcc(MasterAccMQVo masterAccMQVo) throws Exception {
        Result result = Result.error();
        MasterAcc masterAcc = this.buildAddMasterAcc(masterAccMQVo);
        int count = masterAccMapper.insert(masterAcc);
        if(count == 1){
            result = Result.success();
        }
        return result;
    }

    //验证是否开户status=ok未开户,status=error开户
    private Result checkNoOpenMasterAcc(Integer customerId) {
        Result result = Result.error();
        MasterAcc masterAcc = this.queryDBMasterAcc(customerId);
        if(masterAcc == null){
            result = Result.success();
        }
        return result;
    }

    //事务操作：投资充值
    //1.幂等性验证：活期分户流水是否插入，若未插入：
    //2.累加主账户的活期金额
    //3.插入活期分户
    //4.插入活期分户流水
    @Transactional
    public Result rechargeInvest(InvestMQVo investMQVo){
        Result result = Result.error();
        //1.幂等性验证：活期分户流水是否插入
        result = this.checkCurrentSubAccFlow(investMQVo.getOrderSn());
        if(Result.checkStatus(result)){
            MasterAcc masterAcc = this.queryDBMasterAcc(investMQVo.getCustomerId());
            Integer masterAccId = masterAcc.getId();
            //2.累加主账户的活期金额
            this.updateMasterAccCurrentAmt(masterAccId,masterAcc.getCurrentAmt(),investMQVo.getAmount());
            //3.插入活期分户
            result = this.addCurrentSubAcc(masterAccId,investMQVo);
            if(Result.checkStatus(result)) {
                Integer currentSubAccId = (Integer)result.getTarget();
                //4.插入活期分户流水
                result = this.addCurrentSubAccFlow(currentSubAccId,investMQVo);
            }
        }else {
            logger.debug("【账户投资充值记账已完成，不能重复操作】investMQVo="+investMQVo);
        }
        return result;
    }

    //累加主账户的活期金额
    private Result updateMasterAccCurrentAmt(Integer masterAccId, BigDecimal currentAmt, BigDecimal amount) {
        logger.debug("【累加主账户的活期金额】masterAccId=" +masterAccId);
        Result result = Result.error();
        MasterAcc masterAcc = new MasterAcc();
        masterAcc.setId(masterAccId);
        masterAcc.setCurrentAmt(currentAmt.add(amount));
        BeanHelper.setUpdateDefaultField(masterAcc);
        int count = masterAccMapper.updateByPrimaryKeySelective(masterAcc);
        if(count == 1){
            result = Result.success();
            logger.debug("【累加主账户的活期金额】count=" +count);
        }
        return result;
    }

    private Result addCurrentSubAccFlow(Integer currentSubAccId ,InvestMQVo investMQVo){
        logger.debug("【插入活期分户流水】investMQVo=" + investMQVo);
        Result result = Result.error();
        try{
            CurrentSubAccFlow currentSubAccFlow = this.buildAddCurrentSubAccFlow(currentSubAccId,investMQVo);
            int count = currentSubAccFlowMapper.insert(currentSubAccFlow);
            if(count == 1){
                result = Result.success();
            }
            logger.debug("【插入活期分户流水】count=" +count);
        }catch (Exception e){
            throw new RuntimeException(e);
        }

        return result;
    }

    private CurrentSubAccFlow buildAddCurrentSubAccFlow(Integer currentSubAccId ,InvestMQVo investMQVo) throws Exception{
        CurrentSubAccFlow currentSubAccFlow = new CurrentSubAccFlow();
        try{
            BeanUtils.copyProperties(currentSubAccFlow,investMQVo);
            currentSubAccFlow.setCurrentSubId(currentSubAccId);
            currentSubAccFlow.setRemark(investMQVo.getBaseBankName() + ":" + investMQVo.getBankAccount());
            BeanHelper.setAddDefaultField(currentSubAccFlow);
            logger.debug("【构建插入活期分户流水】currentSubAccFlow=" + currentSubAccFlow);
        }catch (Exception e){
            throw new Exception(e);
        }
        return currentSubAccFlow;
    }

    //插入活期分户金额
    private Result addCurrentSubAcc(Integer masterAccId,InvestMQVo investMQVo) {
        logger.debug("【插入活期分户金额】investMQVo=" + investMQVo);
        Result result = Result.error();
        CurrentSubAcc currentSubAcc = this.buildAddCurrentSubAcc(masterAccId,investMQVo);
        BeanHelper.setAddDefaultField(currentSubAcc);
        if(currentSubAcc != null){
            int count = currentSubAccMapper.insert(currentSubAcc);
            if(count == 1){
                result = Result.success();
                result.setTarget(currentSubAcc.getId());
                logger.debug("【插入活期分户金额】count=" + count);
            }
        }
        return result;
    }

    //查看活期分户流水是否存在。存在Result.status=ok， 不存在Result.status=error
    private Result checkCurrentSubAccFlow(String orderSn) {
        logger.debug("【查看活期分户流水是否存在】orderSn=" + orderSn);
        Result result = Result.error();
        try{
            CurrentSubAccFlow currentSubAccFlow = this.queryDBCurrentSubAccFlow(orderSn);
            if(currentSubAccFlow == null){
                result = Result.success();
            }
        }catch (Exception e){
            result = LoggerUtil.addExceptionLog(e,logger);
        }
        logger.debug("【查看活期分户流水是否存在】orderSn=" + orderSn + "，result=" + result);
        return result;
    }

    private CurrentSubAccFlow queryDBCurrentSubAccFlow(String orderSn) {
        Example example = new Example(CurrentSubAccFlow.class);
        example.createCriteria().andEqualTo("orderSn",orderSn);
        CurrentSubAccFlow currentSubAccFlow = currentSubAccFlowMapper.selectOneByExample(example);
        return currentSubAccFlow;
    }


    private MasterAcc queryDBMasterAcc(Integer customerId){
        logger.debug("【查询数据库MasterAcc】customerId=" + customerId);
        Example example = new Example(MasterAcc.class);
        example.createCriteria().andEqualTo("customerId",customerId);
        MasterAcc masterAcc = masterAccMapper.selectOneByExample(example);
        logger.debug("【查询数据库MasterAcc】masterAcc=" + masterAcc);
        return masterAcc;
    }

    private CurrentSubAcc buildAddCurrentSubAcc(Integer masterAccId,InvestMQVo investMQVo) {
        Integer customerId = investMQVo.getCustomerId();
        String bizId = investMQVo.getBizId();
        CurrentSubAcc currentSubAcc = null;
        currentSubAcc = new CurrentSubAcc();
        currentSubAcc.setMasterAccId(masterAccId);
        currentSubAcc.setCustomerId(customerId);
        currentSubAcc.setAmount(investMQVo.getAmount());
        currentSubAcc.setBizId(bizId);
        BeanHelper.setAddDefaultField(currentSubAcc);
        return currentSubAcc;
    }

    private MasterAcc buildAddMasterAcc(MasterAccMQVo masterAccMQVo)throws Exception {
        MasterAcc masterAcc = new MasterAcc();
        BeanUtils.copyProperties(masterAcc,masterAccMQVo);
        BigDecimal zero = BigDecimal.ZERO;
        masterAcc.setCashAmt(zero);
        masterAcc.setCurrentAmt(zero);
        masterAcc.setClaimAmt(zero);
        masterAcc.setDebtAmt(zero);
        masterAcc.setProfitAmt(zero);
        BeanHelper.setAddDefaultField(masterAcc);
        return masterAcc;
    }

    /**
     * 1.验证是否已经插入过放款数据
     * 2.若未插入数据，则查询借款撮合数据，插入和更新账户数据
     * @param loanMap:bizId,orderSn,status
     * @return
     */
    @Override
    @Transactional
    public Result loanNotice(HashMap<String, String> loanMap) {
        logger.debug("【账户管理：放款】入参：loadMap={}" ,loanMap);
        Result result = Result.error();
        String orderSn = loanMap.get("orderSn");//都是借款编号borrowId
        String borrowId = loanMap.get("bizId");//都是借款编号borrowId
        String financeCustomerId = loanMap.get("customerId");//融资客户
        String status = loanMap.get("status");

        //1.验证是否已经插入过放款数据(借款人债务子账户流水)
        result = this.checkNoLoanNotice(orderSn);
        if(Result.checkStatus(result)){
            Integer financeCustomerIdInt = Integer.valueOf(financeCustomerId);
            //查询借款撮合数据
            List<FinanceMatchRes> borrowMatchResList = financeMatchReqServer.getBorrowMatchResList(
                    financeCustomerIdInt, borrowId);
            result = this.dealLoanNoticeData(financeCustomerIdInt,borrowId,borrowMatchResList);
        }
        result = Result.success();
        logger.debug("【账户管理：放款】结果：result=" + result);
        return result;
    }

    //债务增值：债务户增
    @Transactional
    public Result debtAddAccount(List<DebtSubAccFlow> debtFlowList){
        Result result = Result.error();
        //债务户增
        debtFlowList = this.filterDebtSubAccFlowList(debtFlowList);
        if(!debtFlowList.isEmpty()){
            accountCoreService.dealDebtAccount(debtFlowList);
        }
        result = Result.success();
        return result;
    }

    //债务还款到账：债务户减
    @Transactional
    public Result debtReturnArriveAccount(List<DebtSubAccFlow> debtFlowList){
        Result result = Result.error();
        //债务户减
        logger.debug("【账户处理：债务还款到账，债务户减】");
        debtFlowList = this.filterDebtSubAccFlowList(debtFlowList);
        accountCoreService.dealDebtAccount(debtFlowList);
        result = Result.success();
        return result;
    }

    //投资回款记账：债权户减，活期户加
    @Override
    public Result investReturnArriveAccount(List<ClaimSubAccFlow> claimFlowList, List<CurrentSubAccFlow> currentFlowList) {
        Result result = Result.error();
        logger.debug("【账户处理：投资还款到账，债务户减】");
        //债务户减
        claimFlowList = this.filterClaimSubAccFlowList(claimFlowList);
        accountCoreService.dealClaimAccount(claimFlowList);
        //活期户加
        logger.debug("【账户处理：投资还款到账，活期户加】");
        currentFlowList = this.filterCurrentSubAccFlowList(currentFlowList);
        accountCoreService.dealCurrentAccount(currentFlowList);
        result = Result.success();
        return result;
    }

    private List<ClaimSubAccFlow> filterClaimSubAccFlowList(List<ClaimSubAccFlow> paramFlowList) {
        List<ClaimSubAccFlow> resFlowList = new ArrayList();
        List<String> paramOrderSnList = new ArrayList<>();
        for (ClaimSubAccFlow paramFlow : paramFlowList) {
            paramOrderSnList.add(paramFlow.getOrderSn());
        }
        List<ClaimSubAccFlow> dbFlowList = this.queryClaimSubAccFlowList(paramOrderSnList);
        //删除存在数据
        Map<String,ClaimSubAccFlow> paramMap = new HashMap<>();//map<OrderSn,ClaimSubAccFlow>
        for (ClaimSubAccFlow paramFlow : paramFlowList) {
            paramMap.put(paramFlow.getOrderSn(),paramFlow);
        }
        for (ClaimSubAccFlow dbFlow : dbFlowList) {
            paramMap.remove(dbFlow.getOrderSn());
        }
        for (String orderSn : paramMap.keySet()) {
            resFlowList.add(paramMap.get(orderSn));
        }
        paramOrderSnList = null;
        dbFlowList = null;
        paramMap = null;
        logger.debug("【过滤后ClaimSubAccFlow】resFlowList=" + resFlowList);
        return resFlowList;
    }

    private List<ClaimSubAccFlow> queryClaimSubAccFlowList(List<String> paramOrderSnList) {
        Example example = new Example(ClaimSubAccFlow.class);
        example.createCriteria().andIn("orderSn",paramOrderSnList);
        return claimSubAccFlowMapper.selectByExample(example);
    }

    private List<CurrentSubAccFlow> filterCurrentSubAccFlowList(List<CurrentSubAccFlow> paramFlowList) {
        List<CurrentSubAccFlow> resFlowList = new ArrayList();
        List<String> paramOrderSnList = new ArrayList<>();
        for (CurrentSubAccFlow paramFlow : paramFlowList) {
            paramOrderSnList.add(paramFlow.getOrderSn());
        }
        List<CurrentSubAccFlow> dbFlowList = this.queryCurrentSubAccFlowList(paramOrderSnList);
        //删除存在数据
        Map<String,CurrentSubAccFlow> paramMap = new HashMap<>();//map<OrderSn,CurrentSubAccFlow>
        for (CurrentSubAccFlow paramFlow : paramFlowList) {
            paramMap.put(paramFlow.getOrderSn(),paramFlow);
        }
        for (CurrentSubAccFlow dbFlow : dbFlowList) {
            paramMap.remove(dbFlow.getOrderSn());
        }
        for (String orderSn : paramMap.keySet()) {
            resFlowList.add(paramMap.get(orderSn));
        }
        paramOrderSnList = null;
        dbFlowList = null;
        paramMap = null;
        logger.debug("【过滤后CurrentSubAccFlow】resFlowList=" + resFlowList);
        return resFlowList;
    }

    private List<CurrentSubAccFlow> queryCurrentSubAccFlowList(List<String> paramOrderSnList) {
        Example example = new Example(CurrentSubAccFlow.class);
        example.createCriteria().andIn("orderSn",paramOrderSnList);
        return currentSubAccFlowMapper.selectByExample(example);
    }

    private List<DebtSubAccFlow> filterDebtSubAccFlowList(List<DebtSubAccFlow> paramFlowList) {
        List<DebtSubAccFlow> resFlowList = new ArrayList();
        List<String> paramOrderSnList = new ArrayList<>();
        for (DebtSubAccFlow paramFlow : paramFlowList) {
            paramOrderSnList.add(paramFlow.getOrderSn());
        }
        List<DebtSubAccFlow> dbFlowList = this.queryDebtSubAccFlowList(paramOrderSnList);
        //删除存在数据
        Map<String,DebtSubAccFlow> paramMap = new HashMap<>();//map<OrderSn,DebtSubAccFlow>
        for (DebtSubAccFlow paramFlow : paramFlowList) {
            paramMap.put(paramFlow.getOrderSn(),paramFlow);
        }
        for (DebtSubAccFlow dbFlow : dbFlowList) {
            paramMap.remove(dbFlow.getOrderSn());
        }
        for (String orderSn : paramMap.keySet()) {
            resFlowList.add(paramMap.get(orderSn));
        }
        paramOrderSnList = null;
        dbFlowList = null;
        paramMap = null;
        logger.debug("【过滤后DebtSubAccFlow】resFlowList=" + resFlowList);
        return resFlowList;
    }

    private List<DebtSubAccFlow> queryDebtSubAccFlowList(List<String> paramOrderSnList){
        Example example = new Example(DebtSubAccFlow.class);
        example.createCriteria().andIn("orderSn",paramOrderSnList);
        List<DebtSubAccFlow> debtSubAccFlowList = debtSubAccFlowMapper.selectByExample(example);
        return debtSubAccFlowList;
    }

    /** 事务操作：
     * 总述：根据借款撮合结果，插入和更新账户数据
     * 详述：
     * 插入：借款人债务子账户流水，债务子账户。
     * 更新操作：借款人债务金额增加；
     * 插入：投资人活期子账户流水，活期子账户；投资人债权子账户流水，债权子账户。
     * 更新操作：投资人活期金额减，债权金额加。
     */
    private Result dealLoanNoticeData(Integer financeCustomerId,String borrowId,List<FinanceMatchRes> borrowMatchResList) {
        Result result = Result.error();
        if(!borrowMatchResList.isEmpty()){
            result = this.dealLoanNoticeBorrow(financeCustomerId,borrowId,borrowMatchResList);
            result = this.dealLoanNoticeInvest(financeCustomerId,borrowId,borrowMatchResList);
        }
        result = Result.success();
        return result;
    }

    //处理放款：投资人
    private Result dealLoanNoticeInvest(Integer customerId, String borrowId, List<FinanceMatchRes> borrowMatchResList) {
        Result result = Result.error();
        this.dealLoanNoticeInvestCurrent(borrowId,borrowMatchResList);//投资人活期户减
        this.dealLoanNoticeInvestClaim(borrowId,borrowMatchResList);//投资人债权户增
        result = Result.success();
        return result;
    }

    //处理放款：投资人债权户增
    //插入：投资人债权子账户流水，债权子账户。
    private Result dealLoanNoticeInvestClaim( String borrowId, List<FinanceMatchRes> borrowMatchResList) {
        logger.debug("【账户处理：投资债权增】");
        Result result = Result.error();
        List<ClaimSubAccFlow> claimSubAccFlowList = new ArrayList<>();
        for(FinanceMatchRes financeMatchRes : borrowMatchResList){
            //债权子账户流水
            ClaimSubAccFlow claimSubAccFlow = new ClaimSubAccFlow();
            claimSubAccFlow.setAmount(financeMatchRes.getTradeAmt());
            claimSubAccFlow.setCustomerId(financeMatchRes.getInvestCustomerId());
            claimSubAccFlow.setBizId(financeMatchRes.getInvestBizId());
            claimSubAccFlow.setOrderSn(financeMatchRes.getInvestOrderSn());
            claimSubAccFlow.setRemark("借款客户" + financeMatchRes.getFinanceCustomerName() +
                    ",客户编号" + financeMatchRes.getFinanceCustomerId()+ ",借款编号" + borrowId + "借款");
            BeanHelper.setAddDefaultField(claimSubAccFlow);
            claimSubAccFlowList.add(claimSubAccFlow);
        }
        //投资人债权户处理（债权增）：债权子账户，债权子账户流水
        result = accountCoreService.dealClaimAccount(claimSubAccFlowList);
        result = Result.success();
        return result;
    }



    //处理放款：投资人活期户
    //插入：投资人活期子账户流水，活期子账户
    private Result dealLoanNoticeInvestCurrent( String borrowId, List<FinanceMatchRes> borrowMatchResList) {
        logger.debug("【账户处理：投资活期户减】");
        Result result = Result.error();
        List<CurrentSubAccFlow> currentSubAccFlowList = new ArrayList<>();
        BigDecimal zero = BigDecimal.ZERO;
        for(FinanceMatchRes financeMatchRes : borrowMatchResList){
            BigDecimal negativeTradeAmt = BigDecimal.ZERO;//负交易金额，初始值为0
            //投资人活期子账户流水
            CurrentSubAccFlow currentSubAccFlow = new CurrentSubAccFlow();
            //负值：交易金额
            negativeTradeAmt = zero.subtract(financeMatchRes.getTradeAmt());
            currentSubAccFlow.setAmount(negativeTradeAmt);
            currentSubAccFlow.setCustomerId(financeMatchRes.getInvestCustomerId());
            currentSubAccFlow.setBizId(financeMatchRes.getInvestBizId());
            currentSubAccFlow.setOrderSn(financeMatchRes.getInvestOrderSn());
            currentSubAccFlow.setRemark("借款客户" + financeMatchRes.getFinanceCustomerName() +
                    ",客户编号" + financeMatchRes.getFinanceCustomerId()+ ",借款编号" + borrowId + "借款");
            BeanHelper.setAddDefaultField(currentSubAccFlow);
            currentSubAccFlowList.add(currentSubAccFlow);
        }
        //投资人活期户处理（活期减(加一个负值））：活期子账户，活期子账户流水
        result =  accountCoreService.dealCurrentAccount(currentSubAccFlowList);
        result = Result.success();
        return result;
    }




    //处理放款：借款人
    private Result dealLoanNoticeBorrow(Integer customerId, String borrowId, List<FinanceMatchRes> borrowMatchResList) {
        Result result = Result.error();
        result = this.dealLoanNoticeBorrowDebt(customerId,borrowId,borrowMatchResList);//借款人债务户增
        result = this.dealLoanNoticeBorrowCash(customerId,borrowId,borrowMatchResList);//借款人现金户增
        result = Result.success();
        return result;
    }

    //借款人现金户增
    private Result dealLoanNoticeBorrowCash(Integer customerId, String borrowId, List<FinanceMatchRes> borrowMatchResList) {
        logger.debug("【账户处理：借款放款，现金户增】");
        Result result = Result.error();
        result = Result.success();
        List<CashSubAccFlow> cashSubAccFlowList = new ArrayList<>();
        for (FinanceMatchRes financeMatchRes : borrowMatchResList) {
            CashSubAccFlow cashSubAccFlow = new CashSubAccFlow();
            cashSubAccFlow.setOrderSn(financeMatchRes.getFinanceOrderSn());
            cashSubAccFlow.setAmount(financeMatchRes.getTradeAmt());
            cashSubAccFlow.setCustomerId(customerId);
            cashSubAccFlow.setBizId(borrowId);
            cashSubAccFlow.setRemark("投资客户" + financeMatchRes.getInvestCustomerName() +
                    ",编号" + financeMatchRes.getInvestCustomerId()+"出借");
            BeanHelper.setAddDefaultField(cashSubAccFlow);
            cashSubAccFlowList.add(cashSubAccFlow);
        }
        //债权户处理：
        result = accountCoreService.dealCashAccount(cashSubAccFlowList);
        return result;
    }

    //借款人债务户增
    private Result dealLoanNoticeBorrowDebt(Integer customerId, String borrowId, List<FinanceMatchRes> borrowMatchResList) {
        logger.debug("【账户处理：借款放款，债务户增】");
        Result result = Result.error();
        List<DebtSubAccFlow> debtSubAccFlowList = new ArrayList<>();
        //借款人债务子账户流水
        for(FinanceMatchRes financeMatchRes : borrowMatchResList){
            DebtSubAccFlow debtSubAccFlow = new DebtSubAccFlow();
            debtSubAccFlow.setOrderSn(financeMatchRes.getFinanceOrderSn());
            debtSubAccFlow.setAmount(financeMatchRes.getTradeAmt());
            debtSubAccFlow.setCustomerId(customerId);
            debtSubAccFlow.setBizId(borrowId);
            debtSubAccFlow.setRemark("投资客户" + financeMatchRes.getInvestCustomerName() +
                    ",编号" + financeMatchRes.getInvestCustomerId()+"出借");
            BeanHelper.setAddDefaultField(debtSubAccFlow);
            debtSubAccFlowList.add(debtSubAccFlow);
        }
        //债权户处理：
        result = accountCoreService.dealDebtAccount(debtSubAccFlowList);
        result = Result.success();
        return result;
    }


    //验证是否已经插入过放款数据(借款人债务子账户流水)
    private Result checkNoLoanNotice(String orderSn) {
        logger.debug("【检查没有放款数据】入参orderSn=" + orderSn);
        Result result = Result.error();
        DebtSubAccFlow debtSubAccFlow = new DebtSubAccFlow();
        debtSubAccFlow.setOrderSn(orderSn);
        List<DebtSubAccFlow> debtSubAccFlows = debtSubAccFlowMapper.select(debtSubAccFlow);
        if(debtSubAccFlows.isEmpty()){
            result = Result.success();
        }
        logger.debug("【检查没有放款】结果result={}",result);
        return result;
    }

    //投资债权交割
    //受让人：投资债权户减，投资活期户加
    //转让人：投资债权户加，投资活期户减
    @Transactional
    public Result changeInvestclaim(Map<String, Object> paramClaimMap){
        logger.debug("【投资债权交割】入参：paramClaimMap={}" , paramClaimMap);
        Result result = Result.error();
        Integer transferId = Integer.valueOf((String)paramClaimMap.get("transferId"));//转让协议编号
        Integer financeInvestId = Integer.valueOf((String)paramClaimMap.get("financeExtBizId"));//转让人投资编号
        List<Map<String, String>> tradeMapList = (List<Map<String, String>>)paramClaimMap.get("transferList");
        //受让人（投资）：投资活期户减
        result = this.dealChangeClaimInvestCurrent(transferId,financeInvestId,tradeMapList);
        //转让人（融资）：现金户加
        result = this.dealChangeClaimInvestCash(transferId,financeInvestId,tradeMapList);
        //受让人（投资）：投资债权户加，转让人（融资）：投资债权户减
        result = this.dealChangeClaimInvestClaim(transferId,financeInvestId,tradeMapList);
        result = Result.success();
        return result;
    }

    //转让人（融资）：现金户加
    private Result dealChangeClaimInvestCash(Integer transferId, Integer financeInvestId, List<Map<String, String>> tradeMapList) {
        Result result = Result.error();
        List<CashSubAccFlow> flowList = new ArrayList<>();
        BigDecimal zero = BigDecimal.ZERO;
        for (Map<String, String> tradeMap : tradeMapList) {
            //转让人（融资）：现金户加
            CashSubAccFlow flow = new CashSubAccFlow();//现金子账户流水
            flow.setAmount(new BigDecimal(tradeMap.get("tradeAmt")));
            flow.setCustomerId(Integer.valueOf(tradeMap.get("financeCustomerId")));
            flow.setBizId(financeInvestId.toString());
            flow.setOrderSn(financeInvestId.toString());
            flow.setRemark("债权转让：受让客户" + tradeMap.get("investCustomerName") +
                    ",客户编号" + tradeMap.get("investCustomerId") + ",转让编号" + transferId);
            BeanHelper.setAddDefaultField(flow);
            flowList.add(flow);

        }
        logger.debug("【转让人（融资）：现金户加】investCashFlowList=" + flowList);
        //转让人（融资）：现金户加
        result =  accountCoreService.dealCashAccount(flowList);
        result = Result.success();
        return result;
    }

    //受让人（投资）：投资债权户加，转让人（融资）：投资债权户减
    private Result dealChangeClaimInvestClaim(Integer transferId,Integer financeInvestId, List<Map<String, String>> tradeMapList) {
        Result result = Result.error();
        logger.debug("【投资债权交割，受让人（投资）：投资债权户加，转让人（融资）：投资债权户减】入参："+
                "transferId="+ transferId  + ",financeInvestId="+ financeInvestId + ",tradeMapList=" + tradeMapList);
        List<ClaimSubAccFlow> investClaimFlowList = new ArrayList<>();
        List<ClaimSubAccFlow> financeClaimFlowList = new ArrayList<>();
        BigDecimal zero = BigDecimal.ZERO;
        for (Map<String, String> tradeMap : tradeMapList) {
            //受让人（投资）：投资债权户加
            ClaimSubAccFlow investClaimAccFlow = new ClaimSubAccFlow();
            investClaimAccFlow.setAmount(new BigDecimal(tradeMap.get("tradeAmt")));
            investClaimAccFlow.setCustomerId(Integer.valueOf(tradeMap.get("investCustomerId")));
            investClaimAccFlow.setBizId(tradeMap.get("investBizId"));
            investClaimAccFlow.setOrderSn(tradeMap.get("investOrderSn"));
            investClaimAccFlow.setRemark("转让客户" + tradeMap.get("financeCustomerName") +
                    ",客户编号" + tradeMap.get("financeCustomerId") + ",转让协议编号" + transferId );
            BeanHelper.setAddDefaultField(investClaimAccFlow);
            investClaimFlowList.add(investClaimAccFlow);
            //转让人（融资）：投资债权户减
            ClaimSubAccFlow financeClaimFlow = new ClaimSubAccFlow();
            //若是活期户减则使用负值：交易金额
            BigDecimal negativeTradeAmt = BigDecimal.ZERO;//负交易金额，初始值为0
            negativeTradeAmt = zero.subtract(new BigDecimal(tradeMap.get("tradeAmt")));
            financeClaimFlow.setAmount(negativeTradeAmt);
            financeClaimFlow.setCustomerId(Integer.valueOf(tradeMap.get("financeCustomerId")));
            financeClaimFlow.setBizId(String.valueOf(financeInvestId));
            financeClaimFlow.setOrderSn(String.valueOf(financeInvestId));
            financeClaimFlow.setRemark("受让客户" + tradeMap.get("investCustomerName") +
                    ",客户编号" + tradeMap.get("investCustomerId") + ",转让协议编号" + transferId );
            BeanHelper.setAddDefaultField(financeClaimFlow);
            financeClaimFlowList.add(financeClaimFlow);
        }
        //受让人（投资）：投资债权户加
        logger.debug("【受让人（投资）：投资债权户加】investClaimFlowList=" + investClaimFlowList);
        result = accountCoreService.dealClaimAccount(investClaimFlowList);
        //转让人（融资）：投资债权户减
        logger.debug("【转让人（融资）：投资债权户减】financeClaimFlowList=" + financeClaimFlowList);
        result = accountCoreService.dealClaimAccount(financeClaimFlowList);
        result = Result.success();
        return result;
    }

    //受让人（投资）：投资活期户减
    private Result dealChangeClaimInvestCurrent(Integer transferId, Integer financeInvestId,List<Map<String, String>> tradeMapList) {
        Result result = Result.error();
        List<CurrentSubAccFlow> investCurrentFlowList = new ArrayList<>();
        BigDecimal zero = BigDecimal.ZERO;
        for (Map<String, String> tradeMap : tradeMapList) {
            //受让人（投资）：投资活期户减
            CurrentSubAccFlow investCurrentFlow = new CurrentSubAccFlow();//活期子账户流水
            //若是活期户减则使用负值：交易金额
            BigDecimal negativeTradeAmt = BigDecimal.ZERO;//负交易金额，初始值为0
            negativeTradeAmt = zero.subtract(new BigDecimal(tradeMap.get("tradeAmt")));
            investCurrentFlow.setAmount(negativeTradeAmt);
            investCurrentFlow.setCustomerId(Integer.valueOf(tradeMap.get("investCustomerId")));
            investCurrentFlow.setBizId(tradeMap.get("investBizId"));
            investCurrentFlow.setOrderSn(tradeMap.get("investOrderSn"));
            investCurrentFlow.setRemark("债权转让：转让客户" + tradeMap.get("financeCustomerName") +
                    ",客户编号" + tradeMap.get("investCustomerId") + ",转让编号" + transferId);
            BeanHelper.setAddDefaultField(investCurrentFlow);
            investCurrentFlowList.add(investCurrentFlow);

        }
        logger.debug("【受让人（投资）：投资活期户减】investCurrentFlowList=" + investCurrentFlowList);
        //受让人（投资）：投资活期户减
        result =  accountCoreService.dealCurrentAccount(investCurrentFlowList);
        result = Result.success();
        return result;
    }

    public MasterAcc getMasterAccByCustomerId(Integer customerId){
        MasterAcc param = new MasterAcc();
        param.setCustomerId(customerId);
        MasterAcc masterAcc = masterAccMapper.selectOne(param);
        return masterAcc;
    }


    //投资增值记账：债权户加
    public Result investAddAccount(List<ClaimSubAccFlow> claimFlowList){
        Result result = Result.error();
        //债权户加
        claimFlowList = this.filterClaimSubAccFlowList(claimFlowList);
        accountCoreService.dealClaimAccount(claimFlowList);
        result = Result.success();
        return result;
    }



}
