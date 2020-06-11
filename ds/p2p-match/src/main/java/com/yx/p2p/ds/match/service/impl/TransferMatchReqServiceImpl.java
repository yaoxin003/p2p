package com.yx.p2p.ds.match.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.enums.match.FinanceMatchReqBizStateEnum;
import com.yx.p2p.ds.helper.BeanHelper;
import com.yx.p2p.ds.match.mapper.FinanceMatchReqMapper;
import com.yx.p2p.ds.match.mapper.FinanceMatchResMapper;
import com.yx.p2p.ds.match.mapper.InvestMatchReqMapper;
import com.yx.p2p.ds.model.invest.InvestClaim;
import com.yx.p2p.ds.model.match.FinanceMatchReq;
import com.yx.p2p.ds.model.match.FinanceMatchRes;
import com.yx.p2p.ds.model.match.InvestMatchReq;
import com.yx.p2p.ds.server.FinanceMatchReqServer;
import com.yx.p2p.ds.server.InvestServer;
import com.yx.p2p.ds.service.BorrowMatchReqService;
import com.yx.p2p.ds.service.FinanceMatchReqService;
import com.yx.p2p.ds.service.InvestMatchReqService;
import com.yx.p2p.ds.service.TransferMatchReqService;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:转让撮合请求
 * @author: yx
 * @date: 2020/05/09/18:11
 */
