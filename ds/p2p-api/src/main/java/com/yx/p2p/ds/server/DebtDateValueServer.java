package com.yx.p2p.ds.server;

import com.yx.p2p.ds.model.borrow.DebtDateValue;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @description:债权每日价值Server
 * @author: yx
 * @date: 2020/05/31/17:12
 */
public interface DebtDateValueServer {

    public List<DebtDateValue> getDebtDateValuePageList(Date daily, Integer page, Integer rows);

    public Integer getDebtDateValueCount(Date daily);

}
