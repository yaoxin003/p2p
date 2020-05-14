package com.yx.p2p.ds.enums.lending;

/**
 * @description:
 * @author: yx
 * @date: 2020/05/13/17:40
 */
public enum LendingBizStateEnum {
    NEW_ADD("1","新增"),
    FULL_AMT("2","满额");

    private String state;
    private String stateDesc;

    LendingBizStateEnum(String state,String stateDesc){
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
}
