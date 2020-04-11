package com.yx.p2p.ds.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/11/14:36
 */
@Table(name="p2p_payment")
public class Payment extends BaseModel implements Serializable {
    @Id//使用tkmybatis.selectByPrimaryKey方法时需要该字段，否则会将所有字段都当做where条件
    @GeneratedValue(strategy = GenerationType.IDENTITY)//使用该注解，可以获得插入数据库的id
    private Integer id;
    private Integer bizId;//业务编号
    private String systemSource;//系统来源
    private String bankCode;//银行编号(p2p_payment_base_bank表中bankCode字段)
    private String bankAccount;//银行账户
    private BigDecimal amount;//金额
    private String phone;//绑定手机号

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBizId() {
        return bizId;
    }

    public void setBizId(Integer bizId) {
        this.bizId = bizId;
    }

    public String getSystemSource() {
        return systemSource;
    }

    public void setSystemSource(String systemSource) {
        this.systemSource = systemSource;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return super.toString() + "Payment{" +
                "id=" + id +
                ", bizId=" + bizId +
                ", systemSource='" + systemSource + '\'' +
                ", bankCode='" + bankCode + '\'' +
                ", bankAccount='" + bankAccount + '\'' +
                ", amount=" + amount +
                ", phone='" + phone + '\'' +
                '}';
    }
}
