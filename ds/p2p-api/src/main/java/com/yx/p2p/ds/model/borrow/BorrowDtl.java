package com.yx.p2p.ds.model.borrow;

import com.yx.p2p.ds.model.base.BaseModel;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @description:借款明细（保存了借款和投资信息，数据来源于撮合和投资）
 * @author: yx
 * @date: 2020/05/04/8:20
 */
@Table(name="p2p_borrow_dtl")
public class BorrowDtl extends BaseModel implements Serializable{

    private Integer borrowId;//借款编号

    //----------------撮合信息：数据来源于撮合----------------
    private BigDecimal tradeAmt;//交易金额
    private BigDecimal matchShare;//撮合比例:交易金额/融资金额
    private String investBizId;//投资业务编号
    private String investOrderSn;//投资订单编号

    //----------------投资客户信息：数据来源于投资----------------
    private Integer customerId;//投资客户编号
    private String customerName;//投资客户名称
    private String customerIdCard;//身份证号码

    //----------------投资客户银行信息：数据来源于投资----------------
    private Integer customerBankId;//客户银行编号(p2p_payement_customer_bank表主键)
    private String baseBankName;//银行总行名称
    private String bankCode;//银行编码(p2p_payment_base_bank表中bankCode字段)
    private String bankAccount;//银行账户
    private String phone;//绑定手机号

    public Integer getBorrowId() {
        return borrowId;
    }

    public void setBorrowId(Integer borrowId) {
        this.borrowId = borrowId;
    }

    public BigDecimal getTradeAmt() {
        return tradeAmt;
    }

    public void setTradeAmt(BigDecimal tradeAmt) {
        this.tradeAmt = tradeAmt;
    }

    public BigDecimal getMatchShare() {
        return matchShare;
    }

    public void setMatchShare(BigDecimal matchShare) {
        this.matchShare = matchShare;
    }

    public String getInvestBizId() {
        return investBizId;
    }

    public void setInvestBizId(String investBizId) {
        this.investBizId = investBizId;
    }

    public String getInvestOrderSn() {
        return investOrderSn;
    }

    public void setInvestOrderSn(String investOrderSn) {
        this.investOrderSn = investOrderSn;
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

    public Integer getCustomerBankId() {
        return customerBankId;
    }

    public void setCustomerBankId(Integer customerBankId) {
        this.customerBankId = customerBankId;
    }

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

    @Override
    public String toString() {
        return "BorrowDtl{" +
                "borrowId=" + borrowId +
                ", tradeAmt=" + tradeAmt +
                ", matchShare=" + matchShare +
                ", investBizId='" + investBizId + '\'' +
                ", investOrderSn='" + investOrderSn + '\'' +
                ", customerId=" + customerId +
                ", customerName='" + customerName + '\'' +
                ", customerIdCard='" + customerIdCard + '\'' +
                ", customerBankId=" + customerBankId +
                ", baseBankName='" + baseBankName + '\'' +
                ", bankCode='" + bankCode + '\'' +
                ", bankAccount='" + bankAccount + '\'' +
                ", phone='" + phone + '\'' +
                '}' + super.toString();
    }
}
