package com.yx.p2p.ds.service.account;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.model.account.CashSubAccFlow;
import com.yx.p2p.ds.model.account.ClaimSubAccFlow;
import com.yx.p2p.ds.model.account.CurrentSubAccFlow;
import com.yx.p2p.ds.model.account.DebtSubAccFlow;
import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/06/14/11:04
 */
public interface AccountCoreService {

    //债务户处理：债务子账户，债务子账户流水
    public Result dealDebtAccount(List<DebtSubAccFlow> debtSubAccFlowList);

    //活期户处理：活期子账户，活期子账户流水
    public Result dealCurrentAccount(List<CurrentSubAccFlow> currentSubAccFlowList);

    //债权户处理：债权子账户，债权子账户流水
    public Result dealClaimAccount(List<ClaimSubAccFlow> claimSubAccFlowList);

    //现金户处理：现金子账户，现金子账户流水
    public Result dealCashAccount(List<CashSubAccFlow> cashSubAccFlowList);
}
