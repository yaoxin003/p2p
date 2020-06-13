package com.yx.p2p.ds.match.mq;

import com.alibaba.fastjson.JSONArray;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.service.match.TransferMatchReqService;
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
import java.util.Map;

/**
 * @description:投资撮合请求
 * @author: yx
 * @date: 2020/04/24/17:13
 */

@RocketMQMessageListener(
        consumerGroup = "${mq.match.transfer.consumer.group}",
        topic = "${mq.match.transfer.topic}",
        selectorExpression ="matchTransferTag",
        selectorType = SelectorType.TAG,
        messageModel = MessageModel.BROADCASTING)
@Component
public class MQTransferMatchReqListener implements RocketMQListener<MessageExt> {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TransferMatchReqService transferMatchReqService;

    @Override
    public void onMessage(MessageExt messageExt) {
      logger.debug("接收转让撮合请求【MQListener】messageExt=" + messageExt);
        try {
            String body = new String(messageExt.getBody(),"UTF-8");
            List<Map<String,Object>> financeMatchReqList = (List<Map<String,Object>> )JSONArray.parse(body);
            //转让撮合请求
            Result result = transferMatchReqService.transferMatchReq(financeMatchReqList);
        } catch (Exception e) {
            logger.debug("接收转让撮合请求【MQListener】出现异常",e);
        }
    }
}