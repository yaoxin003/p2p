package com.yx.p2p.ds.timer.biz.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.server.CashflowServer;
import com.yx.p2p.ds.timer.biz.service.CashflowJobService;
import org.springframework.stereotype.Service;
import java.util.Date;

/**
 * @description:
 * @author: yx
 * @date: 2020/06/27/19:25
 */
@Service
public class CashflowJobServiceImpl implements CashflowJobService {

    @Reference
    private CashflowServer cashflowServer;

    //借款人还款支付
    public Result borrowReturnPayment(Date returnDate){
        return cashflowServer.borrowReturnPayment(returnDate);
    }
}
