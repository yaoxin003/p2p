package com.yx.p2p.ds.enums.invest;

/**
 * @description:
 * @author: yx
 * @date: 2020/05/14/14:51
 */
public enum TransferBizStateEnum {
    NEW_ADD("1","新增"),//添加协议
    TRANSFER_SUC("2","转让成功");//转让交割后;

    private String state;//状态
    private String stateDesc;//状态描述

    TransferBizStateEnum(String state, String stateDesc) {
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
        return "InvestBizStateEnum{" +
                "state='" + state + '\'' +
                ", stateDesc='" + stateDesc + '\'' +
                '}';
    }
}
