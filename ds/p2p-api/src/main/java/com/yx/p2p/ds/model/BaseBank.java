package com.yx.p2p.ds.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/08/17:13
 */
@Table(name="p2p_payment_base_bank")
public class BaseBank extends BaseModel implements Serializable {
    @Id//使用tkmybatis.selectByPrimaryKey方法时需要该字段，否则会将所有字段都当做where条件
    @GeneratedValue(strategy = GenerationType.IDENTITY)//使用该注解，可以获得插入数据库的id
    private Integer id;
    private String name;//银行名称
    private String bankCode;//银行编号

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    @Override
    public String toString() {
        return super.toString() + "BaseBank{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", bankCode='" + bankCode + '\'' +
                '}';
    }
}
