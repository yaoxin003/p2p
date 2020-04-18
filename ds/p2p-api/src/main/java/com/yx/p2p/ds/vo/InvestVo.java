package com.yx.p2p.ds.vo;

import com.yx.p2p.ds.model.Invest;

import java.math.BigDecimal;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/11/17:13
 */
public class InvestVo extends Invest {
    private String bankCode;//银行编码(p2p_payment_base_bank表中bankCode字段)
    private String bankAccount;//银行账户
    private String phone;//绑定手机号

    private String productName;//投资产品名称
    private BigDecimal yearIrr; //年化收益率
    private Short dayCount;//投资天数(投资类型:1-固定期限)/封闭期(投资类型:2-非固定期限)
    private String investType; //投资类型:1-固定期限,2-非固定期限

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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getYearIrr() {
        return yearIrr;
    }

    public void setYearIrr(BigDecimal yearIrr) {
        this.yearIrr = yearIrr;
    }

    public Short getDayCount() {
        return dayCount;
    }

    public void setDayCount(Short dayCount) {
        this.dayCount = dayCount;
    }

    public String getInvestType() {
        return investType;
    }

    public void setInvestType(String investType) {
        this.investType = investType;
    }

    @Override
    public String toString() {
        return super.toString() + "InvestVo{" +
                "bankCode='" + bankCode + '\'' +
                ", bankAccount='" + bankAccount + '\'' +
                ", phone='" + phone + '\'' +
                ", productName='" + productName + '\'' +
                ", yearIrr=" + yearIrr +
                ", dayCount=" + dayCount +
                ", investType='" + investType + '\'' +
                '}';
    }
}
