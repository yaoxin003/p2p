package com.yx.p2p.ds.model.payment;

import com.yx.p2p.ds.model.base.BaseModel;

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
    private String bizId;//业务编号，如invest.id
    private String orderSn;//订单（系统前缀+年月日时分秒毫秒+时间戳）
    private String systemSource;//系统来源
    private BigDecimal amount;//金额
    private Integer type;//类型：PaymentTypeEnum投资充值，借款放款，转让提现
    private String remark;//备注 PaymentTypeEnum.codeDesc
    //客户信息
    private Integer customerId;//客户编号
    private String customerName;//客户姓名
    private String idCard;//身份证号码
    //银行卡信息
    private String phone;//绑定手机号
    private String bankCode;//银行编号(p2p_payment_base_bank表中bankCode字段)
    private String bankAccount;//银行账户
    private String baseBankName;//银行总行名称

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

    public String getBaseBankName() {
        return baseBankName;
    }

    public void setBaseBankName(String baseBankName) {
        this.baseBankName = baseBankName;
    }


    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "bizId='" + bizId + '\'' +
                ", orderSn='" + orderSn + '\'' +
                ", systemSource='" + systemSource + '\'' +
                ", amount=" + amount +
                ", type=" + type +
                ", remark='" + remark + '\'' +
                ", customerId=" + customerId +
                ", customerName='" + customerName + '\'' +
                ", idCard='" + idCard + '\'' +
                ", phone='" + phone + '\'' +
                ", bankCode='" + bankCode + '\'' +
                ", bankAccount='" + bankAccount + '\'' +
                ", baseBankName='" + baseBankName + '\'' +
                '}' + super.toString();
    }
}
