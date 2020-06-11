package com.yx.p2p.ds.model.invest;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yx.p2p.ds.model.base.BaseModel;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @description:转让协议
 * @author: yx
 * @date: 2020/05/09/13:37
 */
@Table(name="p2p_transfer")
public class Transfer extends BaseModel implements Serializable{

    //----------------转让信息----------------
    private BigDecimal transferAmt;//转让金额（现金+债权）
    private BigDecimal cashAmt;//现金
    private BigDecimal claimAmt;//债权金额
    private BigDecimal expressFee;//加急费
    private BigDecimal discountFee;//折扣费
    private BigDecimal serviceFee;//服务费
    private BigDecimal redeemAmt;//赎回金额：投资类型为固定期限=投资金额+收益；投资类型为非固定期限=转让金额-加急费-服务费

    //----------------投资产品信息----------------
    private Integer investId;//投资编号invest.id
    private Integer investProductId;//投资产品编号(p2p_invest_product表主键)
    private String investProductName; //理财产品名称
    private BigDecimal investYearIrr; //年化收益率
    private String investType; //投资类型:1-固定期限,2-非固定期限
    private Short investDayCount;//投资天数(投资类型:1-固定期限)/封闭期(投资类型:2-非固定期限)

    //----------------客户信息----------------
    private Integer customerId;//融资客户编号
    private String customerName;//融资客户姓名
    private String customerIdCard;//融资客户身份证号

    //----------------客户银行卡信息----------------
    private Integer customerBankId;//客户银行编号(p2p_payement_customer_bank表主键)
    private String baseBankName;//银行总行名称
    private String bankCode;//银行编码(p2p_payment_base_bank表中bankCode字段)
    private String bankAccount;//银行账户
    private String phone;//绑定手机号

    //----------------投资信息----------------
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

    //----------------转让协议明细列表----------------
    @Transient
    private List<TransferDtl> transferDtlList;

    public BigDecimal getTransferAmt() {
        return transferAmt;
    }

    public void setTransferAmt(BigDecimal transferAmt) {
        this.transferAmt = transferAmt;
    }

    public BigDecimal getCashAmt() {
        return cashAmt;
    }

    public void setCashAmt(BigDecimal cashAmt) {
        this.cashAmt = cashAmt;
    }

    public BigDecimal getClaimAmt() {
        return claimAmt;
    }

    public void setClaimAmt(BigDecimal claimAmt) {
        this.claimAmt = claimAmt;
    }

    public BigDecimal getExpressFee() {
        return expressFee;
    }

    public void setExpressFee(BigDecimal expressFee) {
        this.expressFee = expressFee;
    }

    public BigDecimal getDiscountFee() {
        return discountFee;
    }

    public void setDiscountFee(BigDecimal discountFee) {
        this.discountFee = discountFee;
    }

    public BigDecimal getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(BigDecimal serviceFee) {
        this.serviceFee = serviceFee;
    }

    public Integer getInvestId() {
        return investId;
    }

    public void setInvestId(Integer investId) {
        this.investId = investId;
    }

    public Integer getInvestProductId() {
        return investProductId;
    }

    public void setInvestProductId(Integer investProductId) {
        this.investProductId = investProductId;
    }

    public String getInvestProductName() {
        return investProductName;
    }

    public void setInvestProductName(String investProductName) {
        this.investProductName = investProductName;
    }

    public BigDecimal getInvestYearIrr() {
        return investYearIrr;
    }

    public void setInvestYearIrr(BigDecimal investYearIrr) {
        this.investYearIrr = investYearIrr;
    }

    public String getInvestType() {
        return investType;
    }

    public void setInvestType(String investType) {
        this.investType = investType;
    }

    public Short getInvestDayCount() {
        return investDayCount;
    }

    public void setInvestDayCount(Short investDayCount) {
        this.investDayCount = investDayCount;
    }

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

    public String getCustomerIdCard() {
        return customerIdCard;
    }

    public void setCustomerIdCard(String customerIdCard) {
        this.customerIdCard = customerIdCard;
    }

    public Integer getCustomerBankId() {
        return customerBankId;
    }

    public void setCustomerBankId(Integer customerBankId) {
        this.customerBankId = customerBankId;
    }

    public String getBaseBankName() {
        return baseBankName;
    }

    public void setBaseBankName(String baseBankName) {
        this.baseBankName = baseBankName;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public BigDecimal getRedeemAmt() {
        return redeemAmt;
    }

    public void setRedeemAmt(BigDecimal redeemAmt) {
        this.redeemAmt = redeemAmt;
    }

    public List<TransferDtl> getTransferDtlList() {
        return transferDtlList;
    }

    public void setTransferDtlList(List<TransferDtl> transferDtlList) {
        this.transferDtlList = transferDtlList;
    }

    @Override
    public String toString() {
        return "Transfer{" +
                "transferAmt=" + transferAmt +
                ", cashAmt=" + cashAmt +
                ", claimAmt=" + claimAmt +
                ", expressFee=" + expressFee +
                ", discountFee=" + discountFee +
                ", serviceFee=" + serviceFee +
                ", redeemAmt=" + redeemAmt +
                ", investId=" + investId +
                ", investProductId=" + investProductId +
                ", investProductName='" + investProductName + '\'' +
                ", investYearIrr=" + investYearIrr +
                ", investType='" + investType + '\'' +
                ", investDayCount=" + investDayCount +
                ", customerId=" + customerId +
                ", customerName='" + customerName + '\'' +
                ", customerIdCard='" + customerIdCard + '\'' +
                ", customerBankId=" + customerBankId +
                ", baseBankName='" + baseBankName + '\'' +
                ", bankCode='" + bankCode + '\'' +
                ", bankAccount='" + bankAccount + '\'' +
                ", phone='" + phone + '\'' +
                ", investAmt=" + investAmt +
                ", profit=" + profit +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", transferDtlList=" + transferDtlList +
                '}';
    }
}
