package com.yx.p2p.ds.service.invest;

import com.yx.p2p.ds.easyui.Result;

import java.util.Date;

/**
 * @description:
 * @author: yx
 * @date: 2020/06/05/8:26
 */
public interface InvestReturnArriveService {

    //处理投资还款到账，插入投资回款数据
    public Result dealInvestReturn(Date arriveDate);

}
