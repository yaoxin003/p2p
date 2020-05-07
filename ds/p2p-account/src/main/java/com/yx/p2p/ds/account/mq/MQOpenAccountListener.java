package com.yx.p2p.ds.account.mq;

import com.alibaba.fastjson.JSON;
import com.yx.p2p.ds.mq.MasterAccMQVo;
import com.yx.p2p.ds.service.AccountService;
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
 * @description:接收开户通知
 * @author: yx
 * @date: 2020/04/22/10:36
 */
@RocketMQMessageListener(
        consumerGroup = "${rocketmq.account.consumer.group.name}",
        topic = "${mq.account.topic}",
        selectorType = SelectorType.TAG,
        selectorExpression = "accTagAccOpen",
        messageModel = MessageModel.BROADCASTING)
@Component
public class MQOpenAccountListener implements RocketMQListener<MessageExt>{

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AccountService accountService;
    @Override
    public void onMessage(MessageExt messageExt) {
        logger.debug("【接收开户通知MQListener】入参：messageExt=" + messageExt);
        try {
            String body = new String(messageExt.getBody(),"UTF-8");
            MasterAccMQVo masterAccMQVo = JSON.parseObject(body, MasterAccMQVo.class);
            accountService.openAccount(masterAccMQVo);
        } catch (UnsupportedEncodingException e) {
           logger.debug("【接收开户通知】",e);
        }
    }
}
