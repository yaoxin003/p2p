package com.yx.p2p.ds.service;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.model.account.MasterAcc;
import com.yx.p2p.ds.mq.InvestMQVo;
import com.yx.p2p.ds.mq.MasterAccMQVo;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
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
}
