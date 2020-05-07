package com.yx.p2p.ds.borrowsale.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.model.payment.BaseBank;
import com.yx.p2p.ds.model.payment.CustomerBank;
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
        List<BaseBank> allBaseBankList = paymentServer.getAllBaseBankList();
        logger.debug("【查询数据库所有银行总行】allBaseBankList=" + allBaseBankList);
        return allBaseBankList;
    }

    @RequestMapping("getCustomerBankListByCustomerId")
    @ResponseBody
    public List<CustomerBank> getCustomerBankListByCustomerId(Integer customerId){
        logger.debug("【customerId=】" + customerId);
        return paymentServer.getCustomerBankListByCustomerId(customerId);
    }

    @RequestMapping(value="addCustomerBank")
    @ResponseBody
    public Result addCustomerBank(CustomerBank customerBank){
        logger.debug("准备调用绑卡接口【customerBank=】" + customerBank);
        Result result = paymentServer.checkAndAddCustomerBank(customerBank);
        return result;
    }

}
