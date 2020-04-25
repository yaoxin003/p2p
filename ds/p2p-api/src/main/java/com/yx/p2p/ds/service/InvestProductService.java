package com.yx.p2p.ds.service;

import com.yx.p2p.ds.model.invest.InvestProduct;

import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/06/18:06
 */
public interface InvestProductService {

    public List<InvestProduct> getAllInvestProductList();

    public String getAllInvestProductJSON();

    public InvestProduct getInvestProductById(Integer investProductId);

    public InvestProduct getInvestProductByInvestId(Integer investId);
}
