package com.yx.p2p.ds.enums.base;

/**
 * @description:逻辑状态
 * @author: yx
 * @date: 2020/04/18/12:36
 */
public enum  LogicStateEnum {
    ENABLE("1","有效"),
    DISABLE("0","无效");

    private String state;//状态
    private String stateDesc;//状态描述

    LogicStateEnum(String state,String stateDesc){
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
        return "LogicStateEnum{" +
                "state='" + state + '\'' +
                ", stateDesc='" + stateDesc + '\'' +
                '}';
    }
}
