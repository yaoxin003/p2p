package com.yx.p2p.ds.investsale.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yx.p2p.ds.model.InvestProduct;
import com.yx.p2p.ds.server.InvestProductServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/06/18:15
 */
@Controller
@RequestMapping("invest")
public class InvestController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Reference
    private InvestProductServer investProductServer;

    @RequestMapping("getAllInvestProductJSON")
    @ResponseBody
    public String getAllInvestProductJSON(){
        String allInvestProductJSON = investProductServer.getAllInvestProductJSON();
        logger.debug("【allInvestProductJSON=】" + allInvestProductJSON);
        return allInvestProductJSON;
    }

    @RequestMapping("getInvestProductById")
    @ResponseBody
    public InvestProduct getInvestProductById(Integer investProductId){
        InvestProduct investProduct = investProductServer.getInvestProductById(investProductId);
        return investProduct;
    }
}
