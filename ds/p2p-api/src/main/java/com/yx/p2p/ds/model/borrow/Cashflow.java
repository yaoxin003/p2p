package com.yx.p2p.ds.model.borrow;

import com.yx.p2p.ds.model.base.BaseModel;

import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @description:现金流
 * @author: yx
 * @date: 2020/04/30/10:15
 */
@Table(name="p2p_cash_flow")
public class Cashflow extends BaseModel implements Serializable{

    private Integer borrowId;//借款编号

    private Integer returnDateNo;//还款日期编号(借款为0，还款从1开始)

    private Date tradeDate;//交易日期：借款日期/还款日期

    private BigDecimal monthPayment;//月供：借款金额/月供

    private BigDecimal principal;//月本金

    private BigDecimal interest;//月利息

    private BigDecimal manageFee;//月管理费

    private BigDecimal remainPrincipal;//剩余本金

    private BigDecimal paidPrincipal;//已还本金

    public Integer getBorrowId() {
        return borrowId;
    }

    public void setBorrowId(Integer borrowId) {
        this.borrowId = borrowId;
    }

    public Integer getReturnDateNo() {
        return returnDateNo;
    }

    public void setReturnDateNo(Integer returnDateNo) {
        this.returnDateNo = returnDateNo;
    }

    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public BigDecimal getMonthPayment() {
        return monthPayment;
    }

    public void setMonthPayment(BigDecimal monthPayment) {
        this.monthPayment = monthPayment;
    }

    public BigDecimal getPrincipal() {
        return principal;
    }

    public void setPrincipal(BigDecimal principal) {
        this.principal = principal;
    }

    public BigDecimal getInterest() {
        return interest;
    }

    public void setInterest(BigDecimal interest) {
        this.interest = interest;
    }

    public BigDecimal getManageFee() {
        return manageFee;
    }

    public void setManageFee(BigDecimal manageFee) {
        this.manageFee = manageFee;
    }

    public BigDecimal getRemainPrincipal() {
        return remainPrincipal;
    }

    public void setRemainPrincipal(BigDecimal remainPrincipal) {
        this.remainPrincipal = remainPrincipal;
    }

    public BigDecimal getPaidPrincipal() {
        return paidPrincipal;
    }

    public void setPaidPrincipal(BigDecimal paidPrincipal) {
        this.paidPrincipal = paidPrincipal;
    }

    @Override
    public String toString() {
        return "Cashflow{" +
                "borrowId=" + borrowId +
                "returnDateNo=" + returnDateNo +
                ", tradeDate=" + tradeDate +
                ", monthPayment=" + monthPayment +
                ", principal=" + principal +
                ", interest=" + interest +
                ", manageFee=" + manageFee +
                ", remainPrincipal=" + remainPrincipal +
                ", paidPrincipal=" + paidPrincipal +
                '}' + super.toString();
    }
}
