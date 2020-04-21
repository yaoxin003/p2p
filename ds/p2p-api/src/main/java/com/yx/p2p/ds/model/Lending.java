package com.yx.p2p.ds.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @description:出借单
 * @author: yx
 * @date: 2020/04/21/12:39
 */
@Table(name="p2p_lending")
public class Lending extends BaseModel implements Serializable {

    @Id//使用tkmybatis.selectByPrimaryKey方法时需要该字段，否则会将所有字段都当做where条件
    @GeneratedValue(strategy = GenerationType.IDENTITY)//使用该注解，可以获得插入数据库的id
    private Integer id;
    private Integer customerId;//客户主键
    private Integer investId;//投资主键
    private BigDecimal amount;//金额
    private String lendingType;//出借单类型LendingTypeEnum:1-新出借，2-回款再出借

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getInvestId() {
        return investId;
    }

    public void setInvestId(Integer investId) {
        this.investId = investId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getLendingType() {
        return lendingType;
    }

    public void setLendingType(String lendingType) {
        this.lendingType = lendingType;
    }

    @Override
    public String toString() {
        return super.toString() + "Leading{" +
                "id=" + id +
                ", customerId=" + customerId +
                ", investId=" + investId +
                ", amount=" + amount +
                ", lendingType='" + lendingType + '\'' +
                '}';
    }
}
