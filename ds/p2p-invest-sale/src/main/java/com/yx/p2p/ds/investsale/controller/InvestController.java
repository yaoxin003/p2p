package com.yx.p2p.ds.investsale.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.model.invest.InvestProduct;
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
        logger.debug("调用【投资充值】功能begin。【investVo=】"+investVo);
        //投资充值
        result = investServer.rechargeInvest(investVo);
        logger.debug("调用【投资充值】功能end。【result=】" + result);
        return result;
    }

    @RequestMapping("getInvestVoListByCustomerId")
    @ResponseBody
    public List<InvestVo> getInvestVoListByCustomerId(Integer customerId){
        logger.debug("【customerId=】" + customerId);
        InvestVo investVo = new InvestVo();
        investVo.setCustomerId(customerId);
        List<InvestVo> investVoList = investServer.getInvestVoList(investVo);
        logger.debug("【getInvestVoListByCustomerId】investVoList=" + investVoList);
        return investVoList;
    }

    //补偿网关支付
    @RequestMapping(value="compensateGateway")
    @ResponseBody
    public Result compensateGateway(InvestVo investVo){
        logger.debug("【investVo=】" + investVo);
        Result result = investServer.compensateGateway(investVo);
        logger.debug("【补偿网关支付】result=" + result);
        return result;
    }
}
