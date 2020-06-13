package com.yx.p2p.ds.service.payment;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.model.payment.BaseBank;
import com.yx.p2p.ds.model.payment.CustomerBank;
import com.yx.p2p.ds.model.payment.Payment;

import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/08/17:21
 */
public interface PaymentService {

    public List<BaseBank> getAllBaseBankList();

    public List<CustomerBank> getCustomerBankListByCustomerId(Integer customerId);

    public Result checkAndAddCustomerBank(CustomerBank customerBank);

    //网关支付
    public Result gateway(Payment payment);

    public Payment getPaymentById(Integer paymentId);

    //处理公司company1的网关支付结果
    public Result dealCompany1Gateway(String payResult);

    public CustomerBank getCustomerBankById(Integer customerBankId);

    public Result loan(Payment payment);
}
