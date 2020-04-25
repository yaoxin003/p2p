package com.yx.p2p.ds.model.invest;

import com.yx.p2p.ds.model.base.BaseModel;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @description:投资产品
 * @author: yx
 * @date: 2020/04/06/17:57
 */
@Table(name="p2p_invest_product")
public class InvestProduct extends BaseModel implements Serializable {
    private String name; //理财产品名称
    private BigDecimal beginAmt;//起投金额
    private BigDecimal yearIrr; //年化收益率
    private String productType; //产品类型：1-季度投,2-双季投,3-投资宝
    private String investType; //投资类型:1-固定期限,2-非固定期限
    private Short dayCount;//投资天数(投资类型:1-固定期限)/封闭期(投资类型:2-非固定期限)

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBeginAmt() {
        return beginAmt;
    }

    public void setBeginAmt(BigDecimal beginAmt) {
        this.beginAmt = beginAmt;
    }

    public BigDecimal getYearIrr() {
        return yearIrr;
    }

    public void setYearIrr(BigDecimal yearIrr) {
        this.yearIrr = yearIrr;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getInvestType() {
        return investType;
    }

    public void setInvestType(String investType) {
        this.investType = investType;
    }

    public Short getDayCount() {
        return dayCount;
    }

    public void setDayCount(Short dayCount) {
        this.dayCount = dayCount;
    }

    @Override
    public String toString() {
        return super.toString() + "InvestProduct{" +
                ", name='" + name + '\'' +
                ", beginAmt=" + beginAmt +
                ", yearIrr=" + yearIrr +
                ", productType='" + productType + '\'' +
                ", investType='" + investType + '\'' +
                ", dayCount=" + dayCount +
                '}';
    }
}