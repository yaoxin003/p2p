package com.yx.p2p.ds.payment.service.impl;

import com.alibaba.fastjson.JSON;
import com.yx.p2p.ds.easyui.Result;
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

    @Value("${mq.payment.topic}")
    private String payTopic;

    @Value("${mq.payment.tag.invest.suc}")
    private String payInvestSucTag;

    @Value("${mq.payment.tag.invest.fail}")
    private String payInvestFailTag;

    public List<BaseBank> getAllBaseBankList(){
        List<BaseBank> baseBanks = baseBankMapper.selectAll();
        logger.debug("【db.allBasebank】=" + baseBanks);
        return baseBanks;
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
        logger.debug("insertPayment.【result】=" + result);
        return result;
    }

    private void buildAddPayment(Payment payment) {
        BaseBank baseBank = this.queryDBBaseBank(payment.getBankCode());
        payment.setBaseBankName(baseBank.getName());
        //1.设置时间，操作人，状态
        BeanHelper.setAddDefaultField(payment);
    }

    private BaseBank queryDBBaseBank(String bankCode) {
        Example example = new Example(BaseBank.class);
        example.createCriteria().andEqualTo("bankCode",bankCode);
        BaseBank baseBank = baseBankMapper.selectOneByExample(example);
        return baseBank;
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
            //3.使用RocketMQ加入消息队列中通知其他系统（投资人支付通知invest和recored两个系统）
            this.dealPayResultMQ(result,(Map<String,String>)thirdPayResult.getTarget());
        }
        return result;
    }

    private void dealPayResultMQ(Result result, Map<String,String> thirdPayMap) {
        logger.debug("3.【支付结果通知后，准备向RocketMQ发送数据】result=" + result);
        if(Result.checkStatus(result)){
            String retCode = thirdPayMap.get("retCode");
            String orderSn = thirdPayMap.get("orderSn");
            Payment payment = this.queryDBPaymentByOrderSn(orderSn);
            this.sendPayResultMQ(retCode,payment);//发送支付结果MQ
        }
    }

    private Payment queryDBPaymentByOrderSn(String orderSn) {
        Example example = new Example(Payment.class);
        example.createCriteria().andEqualTo("orderSn",orderSn);
        Payment payment = paymentMapper.selectOneByExample(example);//若有多条数据则抛出异常
        logger.debug("【queryDBPaymentByOrderSn】payment=" + payment);
        return payment;
    }

    //发送支付结果MQ
    private Result sendPayResultMQ(String retCode,Payment payment) {
        Result result = Result.success();
        String mqJSON = this.buildPayResultMQJSON(retCode,payment);
        this.sendPayMQ(retCode,mqJSON, payment.getOrderSn());
        return result;
    }

    private void sendPayMQ(String retCode, String mqJSON,String mqKey) {
        String payInvestTag = null;
        if(retCode.equals(RetCodeEnum.PAY_SUC.getCode())) {//支付成功
            payInvestTag = payInvestSucTag;
        }else{//支付失败
            payInvestTag = payInvestFailTag;
        }
        Message message = new Message(payTopic, payInvestTag, mqKey, mqJSON.getBytes());
        logger.debug("【准备发送支付结果MQ】");
        SendResult sendResult = null;
        try {
            sendResult = rocketMQTemplate.getProducer().send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("【发送支付结果MQ】，topic="+ payTopic + ",tag=" + payInvestTag + ",message=" + message);
        logger.debug("【发送支付结果MQ】，sendResult" + sendResult);
    }

    private String buildPayResultMQJSON(String retCode,Payment payment) {
        InvestMQVo investMQVo = new InvestMQVo();
        //对象拷贝
        try {
            BeanUtils.copyProperties(investMQVo,payment);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(retCode.equals(RetCodeEnum.PAY_SUC.getCode())) {//支付成功
            investMQVo.setStatus(InvestMQVo.STATUS_OK);
        }else{//支付失败
            investMQVo.setStatus(InvestMQVo.STATUS_FAIL);
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

    private CustomerBank queryDBCustomerBank(Integer customerBankId) {
        CustomerBank customerBank = customerBankMapper.selectByPrimaryKey(customerBankId);
        logger.debug("【customerBank=】" + customerBank);
        return customerBank;
    }

}
