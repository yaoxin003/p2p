package com.yx.p2p.ds.payment.server.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.model.BaseBank;
import com.yx.p2p.ds.model.CustomerBank;
import com.yx.p2p.ds.model.Payment;
import com.yx.p2p.ds.server.PaymentServer;
import com.yx.p2p.ds.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

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

    public Result checkAndAddPayment(Payment payment){
        return paymentService.checkAndAddPayment(payment);
    }



}
