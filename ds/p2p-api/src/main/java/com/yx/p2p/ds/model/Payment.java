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
    private String bizId;//业务编号，如invest.id
    private String orderSn;//订单（系统前缀+年月日时分秒毫秒+时间戳）
    private String systemSource;//系统来源
    private Integer customerId;//客户编号
    private String customerName;//客户姓名
    private String idCard;//身份证号码
    private String phone;//绑定手机号
    private String bankCode;//银行编号(p2p_payment_base_bank表中bankCode字段)
    private String bankAccount;//银行账户
    private BigDecimal amount;//金额

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
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

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    @Override
    public String toString() {
        return super.toString() + "Payment{" +
                "id=" + id +
                ", bizId='" + bizId + '\'' +
                ", orderSn='" + orderSn + '\'' +
                ", systemSource='" + systemSource + '\'' +
                ", customerId=" + customerId +
                ", customerName='" + customerName + '\'' +
                ", idCard='" + idCard + '\'' +
                ", phone='" + phone + '\'' +
                ", bankCode='" + bankCode + '\'' +
                ", bankAccount='" + bankAccount + '\'' +
                ", amount=" + amount +
                '}';
    }
}
