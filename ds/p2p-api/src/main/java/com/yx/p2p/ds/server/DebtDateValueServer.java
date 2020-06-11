package com.yx.p2p.ds.server;

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

    //获得债权和还款价值总和
    public Map<String,BigDecimal> getSumDebtAndReturnByBorrowIdList(Date daily, List<Integer> borrowIdList);

}
