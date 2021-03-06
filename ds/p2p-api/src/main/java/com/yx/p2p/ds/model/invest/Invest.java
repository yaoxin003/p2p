package com.yx.p2p.ds.model.invest;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yx.p2p.ds.model.base.BaseModel;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/06/17:56
 */
@Table(name="p2p_invest")
public class Invest extends BaseModel implements Serializable {

    //----------------投资信息----------------
    private BigDecimal investAmt;//投资金额
    private BigDecimal profit;//收益
    @JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd",timezone="GMT+8")
    private Date startDate;//投资开始日期
    /**
     * 投资类型1-固定期限：非null，当前日期+1+InvestProduct.dayCount
     * 投资类型2-非固定期限：null
     */
    @JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd",timezone="GMT+8")
    private Date endDate;//投资到期日期

    //----------------投资产品信息----------------
    private Integer investProductId;//投资产品编号(p2p_invest_product表主键)
    private String investProductName; //理财产品名称
    private BigDecimal investYearIrr; //年化收益率
    private String investType; //投资类型:1-固定期限,2-非固定期限
    private Short investDayCount;//投资天数(投资类型:1-固定期限)/封闭期(投资类型:2-非固定期限)

    //----------------客户信息----------------
    private Integer customerId;//投资客户编号
    private String customerName;//投资客户姓名
    private String customerIdCard;//投资客户身份证号

    //----------------客户银行卡信息----------------
    private Integer customerBankId;//客户银行编号(p2p_payement_customer_bank表主键)
    private String baseBankName;//银行总行名称
    private String bankCode;//银行编码(p2p_payment_base_bank表中bankCode字段)
    private String bankAccount;//银行账户
    private String phone;//绑定手机号

    public Integer getInvestProductId() {
        return investProductId;
    }

    public void setInvestProductId(Integer investProductId) {
        this.investProductId = investProductId;
    }

    public String getInvestProductName() {
        return investProductName;
    }

    public void setInvestProductName(String investProductName) {
        this.investProductName = investProductName;
    }

    public BigDecimal getInvestYearIrr() {
        return investYearIrr;
    }

    public void setInvestYearIrr(BigDecimal investYearIrr) {
        this.investYearIrr = investYearIrr;
    }

    public String getInvestType() {
        return investType;
    }

    public void setInvestType(String investType) {
        this.investType = investType;
    }

    public Short getInvestDayCount() {
        return investDayCount;
    }

    public void setInvestDayCount(Short investDayCount) {
        this.investDayCount = investDayCount;
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

    public BigDecimal getInvestAmt() {
        return investAmt;
    }

    public void setInvestAmt(BigDecimal investAmt) {
        this.investAmt = investAmt;
    }

    public BigDecimal getProfit() {
        return profit;
    }

    public void setProfit(BigDecimal profit) {
        this.profit = profit;
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

    @Override
    public String toString() {
        return "Invest{" +
                "investProductId=" + investProductId +
                ", investProductName='" + investProductName + '\'' +
                ", investYearIrr=" + investYearIrr +
                ", investType='" + investType + '\'' +
                ", investDayCount=" + investDayCount +
                ", customerBankId=" + customerBankId +
                ", baseBankName='" + baseBankName + '\'' +
                ", bankCode='" + bankCode + '\'' +
                ", bankAccount='" + bankAccount + '\'' +
                ", phone='" + phone + '\'' +
                ", customerId=" + customerId +
                ", customerName='" + customerName + '\'' +
                ", customerIdCard='" + customerIdCard + '\'' +
                ", investAmt=" + investAmt +
                ", profit=" + profit +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}' + super.toString();
    }
}
