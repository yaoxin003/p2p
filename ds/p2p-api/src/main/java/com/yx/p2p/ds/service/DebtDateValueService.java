package com.yx.p2p.ds.service;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.model.borrow.Borrow;
import com.yx.p2p.ds.model.borrow.Cashflow;
import com.yx.p2p.ds.model.borrow.DebtDateValue;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: yx
 * @date: 2020/05/30/15:27
 */
public interface DebtDateValueService {


    public Result addBatchDebtDateValue(Borrow borrow, List<Cashflow> cashflows);

    /**
     * @description: 分页查询某日债务价值集合
     * @author:  YX
     * @date:    2020/05/20 10:27
     * @param: daily 日期(年月日)
     * @param: page 第几页，首页编号1
     * @param: rows 每页记录数
     * @return: java.util.List<com.yx.p2p.ds.mongo.borr.DebtDailyValue>
     */
    public List<DebtDateValue> queryDebtDateValuePageList(Date daily, Integer page, Integer rows);

    //查询债务某日价值数量
    public Integer queryDebtDateValuePageCount(Date daily);

    //获得债权和还款价值总和
    public Map<String,BigDecimal> getSumDebtAndReturnByBorrowIdList(Date daily,List<Integer> borrowIdList);
}
