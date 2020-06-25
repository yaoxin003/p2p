package com.yx.p2p.ds.model.borrow;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yx.p2p.ds.model.base.BaseModel;
import org.springframework.format.annotation.DateTimeFormat;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @description:借款
 * @author: yx
 * @date: 2020/04/28/19:02
 */
@Table(name="p2p_borrow")
public class Borrow extends BaseModel implements Serializable {

    //----------------借款产品信息----------------
    private Integer borrowProductId;//借款产品编号
    private String borrowProductName;//借款产品名称
    private BigDecimal monthFeeRate;//月费率(扩大100倍)=月利率+月管理费率
    private BigDecimal monthRate; //月利率(扩大100倍)
    private BigDecimal monthManageRate; //月管理费率(扩大100倍)

    //----------------客户银行卡信息----------------
    private Integer customerBankId;//客户银行卡编号
    private String baseBankName;//银行总行名称
    private String bankCode;//银行编码(p2p_payment_base_bank表中bankCode字段)
    private String bankAccount;//银行账户
    private String phone;//绑定手机号

    //----------------客户信息----------------
    private Integer customerId;//客户编号
    private String customerName;//客户姓名
    private String customerIdCard;//客户身份证号

    //----------------借款信息----------------
    private BigDecimal borrowAmt;//借款金额

    private Integer borrowMonthCount;//借款期限

    private BigDecimal yearRate; //贷款年利率=月利率/100*12

    private BigDecimal totalBorrowFee;//总借款费用=总利息+总管理费=月供*借款月数-借款金额

    private BigDecimal totalInterest;//总利息=总借款费用*(月利率/月费率)

    private BigDecimal totalManageFee;//总管理费=总借款费用-总利息

    @DateTimeFormat(pattern = "yyyy-MM-dd")//主要是前后到后台的时间格式的转换
    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")//后台到前台的时间格式的转换
    private Date startDate;//借款开始日期

    @JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd",timezone="GMT+8")
    private Date endDate;//借款结束日期

    @JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd",timezone="GMT+8")
    private Date firstReturnDate;//首期还款日期

    private Integer monthReturnDay;//月还款日15/28

    private BigDecimal monthPayment;//月供=月本息+月管理费

    private BigDecimal monthPrincipalInterest;//月本息

    private BigDecimal monthManageFee;//月管理费

    public String getBaseBankName() {
        return baseBankName;
    }

    public void setBaseBankName(String baseBankName) {
        this.baseBankName = baseBankName;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public Integer getCustomerBankId() {
        return customerBankId;
    }

    public void setCustomerBankId(Integer customerBankId) {
        this.customerBankId = customerBankId;
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

    public String getCustomerIdCard() {
        return customerIdCard;
    }

    public void setCustomerIdCard(String customerIdCard) {
        this.customerIdCard = customerIdCard;
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

    public BigDecimal getYearRate() {
        return yearRate;
    }

    public void setYearRate(BigDecimal yearRate) {
        this.yearRate = yearRate;
    }

    public BigDecimal getMonthManageRate() {
        return monthManageRate;
    }

    public void setMonthManageRate(BigDecimal monthManageRate) {
        this.monthManageRate = monthManageRate;
    }

    public BigDecimal getTotalBorrowFee() {
        return totalBorrowFee;
    }

    public void setTotalBorrowFee(BigDecimal totalBorrowFee) {
        this.totalBorrowFee = totalBorrowFee;
    }

    public BigDecimal getTotalInterest() {
        return totalInterest;
    }

    public void setTotalInterest(BigDecimal totalInterest) {
        this.totalInterest = totalInterest;
    }

    public BigDecimal getTotalManageFee() {
        return totalManageFee;
    }

    public void setTotalManageFee(BigDecimal totalManageFee) {
        this.totalManageFee = totalManageFee;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getFirstReturnDate() {
        return firstReturnDate;
    }

    public void setFirstReturnDate(Date firstReturnDate) {
        this.firstReturnDate = firstReturnDate;
    }

    public Integer getMonthReturnDay() {
        return monthReturnDay;
    }

    public void setMonthReturnDay(Integer monthReturnDay) {
        this.monthReturnDay = monthReturnDay;
    }

    public BigDecimal getMonthPayment() {
        return monthPayment;
    }

    public void setMonthPayment(BigDecimal monthPayment) {
        this.monthPayment = monthPayment;
    }

    public BigDecimal getMonthPrincipalInterest() {
        return monthPrincipalInterest;
    }

    public void setMonthPrincipalInterest(BigDecimal monthPrincipalInterest) {
        this.monthPrincipalInterest = monthPrincipalInterest;
    }

    public BigDecimal getMonthManageFee() {
        return monthManageFee;
    }

    public void setMonthManageFee(BigDecimal monthManageFee) {
        this.monthManageFee = monthManageFee;
    }

    @Override
    public String toString() {
        return "Borrow{" +
                "borrowProductId=" + borrowProductId +
                ", borrowProductName='" + borrowProductName + '\'' +
                ", monthFeeRate=" + monthFeeRate +
                ", monthRate=" + monthRate +
                ", monthManageRate=" + monthManageRate +
                ", customerBankId=" + customerBankId +
                ", baseBankName='" + baseBankName + '\'' +
                ", bankCode='" + bankCode + '\'' +
                ", bankAccount='" + bankAccount + '\'' +
                ", phone='" + phone + '\'' +
                ", customerId=" + customerId +
                ", customerName='" + customerName + '\'' +
                ", customerIdCard='" + customerIdCard + '\'' +
                ", borrowAmt=" + borrowAmt +
                ", borrowMonthCount=" + borrowMonthCount +
                ", yearRate=" + yearRate +
                ", totalBorrowFee=" + totalBorrowFee +
                ", totalInterest=" + totalInterest +
                ", totalManageFee=" + totalManageFee +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", firstReturnDate=" + firstReturnDate +
                ", monthReturnDay=" + monthReturnDay +
                ", monthPayment=" + monthPayment +
                ", monthPrincipalInterest=" + monthPrincipalInterest +
                ", monthManageFee=" + monthManageFee +
                '}' + super.toString();
    }
}
