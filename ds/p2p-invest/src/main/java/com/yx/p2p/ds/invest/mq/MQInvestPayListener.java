package com.yx.p2p.ds.invest.mq;

import com.alibaba.fastjson.JSON;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.mq.InvestMQVo;
import com.yx.p2p.ds.service.InvestService;
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
 * @description:接收支付成功通知
 * @author: yx
 * @date: 2020/04/19/17:50
 */

@RocketMQMessageListener( topic = "${mq.payment.topic}",
        consumerGroup = "${rocketmq.pay.consumer.group.name}",
        selectorExpression ="payTagInvestSuc || payTagInvestFail",
        selectorType = SelectorType.TAG,
        messageModel = MessageModel.BROADCASTING
)
@Component
public class MQInvestPayListener implements RocketMQListener<MessageExt>{

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private InvestService investService;

     @Override
    public void onMessage(MessageExt messageExt) {
         logger.debug("投资支付结果【MQListener】messageExt=" + messageExt);
        try {
            String body = new String(messageExt.getBody(),"UTF-8");
            InvestMQVo investMQVo = JSON.parseObject(body, InvestMQVo.class);
            Result result = investService.receivePayResult(investMQVo);
        } catch (UnsupportedEncodingException e) {
            logger.debug(e.toString(),e);
        }
    }
}
