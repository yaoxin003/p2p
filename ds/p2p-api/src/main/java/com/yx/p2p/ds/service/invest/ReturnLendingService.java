package com.yx.p2p.ds.service.invest;

import com.yx.p2p.ds.easyui.Result;

import java.util.Date;

/**
 * @description:
 * @author: yx
 * @date: 2020/06/05/16:36
 */
public interface ReturnLendingService {

    //处理投资回款出借单
    public Result dealInvestReturnLending(Date arriveDate);
}
