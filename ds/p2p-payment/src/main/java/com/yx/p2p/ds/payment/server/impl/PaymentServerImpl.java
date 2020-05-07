package com.yx.p2p.ds.payment.server.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.model.payment.BaseBank;
import com.yx.p2p.ds.model.payment.CustomerBank;
import com.yx.p2p.ds.model.payment.Payment;
import com.yx.p2p.ds.server.PaymentServer;
import com.yx.p2p.ds.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/08/17:52
 */
@Service
@Component//dubbo需要@Component注解，否则无法识别该服务
public class PaymentServerImpl implements PaymentServer {

    @Autowired
    private PaymentService paymentService;

    public List<CustomerBank> getCustomerBankListByCustomerId(Integer customerId){
        return paymentService.getCustomerBankListByCustomerId(customerId);
    }

    @Override
    public List<BaseBank> getAllBaseBankList() {
        return paymentService.getAllBaseBankList();
    }

    public Result checkAndAddCustomerBank(CustomerBank customerBank){
        return paymentService.checkAndAddCustomerBank(customerBank);
    }

    //网关支付
    public Result gateway(Payment payment){
        return paymentService.gateway(payment);
    }

    public CustomerBank getCustomerBankById(Integer customerBankId){
        return paymentService.getCustomerBankById(customerBankId);
    }

    //放款
    public Result loan(Payment payment){
        return paymentService.loan(payment);
    }

}
