package com.yx.p2p.ds.account.server.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.model.account.ClaimSubAccFlow;
import com.yx.p2p.ds.model.account.CurrentSubAccFlow;
import com.yx.p2p.ds.model.account.DebtSubAccFlow;
import com.yx.p2p.ds.model.account.MasterAcc;
import com.yx.p2p.ds.server.AccountServer;
import com.yx.p2p.ds.service.account.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/05/22/16:00
 */
@Service
@Component
public class AccountServerImpl implements AccountServer {

    @Autowired
    private AccountService accountService;

    public MasterAcc getMasterAccByCustomerId(Integer customerId){
        return accountService.getMasterAccByCustomerId(customerId);
    }

    //债务增值记账
    public Result debtAddAccount(List<DebtSubAccFlow> debtSubAccFlowList){
        return accountService.debtAddAccount(debtSubAccFlowList);
    }
    //债务还款到账
    public Result debtReturnArriveAccount(List<DebtSubAccFlow> debtFlowList){
        return accountService.debtReturnArriveAccount(debtFlowList);
    }

    //投资回款记账：债权户减，现金户加
    public Result investReturnArriveAccount(List<ClaimSubAccFlow> claimFlowList, List<CurrentSubAccFlow> currentFlowList){
        return accountService.investReturnArriveAccount(claimFlowList,currentFlowList);
    }


    //投资增值记账：债权户加
    public Result investAddAccount(List<ClaimSubAccFlow> claimFlowList){
        return accountService.investAddAccount(claimFlowList);
    }
}
