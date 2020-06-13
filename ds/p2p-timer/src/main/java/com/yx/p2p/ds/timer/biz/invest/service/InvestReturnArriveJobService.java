package com.yx.p2p.ds.timer.biz.invest.service;

import com.yx.p2p.ds.easyui.Result;

import java.util.Date;

/**
 * @description:
 * @author: yx
 * @date: 2020/06/12/10:46
 */
public interface InvestReturnArriveJobService {

    //处理投资还款到账，插入投资回款数据
    public Result dealInvestReturn(Date arriveDate);

    //插入出借单并批量发送投资撮合请求
    public Result dealInvestReturnLending(Date arriveDate);
}
