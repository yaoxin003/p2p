package com.yx.p2p.ds.account.service.impl;

import com.sun.org.apache.regexp.internal.RE;
import com.yx.p2p.ds.account.mapper.*;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.helper.BeanHelper;
import com.yx.p2p.ds.model.account.*;
import com.yx.p2p.ds.mq.InvestMQVo;
import com.yx.p2p.ds.mq.MasterAccMQVo;
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
    private ClaimSubAccMapper claimSubAccMapper;
    @Autowired
    private DebtSubAccMapper debtSubAccMapper;
    @Autowired
    private ProfitSubAccMapper profitSubAccMapper;
    @Autowired
    private CurrentSubAccFlowMapper currentSubAccFlowMapper;

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
}
