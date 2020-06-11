package com.yx.p2p.ds.model.invest;

import com.yx.p2p.ds.model.base.BaseModel;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @description:出借单
 * @author: yx
 * @date: 2020/04/21/12:39
 */
@Table(name="p2p_lending")
public class Lending extends BaseModel implements Serializable {

    private Integer customerId;//投资客户主键
    private Integer investId;//投资主键
    private BigDecimal amount;//金额
    private String lendingType;//出借单类型LendingTypeEnum:1-新出借，2-回款再出借
    private Date arriveDate;//到账时间
    private String orderSn;//投资充值业务单号，作为payment系统幂等性验证

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getInvestId() {
        return investId;
    }

    public void setInvestId(Integer investId) {
        this.investId = investId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getLendingType() {
        return lendingType;
    }

    public void setLendingType(String lendingType) {
        this.lendingType = lendingType;
    }

    public Date getArriveDate() {
        return arriveDate;
    }

    public void setArriveDate(Date arriveDate) {
        this.arriveDate = arriveDate;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    @Override
    public String toString() {
        return "Lending{" +
                "customerId=" + customerId +
                ", investId=" + investId +
                ", amount=" + amount +
                ", lendingType='" + lendingType + '\'' +
                ", arriveDate=" + arriveDate +
                ", orderSn='" + orderSn + '\'' +
                '}' + super.toString();
    }
}
