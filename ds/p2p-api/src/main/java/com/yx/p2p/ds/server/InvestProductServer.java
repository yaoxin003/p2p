package com.yx.p2p.ds.server;

import com.yx.p2p.ds.model.InvestProduct;

import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/06/18:11
 */
public interface InvestProductServer {

    public List<InvestProduct> getAllInvestProductList();

    public String getAllInvestProductJSON();

    public InvestProduct getInvestProductById(Integer investProductId);
}
