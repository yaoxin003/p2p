package com.yx.p2p.ds.model.match;

import com.yx.p2p.ds.model.base.BaseModel;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @description:投资撮合请求
 * @author: yx
 * @date: 2020/04/24/18:03
 */
@Table(name="p2p_invest_match_req")
public class InvestMatchReq extends BaseModel implements Serializable{

    private String bizId;//业务编号，如invest.id
    private String orderSn;//订单编号，如lending.id
    private BigDecimal amount;//金额
    private BigDecimal waitAmt;//待撮合金额
    private Integer productId;//产品编号
    private String productName;//产品姓名
    private BigDecimal yearIrr; //年化收益率
    private Integer level;//优先级
    private Integer customerId;//客户编号
    private String remark;//备注

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

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
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

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public BigDecimal getWaitAmt() {
        return waitAmt;
    }

    public void setWaitAmt(BigDecimal waitAmt) {
        this.waitAmt = waitAmt;
    }

    @Override
    public String toString() {
        return super.toString() + "InvestMatchReq{" +
                "bizId='" + bizId + '\'' +
                ", orderSn='" + orderSn + '\'' +
                ", amount=" + amount +
                ", waitAmt=" + waitAmt +
                ", productId='" + productId + '\'' +
                ", productName='" + productName + '\'' +
                ", yearIrr=" + yearIrr +
                ", level=" + level +
                ", customerId=" + customerId +
                ", remark='" + remark + '\'' +
                '}';
    }
}
