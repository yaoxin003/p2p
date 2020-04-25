package com.yx.p2p.ds.mq;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/19/11:50
 */
public class InvestMQVo implements Serializable {

    private String bizId;//业务编号，如invest.id
    private String orderSn;//订单（系统前缀+年月日时分秒毫秒+时间戳）
    private Integer customerId;//客户编号
    private BigDecimal amount;//金额
    private String status;//ok,fail
    private String bankAccount;//银行账户
    private String baseBankName;//银行总行名称

    public static final String STATUS_OK = "ok";
    public static final String STATUS_FAIL = "fail";

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBaseBankName() {
        return baseBankName;
    }

    public void setBaseBankName(String baseBankName) {
        this.baseBankName = baseBankName;
    }

    @Override
    public String toString() {
        return "InvestMQVo{" +
                "bizId='" + bizId + '\'' +
                ", orderSn='" + orderSn + '\'' +
                ", customerId=" + customerId +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                ", bankAccount='" + bankAccount + '\'' +
                ", baseBankName='" + baseBankName + '\'' +
                '}';
    }
}
