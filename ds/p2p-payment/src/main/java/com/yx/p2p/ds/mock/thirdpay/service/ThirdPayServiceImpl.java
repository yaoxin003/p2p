package com.yx.p2p.ds.mock.thirdpay.service;

import com.alibaba.fastjson.JSON;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.mock.thirdpay.enums.RetCodeEnum;
import com.yx.p2p.ds.model.Payment;
import com.yx.p2p.ds.util.HttpClientUtil;
import com.yx.p2p.ds.util.RASUtil;
import com.yx.p2p.ds.util.SHAUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/16/17:34
 */
@Service
public class ThirdPayServiceImpl {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    //RSA加密解密公钥
    @Value("${ras.public.key}")
    private String rasPublicKey;

    @Value("${payment.url}")
    private String paymentUrl;

    public Result dealGateway(Payment payment) {
        logger.debug("【接收网关支付页面参数payment=】" + payment);
        //1.调用银行接口开始划扣
        logger.debug("【1.1.调用银行划扣接口，解密验签】");
        logger.debug("【1.2.对银行结果加签加密】");
        //2.对银行的支付结果加密
        logger.debug("【2.对银行的支付结果加签加密……开始】");
        String bankPayResult = this.buildRASRet(payment);
        logger.debug("【3.对银行的支付结果加签加密……结束】");
        //3.调用p2p的payment系统（公司外网url地址）通知支付结果
        Result result = this.noticeGatewayResult(bankPayResult);
        return result;
    }

    private Result noticeGatewayResult(String bankPayResult) {
        String url = paymentUrl + "/payresult/gateway/company1";
        Map<String,String> map = new HashMap<>();
        map.put("payResult",bankPayResult);
        String httpResult = HttpClientUtil.doPost(url, map);
        logger.debug("【httpRes】=" + httpResult);
        Result result = JSON.parseObject(httpResult, Result.class);
        return result;
    }

    //加签加密
    private String buildRASRet(Payment payment) {
        Map<String,String> map = new HashMap<>();
        map.put("orderSn",payment.getOrderSn());
        map.put("retCode", RetCodeEnum.PAY_SUC.getCode());
        map.put("retInfo","支付成功");
        //加签
        String sign = SHAUtil.encryptSHA(map);
        map.put("sign",sign);
        //1.用公钥加密
        String rsaStr = RASUtil.publicEncrypt(map, rasPublicKey);
        logger.debug("加密结果：【rsa.string=】" + rsaStr);
        return rsaStr;
    }
}
