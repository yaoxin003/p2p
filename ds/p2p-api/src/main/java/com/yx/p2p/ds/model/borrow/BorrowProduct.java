package com.yx.p2p.ds.model.borrow;

import com.yx.p2p.ds.model.base.BaseModel;

import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/26/18:52
 */
@Table(name="p2p_borrow_product")
public class BorrowProduct extends BaseModel implements Serializable{

    private String name; //借款产品名称:学生培训贷，惠农贷，工薪贷，社保贷
    private BigDecimal monthFeeRate;//月费率(扩大100倍)=月利率+月管理费率
    private BigDecimal monthRate; //月利率(扩大100倍)
    private BigDecimal monthManageRate; //月管理费率(扩大100倍)
    private Integer loanDay; //最快放款天数
    private String remark;//备注

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getMonthFeeRate() {
        return monthFeeRate;
    }

    public void setMonthFeeRate(BigDecimal monthFeeRate) {
        this.monthFeeRate = monthFeeRate;
    }

    public BigDecimal getMonthRate() {
        return monthRate;
    }

    public void setMonthRate(BigDecimal monthRate) {
        this.monthRate = monthRate;
    }

    public BigDecimal getMonthManageRate() {
        return monthManageRate;
    }

    public void setMonthManageRate(BigDecimal monthManageRate) {
        this.monthManageRate = monthManageRate;
    }

    public Integer getLoanDay() {
        return loanDay;
    }

    public void setLoanDay(Integer loanDay) {
        this.loanDay = loanDay;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "BorrowProduct{" +
                "name='" + name + '\'' +
                ", monthFeeRate=" + monthFeeRate +
                ", monthRate=" + monthRate +
                ", monthManageRate=" + monthManageRate +
                ", loanDay=" + loanDay +
                ", remark='" + remark + '\'' +
                '}' + super.toString();
    }
}
