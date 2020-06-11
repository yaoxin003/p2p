package com.yx.p2p.ds.service.util.p2p;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @description:现金流对象
 * @author: yx
 * @date: 2020/05/16/18:25
 */
public class CashFlowVo {
    private Date occurDate;//发生日期
    private BigDecimal amount;//金额

    public Date getOccurDate() {
        return occurDate;
    }
    public void setOccurDate(Date occurDate) {
        this.occurDate = occurDate;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "CashFlowVo{" +
                "occurDate=" + occurDate +
                ", amount=" + amount +
                '}';
    }
}
