package com.yx.p2p.ds.service.invest;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.model.invest.InvestClaim;
import com.yx.p2p.ds.model.invest.Lending;
import com.yx.p2p.ds.mq.InvestMQVo;

import java.util.Date;
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

    public List<Lending> addLendingList(List<Lending> lendingList);

    //获得回款出借单集合
    public List<Lending> getReinvestLendingList(List<Integer> investIdList,Date arriveDate);
}
