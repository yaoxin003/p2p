package com.yx.p2p.ds.match.mq;

import com.alibaba.fastjson.JSON;
import com.yx.p2p.ds.mq.InvestMatchReqMQVo;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @description:投资撮合请求
 * @author: yx
 * @date: 2020/04/24/17:13
 */

@RocketMQMessageListener( consumerGroup = "${rocketmq.match.consumer.group.name}",
        topic = "${mq.invest.match.topic}",
        selectorExpression ="matchTagNewInvest",
        selectorType = SelectorType.TAG,
        messageModel = MessageModel.BROADCASTING)
@Component
public class MQInvestMatchReqListener implements RocketMQListener<MessageExt> {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void onMessage(MessageExt messageExt) {
      logger.debug("接收投资撮合请求【MQListener】messageExt=" + messageExt);
        try {
            String body = new String(messageExt.getBody(),"UTF-8");
            InvestMatchReqMQVo mqVo = JSON.parseObject(body, InvestMatchReqMQVo.class);

        } catch (Exception e) {
            logger.debug("接收投资撮合请求【MQListener】",e);
        }
    }
}