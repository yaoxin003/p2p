package com.yx.p2p.ds.service;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.enums.invest.InvestBizStateEnum;
import com.yx.p2p.ds.model.Invest;
import com.yx.p2p.ds.vo.InvestVo;

import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/06/18:04
 */
public interface InvestService {

    //充值投资
    public Result rechargeInvest(InvestVo investVo);

    //该方法暂未使用
    //更新投资业务状态
    public Result updateInvestBizState(Integer investId, InvestBizStateEnum investBizStateEnum);

    public List<InvestVo> getInvestVoList(InvestVo investVo);

    public Result compensateGateway(InvestVo investVo);
}
