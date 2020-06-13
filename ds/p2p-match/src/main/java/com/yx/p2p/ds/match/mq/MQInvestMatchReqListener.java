package com.yx.p2p.ds.match.mq;

import com.alibaba.fastjson.JSONArray;
import com.yx.p2p.ds.model.match.InvestMatchReq;
import com.yx.p2p.ds.service.match.InvestMatchReqService;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * @description:投资批量撮合请求
 * @author: yx
 * @date: 2020/04/24/17:13
 */

@RocketMQMessageListener(
        consumerGroup = "${mq.match.invest.consumer.group}",
        topic = "${mq.match.invest.topic}",
        selectorExpression ="matchInvestTag",
        selectorType = SelectorType.TAG,
        messageModel = MessageModel.BROADCASTING)
@Component
public class MQInvestMatchReqListener implements RocketMQListener<MessageExt> {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private InvestMatchReqService investMatchReqService;

    @Override
    public void onMessage(MessageExt messageExt) {
      logger.debug("接收投资撮合请求【MQListener】messageExt=" + messageExt);
        try {
            String body = new String(messageExt.getBody(),"UTF-8");
            List<InvestMatchReq> investMatchReqList = JSONArray.parseArray(body, InvestMatchReq.class);
            investMatchReqService.addInvestMatchReq(investMatchReqList);
        } catch (Exception e) {
            logger.debug("接收投资撮合请求【MQListener】",e);
        }
    }
}