package com.yx.p2p.ds.service;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.enums.invest.InvestBizStateEnum;
import com.yx.p2p.ds.model.invest.Invest;
import com.yx.p2p.ds.model.invest.InvestClaim;
import com.yx.p2p.ds.model.invest.InvestReturnDtl;
import com.yx.p2p.ds.model.invest.Lending;
import com.yx.p2p.ds.mq.InvestMQVo;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public List<Invest> getInvestListByInvestIdList(List<Integer> investIdList);

    public Result compensateGateway(Invest invest);

    //接收支付结果
    public Result receivePayResult(InvestMQVo investMQVo);

    public Invest getInvestByInvestId(Integer investId);

    public List<InvestClaim> getInvestClaimList(InvestClaim investClaim);

    //放款通知
    public Result loanNotice(HashMap<String, String> loanMap);

    public List<InvestClaim> getInvestClaimList(List<Integer> borrowIdList);

}
