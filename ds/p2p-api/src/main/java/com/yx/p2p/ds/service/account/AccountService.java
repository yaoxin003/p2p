package com.yx.p2p.ds.service.account;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.model.account.ClaimSubAccFlow;
import com.yx.p2p.ds.model.account.CurrentSubAccFlow;
import com.yx.p2p.ds.model.account.DebtSubAccFlow;
import com.yx.p2p.ds.model.account.MasterAcc;
import com.yx.p2p.ds.mq.InvestMQVo;
import com.yx.p2p.ds.mq.MasterAccMQVo;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:和业务相关的AccountService
 * @author: yx
 * @date: 2020/04/22/17:07
 */
public interface AccountService {

    //开户
    public Result openAccount(MasterAccMQVo masterAccMQVo);


    public MasterAcc getMasterAccByCustomerId(Integer customerId);

    //事务操作：投资充值
    public Result rechargeInvest(InvestMQVo investMQVo);

    //事务操作：放款通知
    public Result loanNotice(HashMap<String, String> loanMap);

    //投资债权交割
    public Result changeInvestclaim(Map<String, Object> paramClaimMap);

    //债务增值
    public Result debtAddAccount(List<DebtSubAccFlow> debtFlowList);

    //债务还款到账
    public Result debtReturnArriveAccount(List<DebtSubAccFlow> debtFlowList);

    //投资回款记账：债权户减，活期户加
    public Result investReturnArriveAccount(List<ClaimSubAccFlow> claimFlowList, List<CurrentSubAccFlow> currentFlowList);

    //投资增值记账：债权户加
    public Result investAddAccount(List<ClaimSubAccFlow> claimFlowList);
}
