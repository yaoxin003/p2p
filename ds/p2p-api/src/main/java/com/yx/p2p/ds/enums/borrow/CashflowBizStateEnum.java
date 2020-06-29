package com.yx.p2p.ds.enums.borrow;

/**
 * @description:
 * @author: yx
 * @date: 2020/06/28/19:16
 */
public enum CashflowBizStateEnum {
    NEW_ADD("1","新增"),//添加现金流
    RETURN_PAYING("2","还款支付中"),//还款支付中
    RETURN_PAY_SUC("3","还款支付成功");//还款支付成功

    private String state;//状态
    private String stateDesc;//状态描述

    CashflowBizStateEnum(String state,String stateDesc){
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
        return "BorrowBizStateEnum{" +
                "state='" + state + '\'' +
                ", stateDesc='" + stateDesc + '\'' +
                '}';
    }
}
