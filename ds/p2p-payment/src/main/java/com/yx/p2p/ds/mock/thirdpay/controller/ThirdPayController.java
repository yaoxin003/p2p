package com.yx.p2p.ds.mock.thirdpay.controller;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.mock.thirdpay.service.ThirdPayServiceImpl;
import com.yx.p2p.ds.model.Payment;
import com.yx.p2p.ds.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @description:模拟第三方支付
 * @author: yx
 * @date: 2020/04/12/17:04
 */
@Controller
@RequestMapping("mock/thirdpay")
public class ThirdPayController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ThirdPayServiceImpl thirdPayService;

    //跳转到网关支付页面，输入支付密码
    @RequestMapping("toGateway")
    public String toGateway(Integer paymentId,ModelMap map){
        logger.debug("toGateway.【paymentId=】" + paymentId);
        Payment payment = paymentService.getPaymentById(paymentId);
        map.put("payment",payment);
        logger.debug("【payment=】" + payment);
        return "mock/thirdpay/gateway";
    }

    //1.调用银行扣款解密验签，2.对银行结果加签加密，3.调用p2p.payment系统通知支付结果
    @RequestMapping("gateway")
    @ResponseBody
    public Result gateway(Payment payment){
        logger.debug("gateway.【payment=】" + payment);
        Result result = thirdPayService.dealGateway(payment);
        return result;
    }


}
