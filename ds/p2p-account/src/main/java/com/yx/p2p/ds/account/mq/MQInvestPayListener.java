package com.yx.p2p.ds.account.mq;

import com.alibaba.fastjson.JSON;
import com.yx.p2p.ds.mq.InvestMQVo;
import com.yx.p2p.ds.service.account.AccountService;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

/**
 * @description:接收投资充值支付成功通知
 * @author: yx
 * @date: 2020/04/19/17:50
 */

@RocketMQMessageListener(
        consumerGroup = "${mq.pay.invest.consumer.group}",
        topic = "${mq.pay.invest.topic}",
        selectorExpression ="payInvestSucTag",
        selectorType = SelectorType.TAG,
        messageModel = MessageModel.BROADCASTING
)
@Component
public class MQInvestPayListener implements RocketMQListener<MessageExt>{

    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private AccountService accountService;

     @Override
    public void onMessage(MessageExt messageExt) {
         logger.debug("【接收投资支付成功MQListener】入参：messageExt=" + messageExt);
         try {
             String body = new String(messageExt.getBody(),"UTF-8");
             InvestMQVo investMQVo = JSON.parseObject(body, InvestMQVo.class);
             accountService.rechargeInvest(investMQVo);
         } catch (UnsupportedEncodingException e) {
             logger.debug("接收投资支付成功【MQListener】",e);
         }
    }
}
