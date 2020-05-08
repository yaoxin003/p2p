package com.yx.p2p.ds.invest.mq;

import com.alibaba.fastjson.JSON;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.service.AccountService;
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
import java.util.HashMap;

/**
 * @description:接收放款成功通知
 * 借款人债务户增加
 * 投资人活期户减，债权户加
 * @author: yxpayTagBorrowSuc
 * @date: 2020/05/05/18:46
 */
@RocketMQMessageListener(
        consumerGroup = "${rocketmq.pay.consumer.group.name}",
        topic = "${mq.payment.topic}",
        selectorType = SelectorType.TAG,
        selectorExpression ="payTagBorrowSuc",
        messageModel = MessageModel.BROADCASTING
)
@Component
public class MQLoanPayListener implements RocketMQListener<MessageExt> {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private InvestService investService;

    @Override
    public void onMessage(MessageExt messageExt) {
        logger.debug("【放款支付通知MQListener】入参：messageExt=" + messageExt);
        String body = null;
        try {
            body = new String(messageExt.getBody(),"UTF-8");
            HashMap<String,String> loanMap = JSON.parseObject(body, HashMap.class);
            //放款
           Result result = investService.loanNotice(loanMap);
            logger.debug("【放款支付通知】result=" + result);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
