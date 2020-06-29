package com.yx.p2p.ds.borrow.mq;

import com.alibaba.fastjson.JSON;
import com.yx.p2p.ds.enums.borrow.CashflowBizStateEnum;
import com.yx.p2p.ds.model.borrow.Cashflow;
import com.yx.p2p.ds.model.payment.Payment;
import com.yx.p2p.ds.service.borrow.BorrowService;
import com.yx.p2p.ds.service.borrow.CashflowService;
import com.yx.p2p.ds.util.LoggerUtil;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:借款人还款支付事务MQ
 * @author: yx
 * @date: 2020/06/27/19:17
 */
@Component
@RocketMQTransactionListener(txProducerGroup = "borrowReturnPayProducerGroup")
public class MQTransBorrowReturnListener implements RocketMQLocalTransactionListener {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CashflowService cashflowService;

    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object o) {
        logger.debug("【调用本地业务执行】");
        try{
            String listStr = new String((byte[]) message.getPayload());
            List<Payment> paymentList = JSON.parseArray(listStr, Payment.class);
            if(!paymentList.isEmpty()){
                List<String> cashflowIdList = new ArrayList<>();
                for (Payment payment : paymentList) {
                    String cashflowId = payment.getBizId();
                    cashflowIdList.add(cashflowId);
                }
                if(!cashflowIdList.isEmpty()){
                    cashflowService.dealCashflowReturnPayment(cashflowIdList, CashflowBizStateEnum.RETURN_PAYING);
                }
            }
            logger.debug("【调用本地业务执行完毕】paymentList:{}",paymentList);
            return RocketMQLocalTransactionState.COMMIT;
        }catch(Exception e){
            LoggerUtil.addExceptionLog(e,logger);
            return  RocketMQLocalTransactionState.ROLLBACK;
        }
    }


    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        logger.info("【执行检查任务】");
        return RocketMQLocalTransactionState.UNKNOWN;
    }
}
