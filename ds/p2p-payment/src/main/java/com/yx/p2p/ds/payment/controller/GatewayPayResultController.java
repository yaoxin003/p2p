package com.yx.p2p.ds.payment.controller;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.enums.payment.PaymentBizStateEnum;
import com.yx.p2p.ds.mock.thirdpay.enums.RetCodeEnum;
import com.yx.p2p.ds.model.Payment;
import com.yx.p2p.ds.service.PaymentService;
import com.yx.p2p.ds.util.RASUtil;
import com.yx.p2p.ds.util.SHAUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
