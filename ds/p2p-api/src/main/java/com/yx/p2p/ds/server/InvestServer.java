package com.yx.p2p.ds.server;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.model.invest.Invest;
import com.yx.p2p.ds.model.invest.InvestProduct;
import com.yx.p2p.ds.model.match.FinanceMatchRes;

import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/06/18:11
 */
public interface InvestServer {

    public List<InvestProduct> getAllInvestProductList();

    public String getAllInvestProductJSON();

    public InvestProduct getInvestProductById(Integer investProductId);

    //充值投资
    public Result rechargeInvest(Invest invest);

    public List<Invest> getInvestVoList(Invest invest);

    //补充网关支付
    public Result compensateGateway(Invest invest);

    public Invest getInvestByInvestId(Integer investId);

    //转让赎回申请
    public Result transferApply(Integer investId);


}
