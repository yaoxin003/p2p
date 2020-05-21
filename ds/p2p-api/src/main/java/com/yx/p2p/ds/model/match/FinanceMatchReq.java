package com.yx.p2p.ds.model.match;

import com.yx.p2p.ds.model.base.BaseModel;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @description:融资撮合请求:借款/转让
 * @author: yx
 * @date: 2020/04/30/14:40
 */
@Table(name="p2p_finance_match_req")
public class FinanceMatchReq extends BaseModel implements Serializable{

    private Integer financeCustomerId;//融资客户编号

    private String financeCustomerName;//融资客户名称

    private String financeBizId;//融资业务编号:借款编号/转让协议编号

    private String financeExtBizId;//融资扩展业务编号:借款编号/转让投资编号（撮合系统不使用该字段，给账户系统和投资系统使用）

    private String financeOrderSn;//融资订单编号:借款编号/转让协议明细编号

    private BigDecimal financeAmt;//融资金额:借款金额/转让协议明细金额

    private BigDecimal waitAmt;//待撮合金额

    private Integer level;//优先级:值越大优先级越高

    private Integer borrowProductId;//借款产品编号

    private String borrowProductName;//借款产品名称

    private BigDecimal borrowYearRate;//贷款年利率

    private Integer type;//融资撮合类型：1-借款，2-转让

    private String remark;//备注:借款/转让

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

    public String getFinanceBizId() {
        return financeBizId;
    }

    public void setFinanceBizId(String financeBizId) {
        this.financeBizId = financeBizId;
    }

    public String getFinanceExtBizId() {
        return financeExtBizId;
    }

    public void setFinanceExtBizId(String financeExtBizId) {
        this.financeExtBizId = financeExtBizId;
    }

    public String getFinanceOrderSn() {
        return financeOrderSn;
    }

    public void setFinanceOrderSn(String financeOrderSn) {
        this.financeOrderSn = financeOrderSn;
    }

    public BigDecimal getFinanceAmt() {
        return financeAmt;
    }

    public void setFinanceAmt(BigDecimal financeAmt) {
        this.financeAmt = financeAmt;
    }

    public BigDecimal getWaitAmt() {
        return waitAmt;
    }

    public void setWaitAmt(BigDecimal waitAmt) {
        this.waitAmt = waitAmt;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getBorrowProductId() {
        return borrowProductId;
    }

    public void setBorrowProductId(Integer borrowProductId) {
        this.borrowProductId = borrowProductId;
    }

    public String getBorrowProductName() {
        return borrowProductName;
    }

    public void setBorrowProductName(String borrowProductName) {
        this.borrowProductName = borrowProductName;
    }

    public BigDecimal getBorrowYearRate() {
        return borrowYearRate;
    }

    public void setBorrowYearRate(BigDecimal borrowYearRate) {
        this.borrowYearRate = borrowYearRate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "FinanceMatchReq{" +
                "financeCustomerId=" + financeCustomerId +
                ", financeCustomerName=" + financeCustomerName +
                ", financeBizId='" + financeBizId + '\'' +
                ", financeExtBizId='" + financeExtBizId + '\'' +
                ", financeOrderSn='" + financeOrderSn + '\'' +
                ", financeAmt=" + financeAmt +
                ", waitAmt=" + waitAmt +
                ", level=" + level +
                ", borrowProductId=" + borrowProductId +
                ", borrowProductName='" + borrowProductName + '\'' +
                ", borrowYearRate=" + borrowYearRate +
                ", type=" + type +
                ", remark='" + remark + '\'' +
                '}' + super.toString();
    }
}
