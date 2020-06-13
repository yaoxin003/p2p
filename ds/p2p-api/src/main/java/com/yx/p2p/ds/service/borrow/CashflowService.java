package com.yx.p2p.ds.service.borrow;

import com.yx.p2p.ds.model.borrow.Cashflow;

import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/06/03/18:47
 */
public interface CashflowService {

    //arriveDate 还款到账日期
    //currentPage 开始页编号
    //pageSize 每页条数
    public List<Cashflow> getCashflowListByPage(Date arriveDate, Integer currentPage, Integer pageSize);


    //arriveDate 还款到账日期
    public Integer getCashflowListCount(Date arriveDate);

}
