package com.yx.p2p.ds.account.service.impl;

import com.yx.p2p.ds.account.mapper.*;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.helper.BeanHelper;
import com.yx.p2p.ds.model.account.*;
import com.yx.p2p.ds.service.account.AccountCoreService;
import com.yx.p2p.ds.util.PageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import tk.mybatis.mapper.entity.Example;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @description:账户核心Service
 * 增删改查
 * @author: yx
 * @date: 2020/06/14/11:03
 */
@Service
public class AccountCoreServiceImpl implements AccountCoreService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MasterAccMapper masterAccMapper;

    @Autowired
    private DebtSubAccFlowMapper debtSubAccFlowMapper;

    @Autowired
    private DebtSubAccMapper debtSubAccMapper;

    @Autowired
    private CurrentSubAccMapper currentSubAccMapper;

    @Autowired
    private CurrentSubAccFlowMapper currentSubAccFlowMapper;

    @Autowired
    private ClaimSubAccMapper claimSubAccMapper;

    @Autowired
    private ClaimSubAccFlowMapper claimSubAccFlowMapper;

    @Autowired
    private CashSubAccMapper cashSubAccMapper;

    @Autowired
    private CashSubAccFlowMapper cashSubAccFlowMapper;

    //事务操作：债务户处理
    @Transactional
    public Result dealDebtAccount(List<DebtSubAccFlow> debtSubAccFlowList){
        logger.debug("【账户核心：债务户处理】入参：debtSubAccFlowList={}",debtSubAccFlowList);
        Result result = Result.error();
        DebtSubAcc debtSubAcc = null;
        for (DebtSubAccFlow debtSubAccFlow : debtSubAccFlowList) {
            //主账户
            MasterAcc dbMasterAcc = this.queryDBMasterAcc(debtSubAccFlow.getCustomerId());
            //债务子账户
            debtSubAcc = this.addOrUpdateDebtSubAcc(dbMasterAcc,debtSubAccFlow);
            //更新主账户债务金额
            MasterAcc paramMasterAcc = new MasterAcc();
            paramMasterAcc.setId(dbMasterAcc.getId());
            paramMasterAcc.setDebtAmt(dbMasterAcc.getDebtAmt().add(debtSubAccFlow.getAmount()));
            this.updateMasterAcc(paramMasterAcc);
            debtSubAccFlow.setDebtSubId(debtSubAcc.getId());
        }
        int count = this.addBatchDebtSubAccFlow(debtSubAccFlowList);
        result = Result.success();
        return result;
    }

    private int addBatchDebtSubAccFlow(List<DebtSubAccFlow> debtSubAccFlows) {
        int count = 0;
        if(!debtSubAccFlows.isEmpty()){
            count = debtSubAccFlowMapper.insertList(debtSubAccFlows);
        }
        return count;
    }

    private int updateMasterAcc(MasterAcc paramMasterAcc) {
        BeanHelper.setUpdateDefaultField(paramMasterAcc);
        int count = masterAccMapper.updateByPrimaryKeySelective(paramMasterAcc);
        return count;
    }

    private DebtSubAcc addOrUpdateDebtSubAcc(MasterAcc dbMasterAcc,DebtSubAccFlow debtSubAccFlow) {
        Integer customerId = dbMasterAcc.getCustomerId();
        String bizId = debtSubAccFlow.getBizId();
        BigDecimal amt = debtSubAccFlow.getAmount();
        DebtSubAcc dbDebtSubAcc = this.queryDebtSubAcc(customerId, bizId);
        DebtSubAcc paramSubAcc = new DebtSubAcc();
        if(dbDebtSubAcc == null){//添加
            paramSubAcc.setBizId(bizId);
            paramSubAcc.setCustomerId(customerId);
            paramSubAcc.setMasterAccId(dbMasterAcc.getId());
            paramSubAcc.setAmount(amt);
            BeanHelper.setAddDefaultField(paramSubAcc);
            //插入借款人债务子账户
            debtSubAccMapper.insert(paramSubAcc);
        }else{//更新
            paramSubAcc.setId(dbDebtSubAcc.getId());
            paramSubAcc.setAmount(dbDebtSubAcc.getAmount().add(amt));
            BeanHelper.setUpdateDefaultField(paramSubAcc);
            debtSubAccMapper.updateByPrimaryKeySelective(paramSubAcc);
        }
        return paramSubAcc;
    }

    private MasterAcc queryDBMasterAcc(Integer customerId){
        Example example = new Example(MasterAcc.class);
        example.createCriteria().andEqualTo("customerId",customerId);
        MasterAcc masterAcc = masterAccMapper.selectOneByExample(example);
        return masterAcc;
    }

    private DebtSubAcc queryDebtSubAcc(Integer customerId,String bizId){
        DebtSubAcc param = new DebtSubAcc();
        param.setCustomerId(customerId);
        param.setBizId(bizId);
        DebtSubAcc debtSubAcc = debtSubAccMapper.selectOne(param);
        return debtSubAcc;
    }

    //活期户处理：活期子账户，活期子账户流水
    public Result dealCurrentAccount(List<CurrentSubAccFlow> currentSubAccFlowList) {
        Result result = Result.error();
        logger.debug("【账户核心：活期户处理】currentSubAccFlowList={}",currentSubAccFlowList);
        CurrentSubAcc subAcc = null;
        //投资人活期子账户流水
        for(CurrentSubAccFlow currentSubAccFlow : currentSubAccFlowList){
            //借款人主账户
            MasterAcc dbMasterAcc = this.queryDBMasterAcc(currentSubAccFlow.getCustomerId());
            //活期子账户
            subAcc = this.addOrUpdateCurrentSubAcc(dbMasterAcc,currentSubAccFlow);
            //更新主账户活期金额
            MasterAcc paramMasterAcc = new MasterAcc();
            paramMasterAcc.setId(dbMasterAcc.getId());
            paramMasterAcc.setCurrentAmt(dbMasterAcc.getCurrentAmt().add(currentSubAccFlow.getAmount()));
            this.updateMasterAcc(paramMasterAcc);
            currentSubAccFlow.setCurrentSubId(subAcc.getId());
        }
        //插入投资人活期子账户流水
        this.addBatchCurrentSubAccFlow(currentSubAccFlowList);
        result = Result.success();
        return result;
    }

    private int addBatchCurrentSubAccFlow(List<CurrentSubAccFlow> currentSubAccFlowList) {
        int count = 0;
        if(!currentSubAccFlowList.isEmpty()){
            count = currentSubAccFlowMapper.insertList(currentSubAccFlowList);
        }
        return count;
    }

    private CurrentSubAcc addOrUpdateCurrentSubAcc(MasterAcc dbMasterAcc, CurrentSubAccFlow currentSubAccFlow) {
        Integer customerId = dbMasterAcc.getCustomerId();
        String bizId = currentSubAccFlow.getBizId();
        BigDecimal amt = currentSubAccFlow.getAmount();
        CurrentSubAcc dbCurrentSubAcc =  this.queryCurrentSubAcc(customerId,bizId);
        CurrentSubAcc paramSubAcc = new CurrentSubAcc();
        if(dbCurrentSubAcc == null){//添加
            paramSubAcc.setBizId(bizId);
            paramSubAcc.setCustomerId(customerId);
            paramSubAcc.setMasterAccId(dbMasterAcc.getId());
            paramSubAcc.setAmount(amt);
            BeanHelper.setAddDefaultField(paramSubAcc);
            currentSubAccMapper.insert(paramSubAcc);
        }else{//更新
            paramSubAcc.setId(dbCurrentSubAcc.getId());
            paramSubAcc.setAmount(dbCurrentSubAcc.getAmount().add(amt));
            BeanHelper.setUpdateDefaultField(paramSubAcc);
            currentSubAccMapper.updateByPrimaryKeySelective(paramSubAcc);
        }
        return paramSubAcc;
    }

    private CurrentSubAcc queryCurrentSubAcc(Integer customerId, String bizId) {
        CurrentSubAcc param = new CurrentSubAcc();
        param.setBizId(bizId);
        param.setCustomerId(customerId);
        CurrentSubAcc currentSubAcc = currentSubAccMapper.selectOne(param);
        return currentSubAcc;
    }

    //债权户处理：债权子账户，债权子账户流水
    public Result dealClaimAccount(List<ClaimSubAccFlow> claimSubAccFlowList) {
        logger.debug("【账户核心：债权户处理】claimSubAccFlowList={}",claimSubAccFlowList);
        Result result = Result.error();
        ClaimSubAcc subAcc = null;
        for(ClaimSubAccFlow claimSubAccFlow : claimSubAccFlowList){
            MasterAcc dbMasterAcc = this.queryDBMasterAcc(claimSubAccFlow.getCustomerId());
            //插入或更新债权子账户
            subAcc = this.addOrUpdateClaimSubAcc(dbMasterAcc,claimSubAccFlow);
            //更新主账户债权金额
            MasterAcc paramMasterAcc = new MasterAcc();
            paramMasterAcc.setId(dbMasterAcc.getId());
            paramMasterAcc.setClaimAmt(dbMasterAcc.getClaimAmt().add(claimSubAccFlow.getAmount()));
            this.updateMasterAcc(paramMasterAcc);
            claimSubAccFlow.setClaimSubId(subAcc.getId());
        }
        //插入投资人债权子账户
        this.addBatchClaimSubAccFlow(claimSubAccFlowList);
        result = Result.success();
        return result;
    }

    private ClaimSubAcc addOrUpdateClaimSubAcc(MasterAcc dbMasterAcc, ClaimSubAccFlow claimSubAccFlow) {
        Integer customerId = dbMasterAcc.getCustomerId();
        String bizId = claimSubAccFlow.getBizId();
        BigDecimal amt = claimSubAccFlow.getAmount();
        ClaimSubAcc dbClaimSubAcc = this.queryClaimSubAcc(customerId, bizId);
        ClaimSubAcc claimSubAcc = new ClaimSubAcc();
        if(dbClaimSubAcc == null){///添加
            claimSubAcc.setMasterAccId(dbMasterAcc.getId());
            claimSubAcc.setBizId(bizId);
            claimSubAcc.setCustomerId(claimSubAccFlow.getCustomerId());
            claimSubAcc.setAmount(amt);
            BeanHelper.setAddDefaultField(claimSubAcc);
            claimSubAccMapper.insert(claimSubAcc);
        }else{//更新
            claimSubAcc.setId(dbClaimSubAcc.getId());
            claimSubAcc.setAmount(dbClaimSubAcc.getAmount().add(amt));
            BeanHelper.setUpdateDefaultField(claimSubAcc);
            claimSubAccMapper.updateByPrimaryKeySelective(claimSubAcc);
        }
        return claimSubAcc;
    }

    private ClaimSubAcc queryClaimSubAcc(Integer customerId, String bizId) {
        ClaimSubAcc paramClaimSubAcc = new ClaimSubAcc();
        paramClaimSubAcc.setBizId(bizId);
        paramClaimSubAcc.setCustomerId(customerId);
        ClaimSubAcc dbClaimSubAcc = claimSubAccMapper.selectOne(paramClaimSubAcc);
        return dbClaimSubAcc;
    }


    private int addBatchClaimSubAccFlow(List<ClaimSubAccFlow> claimSubAccFlowList) {
        int count = 0;
        if(!claimSubAccFlowList.isEmpty()){
            count = claimSubAccFlowMapper.insertList(claimSubAccFlowList);
        }
        return count;
    }


    @Override
    public Result dealCashAccount(List<CashSubAccFlow> cashSubAccFlowList) {
        logger.debug("【账户核心：现金户处理】cashSubAccFlowList={}",cashSubAccFlowList);
        Result result = Result.error();
        CashSubAcc subAcc = null;
        for(CashSubAccFlow subAccFlow : cashSubAccFlowList){
            MasterAcc dbMasterAcc = this.queryDBMasterAcc(subAccFlow.getCustomerId());
            //插入或更新债权子账户
            subAcc = this.addOrUpdateCashSubAcc(dbMasterAcc,subAccFlow);
            //更新主账户债权金额
            MasterAcc paramMasterAcc = new MasterAcc();
            paramMasterAcc.setId(dbMasterAcc.getId());
            paramMasterAcc.setClaimAmt(dbMasterAcc.getClaimAmt().add(subAccFlow.getAmount()));
            this.updateMasterAcc(paramMasterAcc);
            subAccFlow.setCashSubId(subAcc.getId());
        }
        //插入投资人现金子账户流水
        this.addBatchCashSubAccFlow(cashSubAccFlowList);
        result = Result.success();
        return result;
    }

    private int addBatchCashSubAccFlow(List<CashSubAccFlow> cashSubAccFlowList) {
        int count = 0;
        if(!cashSubAccFlowList.isEmpty()){
            count = cashSubAccFlowMapper.insertList(cashSubAccFlowList);
        }
        return count;
    }

    private CashSubAcc addOrUpdateCashSubAcc(MasterAcc dbMasterAcc, CashSubAccFlow subAccFlow) {
        Integer customerId = dbMasterAcc.getCustomerId();
        String bizId = subAccFlow.getBizId();
        BigDecimal amt = subAccFlow.getAmount();
        CashSubAcc dbCashSubAcc = this.queryCashSubAcc(customerId, bizId);
        CashSubAcc cashSubAcc  = new CashSubAcc();
        if(dbCashSubAcc == null){///添加
            cashSubAcc.setMasterAccId(dbMasterAcc.getId());
            cashSubAcc.setBizId(bizId);
            cashSubAcc.setCustomerId(subAccFlow.getCustomerId());
            cashSubAcc.setAmount(amt);
            BeanHelper.setAddDefaultField(cashSubAcc);
            cashSubAccMapper.insert(cashSubAcc);
        }else{//更新
            cashSubAcc.setId(dbCashSubAcc.getId());
            cashSubAcc.setAmount(dbCashSubAcc.getAmount().add(amt));
            BeanHelper.setUpdateDefaultField(cashSubAcc);
            cashSubAccMapper.updateByPrimaryKeySelective(cashSubAcc);
        }
        return cashSubAcc;
    }

    private CashSubAcc queryCashSubAcc(Integer customerId, String bizId) {
        CashSubAcc param = new CashSubAcc();
        param.setBizId(bizId);
        param.setCustomerId(customerId);
        CashSubAcc dbCashSubAcc = cashSubAccMapper.selectOne(param);
        return dbCashSubAcc;
    }
}
