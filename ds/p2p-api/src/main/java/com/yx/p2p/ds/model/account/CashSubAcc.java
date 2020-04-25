package com.yx.p2p.ds.model.account;

import com.yx.p2p.ds.model.base.BaseModel;

import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @description:现金子账户
 * @author: yx
 * @date: 2020/04/22/17:21
 */
@Table(name="p2p_cash_sub_acc")
public class CashSubAcc extends BaseModel implements Serializable {
    private Integer masterAccId ;//主账户主键
    private Integer customerId; //客户编号
    private String bizId;//业务编号，如invest.id
    private BigDecimal amount; //金额

    public Integer getMasterAccId() {
        return masterAccId;
    }

    public void setMasterAccId(Integer masterAccId) {
        this.masterAccId = masterAccId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    @Override
    public String toString() {
        return super.toString() + "CashSubAcc{" +
                "masterAccId=" + masterAccId +
                ", customerId=" + customerId +
                ", bizId=" + bizId +
                ", amount=" + amount +
                '}';
    }
}
