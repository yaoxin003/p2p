package com.yx.p2p.ds.enums.match;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/30/18:25
 */
public enum InvestMatchReqBizStateEnum {
    NEW_ADD("1","新增"),
    MATCHING("2","撮合中"),
    MATCH_FINISH("3","撮合完成");

    private String state;//状态
    private String stateDesc;//状态描述

    InvestMatchReqBizStateEnum(String state,String stateDesc){
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
        return "InvestMatchReqBizStateEnum{" +
                "state='" + state + '\'' +
                ", stateDesc='" + stateDesc + '\'' +
                '}';
    }
}
