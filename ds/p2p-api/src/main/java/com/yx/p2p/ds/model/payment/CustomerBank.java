package com.yx.p2p.ds.model.payment;

import com.yx.p2p.ds.model.base.BaseModel;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/08/17:16
 */
@Table(name="p2p_payment_customer_bank")
public class CustomerBank extends BaseModel implements Serializable{

    private Integer customerId;//客户编号
    private String baseBankName;//银行总行名称
    private String bankCode;//银行编码(p2p_payment_base_bank表中bankCode字段)
    private String bankAccount;//银行账户
    private String phone;//绑定手机号

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
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

    public String getBaseBankName() {
        return baseBankName;
    }

    public void setBaseBankName(String baseBankName) {
        this.baseBankName = baseBankName;
    }

    @Override
    public String toString() {
        return super.toString() + "CustomerBank{" +
                ", customerId=" + customerId +
                ", baseBankName='" + baseBankName + '\'' +
                ", bankCode='" + bankCode + '\'' +
                ", bankAccount='" + bankAccount + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
