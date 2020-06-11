package com.yx.p2p.ds.server;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.model.invest.*;
import com.yx.p2p.ds.model.match.FinanceMatchRes;

import java.util.Date;
import java.util.List;
import java.util.Map;

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

    public List<Invest> getInvestListByInvestIdList(List<Integer> investIdList);

    //补充网关支付
    public Result compensateGateway(Invest invest);

    public Invest getInvestByInvestId(Integer investId);

    //转让赎回申请
    public Result transferApply(Integer investId);

    //投资提现申请
    public Result withdrawApply(Integer investId);

    //获得转让协议文本
    public Map<String,Object> getTransferContractText(Integer investId);

    public List<InvestClaim> getInvestClaimList(List<Integer> borrowIdList);

    public List<Lending> addLendingList(List<Lending> lendingList);

}
