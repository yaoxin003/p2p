package com.yx.p2p.ds.timer.biz.invest.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.server.InvestReturnArriveServer;
import com.yx.p2p.ds.timer.biz.invest.service.InvestReturnArriveJobService;
import org.springframework.stereotype.Service;
import java.util.Date;

/**
 * @description:
 * @author: yx
 * @date: 2020/06/12/10:46
 */
@Service
public class InvestReturnArriveJobServiceImpl implements InvestReturnArriveJobService {

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
}
