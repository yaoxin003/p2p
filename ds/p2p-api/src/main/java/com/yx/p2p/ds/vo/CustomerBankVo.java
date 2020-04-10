package com.yx.p2p.ds.vo;

import com.yx.p2p.ds.model.CustomerBank;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/10/7:58
 */
public class CustomerBankVo extends CustomerBank {

    private String baseBankName;

    public String getBaseBankName() {
        return baseBankName;
    }

    public void setBaseBankName(String baseBankName) {
        this.baseBankName = baseBankName;
    }

    @Override
    public String toString() {
        return super.toString() + "CustomerBankVo{" +
                "baseBankName='" + baseBankName + '\'' +
                '}';
    }
}
