package com.yx.p2p.ds.timer.biz.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.model.account.ClaimSubAccFlow;
import com.yx.p2p.ds.model.account.CurrentSubAccFlow;
import com.yx.p2p.ds.model.account.DebtSubAccFlow;
import com.yx.p2p.ds.server.AccountServer;
import com.yx.p2p.ds.timer.biz.service.AccountJobService;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/06/15/19:28
 */
@Service
public class AccountJobServiceImpl implements AccountJobService {

    @Reference
    private AccountServer accountServer;

    //债务增值
    @Override
    public Result debtAddAccount(List<DebtSubAccFlow> debtFlowList){
        return accountServer.debtAddAccount(debtFlowList);
    }

    //债务还款到账
    @Override
    public Result debtReturnArriveAccount(List<DebtSubAccFlow> debtFlowList){
        return accountServer.debtReturnArriveAccount(debtFlowList);
    }

    //投资回款记账：债权户减，活期户加
    @Override
    public Result investReturnArriveAccount(List<ClaimSubAccFlow> claimFlowList, List<CurrentSubAccFlow> currentFlowList) {
        return accountServer.investReturnArriveAccount(claimFlowList, currentFlowList);
    }

    //投资增值记账：债权户加
    public Result investAddAccount(List<ClaimSubAccFlow> claimFlowList){
        return accountServer.investAddAccount(claimFlowList);
    }

}
