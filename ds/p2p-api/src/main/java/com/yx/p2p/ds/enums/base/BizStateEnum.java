package com.yx.p2p.ds.enums.base;

/**
 * @description:业务状态
 * @author: yx
 * @date: 2020/04/18/13:12
 */
public enum BizStateEnum {

    NEW_ADD("1","新增");

    private String state;//状态
    private String stateDesc;//状态描述

    BizStateEnum(String state, String stateDesc) {
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
        return "BizStateEnum{" +
                "state='" + state + '\'' +
                ", stateDesc='" + stateDesc + '\'' +
                '}';
    }
}
