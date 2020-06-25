package com.yx.p2p.ds.server;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.model.account.ClaimSubAccFlow;
import com.yx.p2p.ds.model.account.CurrentSubAccFlow;
import com.yx.p2p.ds.model.account.DebtSubAccFlow;
import com.yx.p2p.ds.model.account.MasterAcc;
import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/05/22/15:58
 */
public interface AccountServer {

    public MasterAcc getMasterAccByCustomerId(Integer customerId);

    //债务增值
    public Result debtAddAccount(List<DebtSubAccFlow> debtFlowList);

    //债务还款到账
    public Result debtReturnArriveAccount(List<DebtSubAccFlow> debtFlowList);

    //投资回款记账：债权户减，活期户加
    public Result investReturnArriveAccount(List<ClaimSubAccFlow> claimFlowList, List<CurrentSubAccFlow> currentFlowList);

    //投资增值记账：债权户加
    public Result investAddAccount(List<ClaimSubAccFlow> claimFlowList);

}
