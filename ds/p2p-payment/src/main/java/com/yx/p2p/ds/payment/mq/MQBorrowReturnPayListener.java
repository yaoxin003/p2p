package com.yx.p2p.ds.payment.mq;

import com.alibaba.fastjson.JSON;
import com.yx.p2p.ds.model.borrow.Cashflow;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/06/28/9:47
 */
@RocketMQMessageListener(
        consumerGroup = "${mq.borrow.return.pay.consumer.group}",
        topic = "${mq.borrow.return.pay.topic}",
        selectorType = SelectorType.TAG,
        //selectorExpression ="borrowReturnPayTag",
        messageModel = MessageModel.BROADCASTING
)
@Component
public class MQBorrowReturnPayListener implements RocketMQListener<MessageExt> {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void onMessage(MessageExt messageExt) {
        logger.debug("【借款人还款支付】");
        String body = null;
        List<Cashflow> cashflowList = null;
        try {
            body = new String(messageExt.getBody(),"UTF-8");
             cashflowList = JSON.parseArray(body, Cashflow.class);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        logger.debug("【借款人还款支付】cashflowList{}",cashflowList);
    }

}
