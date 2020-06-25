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
@Table(name="p2p_invest_claim")
public class InvestClaim extends BaseModel implements Serializable{

    //----------------撮合信息----------------
    private BigDecimal buyAmt;//买入金额
    private BigDecimal holdShare;//持有比例
    private Integer parentId;//父投资债权编号：新借款为0/转让为父编号
    private Integer investCustomerId;//投资客户编号
    private String investCustomerName;//投资客户姓名
    //----------------投资信息信息----------------
    private Integer investId;//投资编号
    private Integer lendingId;//出借单编号
    //----------------借款信息----------------
    private Integer borrowId;//借款编号
    private Integer borrowCustomerId;//借款客户编号
    private String borrowCustomerName;//借款客户姓名
    private Integer borrowProductId;//借款产品编号
    private String borrowProductName;//借款产品名称
    private BigDecimal borrowYearRate;//贷款年利率


    @Override
    public boolean equals(Object obj) {
        if(obj instanceof InvestClaim){
            InvestClaim investClaim = (InvestClaim) obj;
            if(investClaim.getId() == this.getId()){
                return true;
            }
        }
        return false;
    }

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

    public BigDecimal getBuyAmt() {
        return buyAmt;
    }

    public void setBuyAmt(BigDecimal buyAmt) {
        this.buyAmt = buyAmt;
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

    @Override
    public String toString() {
        return "InvestClaim{" +
                "buyAmt=" + buyAmt +
                ", holdShare=" + holdShare +
                ", parentId=" + parentId +
                ", investCustomerId=" + investCustomerId +
                ", investCustomerName='" + investCustomerName + '\'' +
                ", investId=" + investId +
                ", lendingId=" + lendingId +
                ", borrowId=" + borrowId +
                ", borrowCustomerId=" + borrowCustomerId +
                ", borrowCustomerName='" + borrowCustomerName + '\'' +
                ", borrowProductId=" + borrowProductId +
                ", borrowProductName='" + borrowProductName + '\'' +
                ", borrowYearRate=" + borrowYearRate +
                '}' + super.toString();
    }
}