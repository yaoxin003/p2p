package com.yx.p2p.ds.service;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.enums.invest.InvestBizStateEnum;
import com.yx.p2p.ds.model.invest.Invest;
import com.yx.p2p.ds.mq.InvestMQVo;

import java.util.HashMap;
import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/06/18:04
 */
public interface InvestService {

    //充值投资
    public Result rechargeInvest(Invest invest);

    //更新投资业务状态
    public Result updateInvestBizState(Integer investId, InvestBizStateEnum investBizStateEnum);

    public List<Invest> getInvestVoList(Invest invest);

    public Result compensateGateway(Invest invest);

    //接收支付结果
    public Result receivePayResult(InvestMQVo investMQVo);

    public Invest getInvestByInvestId(Integer investId);

    //放款通知
    public Result loanNotice(HashMap<String, String> loanMap);
}
