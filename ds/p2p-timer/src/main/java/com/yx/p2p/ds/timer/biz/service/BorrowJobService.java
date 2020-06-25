package com.yx.p2p.ds.timer.biz.service;

import com.yx.p2p.ds.model.borrow.DebtDateValue;
import java.util.Date;
import java.util.List;

/**
 * @description:债务每日价值
 * @author: yx
 * @date: 2020/06/15/19:31
 */
public interface BorrowJobService {

    //分页查询债务每日价值
    public List<DebtDateValue> getDebtDateValuePageList(Date daily, Integer page, Integer rows);

    //查询债务每日价值数量
    public Integer getDebtDateValueCount(Date daily);
}
