package com.yx.p2p.ds.model.invest;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yx.p2p.ds.model.base.BaseModel;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/06/17:56
 */
@Table(name="p2p_invest")
public class Invest extends BaseModel implements Serializable {

    private Integer customerBankId;//客户银行编号(p2p_payement_customer_bank表主键)
    private Integer investProductId;//投资产品编号(p2p_invest_product表主键)
    private Integer customerId;//客户编号
    private BigDecimal investAmt;//投资金额
    private BigDecimal profit;//收益
    @JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd",timezone="GMT+8")
    private Date startDate;//投资开始日期
    /**
     * 投资类型1-固定期限：非null，当前日期+1+InvestProduct.dayCount
     * 投资类型2-非固定期限：null
     */
    @JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd",timezone="GMT+8")
    private Date endDate;//投资到期日期

    public Integer getInvestProductId() {
        return investProductId;
    }

    public void setInvestProductId(Integer investProductId) {
        this.investProductId = investProductId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getInvestAmt() {
        return investAmt;
    }

    public void setInvestAmt(BigDecimal investAmt) {
        this.investAmt = investAmt;
    }

    public BigDecimal getProfit() {
        return profit;
    }

    public void setProfit(BigDecimal profit) {
        this.profit = profit;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer getCustomerBankId() {
        return customerBankId;
    }

    public void setCustomerBankId(Integer customerBankId) {
        this.customerBankId = customerBankId;
    }

    @Override
    public String toString() {
        return super.toString() + "Invest{" +
                ", customerBankId=" + customerBankId +
                ", investProductId=" + investProductId +
                ", customerId=" + customerId +
                ", investAmt=" + investAmt +
                ", profit=" + profit +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
