package com.yx.p2p.ds.invest.service.impl;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.invest.mapper.InvestClaimHistoryMapper;
import com.yx.p2p.ds.model.invest.InvestClaim;
import com.yx.p2p.ds.model.invest.InvestClaimHistory;
import com.yx.p2p.ds.service.invest.InvestClaimHistoryService;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
