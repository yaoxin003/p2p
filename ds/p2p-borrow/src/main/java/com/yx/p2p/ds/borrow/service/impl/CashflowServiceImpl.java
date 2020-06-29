package com.yx.p2p.ds.borrow.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.yx.p2p.ds.borrow.mapper.CashflowMapper;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.enums.SystemSourceEnum;
import com.yx.p2p.ds.enums.borrow.CashflowBizStateEnum;
import com.yx.p2p.ds.enums.payment.PaymentBizStateEnum;
import com.yx.p2p.ds.enums.payment.PaymentTypeEnum;
import com.yx.p2p.ds.helper.BeanHelper;
import com.yx.p2p.ds.model.borrow.Borrow;
import com.yx.p2p.ds.model.borrow.Cashflow;
import com.yx.p2p.ds.model.payment.Payment;
import com.yx.p2p.ds.service.borrow.BorrowService;
import com.yx.p2p.ds.service.borrow.CashflowService;
import com.yx.p2p.ds.util.OrderUtil;
import com.yx.p2p.ds.util.PageUtil;
import org.apache.ibatis.session.RowBounds;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @description:现金流
 * @author: yx
 * @date: 2020/06/03/18:44
 */
@Service
@Component//dubbo需要@Component注解，否则无法识别该服务
public class CashflowServiceImpl implements CashflowService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CashflowMapper cashflowMapper;

    @Autowired
    private BorrowService borrowService;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Value("${mq.borrow.return.pay.producer.group}")
    private String borrowReturnPayProducerGroup;
    @Value("${mq.borrow.return.pay.topic}")
    private String borrowReturnPayTopic;
    @Value("${mq.borrow.return.pay.tag}")
    private String borrowReturnPayTag;

    static final Integer PAGE_SIZE = 500;

    //arriveDate 还款到账日期
    //currentPage 开始页编号
    //pageSize 每页条数
    public List<Cashflow> getCashflowListByPage(Date arriveDate, Integer currentPage, Integer pageSize){
        logger.debug("【分页查询现金流集合】入参：arriveDate=" + arriveDate
                + ",currentPage=" + currentPage + ",pageSize=" + pageSize);
        Integer offset = (currentPage-1) * pageSize;
        List<Cashflow> cashflowList = this.queryCashflowListByPage(arriveDate, offset, pageSize);
        logger.debug("【分页查询现金流集合】结果：cashflowList=" + cashflowList);
        return cashflowList;
    }

    //arriveDate 还款到账日期
    public Integer getCashflowListCount(Date arriveDate){
        logger.debug("【分页查询现金流总数量】入参：arriveDate=" + arriveDate);
        Cashflow param = new Cashflow();
        param.setArriveDate(arriveDate);
        int count = cashflowMapper.selectCount(param);
        logger.debug("【分页查询现金流总数量】结果：count=" + count);
        return count;
    }

    //arriveDate 还款到账日期
    //offset 开始记录编号，不是开始页编号
    //limit 每页条数
    private List<Cashflow> queryCashflowListByPage(Date arriveDate, Integer offset, Integer limit){
        logger.debug("【分页查询现金流集合】入参：arriveDate=" + arriveDate
                + ",offset=" + offset + ",limit=" + limit);
        Cashflow param = new Cashflow();
        param.setArriveDate(arriveDate);
        RowBounds rowBounds = new RowBounds(offset,limit);
        List<Cashflow> cashflowList = cashflowMapper.selectByRowBounds(param, rowBounds);
        logger.debug("【分页查询现金流集合】结果：cashflowList=" + cashflowList);
        return cashflowList;
    }

    //arriveDate 还款日期
    private Integer queryReturnCashflowListCount(Date returnDate){
        logger.debug("【分页查询现金流总数量】入参：returnDate=" + returnDate);
        Cashflow param = new Cashflow();
        param.setTradeDate(returnDate);
        int count = cashflowMapper.selectCount(param);
        logger.debug("【分页查询现金流总数量】结果：count=" + count);
        return count;
    }

    //returnDate 还款日期
    //offset 开始记录编号，不是开始页编号
    //limit 每页条数
    private List<Cashflow> queryReturnCashflowListByPage(Date returnDate, Integer offset, Integer limit){
        logger.debug("【分页查询现金流集合】入参：returnDate=" + returnDate
                + ",offset=" + offset + ",limit=" + limit);
        Cashflow param = new Cashflow();
        param.setTradeDate(returnDate);
        RowBounds rowBounds = new RowBounds(offset,limit);
        List<Cashflow> cashflowList = cashflowMapper.selectByRowBounds(param, rowBounds);
        logger.debug("【分页查询现金流集合】结果：cashflowList=" + cashflowList);
        return cashflowList;
    }

    //借款人还款支付
    public Result borrowReturnPayment(Date returnDate){
        Result result = Result.error();
        Integer totalCount = this.queryReturnCashflowListCount(returnDate);
        int pageCount = PageUtil.getPageCount(totalCount, PAGE_SIZE);
        for(int i=1; i<=pageCount; i++){
            Integer offset = PageUtil.getOffset(i, PAGE_SIZE);
            List<Cashflow> cashflows = this.queryReturnCashflowListByPage(returnDate, offset, PAGE_SIZE);
            List<Payment> payments =  this.buildBorrowReturnPayment(cashflows);
            this.sendMQBorrowReturnPayment(payments);
        }
        result = Result.success();
        return result;
    }

    private List<Payment> buildBorrowReturnPayment(List<Cashflow> cashflows) {
        List<Payment> payments = null;
        if(!cashflows.isEmpty()){
            payments = new ArrayList<>();
            Set<Integer> paramBorrowIdSet = new HashSet<>();
            for (Cashflow cashflow : cashflows) {
                paramBorrowIdSet.add(cashflow.getBorrowId());
            }
            List<Borrow> dbBorrowList = borrowService.getBorrowListByBorrowIdList(paramBorrowIdSet);
            if(!dbBorrowList.isEmpty()){
                Map<Integer,Borrow> borrowMap = new HashMap<>();
                for (Borrow borrow : dbBorrowList) {
                    borrowMap.put(borrow.getId(),borrow);
                }
                for (Cashflow cashflow : cashflows) {
                    Borrow borrow = borrowMap.get(cashflow.getBorrowId());
                    Payment payment = new Payment();
                    //银行信息
                    payment.setBaseBankName(borrow.getBaseBankName());
                    payment.setBankAccount(borrow.getBankAccount());
                    payment.setBankCode(borrow.getBankCode());
                    payment.setPhone(borrow.getPhone());
                    //客户信息
                    payment.setCustomerId(borrow.getCustomerId());
                    payment.setCustomerName(borrow.getCustomerName());
                    payment.setIdCard(borrow.getCustomerIdCard());
                    //支付信息
                    payment.setAmount(cashflow.getMonthPayment());
                    payment.setBizId(cashflow.getIdStr());
                    String orderSn = OrderUtil.ORDERSN_PREFIX_CASH_FLOW + cashflow.getIdStr();
                    payment.setOrderSn(orderSn);
                    payment.setSystemSource(SystemSourceEnum.BORROW.getName());
                    payment.setType(PaymentTypeEnum.BORROW_RETURN.getCode());
                    payment.setRemark(PaymentTypeEnum.BORROW_RETURN.getCodeDesc());
                    BeanHelper.setAddDefaultField(payment);
                    payments.add(payment);
                }
            }
        }
        return payments;
    }

    //发送借款还款MQ
    private Result sendMQBorrowReturnPayment(List<Payment> paymentList){
        Result result = Result.error();
        if(!paymentList.isEmpty()){
            Message message = MessageBuilder.withPayload(JSON.toJSONString(paymentList)).build();
            TransactionSendResult transactionSendResult = rocketMQTemplate.sendMessageInTransaction(
                        borrowReturnPayProducerGroup, borrowReturnPayTopic, message, borrowReturnPayTag);
            logger.debug("【发送MQ借款人还款支付】发送状态={}",transactionSendResult.getLocalTransactionState());
        }
        result = Result.success();
        return result;
    }

    private int updateCashflow(String cashflowId, CashflowBizStateEnum bizStateEnum){
        Cashflow cashflow = new Cashflow();
        cashflow.setIdStr(cashflowId);
        cashflow.setBizState(bizStateEnum.getState());
        BeanHelper.setUpdateDefaultField(cashflow);
        int count = cashflowMapper.updateByPrimaryKeySelective(cashflow);
        return count;
    }

    private int updateCashflowList(List<String> cashflowIdList,CashflowBizStateEnum bizStateEnum){
        int count = 0;
        for (String cashflowId : cashflowIdList) {
            this.updateCashflow(cashflowId,bizStateEnum);
            ++count;
        }
        return count;
    }

    @Transactional
    public Result dealCashflowReturnPayment(List<String> cashflowIdList,CashflowBizStateEnum bizStateEnum){
        Result result = Result.error();
        this.updateCashflowList(cashflowIdList,bizStateEnum);
        result = Result.success();
        return result;
    }
}
