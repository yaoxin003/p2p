package com.yx.p2p.ds.enums.account;

/**
 * @description:
 * @author: yx
 * @date: 2020/06/17/15:12
 */
public enum AccountRemarkEnum {

    INVEST_RETURN("投资回款"),
    INVEST_ADD("投资增值"),
    DEBT_ADD("债务增值"),
    DEBT_RETURN_ARRIVE("债务还款到账");

    private String desc;

    AccountRemarkEnum(String desc){
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
