package com.yx.p2p.ds.model.invest;

import com.yx.p2p.ds.model.base.BaseModel2;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @description:投资债权价值明细（Mycat）
 * @author: yx
 * @date: 2020/06/12/17:36
 */
@Table(name="p2p_invest_debt_val_dtl")
public class InvestDebtValDtl extends BaseModel2 implements Serializable{
    private Date arriveDate;//到账日期
    private Integer investId;//投资编号
    private Integer borrowId;//借款编号
    private Integer investCustomerId;//投资客户编号
    private String investCustomerName;//投资客户姓名
    private BigDecimal holdShare;//持有比例
    private BigDecimal addAmt;//增值
    private BigDecimal holdAddAmt;//持有增值
    private BigDecimal returnAmt;//还款到账金额
    private BigDecimal holdReturnAmt;//持有还款到账金额
    private BigDecimal debtValue;//债权价值
    private BigDecimal holdDebtValue;//持有债权价值

    public Date getArriveDate() {
        return arriveDate;
    }

    public void setArriveDate(Date arriveDate) {
        this.arriveDate = arriveDate;
    }

    public Integer getInvestId() {
        return investId;
    }

    public void setInvestId(Integer investId) {
        this.investId = investId;
    }

    public Integer getBorrowId() {
        return borrowId;
    }

    public void setBorrowId(Integer borrowId) {
        this.borrowId = borrowId;
    }

    public Integer getInvestCustomerId() {
        return investCustomerId;
    }

    public void setInvestCustomerId(Integer investCustomerId) {
        this.investCustomerId = investCustomerId;
    }

    public String getInvestCustomerName() {
        return investCustomerName;
    }

    public void setInvestCustomerName(String investCustomerName) {
        this.investCustomerName = investCustomerName;
    }


    public BigDecimal getHoldShare() {
        return holdShare;
    }

    public void setHoldShare(BigDecimal holdShare) {
        this.holdShare = holdShare;
    }

    public BigDecimal getAddAmt() {
        return addAmt;
    }

    public void setAddAmt(BigDecimal addAmt) {
        this.addAmt = addAmt;
    }

    public BigDecimal getHoldAddAmt() {
        return holdAddAmt;
    }

    public void setHoldAddAmt(BigDecimal holdAddAmt) {
        this.holdAddAmt = holdAddAmt;
    }

    public BigDecimal getReturnAmt() {
        return returnAmt;
    }

    public void setReturnAmt(BigDecimal returnAmt) {
        this.returnAmt = returnAmt;
    }

    public BigDecimal getHoldReturnAmt() {
        return holdReturnAmt;
    }

    public void setHoldReturnAmt(BigDecimal holdReturnAmt) {
        this.holdReturnAmt = holdReturnAmt;
    }

    public BigDecimal getDebtValue() {
        return debtValue;
    }

    public void setDebtValue(BigDecimal debtValue) {
        this.debtValue = debtValue;
    }

    public BigDecimal getHoldDebtValue() {
        return holdDebtValue;
    }

    public void setHoldDebtValue(BigDecimal holdDebtValue) {
        this.holdDebtValue = holdDebtValue;
    }

    @Override
    public String toString() {
        return "InvestDebtValDtl{" +
                "arriveDate=" + arriveDate +
                ", investId=" + investId +
                ", borrowId=" + borrowId +
                ", investCustomerId=" + investCustomerId +
                ", investCustomerName='" + investCustomerName + '\'' +
                ", holdShare=" + holdShare +
                ", addAmt=" + addAmt +
                ", holdAddAmt=" + holdAddAmt +
                ", returnAmt=" + returnAmt +
                ", holdReturnAmt=" + holdReturnAmt +
                ", debtValue=" + debtValue +
                ", holdDebtValue=" + holdDebtValue +
                '}' + super.toString();
    }
}
