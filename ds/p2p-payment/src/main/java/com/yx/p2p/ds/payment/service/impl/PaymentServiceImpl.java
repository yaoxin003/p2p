package com.yx.p2p.ds.payment.service.impl;

import com.yx.p2p.ds.constant.SysConstant;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.helper.BeanHelper;
import com.yx.p2p.ds.model.BaseBank;
import com.yx.p2p.ds.model.CustomerBank;
import com.yx.p2p.ds.model.Payment;
import com.yx.p2p.ds.payment.mapper.BaseBankMapper;
import com.yx.p2p.ds.payment.mapper.CustomerBankMapper;
import com.yx.p2p.ds.payment.mapper.PaymentMapper;
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
    @Autowired
    private PaymentMapper paymentMapper;

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

    /**
     * 检查银行账户是否存在
     * Result.Status=error 银行账户已存在
     * Result.Status=ok 银行账户不存在
     * @param customerBank
     * @return
     */
    private Result checkBankAccount(CustomerBank customerBank){
        Result result = Result.success();
        CustomerBank param = new CustomerBank();
        param.setBankAccount(customerBank.getBankAccount());
        List<CustomerBank> customerBanks = customerBankMapper.select(param);
        if(!customerBanks.isEmpty()){
            result = Result.error(customerBanks.size(),"银行账户已存在");
        }
        logger.debug("checkBankAccount.【result】=" + result);
        return result;
    }

    public Result checkAndAddCustomerBank(CustomerBank customerBank){
        Result result = this.checkBankAccount(customerBank);
        if(Result.STATUS_OK.equals(result.getStatus())){//银行账户不存在
            result = this.addCustomerBank(customerBank);
        }
        return result;
    }

    private Result addCustomerBank(CustomerBank customerBank){
        Result result = Result.success();
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
            result = Result.error(count,e.toString());
            logger.error(e.toString(),e);
        }
        logger.debug("insertBankAccount.【result】=" + result);
        return result;
    }

    public Result checkAndAddPayment(Payment payment){
        Result result = this.checkPayment(payment);
        if(Result.STATUS_OK.equals(result.getStatus())){//业务单不存在
            result = this.addPayment(payment);
        }
        return result;
    }

    /**
     * 检查支付单是否存在
     * Result.Status=error 支付单已存在
     * Result.Status=ok 支付单不存在
     * @param payment
     */
    private Result checkPayment(Payment payment){
        Result result = Result.success();
        Payment param = new Payment();
        param.setBizId(payment.getBizId());
        param.setSystemSource(payment.getSystemSource());
        List<Payment> payments = paymentMapper.select(payment);
        if(!payments.isEmpty()){
            result = Result.error(payments.size(),"该笔数据已生成支付单");
        }
        return result;
    }

    private Result addPayment(Payment payment){
        logger.debug("【payment=】" + payment);
        Result result = Result.success();
        int count = 0;
        try{
            //1.设置时间和操作人
            BeanHelper.setDefaultTimeField(payment,"createTime","updateTime");
            Map<String,Integer> operatorMap = new HashMap<>();
            operatorMap.put("creator", SysConstant.operator);
            operatorMap.put("reviser",SysConstant.operator);
            BeanHelper.setDefaultOperatorField(payment,operatorMap);
            logger.debug("【payment=】" + payment);
            //2.插入数据库
            count = paymentMapper.insert(payment);
            logger.debug("【payment.insert.db.count=】" + count);
        }catch(Exception e){
            result = Result.error(count,e.toString());
            logger.error(e.toString(),e);
        }
        logger.debug("insertPayment.【result】=" + result);
        return result;
    }

}
