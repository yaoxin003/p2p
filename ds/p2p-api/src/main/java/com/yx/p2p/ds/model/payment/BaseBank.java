package com.yx.p2p.ds.model.payment;

import com.yx.p2p.ds.model.base.BaseModel;

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

    private String name;//银行总行名称
    private String bankCode;//银行编码

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
                ", name='" + name + '\'' +
                ", bankCode='" + bankCode + '\'' +
                '}';
    }
}
