package com.yx.p2p.ds.enums.investproduct;

/**
 * @description: //投资类型:1-固定期限,2-非固定期限
 * @author: yx
 * @date: 2020/04/10/13:36
 */
public enum InvestTypeEnum {
    FIXED("1","固定期限"),
    NO_FIXED("2","非固定期限");


    private String state;//状态
    private String stateDesc;//状态描述

    InvestTypeEnum(String state, String stateDesc) {
        this.state = state;
        this.stateDesc = stateDesc;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStateDesc() {
        return stateDesc;
    }

    public void setStateDesc(String stateDesc) {
        this.stateDesc = stateDesc;
    }

    @Override
    public String toString() {
        return "InvestTypeEnum{" +
                "state='" + state + '\'' +
                ", stateDesc='" + stateDesc + '\'' +
                '}';
    }
}
