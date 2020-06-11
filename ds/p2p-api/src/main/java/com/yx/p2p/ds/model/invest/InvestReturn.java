package com.yx.p2p.ds.model.invest;

import com.yx.p2p.ds.model.base.BaseModel;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @description:
 * @author: yx
 * @date: 2020/06/04/9:03
 */
@Table(name="p2p_invest_return")
public class InvestReturn extends BaseModel implements Serializable {

    private Integer investCustomerId;//投资人客户编号
    private String investCustomerName;//投资人客户姓名
    private Integer investId;//投资编号
    private Date arriveDate;//还款到账时间
    private BigDecimal returnTotalAmt;//还款到账总金额

    public Integer getInvestCustomerId() {
        return investCustomerId;
    }

    public void setInvestCustomerId(Integer investCustomerId) {
        this.investCustomerId = investCustomerId;
    }

    public String getInvestCustomerName() {
        return investCustomerName;
    }

    public void setInvestCustomerName(String investCustomerName) {
        this.investCustomerName = investCustomerName;
    }

    public Integer getInvestId() {
        return investId;
    }

    public void setInvestId(Integer investId) {
        this.investId = investId;
    }

    public Date getArriveDate() {
        return arriveDate;
    }

    public void setArriveDate(Date arriveDate) {
        this.arriveDate = arriveDate;
    }

    public BigDecimal getReturnTotalAmt() {
        return returnTotalAmt;
    }

    public void setReturnTotalAmt(BigDecimal returnTotalAmt) {
        this.returnTotalAmt = returnTotalAmt;
    }

    @Override
    public String toString() {
        return "InvestReturn{" +
                "investCustomerId=" + investCustomerId +
                ", investCustomerName='" + investCustomerName + '\'' +
                ", investId=" + investId +
                ", arriveDate=" + arriveDate +
                ", returnTotalAmt=" + returnTotalAmt +
                '}' + super.toString();
    }
}
