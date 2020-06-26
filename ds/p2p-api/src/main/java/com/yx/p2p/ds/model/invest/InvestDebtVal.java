package com.yx.p2p.ds.model.invest;

import com.yx.p2p.ds.model.base.BaseModel2;

import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @description:投资债权价值（Mycat）
 * @author: yx
 * @date: 2020/06/12/17:36
 */
@Table(name="p2p_invest_debt_val")
public class InvestDebtVal extends BaseModel2 implements Serializable {

    private Date arriveDate;//到账日期
    private Integer investId;//投资编号
    private Integer investCustomerId;//投资客户编号
    private String investCustomerName;//投资客户姓名
    private BigDecimal totalHoldAddAmt;//总持有增值金额
    private BigDecimal totalHoldReturnAmt;//总持有还款金额

    public Integer getInvestId() {
        return investId;
    }

    public void setInvestId(Integer investId) {
        this.investId = investId;
    }

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

    public BigDecimal getTotalHoldAddAmt() {
        return totalHoldAddAmt;
    }

    public void setTotalHoldAddAmt(BigDecimal totalHoldAddAmt) {
        this.totalHoldAddAmt = totalHoldAddAmt;
    }

    public Date getArriveDate() {
        return arriveDate;
    }

    public void setArriveDate(Date arriveDate) {
        this.arriveDate = arriveDate;
    }

    public BigDecimal getTotalHoldReturnAmt() {
        return totalHoldReturnAmt;
    }

    public void setTotalHoldReturnAmt(BigDecimal totalHoldReturnAmt) {
        this.totalHoldReturnAmt = totalHoldReturnAmt;
    }

    @Override
    public String toString() {
        return "InvestDebtVal{" +
                "arriveDate=" + arriveDate +
                ", investId=" + investId +
                ", investCustomerId=" + investCustomerId +
                ", investCustomerName='" + investCustomerName + '\'' +
                ", totalHoldAddAmt=" + totalHoldAddAmt +
                ", totalHoldReturnAmt=" + totalHoldReturnAmt +
                '}' + super.toString();
    }
}
