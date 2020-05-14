package com.yx.p2p.ds.invest.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.enums.SystemSourceEnum;
import com.yx.p2p.ds.enums.invest.InvestBizStateEnum;
import com.yx.p2p.ds.enums.investproduct.InvestTypeEnum;
import com.yx.p2p.ds.enums.match.InvestMatchReqLevelEnum;
import com.yx.p2p.ds.enums.match.MatchRemarkEnum;
import com.yx.p2p.ds.enums.mq.MQStatusEnum;
import com.yx.p2p.ds.enums.payment.PaymentTypeEnum;
import com.yx.p2p.ds.helper.BeanHelper;
import com.yx.p2p.ds.invest.mapper.InvestClaimHistoryMapper;
import com.yx.p2p.ds.invest.mapper.InvestClaimMapper;
import com.yx.p2p.ds.invest.mapper.InvestMapper;
import com.yx.p2p.ds.model.crm.Customer;
import com.yx.p2p.ds.model.invest.Invest;
import com.yx.p2p.ds.model.invest.InvestClaim;
import com.yx.p2p.ds.model.invest.InvestClaimHistory;
import com.yx.p2p.ds.model.invest.InvestProduct;
import com.yx.p2p.ds.model.match.FinanceMatchRes;
import com.yx.p2p.ds.model.match.InvestMatchReq;
import com.yx.p2p.ds.model.payment.CustomerBank;
import com.yx.p2p.ds.model.payment.Payment;
import com.yx.p2p.ds.mq.InvestMQVo;
import com.yx.p2p.ds.server.CrmServer;
import com.yx.p2p.ds.server.FinanceMatchReqServer;
import com.yx.p2p.ds.server.PaymentServer;
import com.yx.p2p.ds.service.InvestClaimHistoryService;
import com.yx.p2p.ds.service.InvestProductService;
import com.yx.p2p.ds.service.InvestService;
import com.yx.p2p.ds.service.LendingService;
import com.yx.p2p.ds.util.BigDecimalUtil;
import com.yx.p2p.ds.util.DateUtil;
import com.yx.p2p.ds.util.LoggerUtil;
import com.yx.p2p.ds.util.OrderUtil;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/06/18:03
 */
@Service
public class InvestClaimHistoryServiceImpl implements InvestClaimHistoryService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private InvestClaimHistoryMapper investClaimHistoryMapper;

    public Result genInvestClaimHistoryList(List<InvestClaim> investClaimList){
        Result result = Result.error();
        List<InvestClaimHistory> investClaimHistoryList = this.buildAddInvestClaimHistoryList(investClaimList);
        investClaimHistoryMapper.insertList(investClaimHistoryList);
        result = Result.success();
        return result;
    }

    private List<InvestClaimHistory> buildAddInvestClaimHistoryList(List<InvestClaim> investClaimList) {
        List<InvestClaimHistory> historyList = new ArrayList<>();
        for (InvestClaim investClaim : investClaimList) {
            InvestClaimHistory history = new InvestClaimHistory();
            try {
                BeanUtils.copyProperties(history,investClaim);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            historyList.add(history);
        }
        return historyList;
    }



}
