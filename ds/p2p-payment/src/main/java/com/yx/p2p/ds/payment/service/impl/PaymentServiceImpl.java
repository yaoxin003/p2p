package com.yx.p2p.ds.payment.service.impl;

import com.yx.p2p.ds.constant.SysConstant;
import com.yx.p2p.ds.helper.BeanHelper;
import com.yx.p2p.ds.model.BaseBank;
import com.yx.p2p.ds.model.CustomerBank;
import com.yx.p2p.ds.payment.mapper.BaseBankMapper;
import com.yx.p2p.ds.payment.mapper.CustomerBankMapper;
import com.yx.p2p.ds.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/08/17:19
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BaseBankMapper baseBankMapper;
    @Autowired
    private CustomerBankMapper customerBankMapper;

    public List<BaseBank> getAllBaseBankList(){
        List<BaseBank> baseBanks = baseBankMapper.selectAll();
        logger.debug("【db.allBasebank】=" + baseBanks);
        return baseBanks;
    }

    public List<CustomerBank> getCustomerBankListByCustomerId(Integer customerId){
        logger.debug("【customerId】=" + customerId);
        CustomerBank customerBank = new CustomerBank();
        customerBank.setCustomerId(customerId);
        List<CustomerBank> customerBanks = customerBankMapper.select(customerBank);
        logger.debug("【db.customerBanks】=" + customerBanks);
        return customerBanks;
    }

    public Integer addCustomerBank(CustomerBank customerBank){
        int count = 0;
        try{
            //1.设置时间和操作人
            BeanHelper.setDefaultTimeField(customerBank,"createTime","updateTime");
            Map<String,Integer> operatorMap = new HashMap<>();
            operatorMap.put("creator", SysConstant.operator);
            operatorMap.put("reviser",SysConstant.operator);
            BeanHelper.setDefaultOperatorField(customerBank,operatorMap);
            logger.debug("【customerBank=】" + customerBank);
            //2.插入数据库
            count = customerBankMapper.insert(customerBank);
            logger.debug("【customer.bank.insert.db.count=】" + count);
        }catch(Exception e){
            logger.error(e.toString(),e);
        }
        return count;
    }


}
