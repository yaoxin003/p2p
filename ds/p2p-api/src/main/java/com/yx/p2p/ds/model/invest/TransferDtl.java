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
    private Integer borrowCustomerId;//借款客户编号
    private String borrowCustomerName;//借款客户姓名

    //----------------投资债权明细中的借款信息----------------
    private Integer borrowId;//借款编号
    private Integer borrowProductId;//借款产品编号
    private String borrowProductName;//借款产品名称
    private BigDecimal borrowYearRate;//贷款年利率

    //----------------撮合信息----------------
    private Integer investCustomerId;//投资客户编号
    private String investCustomerName;//投资客户姓名
    private BigDecimal buyAmt;//买入金额
    private BigDecimal holdShare;//持有比例

    //----------------投资信息信息----------------
    private Integer investId;//投资编号
    private Integer lendingId;//出借单编号

    //----------------借款信息----------------
    private String borrowCustomerIdCard;//借款客户身份证号
    private BigDecimal borrowAmt;//借款金额
    private Integer borrowMonthCount;//借款期限
    private BigDecimal borrowTotalBorrowFee;//总借款费用=总利息+总管理费=月供*借款月数-借款金额
    private BigDecimal borrowTotalInterest;//总利息=总借款费用*(月利率/月费率)
    private BigDecimal borrowTotalManageFee;//总管理费=总借款费用-总利息
    @JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd",timezone="GMT+8")
    private Date borrowStartDate;//借款开始日期
    @JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd",timezone="GMT+8")
    private Date borrowEndDate;//借款结束日期
    @JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd",timezone="GMT+8")
    private Date borrowFirstReturnDate;//首期还款日期
    private Integer borrowMonthReturnDay;//月还款日15/28
    private BigDecimal borrowMonthPayment;//月供=月本息+月管理费

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

    public String getBorrowCustomerIdCard() {
        return borrowCustomerIdCard;
    }

    public void setBorrowCustomerIdCard(String borrowCustomerIdCard) {
        this.borrowCustomerIdCard = borrowCustomerIdCard;
    }

    public BigDecimal getBorrowAmt() {
        return borrowAmt;
    }

    public void setBorrowAmt(BigDecimal borrowAmt) {
        this.borrowAmt = borrowAmt;
    }

    public Integer getBorrowMonthCount() {
        return borrowMonthCount;
    }

    public void setBorrowMonthCount(Integer borrowMonthCount) {
        this.borrowMonthCount = borrowMonthCount;
    }

    public BigDecimal getBorrowTotalBorrowFee() {
        return borrowTotalBorrowFee;
    }

    public void setBorrowTotalBorrowFee(BigDecimal borrowTotalBorrowFee) {
        this.borrowTotalBorrowFee = borrowTotalBorrowFee;
    }

    public BigDecimal getBorrowTotalInterest() {
        return borrowTotalInterest;
    }

    public void setBorrowTotalInterest(BigDecimal borrowTotalInterest) {
        this.borrowTotalInterest = borrowTotalInterest;
    }

    public BigDecimal getBorrowTotalManageFee() {
        return borrowTotalManageFee;
    }

    public void setBorrowTotalManageFee(BigDecimal borrowTotalManageFee) {
        this.borrowTotalManageFee = borrowTotalManageFee;
    }

    public Date getBorrowStartDate() {
        return borrowStartDate;
    }

    public void setBorrowStartDate(Date borrowStartDate) {
        this.borrowStartDate = borrowStartDate;
    }

    public Date getBorrowEndDate() {
        return borrowEndDate;
    }

    public void setBorrowEndDate(Date borrowEndDate) {
        this.borrowEndDate = borrowEndDate;
    }

    public Date getBorrowFirstReturnDate() {
        return borrowFirstReturnDate;
    }

    public void setBorrowFirstReturnDate(Date borrowFirstReturnDate) {
        this.borrowFirstReturnDate = borrowFirstReturnDate;
    }

    public Integer getBorrowMonthReturnDay() {
        return borrowMonthReturnDay;
    }

    public void setBorrowMonthReturnDay(Integer borrowMonthReturnDay) {
        this.borrowMonthReturnDay = borrowMonthReturnDay;
    }

    public BigDecimal getBorrowMonthPayment() {
        return borrowMonthPayment;
    }

    public void setBorrowMonthPayment(BigDecimal borrowMonthPayment) {
        this.borrowMonthPayment = borrowMonthPayment;
    }

    @Override
    public String toString() {
        return "TransferDtl{" +
                "transferId=" + transferId +
                ", investClaimId=" + investClaimId +
                ", borrowCustomerId=" + borrowCustomerId +
                ", borrowCustomerName='" + borrowCustomerName + '\'' +
                ", borrowId=" + borrowId +
                ", investCustomerId=" + investCustomerId +
                ", investCustomerName='" + investCustomerName + '\'' +
                ", borrowProductId=" + borrowProductId +
                ", borrowProductName='" + borrowProductName + '\'' +
                ", borrowYearRate=" + borrowYearRate +
                ", buyAmt=" + buyAmt +
                ", holdShare=" + holdShare +
                ", investId=" + investId +
                ", lendingId=" + lendingId +
                ", borrowCustomerIdCard='" + borrowCustomerIdCard + '\'' +
                ", borrowAmt=" + borrowAmt +
                ", borrowMonthCount=" + borrowMonthCount +
                ", borrowTotalBorrowFee=" + borrowTotalBorrowFee +
                ", borrowTotalInterest=" + borrowTotalInterest +
                ", borrowTotalManageFee=" + borrowTotalManageFee +
                ", borrowStartDate=" + borrowStartDate +
                ", borrowEndDate=" + borrowEndDate +
                ", borrowFirstReturnDate=" + borrowFirstReturnDate +
                ", borrowMonthReturnDay=" + borrowMonthReturnDay +
                ", borrowMonthPayment=" + borrowMonthPayment +
                '}';
    }
}
