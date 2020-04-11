package com.yx.p2p.ds.server;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.model.Invest;
import com.yx.p2p.ds.model.InvestProduct;

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

   //添加新投资
    public Result addNewInvest(Invest invest);
}
