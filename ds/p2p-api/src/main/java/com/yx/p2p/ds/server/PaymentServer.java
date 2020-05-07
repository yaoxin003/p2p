package com.yx.p2p.ds.server;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.model.payment.BaseBank;
import com.yx.p2p.ds.model.payment.CustomerBank;
import com.yx.p2p.ds.model.payment.Payment;

import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/08/18:33
 */
public interface PaymentServer {

    public List<CustomerBank> getCustomerBankListByCustomerId(Integer customerId);

    public List<BaseBank> getAllBaseBankList();

    public Result checkAndAddCustomerBank(CustomerBank customerBank);

    //网关支付
    public Result gateway(Payment payment);

    public CustomerBank getCustomerBankById(Integer customerBankId);

    //放款
    public Result loan(Payment payment);
}