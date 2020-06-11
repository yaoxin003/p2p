package com.yx.p2p.ds.service;

import com.yx.p2p.ds.easyui.Result;

import java.util.Date;

/**
 * @description:
 * @author: yx
 * @date: 2020/06/05/16:36
 */
public interface ReturnLendingService {

    //处理回款
    public Result dealInvestReturn(Date arriveDate);
}
