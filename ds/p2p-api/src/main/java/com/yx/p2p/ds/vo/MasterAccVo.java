package com.yx.p2p.ds.vo;

import com.yx.p2p.ds.model.account.MasterAcc;

import java.math.BigDecimal;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/22/15:25
 */
public class MasterAccVo extends MasterAcc{
    private BigDecimal  cashAmt;//现金户金额
    private BigDecimal currentAmt;//活期户金额
    private BigDecimal claimAmt;//债权户金额
    private BigDecimal debtAmt;//债务户金额
    private BigDecimal profitAmt;//收益户金额

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
        return "MasterAccVo{" +
                "cashAmt=" + cashAmt +
                ", currentAmt=" + currentAmt +
                ", claimAmt=" + claimAmt +
                ", debtAmt=" + debtAmt +
                ", profitAmt=" + profitAmt +
                '}';
    }
}
