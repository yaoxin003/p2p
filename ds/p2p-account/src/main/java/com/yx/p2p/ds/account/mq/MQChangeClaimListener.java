package com.yx.p2p.ds.account.mq;

import com.alibaba.fastjson.JSON;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.service.AccountService;
import com.yx.p2p.ds.service.TransferService;
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
import java.util.Map;

/**
 * @description:投资债权交割
 * @author: yx
 * @date: 2020/05/14/9:36
 */
@RocketMQMessageListener(
        consumerGroup = "${mq.invest.claim.change.consumer.group}",
        topic = "${mq.invest.claim.change.topic}",
        selectorType = SelectorType.TAG,
        selectorExpression ="investClaimChangeTag",
        messageModel = MessageModel.BROADCASTING
)
@Component
public class MQChangeClaimListener implements RocketMQListener<MessageExt> {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AccountService accountService;

    @Override
    public void onMessage(MessageExt messageExt) {
        logger.debug("投资债权交割【MQListener】messageExt=" + messageExt);
        try {
            String body = new String(messageExt.getBody(),"UTF-8");
            Map claimMap = JSON.parseObject(body,Map.class);
            Result result = accountService.changeInvestclaim(claimMap);
        } catch (UnsupportedEncodingException e) {
            logger.debug("投资债权交割【MQListener】异常:" + e.toString(),e);
        }
    }
}
