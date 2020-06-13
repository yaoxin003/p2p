package com.yx.p2p.ds.borrow.service;

import com.yx.p2p.ds.borrow.mongo.borrow.DebtDailyValue;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.model.borrow.Borrow;
import com.yx.p2p.ds.model.borrow.Cashflow;
import java.util.Date;
import java.util.List;

/**
 * @description:暂不使用MogoDB，计算债务每日价值
 * @author: yx
 * @date: 2020/05/19/15:05
 */
public interface DebtDailyValueService {


    public Result addBatchDebtDailyValue(Borrow borrow,List<Cashflow> cashflows);

    /**
        * @description: 分页查询某日债务价值集合
        * @author:  YX
        * @date:    2020/05/20 10:27
        * @param: daily 日期(年月日)
        * @param: page 第几页，首页编号1
        * @param: rows 每页记录数
        * @return: java.util.List<com.yx.p2p.ds.mongo.borrow.DebtDailyValue>
        */
    public List<DebtDailyValue> queryDebtDailyValuePageList(Date daily,Integer page,Integer rows);

    //查询债务某日价值数量
    public Integer queryDebtDailyValuePageCount(Date daily);


}
