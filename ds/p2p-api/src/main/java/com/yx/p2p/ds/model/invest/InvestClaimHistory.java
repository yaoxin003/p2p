package com.yx.p2p.ds.model.invest;

import com.yx.p2p.ds.model.base.BaseModel;

import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @description:投资债权明细
 * @author: yx
 * @date: 2020/05/07/17:46
 */
@Table(name="p2p_invest_claim_history")
public class InvestClaimHistory extends BaseModel implements Serializable{

    private Integer investId;//投资编号
    private Integer lendingId;//出借单编号
    private Integer borrowId;//借款编号
    private Integer borrowCustomerId;//借款客户编号
    private String borrowCustomerName;//借款客户姓名
    private Integer investCustomerId;//投资客户编号
    private String investCustomerName;//投资客户姓名
    private BigDecimal buyAmt;//买入金额
    private BigDecimal claimAmt;//债权金额：会增值变化
    private BigDecimal holdShare;//持有比例
    private Integer borrowProductId;//借款产品编号
    private String borrowProductName;//借款产品名称
    private BigDecimal borrowYearRate;//贷款年利率
    private Integer parentId;//父投资债权编号：新借款为0/转让为父编号

    public Integer getInvestId() {
        return investId;
    }

    public void setInvestId(Integer investId) {
        this.investId = investId;
    }

    public Integer getLendingId() {
        return lendingId;
    }

    public void setLendingId(Integer lendingId) {
        this.lendingId = lendingId;
    }

    public Integer getBorrowId() {
        return borrowId;
    }

    public void setBorrowId(Integer borrowId) {
        this.borrowId = borrowId;
    }

    public Integer getBorrowCustomerId() {
        return borrowCustomerId;
    }

    public void setBorrowCustomerId(Integer borrowCustomerId) {
        this.borrowCustomerId = borrowCustomerId;
    }

    public String getBorrowCustomerName() {
        return borrowCustomerName;
    }

    public void setBorrowCustomerName(String borrowCustomerName) {
        this.borrowCustomerName = borrowCustomerName;
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

    public BigDecimal getBuyAmt() {
        return buyAmt;
    }

    public void setBuyAmt(BigDecimal buyAmt) {
        this.buyAmt = buyAmt;
    }

    public BigDecimal getClaimAmt() {
        return claimAmt;
    }

    public void setClaimAmt(BigDecimal claimAmt) {
        this.claimAmt = claimAmt;
    }

    public BigDecimal getHoldShare() {
        return holdShare;
    }

    public void setHoldShare(BigDecimal holdShare) {
        this.holdShare = holdShare;
    }

    public Integer getBorrowProductId() {
        return borrowProductId;
    }

    public void setBorrowProductId(Integer borrowProductId) {
        this.borrowProductId = borrowProductId;
    }

    public String getBorrowProductName() {
        return borrowProductName;
    }

    public void setBorrowProductName(String borrowProductName) {
        this.borrowProductName = borrowProductName;
    }

    public BigDecimal getBorrowYearRate() {
        return borrowYearRate;
    }

    public void setBorrowYearRate(BigDecimal borrowYearRate) {
        this.borrowYearRate = borrowYearRate;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    @Override
    public String toString() {
        return "InvestClaimHistory{" +
                "investId=" + investId +
                ", lendingId=" + lendingId +
                ", borrowId=" + borrowId +
                ", borrowCustomerId=" + borrowCustomerId +
                ", borrowCustomerName='" + borrowCustomerName + '\'' +
                ", investCustomerId=" + investCustomerId +
                ", investCustomerName='" + investCustomerName + '\'' +
                ", buyAmt=" + buyAmt +
                ", claimAmt=" + claimAmt +
                ", holdShare=" + holdShare +
                ", borrowProductId=" + borrowProductId +
                ", borrowProductName='" + borrowProductName + '\'' +
                ", borrowYearRate=" + borrowYearRate +
                ", parentId=" + parentId +
                '}' + super.toString();
    }
}
