package com.yx.p2p.ds.model.invest;

import com.yx.p2p.ds.model.base.BaseModel;

import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @description:
 * @author: yx
 * @date: 2020/06/04/9:03
 */
@Table(name="p2p_invest_return_dtl")
public class InvestReturnDtl extends BaseModel implements Serializable {

    private Integer investId;//投资编号
    private Integer borrowId;//借款编号
    private Date arriveDate;//还款到账时间
    private BigDecimal returnAmt;//还款金额
    private BigDecimal holdReturnAmt;//持有还款金额
    private BigDecimal holdShare;//持有比例
    private Integer investCustomerId;//投资人客户编号
    private String investCustomerName;//投资人客户姓名

    public Integer getInvestId() {
        return investId;
    }

    public void setInvestId(Integer investId) {
        this.investId = investId;
    }

    public Date getArriveDate() {
        return arriveDate;
    }

    public void setArriveDate(Date arriveDate) {
        this.arriveDate = arriveDate;
    }

    public Integer getBorrowId() {
        return borrowId;
    }

    public void setBorrowId(Integer borrowId) {
        this.borrowId = borrowId;
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

    public BigDecimal getHoldShare() {
        return holdShare;
    }

    public void setHoldShare(BigDecimal holdShare) {
        this.holdShare = holdShare;
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

    @Override
    public String toString() {
        return "InvestReturnDtl{" +
                "investId=" + investId +
                ", borrowId=" + borrowId +
                ", arriveDate=" + arriveDate +
                ", returnAmt=" + returnAmt +
                ", holdReturnAmt=" + holdReturnAmt +
                ", holdShare=" + holdShare +
                ", investCustomerId=" + investCustomerId +
                ", investCustomerName='" + investCustomerName + '\'' +
                '}' + super.toString();
    }
}
