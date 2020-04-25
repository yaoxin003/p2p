package com.yx.p2p.ds.enums.match;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/25/15:09
 */
public enum  MatchRemarkEnum {
    NEW_INVEST("新投资"),
    REINVEST("回款再投"),
    BORROW("借款"),
    TRANSFER("转让");

    private String desc;

    MatchRemarkEnum(String desc){
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "MatchRemarkEnum{" +
                "desc='" + desc + '\'' +
                '}';
    }
}
