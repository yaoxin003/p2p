package com.yx.p2p.ds.account.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.sun.org.apache.regexp.internal.RE;
import com.yx.p2p.ds.account.mapper.*;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.helper.BeanHelper;
import com.yx.p2p.ds.model.account.*;
import com.yx.p2p.ds.model.borrow.Borrow;
import com.yx.p2p.ds.model.match.FinanceMatchRes;
import com.yx.p2p.ds.mq.InvestMQVo;
import com.yx.p2p.ds.mq.MasterAccMQVo;
import com.yx.p2p.ds.server.FinanceMatchReqServer;
import com.yx.p2p.ds.service.AccountService;
import com.yx.p2p.ds.util.LoggerUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.omg.CORBA.Current;
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

/**
 * @description:
 * @author: yx
 * @date: 2020/04/22/17:07
 */
@Service
public class AccountServiceImpl implements AccountService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MasterAccMapper masterAccMapper;
    @Autowired
    private CashSubAccMapper cashSubAccMapper;
    @Autowired
    private CurrentSubAccMapper currentSubAccMapper;
    @Autowired
    private CurrentSubAccFlowMapper currentSubAccFlowMapper;
    @Autowired
    private ClaimSubAccMapper claimSubAccMapper;

    @Autowired
    private ClaimSubAccFlowMapper claimSubAccFlowMapper;

    @Autowired
    private DebtSubAccMapper debtSubAccMapper;
    @Autowired
    private DebtSubAccFlowMapper debtSubAccFlowMapper;
    @Autowired
    private ProfitSubAccMapper profitSubAccMapper;

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

   /* private ProfitSubAcc buildProfitSubAcc(Integer masterAccId, Integer customerId) {
        ProfitSubAcc profitSubAcc = new ProfitSubAcc();
        profitSubAcc.setMasterAccId(masterAccId);
        profitSubAcc.setCustomerId(customerId);
        profitSubAcc.setAmount(BigDecimal.ZERO);
        BeanHelper.setAddDefaultField(profitSubAcc);
        return profitSubAcc;
    }

    private DebtSubAcc buildDebtSubAcc(Integer masterAccId, Integer customerId) {
        DebtSubAcc debtSubAcc = new DebtSubAcc();
        debtSubAcc.setMasterAccId(masterAccId);
        debtSubAcc.setCustomerId(customerId);
        debtSubAcc.setAmount(BigDecimal.ZERO);
        BeanHelper.setAddDefaultField(debtSubAcc);
        return debtSubAcc;
    }

    private ClaimSubAcc buildClaimSubAcc(Integer masterAccId, Integer customerId) {
        ClaimSubAcc claimSubAcc = new ClaimSubAcc();
        claimSubAcc.setMasterAccId(masterAccId);
        claimSubAcc.setCustomerId(customerId);
        claimSubAcc.setAmount(BigDecimal.ZERO);
        BeanHelper.setAddDefaultField(claimSubAcc);
        return claimSubAcc;
    }



    private CashSubAcc buildCashSubAcc(Integer masterAccId, Integer customerId) {
        CashSubAcc cashSubAcc = new CashSubAcc();
        cashSubAcc.setMasterAccId(masterAccId);
        cashSubAcc.setCustomerId(customerId);
        cashSubAcc.setAmount(BigDecimal.ZERO);
        BeanHelper.setAddDefaultField(cashSubAcc);
        return cashSubAcc;
    }*/

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
    public Result loan(HashMap<String, String> loanMap) {
        logger.debug("【放款，入参：loadMap=】" + loanMap);
        Result result = Result.error();
        String orderSn = loanMap.get("orderSn");
        String borrowId = loanMap.get("bizId");//借款编号
        String customerId = loanMap.get("customerId");//融资客户
        String status = loanMap.get("status");
        //1.验证是否已经插入过放款数据(借款人债务子账户流水)
        result = this.checkNoLoan(orderSn);
        if(Result.checkStatus(result)){
            //查询借款撮合数据
            List<FinanceMatchRes> borrowMatchResList = financeMatchReqServer.getBorrowMatchResList(
                    Integer.valueOf(customerId), borrowId);
            result = this.dealLoanData(customerId,borrowId,borrowMatchResList);
        }
        result = Result.success();
        logger.debug("【放款，结果：result=】" + result);
        return result;
    }

    /** 事务操作：
     * 总述：根据借款撮合结果，插入和更新账户数据
     * 详述：
     * 插入：借款人债务子账户流水，债务子账户。
     * 更新操作：借款人债务金额增加；
     * 插入：投资人活期子账户流水，活期子账户；投资人债权子账户流水，债权子账户。
     * 更新操作：投资人活期金额减，债权金额加。
     */
    @Transactional
    private Result dealLoanData(String customerId,String borrowId,List<FinanceMatchRes> borrowMatchResList) {
        logger.debug("【准备处理放款，入参：customerId=】。入参：customerId="+ customerId
                + ",borrowId=" + borrowId+ ",borrowMatchResList=" + borrowMatchResList);
        Result result = Result.error();
        if(!borrowMatchResList.isEmpty()){
            Integer customerIdInt = Integer.valueOf(customerId);
            result = this.dealLoanBorrow(customerIdInt,borrowId,borrowMatchResList);
            result = this.dealLoanInvest(customerIdInt,borrowId,borrowMatchResList);
        }
        result = Result.success();
        return result;
    }

    //处理放款：投资人
    private Result dealLoanInvest(Integer customerId, String borrowId, List<FinanceMatchRes> borrowMatchResList) {
        logger.debug("【借款人放款账户处理】插入投资账户处理。入参：customerId="+ customerId
                + ",borrowId=" + borrowId+ ",borrowMatchResList=" + borrowMatchResList);
        Result result = Result.error();
        //借款人主账户
        this.dealLoanInvestCurrent(borrowId,borrowMatchResList);
        this.dealLoanInvestClaim(borrowId,borrowMatchResList);
        result = Result.success();
        return result;
    }

    //处理放款：投资人债权户
    //插入：投资人债权子账户流水，债权子账户。
    private Result dealLoanInvestClaim( String borrowId, List<FinanceMatchRes> borrowMatchResList) {
        logger.debug("【借款人放款账户处理】插入投资人债权子账户流水，债权子账户。" +
                "入参：" + ",borrowId=" + borrowId+ ",borrowMatchResList=" + borrowMatchResList);
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
        //投资人债权子账户流水
        for(ClaimSubAccFlow claimSubAccFlow : claimSubAccFlowList){
            MasterAcc masterAcc = this.queryDBMasterAcc(claimSubAccFlow.getCustomerId());
            //更新投资人主账户债权金额
            MasterAcc paramMaster = new MasterAcc();
            paramMaster.setId(masterAcc.getId());
            paramMaster.setClaimAmt(masterAcc.getClaimAmt().add(claimSubAccFlow.getAmount()));
            BeanHelper.setUpdateDefaultField(paramMaster);
            masterAccMapper.updateByPrimaryKeySelective(paramMaster);
            //插入或更新投资人债权子账户
            ClaimSubAcc paramClaimSubAcc = new ClaimSubAcc();
            paramClaimSubAcc.setBizId(claimSubAccFlow.getBizId());
            paramClaimSubAcc.setCustomerId(claimSubAccFlow.getCustomerId());
            ClaimSubAcc dbClaimSubAcc = claimSubAccMapper.selectOne(paramClaimSubAcc);
            ClaimSubAcc claimSubAcc = new ClaimSubAcc();
            if(dbClaimSubAcc == null){//插入
                claimSubAcc.setMasterAccId(masterAcc.getId());
                claimSubAcc.setBizId(claimSubAccFlow.getBizId());
                claimSubAcc.setCustomerId(claimSubAccFlow.getCustomerId());
                claimSubAcc.setAmount(claimSubAccFlow.getAmount());
                BeanHelper.setAddDefaultField(claimSubAcc);
                claimSubAccMapper.insert(claimSubAcc);
            }else{//更新
                claimSubAcc.setId(dbClaimSubAcc.getId());
                claimSubAcc.setAmount(dbClaimSubAcc.getAmount().add(claimSubAccFlow.getAmount()));
                BeanHelper.setUpdateDefaultField(claimSubAcc);
                claimSubAccMapper.updateByPrimaryKeySelective(claimSubAcc);
            }
            claimSubAccFlow.setClaimSubId(claimSubAcc.getId());
        }
        //插入投资人债权子账户
        claimSubAccFlowMapper.insertBatchClaimSubAccFlow(claimSubAccFlowList);
        result = Result.success();
        return result;
    }
    //处理放款：投资人活期户
    //插入：投资人活期子账户流水，活期子账户
    private Result dealLoanInvestCurrent( String borrowId, List<FinanceMatchRes> borrowMatchResList) {
        logger.debug("【借款人放款账户处理】插入投资人活期子账户流水，活期子账户。" +
                "入参：" + "borrowId=" + borrowId+ ",borrowMatchResList=" + borrowMatchResList);
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
        //投资人活期子账户流水
        for(CurrentSubAccFlow currentSubAccFlow : currentSubAccFlowList){
            MasterAcc masterAcc = this.queryDBMasterAcc(currentSubAccFlow.getCustomerId());
            //更新投资人主账户活期金额(加一个负值）
            MasterAcc paramMaster = new MasterAcc();
            paramMaster.setId(masterAcc.getId());
            paramMaster.setCurrentAmt(masterAcc.getCurrentAmt().add(currentSubAccFlow.getAmount()));
            BeanHelper.setUpdateDefaultField(paramMaster);
            masterAccMapper.updateByPrimaryKeySelective(paramMaster);
            //插入或更新投资人活期子账户
            CurrentSubAcc paramCurrentSubAcc = new CurrentSubAcc();
            paramCurrentSubAcc.setBizId(currentSubAccFlow.getBizId());
            paramCurrentSubAcc.setCustomerId(currentSubAccFlow.getCustomerId());
            CurrentSubAcc dbCurrentSubAcc = currentSubAccMapper.selectOne(paramCurrentSubAcc);
            CurrentSubAcc currentSubAcc = new CurrentSubAcc();
            if(dbCurrentSubAcc == null){//插入
                currentSubAcc.setMasterAccId(masterAcc.getId());
                currentSubAcc.setBizId(currentSubAccFlow.getBizId());
                currentSubAcc.setCustomerId(currentSubAccFlow.getCustomerId());
                currentSubAcc.setAmount(currentSubAccFlow.getAmount());
                BeanHelper.setAddDefaultField(currentSubAcc);
                currentSubAccMapper.insert(currentSubAcc);
            }else{//更新
                currentSubAcc.setId(dbCurrentSubAcc.getId());
                //更新投资人子账户活期金额(加一个负值）
                currentSubAcc.setAmount(dbCurrentSubAcc.getAmount().add(currentSubAccFlow.getAmount()));
                BeanHelper.setUpdateDefaultField(currentSubAcc);
                currentSubAccMapper.updateByPrimaryKeySelective(currentSubAcc);
            }
            currentSubAccFlow.setCurrentSubId(currentSubAcc.getId());
        }
        //插入投资人活期子账户流水
        currentSubAccFlowMapper.insertBatchCurrentSubAccFlow(currentSubAccFlowList);
        result = Result.success();
        return result;
    }

    //处理放款：借款人
    private Result dealLoanBorrow(Integer customerId, String borrowId, List<FinanceMatchRes> borrowMatchResList) {
        logger.debug("【借款人放款账户处理】插入借款人债务子账户流水，债务子账户；"
                +"更新操作：借款人债务金额增加。入参：customerId="+ customerId
                + ",borrowId=" + borrowId+ ",borrowMatchResList=" + borrowMatchResList);
        Result result = Result.error();
        //借款人主账户
        MasterAcc masterAcc = this.queryDBMasterAcc(customerId);
        List<DebtSubAccFlow> debtSubAccFlowList = new ArrayList<>();
        BigDecimal totalAmt = BigDecimal.ZERO;
        //借款人债务子账户流水
        for(FinanceMatchRes financeMatchRes : borrowMatchResList){
            DebtSubAccFlow debtSubAccFlow = new DebtSubAccFlow();
            debtSubAccFlow.setOrderSn(financeMatchRes.getFinanceOrderSn());
            debtSubAccFlow.setAmount(financeMatchRes.getTradeAmt());
            debtSubAccFlow.setCustomerId(customerId);
            debtSubAccFlow.setBizId(borrowId);
            debtSubAccFlow.setRemark("投资客户" + financeMatchRes.getInvestCustomerName() +
                    ",编号" + financeMatchRes.getInvestCustomerId()+"出借");
            totalAmt = totalAmt.add(financeMatchRes.getTradeAmt());
            BeanHelper.setAddDefaultField(debtSubAccFlow);
            debtSubAccFlowList.add(debtSubAccFlow);
        }
        //借款人债务子账户
        DebtSubAcc debtSubAcc = new DebtSubAcc();
        debtSubAcc.setMasterAccId(masterAcc.getId());
        debtSubAcc.setBizId(borrowId);
        debtSubAcc.setCustomerId(customerId);
        debtSubAcc.setAmount(totalAmt);
        BeanHelper.setAddDefaultField(debtSubAcc);
        //更新主账户金额
        MasterAcc dbMasterAcc = new MasterAcc();
        dbMasterAcc.setId(masterAcc.getId());
        dbMasterAcc.setDebtAmt(masterAcc.getDebtAmt().add(totalAmt));
        BeanHelper.setUpdateDefaultField(dbMasterAcc);
        masterAccMapper.updateByPrimaryKeySelective(dbMasterAcc);
        //插入借款人债务子账户
        debtSubAccMapper.insert(debtSubAcc);
        //插入借款人债务子账户流水
        for(DebtSubAccFlow debtSubAccFlow : debtSubAccFlowList){
            debtSubAccFlow.setDebtSubId(debtSubAcc.getId());
        }
        debtSubAccFlowMapper.insertBatchDebtSubAccFlow(debtSubAccFlowList);
        result = Result.success();
        return result;
    }

    //验证是否已经插入过放款数据(借款人债务子账户流水)
    private Result checkNoLoan(String orderSn) {
        logger.debug("【检查没有放款数据】入参orderSn=" + orderSn);
        Result result = Result.error();
        DebtSubAccFlow debtSubAccFlow = new DebtSubAccFlow();
        debtSubAccFlow.setOrderSn(orderSn);
        List<DebtSubAccFlow> debtSubAccFlows = debtSubAccFlowMapper.select(debtSubAccFlow);
        if(debtSubAccFlows.isEmpty()){
            result = Result.success();
        }
        logger.debug("【检查没有放款数据】result.status=error说明已经执行操作；" +
                "result.status=ok说明没有放款数据，继续执行。result=" + result);
        return result;
    }



}
