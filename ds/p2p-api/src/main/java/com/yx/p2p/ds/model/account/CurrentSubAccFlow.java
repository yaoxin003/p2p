package com.yx.p2p.ds.model.account;

import com.yx.p2p.ds.model.base.BaseModel;

import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @description: 活期分户流水
 * @author: yx
 * @date: 2020/04/23/8:03
 */
@Table(name="p2p_current_sub_acc_flow")
public class CurrentSubAccFlow extends BaseModel implements Serializable{
    private Integer currentSubId ;//活期分户主键
    private Integer customerId; //客户编号
    private String bizId;//业务编号，如invest.id
    private String orderSn;//订单（系统前缀+年月日时分秒毫秒+时间戳）
    private BigDecimal amount; //金额
    private String remark;//备注

    public Integer getCurrentSubId() {
        return currentSubId;
    }

    public void setCurrentSubId(Integer currentSubId) {
        this.currentSubId = currentSubId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return super.toString() + "CurrentSubAccFlow{" +
                "currentSubId=" + currentSubId +
                ", customerId=" + customerId +
                ", amount=" + amount +
                ", bizId=" + bizId +
                ", orderSn=" + orderSn +
                ", remark='" + remark + '\'' +
                '}';
    }
}
