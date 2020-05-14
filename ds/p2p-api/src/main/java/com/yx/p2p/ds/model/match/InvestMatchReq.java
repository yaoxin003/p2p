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

    //----------------投资产品信息----------------
    private Integer productId;//产品编号
    private String productName;//产品姓名
    private BigDecimal yearIrr; //年化收益率

    //----------------客户信息----------------
    private Integer investCustomerId;//投资客户编号
    private String investCustomerName;//投资客户名称

    //----------------投资信息----------------
    private String investBizId;//业务编号，如invest.id
    private BigDecimal investAmt;//金额

    private String investOrderSn;//订单编号，如lending.id
    private BigDecimal waitAmt;//待撮合金额
    private Integer level;//优先级：值越大优先级越高
    private String remark;//备注

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof InvestMatchReq ){
            InvestMatchReq investMatchReq = (InvestMatchReq) obj;
            if(investMatchReq.getId() == this.getId()){
                return true;
            }
        }
        return false;
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

    public String getInvestBizId() {
        return investBizId;
    }

    public void setInvestBizId(String investBizId) {
        this.investBizId = investBizId;
    }

    public String getInvestOrderSn() {
        return investOrderSn;
    }

    public void setInvestOrderSn(String investOrderSn) {
        this.investOrderSn = investOrderSn;
    }

    public BigDecimal getInvestAmt() {
        return investAmt;
    }

    public void setInvestAmt(BigDecimal investAmt) {
        this.investAmt = investAmt;
    }

    public BigDecimal getWaitAmt() {
        return waitAmt;
    }

    public void setWaitAmt(BigDecimal waitAmt) {
        this.waitAmt = waitAmt;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "InvestMatchReq{" +
                "investCustomerId=" + investCustomerId +
                ", investCustomerName='" + investCustomerName + '\'' +
                ", investBizId='" + investBizId + '\'' +
                ", investOrderSn='" + investOrderSn + '\'' +
                ", investAmt=" + investAmt +
                ", waitAmt=" + waitAmt +
                ", productId=" + productId +
                ", productName='" + productName + '\'' +
                ", yearIrr=" + yearIrr +
                ", level=" + level +
                ", remark='" + remark + '\'' +
                '}' + super.toString();
    }
}
