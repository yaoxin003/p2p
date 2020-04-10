package com.yx.p2p.ds.service;

import com.yx.p2p.ds.model.BaseBank;
import com.yx.p2p.ds.model.CustomerBank;

import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/08/17:21
 */
public interface PaymentService {
    public List<BaseBank> getAllBaseBankList();

    public List<CustomerBank> getCustomerBankListByCustomerId(Integer customerId);

    public Integer addCustomerBank(CustomerBank customerBank);
}
