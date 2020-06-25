package com.yx.p2p.ds.timer.biz.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.model.invest.InvestDebtVal;
import com.yx.p2p.ds.server.InvestReturnArriveServer;
import com.yx.p2p.ds.timer.biz.service.InvestJobService;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/06/12/10:46
 */
@Service
public class InvestJobServiceImpl implements InvestJobService {

    @Reference
    private InvestReturnArriveServer investReturnArriveServer;

    @Override
    public Result dealInvestReturn(Date arriveDate) {
        return investReturnArriveServer.dealInvestReturn(arriveDate);
    }

    @Override
    public Result dealInvestReturnLending(Date arriveDate) {
        return investReturnArriveServer.dealInvestReturnLending(arriveDate);
    }

    public Integer getInvestDebtValCount(Date arriveDate){
        return investReturnArriveServer.getInvestDebtValCount(arriveDate);
    }

    public List<InvestDebtVal> getInvestDebtValPageList(Date arriveDate, int page, int rows){
        return investReturnArriveServer.getInvestDebtValPageList(arriveDate, page, rows);
    }

    @Override
    public Integer getInvestDebtValReturnAmtCount(Date arriveDate) {
        return investReturnArriveServer.getInvestDebtValReturnAmtCount(arriveDate) ;
    }

    @Override
    public List<InvestDebtVal> getInvestDebtValReturnAmtPageList(Date arriveDate, int page, int rows) {
        return investReturnArriveServer.getInvestDebtValReturnAmtPageList(arriveDate, page, rows);
    }
}