@Service
public class TransferMatchReqServiceImpl implements TransferMatchReqService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Reference
    private InvestServer investServer;

    @Autowired
    private BorrowMatchReqService borrowMatchReqService;

    @Autowired
    private InvestMatchReqService investMatchReqService;

    @Autowired
    private FinanceMatchReqService financeMatchReqService;

    @Autowired
    private FinanceMatchReqMapper financeMatchReqMapper;

    @Autowired
    private InvestMatchReqMapper investMatchReqMapper;

    @Autowired
    private FinanceMatchResMapper financeMatchResMapper;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Value("${mq.invest.claim.change.producer.group}")
    private String investClaimChangeProducerGroup;
    @Value("${mq.invest.claim.change.topic}")
    private String investClaimChangeTopic;
    @Value("${mq.invest.claim.change.tag}")
    private String investClaimChangeTag;


    //转让撮合请求
    //1.查询是否已经转让撮合，若未撮合：
    //2.加撮合锁redis
    //3.判断投资金额是否大于转让债权集合金额，若大于：
    //4.循环撮合转让债权集合中的每笔债权
    //5.事务操作：更新投资撮合请求集合，更新转让撮合请求，批量插入转让撮合结果
    //6.债权交割(MQ)
    @Override
    public Result transferMatchReq(List<Map<String,Object>> transferMatchReqMapList) {
        Result result = Result.error();
        logger.debug("【解析MQ转让撮合请求】入参：transferMatchReqMapList=" + transferMatchReqMapList);
        for (Map<String, Object> transferMatchReqMap : transferMatchReqMapList) {
            String transferIdStr = (String)transferMatchReqMap.get("transferId");
            String financeExtBizId = (String)transferMatchReqMap.get("financeExtBizId");
            List<Map<String,String>> transferMatchReqList =
                    (List<Map<String,String>>)transferMatchReqMap.get("transferList");
            List<FinanceMatchReq> financeMatchReqList = this.buildFinanceMatchReqList(financeExtBizId,transferMatchReqList);
            //1.查询是否已经转让撮合，若未撮合：
            //2.加撮合锁redis
            //3.判断投资金额是否大于转让债权集合金额，若大于：
            List<InvestMatchReq> resNoMatchInvestReqList = investMatchReqService.getWaitMatchAmtInvestReqList();//未撮投资
            result = this.checkTransfer(financeMatchReqList,resNoMatchInvestReqList);
            if(Result.checkStatus(result)){
                //4.循环撮合转让债权集合中的每笔债权
                List<InvestMatchReq> resMatchedInvestReqList = new ArrayList<>();//已撮投资
                Map<String,List<FinanceMatchRes>> resFinanceMatchResMap = new HashMap<>();
                for (FinanceMatchReq financeMatchReq : financeMatchReqList) {
                    //业务状态：转让撮合确认
                    financeMatchReq.setBizState(FinanceMatchReqBizStateEnum.TRANSFER_MATCH_CONFIRM.getBizState());
                    List<FinanceMatchRes> resPartFinanceMatchResList = new ArrayList<>();//撮合结果
                    result = financeMatchReqService.dealFinanceMatch(financeMatchReq,
                            resNoMatchInvestReqList,resMatchedInvestReqList,resPartFinanceMatchResList);
                    //用于插入FinanceMatchResList,因为FinanceMatchRes中需要FinanceMatchReq.id
                    resFinanceMatchResMap.put(financeMatchReq.getFinanceOrderSn(),resPartFinanceMatchResList);
                    if(! Result.checkStatus(result)){//撮合失败不再继续执行
                        return result;
                    }
                }
                logger.debug("【转让撮合结果如下：】");
                logger.debug("【financeMatchReqList=】" + financeMatchReqList);
                logger.debug("【resNoMatchInvestReqList=】" + resNoMatchInvestReqList);
                logger.debug("【resMatchedInvestReqList=】" + resMatchedInvestReqList);
                //5.事务操作：更新投资撮合请求集合，更新转让撮合请求，批量插入转让撮合结果
                List<FinanceMatchRes> resAllFinanceMatchResList = new ArrayList<>();//撮合结果
                result = this.updateInvestAndTransferAndAddResMatch(resMatchedInvestReqList,
                        financeMatchReqList, resFinanceMatchResMap,resAllFinanceMatchResList);
                logger.debug("【resAllFinanceMatchResList=】" + resAllFinanceMatchResList);

                //封装债权交割MQListener 入参List<Map<String,Object>>
                Map<String,Object> changeClaimMap = this.buildChangeClaimMapList(transferIdStr,financeExtBizId,resAllFinanceMatchResList);
                //6.债权交割MQ
                logger.debug("【changeClaimMap=】" + changeClaimMap);
                this.sendChangeClaimMQ(transferIdStr,changeClaimMap);
            }else{

            }
        }
        return result;
    }

    //债权交割MQ
    private void sendChangeClaimMQ(String transferId,Map<String,Object> changeClaimMapList) {
        String mqJSON = JSON.toJSONString(changeClaimMapList);
        DefaultMQProducer producer = null;
        try {
            Message message = new Message(investClaimChangeTopic,investClaimChangeTag,
                    "transfer"+transferId,mqJSON.getBytes());
            producer = rocketMQTemplate.getProducer();
            producer.setProducerGroup(investClaimChangeProducerGroup);
            SendResult sendResult = producer.send(message);
            logger.debug("【发送投资债权交割MQ】，producer=" + producer.getProducerGroup() +
                    "，topic="+ investClaimChangeTopic + ",tag=" + investClaimChangeTag + ",message=" + message);
            logger.debug("【发送投资债权交割结果MQ】，sendResult" + sendResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //构建债权交割MQ参数
    private Map<String,Object> buildChangeClaimMapList(String transferId,String financeExtBizId,
                                                       List<FinanceMatchRes> resAllFinanceMatchResList) {
        Map<String,Object> outMap = new HashMap<>();
        outMap.put("transferId",transferId);
        outMap.put("financeExtBizId",financeExtBizId);
        List<Map<String,String>> outList = new ArrayList<>();
        for (FinanceMatchRes financeMatchRes : resAllFinanceMatchResList) {
            Map<String,String> inMap = new HashMap<>();
            inMap.put("financeCustomerId",String.valueOf(financeMatchRes.getFinanceCustomerId()));
            inMap.put("financeCustomerName",financeMatchRes.getFinanceCustomerName());
            inMap.put("financeBizId",transferId);
            inMap.put("financeOrderSn",financeMatchRes.getFinanceOrderSn());
            inMap.put("investCustomerId",String.valueOf(financeMatchRes.getInvestCustomerId()));
            inMap.put("investCustomerName",financeMatchRes.getInvestCustomerName());
            inMap.put("investBizId",financeMatchRes.getInvestBizId());
            inMap.put("investOrderSn",financeMatchRes.getInvestOrderSn());
            inMap.put("tradeAmt",String.valueOf(financeMatchRes.getTradeAmt()));//tradeAmt
            inMap.put("matchShare",String.valueOf(financeMatchRes.getMatchShare()));
            inMap.put("borrowProductId",String.valueOf(financeMatchRes.getBorrowProductId()));
            inMap.put("borrowProductName",financeMatchRes.getBorrowProductName());
            inMap.put("borrowYearRate",String.valueOf(financeMatchRes.getBorrowYearRate()));
            outList.add(inMap);
        }
        outMap.put("transferList",outList);
        return outMap;
    }

    private List<FinanceMatchReq> buildFinanceMatchReqList( String financeExtBizId,List<Map<String, String>> transferMatchReqList) {
        List<FinanceMatchReq> financeMatchReqList = new ArrayList<>();
        for (Map<String, String> map : transferMatchReqList) {
            FinanceMatchReq financeMatchReq = new FinanceMatchReq();
            financeMatchReq.setFinanceExtBizId(financeExtBizId);
            try {
                BeanUtils.copyProperties(financeMatchReq,map);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            BeanHelper.setAddDefaultField(financeMatchReq);
            financeMatchReqList.add(financeMatchReq);
        }
        logger.debug("【构建融资撮合请求集合】结果：financeMatchReqList=" + financeMatchReqList);
        return financeMatchReqList;
    }

    private Result checkTransfer(List<FinanceMatchReq> financeMatchReqList,List<InvestMatchReq> resNoMatchInvestReqList) {
        Result result = Result.error();
        BigDecimal waitFianceAmt = BigDecimal.ZERO;//待撮合融资
        BigDecimal waitInvetAmt = BigDecimal.ZERO;//待撮合投资
        for (FinanceMatchReq financeMatchReq : financeMatchReqList) {
            waitFianceAmt = waitFianceAmt.add(financeMatchReq.getWaitAmt());
        }
        for (InvestMatchReq investMatchReq : resNoMatchInvestReqList) {
            waitInvetAmt = waitInvetAmt.add(investMatchReq.getWaitAmt());
        }
        if(waitInvetAmt.compareTo(waitFianceAmt)>= 0){//可以撮合
            logger.debug("投资金额大于等于转让金额，可以撮合");
            result = Result.success();
        }else{
            String msg = "投资金额"+ waitInvetAmt + "小于转让金额" + waitFianceAmt + "，不能撮合";
            result = Result.error(msg);
            logger.debug(msg);
        }
        return result;
    }


    //5.事务操作：更新投资撮合请求集合，更新转让撮合请求，批量插入转让撮合结果
    @Transactional
    public Result updateInvestAndTransferAndAddResMatch(List<InvestMatchReq> resMatchedInvestReqList,
             List<FinanceMatchReq> financeMatchReqList, Map<String,List<FinanceMatchRes>> resFinanceMatchResMap,
                                                        List<FinanceMatchRes> resFinanceMatchResList){
        Result result = Result.error();
        //更新投资撮合请求集合
        for (InvestMatchReq investMatchReq : resMatchedInvestReqList) {
            InvestMatchReq paramInvestMatchReq = new InvestMatchReq();
            paramInvestMatchReq.setId(investMatchReq.getId());
            paramInvestMatchReq.setWaitAmt(investMatchReq.getWaitAmt());
            BeanHelper.setUpdateDefaultField(paramInvestMatchReq);
            investMatchReqMapper.updateByPrimaryKeySelective(paramInvestMatchReq);
        }
        //批量插入转让撮合请求
        financeMatchReqMapper.insertList(financeMatchReqList);
        //1.设置financeMatchRes.financeMatchId
        //2.插入resFinanceMatchResList
        for (FinanceMatchReq financeMatchReq : financeMatchReqList) {
            List<FinanceMatchRes> financeMatchResList = resFinanceMatchResMap.get(financeMatchReq.getFinanceOrderSn());
            for (FinanceMatchRes financeMatchRes : financeMatchResList) {
                financeMatchRes.setFinanceMatchId(financeMatchReq.getId());
                resFinanceMatchResList.add(financeMatchRes);
            }
        }
        //批量插入转让撮合结果
        financeMatchResMapper.insertList(resFinanceMatchResList);
        result = Result.success();
        return result;
    }


}
