package com.yx.p2p.ds.investsale.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.model.invest.Invest;
import com.yx.p2p.ds.model.invest.InvestProduct;
import com.yx.p2p.ds.server.InvestServer;
import com.yx.p2p.ds.server.PaymentServer;
import com.yx.p2p.ds.service.investsale.InvestSaleService;
import com.yx.p2p.ds.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;
import java.util.Map;

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

    //全部投资产品列表
    @RequestMapping("getAllInvestProductList")
    @ResponseBody
    public List<InvestProduct> getAllInvestProductList(){
        List<InvestProduct> allInvestProductList = investServer.getAllInvestProductList();
        logger.debug("【allInvestProductList=】" + allInvestProductList);
        return allInvestProductList;
    }

    //投资产品列表JSON
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
    public Result lend(Invest invest){
        Result result = null;
        logger.debug("【调用投资充值功能】【投资系统】入参invest="+invest);
        //投资充值
        result = investServer.rechargeInvest(invest);
        logger.debug("【调用投资充值功能】【投资系统】结果result=" + result);
        return result;
    }

    @RequestMapping("getInvestVoListByCustomerId")
    @ResponseBody
    public List<Invest> getInvestVoListByCustomerId(Integer customerId){
        logger.debug("【customerId=】" + customerId);
        Invest invest = new Invest();
        invest.setCustomerId(customerId);
        List<Invest> investVoList = investServer.getInvestVoList(invest);
        logger.debug("【getInvestVoListByCustomerId】investVoList=" + investVoList);
        return investVoList;
    }

    //补偿网关支付
    @RequestMapping(value="compensateGateway")
    @ResponseBody
    public Result compensateGateway(Invest invest){
        logger.debug("【invest=】" + invest);
        Result result = investServer.compensateGateway(invest);
        logger.debug("【补偿网关支付】result=" + result);
        return result;
    }

    //转让赎回申请
    @RequestMapping(value="transferApply")
    @ResponseBody
    public Result transferApply(@RequestParam(value="investId") Integer investId,
                                @RequestParam(value="transferDate") String transferDate){
        logger.debug("【转让赎回申请】入参：investId={},transferDate={}" , investId,transferDate);
        Date arriveDate = new Date();
        Result result = investServer.transferApply(investId, DateUtil.str2Date(transferDate));
        logger.debug("【转让赎回申请】结果：result={}" , result);
        return result;
    }

    //投资提现
    @RequestMapping(value="withdrawApply")
    @ResponseBody
    public Result withdrawApply(Integer investId){
        logger.debug("【投资提现】入参：investId=" + investId);
        Result result = investServer.withdrawApply(investId);
        logger.debug("【投资提现】结果：result=" + result);
        return result;
    }

    //获得转让协议文本
    @RequestMapping(value="getTransferContractText")
    @ResponseBody
    public Map<String,Object> getTransferContractText(Integer investId) {
        logger.debug("【转让协议文本】入参：investId=" + investId);
        Map<String, Object> contractMap = investServer.getTransferContractText(investId);
        logger.debug("【转让协议文本】结果：contractMap=" + contractMap);
        return contractMap;
    }

}
