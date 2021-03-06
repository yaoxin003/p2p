package com.yx.p2p.ds.server;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.model.borrow.Cashflow;

import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/06/04/8:05
 */
public interface CashflowServer {

    //arriveDate 还款到账日期
    //currentPage 开始页编号
    //pageSize 每页条数
    public List<Cashflow> getCashflowListByPage(Date arriveDate, Integer currentPage, Integer pageSize);

    //arriveDate 还款到账日期
    public Integer getCashflowListCount(Date arriveDate);

    //借款人还款支付
    public Result borrowReturnPayment(Date returnDate);

}
