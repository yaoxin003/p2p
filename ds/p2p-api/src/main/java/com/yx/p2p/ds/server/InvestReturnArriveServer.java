package com.yx.p2p.ds.server;

import com.yx.p2p.ds.easyui.Result;

import java.util.Date;

/**
 * @description:投资回款到账
 * @author: yx
 * @date: 2020/06/11/16:36
 */

public interface InvestReturnArriveServer {
    //处理投资还款到账，插入投资回款数据
    public Result dealInvestReturn(Date arriveDate);

    //插入出借单并批量发送投资撮合请求
    public Result dealInvestReturnLending(Date arriveDate);
}
