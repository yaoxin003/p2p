package com.yx.p2p.ds.invest.server.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.yx.p2p.ds.model.InvestProduct;
import com.yx.p2p.ds.server.InvestProductServer;
import com.yx.p2p.ds.service.InvestProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/06/18:10
 */
@Service
@Component
public class InvestProductServerImpl implements InvestProductServer {

    @Autowired
    private InvestProductService investProductService;

    public List<InvestProduct> getAllInvestProductList(){
        return investProductService.getAllInvestProductList();
    }

    public String getAllInvestProductJSON(){
        return investProductService.getAllInvestProductJSON();
    }

    public InvestProduct getInvestProductById(Integer investProductId){
        return investProductService.getInvestProductById(investProductId);
    }

}
