package com.yx.p2p.ds.server;

import com.yx.p2p.ds.model.BaseBank;
import com.yx.p2p.ds.model.CustomerBank;

import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/08/18:33
 */
public interface PaymentServer {

    public List<CustomerBank> getCustomerBankListByCustomerId(Integer customerId);

    public List<BaseBank> getAllBaseBankList();

    public Integer addCustomerBank(CustomerBank customerBank);
}