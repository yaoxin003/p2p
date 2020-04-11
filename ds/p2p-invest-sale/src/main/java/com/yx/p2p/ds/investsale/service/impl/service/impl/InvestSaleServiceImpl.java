package com.yx.p2p.ds.investsale.service.impl.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.enums.payment.SystemSourceEnum;
import com.yx.p2p.ds.model.Payment;
import com.yx.p2p.ds.server.PaymentServer;
import com.yx.p2p.ds.service.InvestSaleService;
import com.yx.p2p.ds.vo.InvestVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/11/15:27
 */
@Service
public class InvestSaleServiceImpl implements InvestSaleService{

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Reference
    private PaymentServer paymentServer;

    @Override
    public Result addPayment(InvestVo investVo) {
        logger.debug("【investVo=】" + investVo);
        Payment payment = this.buildAddPayment(investVo);
        Result result = paymentServer.checkAndAddPayment(payment);
        return result;
    }

    private Payment buildAddPayment(InvestVo investVo) {
        Payment payment = new Payment();
        payment.setSystemSource(SystemSourceEnum.INVEST.getName());
        payment.setBizId(investVo.getId());
        payment.setAmount(investVo.getInvestAmt());
        payment.setBankAccount(investVo.getBankAccount());
        payment.setBankCode(investVo.getBankCode());
        payment.setPhone(investVo.getPhone());
        logger.debug("【buildAddPayment from investVo to payment=】" + payment);
        return payment;
    }
}
