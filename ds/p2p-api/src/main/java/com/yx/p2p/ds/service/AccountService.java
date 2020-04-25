package com.yx.p2p.ds.service;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.mq.InvestMQVo;
import com.yx.p2p.ds.mq.MasterAccMQVo;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/22/17:07
 */
public interface AccountService {

    //开户
    public Result openAccount(MasterAccMQVo masterAccMQVo);

    //事务操作：投资充值
    public Result rechargeInvest(InvestMQVo investMQVo);
}
