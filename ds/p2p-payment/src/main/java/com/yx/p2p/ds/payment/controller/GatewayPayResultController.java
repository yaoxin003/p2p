package com.yx.p2p.ds.payment.controller;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @description:接收各第三方网关支付的支付结果Controller
 * @author: yx
 * @date: 2020/04/15/6:52
 */
@Controller
@RequestMapping("payresult/gateway")
public class GatewayPayResultController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PaymentService paymentService;

    //  接收并解析网关支付（Company1公司）的支付结果
    //1.接收支付结果，解密验签
    //2.修改payment表支付状态
    //3.使用RocketMQ加入消息队列中通知其他系统
    @RequestMapping("company1")
    @ResponseBody
    public Result company1(HttpServletRequest request){
        String payResult = request.getParameter("payResult");
        logger.debug("company1【request.payResult=】" + payResult);
        Result result = paymentService.dealCompany1Gateway(payResult);
        return result;
    }

}
