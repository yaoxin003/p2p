package com.yx.p2p.ds.payment.service.impl;

import com.alibaba.fastjson.JSON;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.enums.mq.MQStatusEnum;
import com.yx.p2p.ds.enums.payment.PaymentBizStateEnum;
import com.yx.p2p.ds.helper.BeanHelper;
import com.yx.p2p.ds.mock.thirdpay.enums.RetCodeEnum;
import com.yx.p2p.ds.model.payment.BaseBank;
import com.yx.p2p.ds.model.payment.CustomerBank;
import com.yx.p2p.ds.model.payment.Payment;
import com.yx.p2p.ds.mq.InvestMQVo;
import com.yx.p2p.ds.payment.mapper.BaseBankMapper;
import com.yx.p2p.ds.payment.mapper.CustomerBankMapper;
import com.yx.p2p.ds.payment.mapper.PaymentMapper;
import com.yx.p2p.ds.service.PaymentService;
import com.yx.p2p.ds.util.LoggerUtil;
import com.yx.p2p.ds.util.RASUtil;
import com.yx.p2p.ds.util.SHAUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/08/17:19
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BaseBankMapper baseBankMapper;
    @Autowired
    private CustomerBankMapper customerBankMapper;
    @Autowired
    private PaymentMapper paymentMapper;

    @Value("${mock.thirdpay.url}")
    private String mockThirdpayUrl;

    //RSA加密解密私钥
    @Value("${ras.private.key}")
    private String rasPrivateKey;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Value("${mq.pay.invest.producer.group}")
    private String payInvestProducerGroup;

    @Value("${mq.pay.invest.topic}")
    private String payInvestTopic;

    @Value("${mq.pay.invest.suc.tag}")
    private String payInvestSucTag;

    @Value("${mq.pay.invest.fail.tag}")
    private String payInvestFailTag;

    @Value("${mq.pay.borrow.producer.group}")
    private String payBorrowProducerGroup;

    @Value("${mq.pay.borrow.topic}")
    private String payBorrowTopic;

    @Value("${mq.pay.borrow.suc.tag}")
    private String payBorrowSucTag;

    @Value("${mq.pay.borrow.fail.tag}")
    private String payBorrowFailTag;

    public List<BaseBank> getAllBaseBankList(){
        List<BaseBank> allBaseBankList = baseBankMapper.selectAll();
        logger.debug("【查询数据库所有银行总行】allBaseBankList=" + allBaseBankList);
        return allBaseBankList;
    }

    public List<CustomerBank> getCustomerBankListByCustomerId(Integer customerId){
        logger.debug("【customerId】=" + customerId);
        CustomerBank customerBank = new CustomerBank();
        customerBank.setCustomerId(customerId);
        List<CustomerBank> customerBanks = customerBankMapper.select(customerBank);
        logger.debug("【db.customerBanks】=" + customerBanks);
        return customerBanks;
    }

    /**
     * 检查银行账户是否存在
     * Result.Status=error 银行账户已存在
     * Result.Status=ok 银行账户不存在
     * @param customerBank
     * @return
     */
    private Result checkBankAccount(CustomerBank customerBank){
        Result result = Result.success();
        CustomerBank param = new CustomerBank();
        param.setBankAccount(customerBank.getBankAccount());
        List<CustomerBank> customerBanks = customerBankMapper.select(param);
        if(!customerBanks.isEmpty()){
            result = Result.error(customerBanks.size(),"银行账户已存在");
        }
        logger.debug("checkBankAccount.【result】=" + result);
        return result;
    }

    public Result checkAndAddCustomerBank(CustomerBank customerBank){
        Result result = this.checkBankAccount(customerBank);
        if(Result.checkStatus(result)){//银行账户不存在
            result = this.addCustomerBank(customerBank);
        }
        return result;
    }

    private Result addCustomerBank(CustomerBank customerBank){
        Result result = Result.success();
        int count = 0;
        try{
            //1.设置时间，操作人，状态
            BeanHelper.setAddDefaultField(customerBank);
            logger.debug("【customerBank=】" + customerBank);
            //2.插入数据库
            count = customerBankMapper.insert(customerBank);
            logger.debug("【customer.bank.insert.db.count=】" + count);
        }catch(Exception e){
            result = Result.error(count,e.toString());
            logger.error(e.toString(),e);
        }
        logger.debug("insertBankAccount.【result】=" + result);
        return result;
    }

    //网关支付
    public Result gateway(Payment payment){
        logger.debug("【网关支付】payment=" + payment);
        Result result = this.checkAndAddPayment(payment);
        logger.debug("【网关支付】result=" + result);
        if(Result.checkStatus(result)){
            //封装网关支付url
            result.setTarget(this.buildGatewayURL((Payment) result.getTarget()));
        }
        return result;
    }

    private String buildGatewayURL(Payment payment){
        String url = mockThirdpayUrl + "/" + "mock/thirdpay/toGateway?paymentId=" + payment.getId();
        return url;
    }

    public Payment getPaymentById(Integer paymentId){
        Payment payment = paymentMapper.selectByPrimaryKey(paymentId);
        logger.debug("【payment=】" + payment);
        return payment;
    }

    private Result checkAndAddPayment(Payment payment){
        Result result = Result.success();
        Payment queryPay = this.queryPaymentBySystemSourceAndBizId(payment.getSystemSource(),payment.getBizId());
        if(queryPay == null){//支付单不存在
            logger.debug("【支付单不存在】");
            result = this.addPayment(payment);
            result.setTarget(payment);//为回调方法使用，需要payment.id字段
        }else {
            logger.debug("【支付单存在】queryPay=" + queryPay);
            if(PaymentBizStateEnum.NEW_ADD.getState().equals(queryPay.getBizState())){//支付单状态：新增
                result.setTarget(queryPay);//为回调方法使用，需要payment.id字段
            }else {
                result = Result.error("该笔数据已生成支付单，状态为" +
                        PaymentBizStateEnum.getStateDesc(queryPay.getBizState()));
            }
        }
        return result;
    }

    /**
        * @description:
     * * 检查支付单是否存在
     * Result.Status=error 支付单已存在
     * Result.Status=ok 支付单不存在
        * @author:  YX
        * @date:    2020/04/18 7:32
        * @param: systemSource 系统来源
        * @param: bizId 业务编号，如invest.id
        * @return: com.yx.p2p.ds.model.Payment
        * @throws:
        */
    private Payment queryPaymentBySystemSourceAndBizId(String systemSource,String bizId){
        Payment resPay = null;
        if(StringUtils.isNotBlank(systemSource) && StringUtils.isNotBlank(bizId)){
            Payment query = new Payment();
            query.setSystemSource(systemSource);
            query.setBizId(bizId);
            List<Payment> payments = paymentMapper.select(query);
           if(!payments.isEmpty()) {
               resPay = payments.get(0);
           }
        }
        logger.debug("【查询数据库payment：systemSource和bizId】resPay=" + resPay);
        return resPay;
    }

    private Result addPayment(Payment payment){
        logger.debug("【payment=】" + payment);
        Result result = Result.success();
        int count = 0;
        try{
            this.buildAddPayment(payment);
            //2.插入数据库
            count = paymentMapper.insert(payment);
            logger.debug("【payment.insert.db.count=】" + count);
        }catch(Exception e){
            result = Result.error(count,e.toString());
            logger.error(e.toString(),e);
        }
        logger.debug("【插入支付数据】count=" + count + "，【result】=" + result);
        return result;
    }

    private void buildAddPayment(Payment payment) {
        //设置时间，操作人，状态
        BeanHelper.setAddDefaultField(payment);
    }

    //处理公司company1的网关支付结果
    //1.接收支付结果，解密验签
    //2.修改payment表支付状态
    //3.使用RocketMQ加入消息队列中通知其他系统（投资人支付通知invest和recored两个系统）
    public Result dealCompany1Gateway(String payResult){
        //1.解析第三方支付结果，解密验签
        Result thirdPayResult = this.parsePayResult(payResult);
        Result result = null;
        if(Result.checkStatus(thirdPayResult)){
            //2.修改payment表支付状态
            result = this.updatePaymentState(thirdPayResult);
            if(Result.checkStatus(result)){
                //3.使用RocketMQ加入消息队列中通知其他系统（投资人支付通知invest和recored两个系统）
                result = this.dealInvestPayResultMQ(result,(Map<String,String>)thirdPayResult.getTarget());
            }
        }
        return result;
    }

    private Result dealInvestPayResultMQ(Result result, Map<String,String> thirdPayMap) {
        logger.debug("3.【支付投资支付结果通知后，准备向RocketMQ发送数据】result=" + result);
        String retCode = thirdPayMap.get("retCode");
        String orderSn = thirdPayMap.get("orderSn");
        Payment payment = this.queryDBPaymentByOrderSn(orderSn);
        return this.sendInvestPayResultMQ(retCode,payment);//发送支付结果MQ
    }

    private Payment queryDBPaymentByOrderSn(String orderSn) {
        Example example = new Example(Payment.class);
        example.createCriteria().andEqualTo("orderSn",orderSn);
        Payment payment = paymentMapper.selectOneByExample(example);//若有多条数据则抛出异常
        logger.debug("【queryDBPaymentByOrderSn】payment=" + payment);
        return payment;
    }

    //发送支付结果MQ
    private Result sendInvestPayResultMQ(String retCode,Payment payment) {
        Result result = Result.success();
        String mqJSON = this.buildInvestPayResultMQJSON(retCode,payment);
        result = this.sendInvestPayMQ(retCode,mqJSON, payment.getBizId());
        return result;
    }

    private Result sendInvestPayMQ(String retCode, String mqJSON,String mqKey) {
        logger.debug("【准备发送投资支付结果MQ】mqJSON=" + mqJSON + ",mqKey=" + mqKey);
        Result result = Result.error();
        String payInvestTag = null;
        if(retCode.equals(RetCodeEnum.PAY_SUC.getCode())) {//支付成功
            payInvestTag = payInvestSucTag;
        }else{//支付失败
            payInvestTag = payInvestFailTag;
        }
        Message message = new Message(payInvestTopic, payInvestTag, mqKey, mqJSON.getBytes());
        SendResult sendResult = null;
        DefaultMQProducer producer = null;
        try {
            //sendResult = rocketMQTemplate.getProducer().send(message);
            producer = rocketMQTemplate.getProducer();
            producer.setProducerGroup(payInvestProducerGroup);
            sendResult = producer.send(message);
            result = Result.success();
        } catch (Exception e) {
            result = LoggerUtil.addExceptionLog(e,logger);
        }
        logger.debug("【发送投资支付结果MQ】，producer=" + producer.getProducerGroup() +
                ",topic="+ payInvestTopic + ",tag=" + payInvestTag + ",message=" + message);
        logger.debug("【发送投资支付结果MQ】，sendResult" + sendResult);
        return result;
    }

    private String buildInvestPayResultMQJSON(String retCode,Payment payment) {
        InvestMQVo investMQVo = new InvestMQVo();
        //对象拷贝
        try {
            BeanUtils.copyProperties(investMQVo,payment);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(retCode.equals(RetCodeEnum.PAY_SUC.getCode())) {//支付成功
            investMQVo.setStatus(MQStatusEnum.OK.getStatus());
        }else{//支付失败
            investMQVo.setStatus(MQStatusEnum.FAIL.getStatus());
        }
        logger.debug("【发送支付结果前对象】investMQVo=" + investMQVo);
        String investMQString = JSON.toJSONString(investMQVo);
        return investMQString;
    }

    //修改payment表支付状态，使用orderSn根据thirdPay.retCode作为判断条件
    private Result updatePaymentState(Result result) {
        Map<String,String> thirdPayResult = (Map<String,String>)result.getTarget();
        logger.debug("2.1.【更新订单状态，重新封装订单数据】thirdPayResult=" + thirdPayResult );
        String retCode = thirdPayResult.get("retCode");
        Payment payment = new Payment();
        payment.setOrderSn(thirdPayResult.get("orderSn"));
        if(retCode.equals(RetCodeEnum.PAY_SUC.getCode())){//支付成功
            //支付成功
            payment.setBizState(PaymentBizStateEnum.PAY_SUC.getState());
        }else{
            //支付失败
            payment.setBizState(PaymentBizStateEnum.PAY_FAIL.getState());
        }
        result = this.updatePaymentDBByThirdPay(payment);
        return result;
    }

    //根据第三方支付结果更新支付单状态
    private Result updatePaymentDBByThirdPay(Payment payment) {
        logger.debug("2.2【根据第三方支付结果准备更新支付单】payment=" + payment);
        Result result = Result.error();
        try{

            BeanHelper.setUpdateDefaultField(payment);
            Example example = new Example(Payment.class);
            String orderSn = payment.getOrderSn();
            example.createCriteria().andEqualTo("orderSn",orderSn);
            int count = paymentMapper.updateByExampleSelective(payment, example);
            if(count == 1){
                result = Result.success();
            }else{
                String errorMsg = "根据第三方支付结果更新支付单失败，【orderSn=】" + orderSn;
                result = Result.error(count,errorMsg);
                logger.debug(errorMsg);
            }
        }catch (Exception e){
            result = LoggerUtil.addExceptionLog(e,logger);
        }
        return result;
    }

    //1.接收支付结果，解密验签
    private Result parsePayResult(String payResult) {
        logger.debug("【1.1.支付结果，解析第三方支付结果，解密】");
        Result result = Result.error();
        if(StringUtils.isNotBlank(payResult)){
            //解密
            Object rasRes = RASUtil.privateDecrypt(payResult, rasPrivateKey);
            //验签
            result = this.checkSign(rasRes);
        }
        return result;
    }

    //验签
    private Result checkSign(Object rasRes) {
        logger.debug("【1.2.支付结果，解析第三方支付结果，验签】");
        Result result = Result.success();
        if(rasRes != null){
            Map<String,String> map = (Map<String,String>)rasRes;
            logger.debug("【rasResult.map】=" + map.keySet());
            String orderSn = map.get("orderSn");
            String retCode = map.get("retCode");
            String retInfo =  map.get("retInfo");
            String sign = map.get("sign");
            logger.debug("【原签名】sign=" + sign);

            Map<String,String> selfMap = new HashMap<>();
            selfMap.put("orderSn",orderSn);
            selfMap.put("retCode",retCode);
            selfMap.put("retInfo",retInfo);

            //验签
            String selfSign = SHAUtil.encryptSHA(selfMap);
            logger.debug("【生成签名】selfSign=" + selfSign);

            if(selfSign.equals(sign)){
                logger.debug("【验签正确】");
                result.setTarget(selfMap);//修改payment表支付状态，需要使用
            }else {
                String errorMsg = "【验签错误】【orderSn=】" + orderSn;
                logger.debug(errorMsg);
                result = Result.error(errorMsg);
            }
        }
        return result;
    }

    public CustomerBank getCustomerBankById(Integer customerBankId){
        logger.debug("【customerBankId=】" + customerBankId);
        CustomerBank customerBank = this.queryDBCustomerBank(customerBankId);

        return customerBank;
    }

    //放款
    //1.添加放款支付单
    //2.调用银行放款借款(加签加密)
    //3.根据银行放款通知(解密验签)，更新支付单状态
    //4.放款结果通知MQ(通知方：借款，撮合，投资，账户)
    @Override
    public Result loan(Payment payment) {
        logger.debug("【放款接口】入参payment=" +payment);
        //添加放款支付单状态
        Result result = this.addPayment(payment);
        logger.debug("【加签加密，调用银行放款同步接口】");
        logger.debug("【解密验签，处理银行返回放款结果");
        //更新支付单状态
        result = this.updateLoanPayment(payment);
        //放款结果通知MQ(通知方：借款，撮合，投资，账户)
        result = this.dealLoanPayResultMQ(payment);
        logger.debug("【放款接口】结果result=" +result);
        return result;
    }

    private Result dealLoanPayResultMQ(Payment payment) {
        Result result = Result.error();
        Map<String,String> loanNotify = new HashMap<>();
        loanNotify.put("bizId",payment.getBizId());//biz=borrow.id
        loanNotify.put("orderSn",payment.getOrderSn());//orderSn=borrow.id
        loanNotify.put("customerId",String.valueOf(payment.getCustomerId()));
        loanNotify.put("status", MQStatusEnum.OK.getStatus());
         String mqJSON = JSON.toJSONString(loanNotify);
         result = this.sendLoanPayMQ(mqJSON,payment.getBizId());
         return result;
    }

    //放款结果通知MQ(通知方：借款，撮合，投资，账户)
    private Result sendLoanPayMQ(String mqJSON,String mqKey) {
        logger.debug("【准备发送放款支付结果MQ】入参mqJSON=" + mqJSON + ",mqKey=" + mqKey);
        Result result = Result.error();
        Message message = new Message(payBorrowTopic, payBorrowSucTag, mqKey, mqJSON.getBytes());
        SendResult sendResult = null;
        DefaultMQProducer producer = null;
        try {
            producer = rocketMQTemplate.getProducer();
            producer.setProducerGroup(payBorrowProducerGroup);
            sendResult = producer.send(message);
            result = Result.success();
        } catch (Exception e) {
            result = LoggerUtil.addExceptionLog(e,logger);
        }
        logger.debug("【发送放款支付结果MQ】，producer=" + producer.getProducerGroup() +
                "，topic="+ payBorrowTopic + ",tag=" + payBorrowSucTag + ",message=" + message);
        logger.debug("【发送放款支付结果MQ】，sendResult" + sendResult);
        return result;
    }

    //更新放款订单状态
    private Result updateLoanPayment(Payment payment) {
        Result result = Result.error();
        Payment param = new Payment();
        param.setOrderSn(payment.getOrderSn());
        param.setBizState(PaymentBizStateEnum.LOAN_SUC.getState());
        result = this.updatePaymentDBByThirdPay(param);
        return result;
    }

    private CustomerBank queryDBCustomerBank(Integer customerBankId) {
        CustomerBank customerBank = customerBankMapper.selectByPrimaryKey(customerBankId);
        logger.debug("【customerBank=】" + customerBank);
        return customerBank;
    }

}
