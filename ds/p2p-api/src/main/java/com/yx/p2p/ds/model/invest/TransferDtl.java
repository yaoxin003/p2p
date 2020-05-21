package com.yx.p2p.ds.model.invest;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yx.p2p.ds.model.base.BaseModel;

import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @description:转让协议明细
 * @author: yx
 * @date: 2020/05/09/13:39
 */
@Table(name="p2p_transfer_dtl")
public class TransferDtl extends BaseModel implements Serializable{

    //----------------转让协议编号----------------
    private Integer transferId;

    //----------------投资债权明细----------------
    private Integer investClaimId;//投资债权明细编号

    //----------------撮合信息----------------
    private BigDecimal buyAmt;//买入金额
    private BigDecimal holdShare;//持有比例

    //----------------投资信息信息----------------
    private Integer investId;//投资编号
    private Integer lendingId;//出借单编号


    //----------------借款信息----------------
    private Integer borrowId;//借款编号
    private Integer customerId;//借款客户编号
    private String customerName;//借款客户姓名
    private Integer borrowProductId;//借款产品编号
    private String borrowProductName;//借款产品名称
    private BigDecimal borrowYearRate;//贷款年利率

    public Integer getTransferId() {
        return transferId;
    }

    public void setTransferId(Integer transferId) {
        this.transferId = transferId;
    }

    public Integer getInvestClaimId() {
        return investClaimId;
    }

    public void setInvestClaimId(Integer investClaimId) {
        this.investClaimId = investClaimId;
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

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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

    @Override
    public String toString() {
        return "TransferDtl{" +
                "transferId=" + transferId +
                ", investClaimId=" + investClaimId +
                ", investId=" + investId +
                ", lendingId=" + lendingId +
                ", borrowId=" + borrowId +
                ", customerId=" + customerId +
                ", customerName='" + customerName + '\'' +
                ", buyAmt=" + buyAmt +
                ", holdShare=" + holdShare +
                ", borrowProductId=" + borrowProductId +
                ", borrowProductName='" + borrowProductName + '\'' +
                ", borrowYearRate=" + borrowYearRate +
                '}' + super.toString();
    }
}
