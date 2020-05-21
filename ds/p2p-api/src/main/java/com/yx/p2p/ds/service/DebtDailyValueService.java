package com.yx.p2p.ds.service;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.model.borrow.Borrow;
import com.yx.p2p.ds.model.borrow.Cashflow;
import com.yx.p2p.ds.mongo.borrow.DebtDailyValue;

import java.util.Date;
import java.util.List;

/**
 * @description: 计算债务每日价值
 * @author: yx
 * @date: 2020/05/19/15:15
 */
public interface DebtDailyValueService {

    //批量添加债务每日价值
    public Result addBatchDebtDailyValue(Borrow borrow,List<Cashflow> cashflows);

    //查询某日债务价值集合
    public List<DebtDailyValue> queryDebtDailyValuePageList(Date daily,Integer page,Integer rows);

    //查询债务某日价值数量
    public Integer queryDebtDailyValuePageCount(Date daily);

}
