package com.yx.p2p.ds.model.account;

import com.yx.p2p.ds.model.base.BaseModel;

import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @description: 主账户
 * @author: yx
 * @date: 2020/04/22/9:36
 */
@Table(name="p2p_master_acc")
public class MasterAcc extends BaseModel implements Serializable {

    private Integer customerId;//客户编号
    private String customerName;//客户姓名
    private String idCard;//身份证号码
    private BigDecimal cashAmt;//现金金额
    private BigDecimal currentAmt;//活期金额
    private BigDecimal claimAmt;//债权金额
    private BigDecimal debtAmt;//债务金额
    private BigDecimal profitAmt;//收益金额

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
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

    public BigDecimal getCashAmt() {
        return cashAmt;
    }

    public void setCashAmt(BigDecimal cashAmt) {
        this.cashAmt = cashAmt;
    }

    public BigDecimal getCurrentAmt() {
        return currentAmt;
    }

    public void setCurrentAmt(BigDecimal currentAmt) {
        this.currentAmt = currentAmt;
    }

    public BigDecimal getClaimAmt() {
        return claimAmt;
    }

    public void setClaimAmt(BigDecimal claimAmt) {
        this.claimAmt = claimAmt;
    }

    public BigDecimal getDebtAmt() {
        return debtAmt;
    }

    public void setDebtAmt(BigDecimal debtAmt) {
        this.debtAmt = debtAmt;
    }

    public BigDecimal getProfitAmt() {
        return profitAmt;
    }

    public void setProfitAmt(BigDecimal profitAmt) {
        this.profitAmt = profitAmt;
    }

    @Override
    public String toString() {
        return "MasterAcc{" +
                "customerId=" + customerId +
                ", customerName='" + customerName + '\'' +
                ", idCard='" + idCard + '\'' +
                ", cashAmt=" + cashAmt +
                ", currentAmt=" + currentAmt +
                ", claimAmt=" + claimAmt +
                ", debtAmt=" + debtAmt +
                ", profitAmt=" + profitAmt +
                '}';
    }
}
