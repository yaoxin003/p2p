package com.yx.p2p.ds.model.match;

import com.yx.p2p.ds.model.base.BaseModel;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @description:融资撮合结果:借款/转让
 * @author: yx
 * @date: 2020/04/29/10:43
 */
@Table(name="p2p_finance_match_res")
public class FinanceMatchRes extends BaseModel implements Serializable{

    private Integer investCustomerId;//投资客户编号
    private String investCustomerName;//投资客户名称

    private Integer financeCustomerId;//融资客户编号
    private String financeCustomerName;//融资客户名称

    private Integer investMatchId;//投资撮合编号

    private Integer financeMatchId;//融资撮合编号

    private String investBizId;//投资业务编号

    private String financeBizId;//融资业务编号:借款编号/转让协议编号

    private String investOrderSn;//投资订单编号

    private String financeOrderSn;//融资订单编号:借款编号/投资持有债权编号

    private BigDecimal tradeAmt;//交易金额

    private BigDecimal financeAmt;//融资金额:借款金额/转让协议明细金额

    private BigDecimal matchShare;//撮合比例:交易金额/融资金额

    private Integer borrowProductId;//借款产品编号

    private BigDecimal borrowYearRate;//贷款年利率

    private String borrowProductName;//借款产品名称

    private String remark;//备注:借款/转让

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

    public Integer getFinanceCustomerId() {
        return financeCustomerId;
    }

    public void setFinanceCustomerId(Integer financeCustomerId) {
        this.financeCustomerId = financeCustomerId;
    }

    public String getFinanceCustomerName() {
        return financeCustomerName;
    }

    public void setFinanceCustomerName(String financeCustomerName) {
        this.financeCustomerName = financeCustomerName;
    }

    public Integer getInvestMatchId() {
        return investMatchId;
    }

    public void setInvestMatchId(Integer investMatchId) {
        this.investMatchId = investMatchId;
    }

    public Integer getFinanceMatchId() {
        return financeMatchId;
    }

    public void setFinanceMatchId(Integer financeMatchId) {
        this.financeMatchId = financeMatchId;
    }

    public String getInvestBizId() {
        return investBizId;
    }

    public void setInvestBizId(String investBizId) {
        this.investBizId = investBizId;
    }

    public String getFinanceBizId() {
        return financeBizId;
    }

    public void setFinanceBizId(String financeBizId) {
        this.financeBizId = financeBizId;
    }

    public String getInvestOrderSn() {
        return investOrderSn;
    }

    public void setInvestOrderSn(String investOrderSn) {
        this.investOrderSn = investOrderSn;
    }

    public String getFinanceOrderSn() {
        return financeOrderSn;
    }

    public void setFinanceOrderSn(String financeOrderSn) {
        this.financeOrderSn = financeOrderSn;
    }

    public BigDecimal getTradeAmt() {
        return tradeAmt;
    }

    public void setTradeAmt(BigDecimal tradeAmt) {
        this.tradeAmt = tradeAmt;
    }

    public BigDecimal getFinanceAmt() {
        return financeAmt;
    }

    public void setFinanceAmt(BigDecimal financeAmt) {
        this.financeAmt = financeAmt;
    }

    public BigDecimal getMatchShare() {
        return matchShare;
    }

    public void setMatchShare(BigDecimal matchShare) {
        this.matchShare = matchShare;
    }

    public Integer getBorrowProductId() {
        return borrowProductId;
    }

    public void setBorrowProductId(Integer borrowProductId) {
        this.borrowProductId = borrowProductId;
    }

    public BigDecimal getBorrowYearRate() {
        return borrowYearRate;
    }

    public void setBorrowYearRate(BigDecimal borrowYearRate) {
        this.borrowYearRate = borrowYearRate;
    }

    public String getBorrowProductName() {
        return borrowProductName;
    }

    public void setBorrowProductName(String borrowProductName) {
        this.borrowProductName = borrowProductName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "FinanceMatchRes{" +
                "investCustomerId=" + investCustomerId +
                ", investCustomerName='" + investCustomerName + '\'' +
                ", financeCustomerId=" + financeCustomerId +
                ", financeCustomerName='" + financeCustomerName + '\'' +
                ", investMatchId=" + investMatchId +
                ", financeMatchId=" + financeMatchId +
                ", investBizId='" + investBizId + '\'' +
                ", financeBizId='" + financeBizId + '\'' +
                ", investOrderSn='" + investOrderSn + '\'' +
                ", financeOrderSn='" + financeOrderSn + '\'' +
                ", tradeAmt=" + tradeAmt +
                ", financeAmt=" + financeAmt +
                ", matchShare=" + matchShare +
                ", borrowProductId=" + borrowProductId +
                ", borrowYearRate=" + borrowYearRate +
                ", borrowProductName='" + borrowProductName + '\'' +
                ", remark='" + remark + '\'' +
                '}' + super.toString();
    }
}
