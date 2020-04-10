package com.yx.p2p.ds.investsale.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yx.p2p.ds.model.BaseBank;
import com.yx.p2p.ds.model.CustomerBank;
import com.yx.p2p.ds.server.PaymentServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/09/8:39
 */
@Controller
@RequestMapping("payment")
public class PaymentController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Reference
    private PaymentServer paymentServer;

    @RequestMapping("getAllBaseBankList")
    @ResponseBody
    public List<BaseBank> getAllBaseBankList(){
        return paymentServer.getAllBaseBankList();
    }

    @RequestMapping("getCustomerBankListByCustomerId")
    @ResponseBody
    public List<CustomerBank> getCustomerBankListByCustomerId(Integer customerId){
        logger.debug("【customerId=】" + customerId);
        return paymentServer.getCustomerBankListByCustomerId(customerId);
    }

    @RequestMapping("addCustomerBank")
    @ResponseBody
    public Integer addCustomerBank(CustomerBank customerBank){
        return paymentServer.addCustomerBank(customerBank);
    }

}
