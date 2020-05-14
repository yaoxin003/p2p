package com.yx.p2p.ds.service;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.model.invest.InvestClaim;
import com.yx.p2p.ds.mq.InvestMQVo;

import java.util.List;

/**
 * @description:出借单Service
 * @author: yx
 * @date: 2020/04/21/13:12
 */
public interface LendingService {

    //添加新出借单
    public Result addNewLending(InvestMQVo investMQVo);

    public Result checkNoLendingByOrderSn(String orderSn);

    //检查并处理出借单满额
    public Result checkAndDealFullAmt(List<InvestClaim> matchInvestClaimList);

}
