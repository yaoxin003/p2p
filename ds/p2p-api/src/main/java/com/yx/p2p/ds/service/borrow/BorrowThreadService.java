package com.yx.p2p.ds.service.borrow;

import com.yx.p2p.ds.model.borrow.Borrow;

/**
 * @description:
 * @author: yx
 * @date: 2020/07/14/17:00
 */
public interface BorrowThreadService {

    //线程池事务操作：添加现金流和债务每日价值
    public void addCashflowAndDebtDateValue(Borrow borrow);
}
