package com.yx.p2p.ds.model.borrow;

import com.yx.p2p.ds.model.base.BaseModel;
import com.yx.p2p.ds.model.base.BaseModel2;

import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @description:债务每日价值
 * MySQL实体类
 * @author: yx
 * @date: 2020/05/30/15:17
 */
@Table(name="p2p_debt_date_value")
public class DebtDateValue extends BaseModel2 implements Serializable {

    private Integer borrowId;//借款编号Borrow.id
    private Date daily;//日期
    private BigDecimal value;//债务每日价值
    private BigDecimal returnAmt;//还款到账金额

    public Integer getBorrowId() {
        return borrowId;
    }

    public void setBorrowId(Integer borrowId) {
        this.borrowId = borrowId;
    }

    public Date getDaily() {
        return daily;
    }

    public void setDaily(Date daily) {
        this.daily = daily;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getReturnAmt() {
        return returnAmt;
    }

    public void setReturnAmt(BigDecimal returnAmt) {
        this.returnAmt = returnAmt;
    }

    @Override
    public String toString() {
        return "DebtDateValue{" +
                "borrowId=" + borrowId +
                ", daily=" + daily +
                ", value=" + value +
                ", returnAmt=" + returnAmt +
                '}' + super.toString();
    }
}
