package com.yx.p2p.ds.investsale.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.model.Invest;
import com.yx.p2p.ds.model.InvestProduct;
import com.yx.p2p.ds.model.Payment;
import com.yx.p2p.ds.server.InvestServer;
import com.yx.p2p.ds.server.PaymentServer;
import com.yx.p2p.ds.service.InvestSaleService;
import com.yx.p2p.ds.vo.InvestVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
    private InvestServer investServer;

    @Reference
    private PaymentServer paymentServer;

    @Autowired
    private InvestSaleService investSaleService;

    //投资产品列表
    @RequestMapping("getAllInvestProductJSON")
    @ResponseBody
    public String getAllInvestProductJSON(){
        String allInvestProductJSON = investServer.getAllInvestProductJSON();
        logger.debug("【allInvestProductJSON=】" + allInvestProductJSON);
        return allInvestProductJSON;
    }

    @RequestMapping("getInvestProductById")
    @ResponseBody
    public InvestProduct getInvestProductById(Integer investProductId){
        InvestProduct investProduct = investServer.getInvestProductById(investProductId);
        return investProduct;
    }

    //出借
    @RequestMapping("lend")
    @ResponseBody
    public Result lend(InvestVo investVo){
        Result result = null;
        logger.debug("调用【投资出借】功能begin。【investVo=】"+investVo);

        logger.debug("【投资出借-1.添加新投资】begin");
        //1.添加新投资
        result = investServer.addNewInvest(investVo);
        logger.debug("【投资出借-1.添加新投资】end。【result=】" + result);

        //2.添加投资人支付信息
        logger.debug("【投资出借-2.添加投资人支付信息】begin");
        result = investSaleService.addPayment((InvestVo)result.getTarget());
        logger.debug("【投资出借-2.添加投资人支付信息】。【result=】" + result);

        logger.debug("调用【投资出借】功能end");
        return result;
    }

}
