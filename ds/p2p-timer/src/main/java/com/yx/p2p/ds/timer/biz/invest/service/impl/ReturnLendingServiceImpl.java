package com.yx.p2p.ds.timer.biz.invest.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.enums.SystemSourceEnum;
import com.yx.p2p.ds.enums.lending.LendingTypeEnum;
import com.yx.p2p.ds.enums.match.InvestMatchReqLevelEnum;
import com.yx.p2p.ds.enums.match.MatchRemarkEnum;
import com.yx.p2p.ds.helper.BeanHelper;
import com.yx.p2p.ds.model.invest.Invest;
import com.yx.p2p.ds.model.invest.InvestReturn;
import com.yx.p2p.ds.model.invest.Lending;
import com.yx.p2p.ds.model.match.InvestMatchReq;
import com.yx.p2p.ds.server.InvestServer;
import com.yx.p2p.ds.service.InvestReturnService;
import com.yx.p2p.ds.service.ReturnLendingService;
import com.yx.p2p.ds.util.LoggerUtil;
import com.yx.p2p.ds.util.OrderUtil;
import com.yx.p2p.ds.util.PageUtil;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @description:回款出借单
 * @author: yx
 * @date: 2020/06/05/16:34
 */
@Service
public class ReturnLendingServiceImpl implements ReturnLendingService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    static final int PAGESIZE = 500;

    @Autowired
    private InvestReturnService investReturnService;

    @Reference
    private InvestServer investServer;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Value("${mq.match.invest.producer.group}")
    private String matchInvestProducerGroup;

    @Value("${mq.match.invest.topic}")
    private String matchInvestTopic;

    @Value("${mq.match.invest.tag}")
    private String matchInvestTag;

  /**
      * @description: 处理投资回款
       * 1.生成回款出借单
       * 2.批量发送投资撮合
      * @param: arriveDate 到账时间
      */
    @Override
    public Result dealInvestReturn(Date arriveDate) {
        logger.debug("【处理投资回款】入参：arriveDate=" + arriveDate);
        Result result = this.genAndSendReturnLending(arriveDate);
        return result;
    }

    /**
     * @description: 生成回款出借单并批量发送投资撮合
     * 1.查询投资回款集合
     * 2.构建出借单对象集合
     * 3.批量插入出借单
     * 4.批量发送投资撮合
     * @param: arriveDate 到账时间
     */
    private Result genAndSendReturnLending(Date arriveDate){
        Result result = Result.error();
       //1.查询投资回款集合
        Integer totalCount = investReturnService.getInvestReturnListCount(arriveDate);
        int pageCount = PageUtil.getPageCount(totalCount, PAGESIZE);
        for(int i=1; i<=pageCount; i++){
            List<InvestReturn> investReturnList = investReturnService.getInvestReturnList(arriveDate, i, PAGESIZE);
            //过滤已生成回款出借单
            //2.构建出借单对象集合
            List<Lending> lendingList = new ArrayList<>();
            for (InvestReturn investReturn : investReturnList) {
                Lending lending = this.buildAddLending(investReturn);
                lendingList.add(lending);
            }
            lendingList = investServer.addLendingList(lendingList);
            result = this.sendBatchInvestMatch(lendingList);
        }
        return result;
    }

    private Lending buildAddLending(InvestReturn investReturn) {
        Lending lending = new Lending();
        lending.setInvestId(investReturn.getInvestId());
        lending.setAmount(investReturn.getReturnTotalAmt());
        lending.setLendingType(LendingTypeEnum.RETURN_LEND.getCode());//回款出借单
        lending.setCustomerId(investReturn.getInvestCustomerId());
        lending.setArriveDate(investReturn.getArriveDate());
        lending.setOrderSn(OrderUtil.genOrderSn(SystemSourceEnum.INVEST.getName()));
        BeanHelper.setAddDefaultField(lending);
        return lending;
    }

    //批量发送投资撮合
    private Result sendBatchInvestMatch(List<Lending> lendingList){
        Result result = Result.error();
        Map<Integer,Invest> investMap = this.getInvestMapByLendingList(lendingList);
        List<InvestMatchReq> investMatchReqList = this.buildInvestMatchReqList(lendingList,investMap);
        result = this.sendInvestMatchReqMQ(investMatchReqList);
        return result;
    }

    private Map<Integer,Invest> getInvestMapByLendingList(List<Lending> lendingList) {
        List<Integer> investIdList = new ArrayList<>();
        for (Lending lending : lendingList) {
            investIdList.add(lending.getInvestId());
        }
        List<Invest> investList = investServer.getInvestListByInvestIdList(investIdList);
        Map<Integer,Invest> investmap = new HashMap<>();
        for (Invest invest : investList) {
            investmap.put(invest.getId(),invest);
        }
        return investmap;
    }


    private List<InvestMatchReq> buildInvestMatchReqList(List<Lending> lendingList,Map<Integer,Invest> investMap) {
        List<InvestMatchReq> investMatchReqList = new ArrayList<>();
        for (Lending lending : lendingList) {
            Integer investId = lending.getInvestId();
            Invest invest = investMap.get(investId);
            InvestMatchReq investMatchReq = new InvestMatchReq();
            investMatchReq.setInvestAmt(lending.getAmount());
            investMatchReq.setWaitAmt(investMatchReq.getInvestAmt());
            investMatchReq.setInvestCustomerId(lending.getCustomerId());
            investMatchReq.setInvestBizId(String.valueOf(investId));
            investMatchReq.setLevel(InvestMatchReqLevelEnum.REINVEST.getLevel());
            investMatchReq.setRemark(MatchRemarkEnum.REINVEST.getDesc());
            investMatchReq.setInvestOrderSn(String.valueOf(lending.getId()));//lending.id
            investMatchReq.setInvestCustomerName(invest.getCustomerName());
            investMatchReq.setProductId(invest.getInvestProductId());
            investMatchReq.setProductName(invest.getInvestProductName());
            investMatchReq.setYearIrr(invest.getInvestYearIrr());
            BeanHelper.setAddDefaultField(investMatchReq);
            investMatchReqList.add(investMatchReq);
        }
        return investMatchReqList;
    }

    //批量发送投资撮合MQ
    private Result sendInvestMatchReqMQ(List<InvestMatchReq> investMatchReqList) {
        Result result = Result.error();
        String mqVoStr = JSON.toJSONString(investMatchReqList);
        DefaultMQProducer producer = null;
        try {
            String reqKey = "";
            for (InvestMatchReq investMatchReq : investMatchReqList) {
                reqKey = investMatchReq.getInvestOrderSn() + ",";
            }
            logger.debug("【发送投资撮合回款投资】，investMatchReqList=" + investMatchReqList);
            Message message = new Message(matchInvestTopic,matchInvestTag,reqKey ,mqVoStr.getBytes());
            producer = rocketMQTemplate.getProducer();
            producer.setProducerGroup(matchInvestProducerGroup);
            SendResult sendResult = producer.send(message);
            logger.debug("【发送投资撮合回款投资MQ】，producer=" + producer.getProducerGroup() +
                    "，topic="+ matchInvestTopic + ",tag=" + matchInvestTag + ",message=" + message);
            logger.debug("【发送投资撮合回款投资结果MQ】，sendResult" + sendResult);
            result = Result.success();
        } catch (Exception e) {
            result = LoggerUtil.addExceptionLog(e,logger);
        }
        return result;
    }

}
