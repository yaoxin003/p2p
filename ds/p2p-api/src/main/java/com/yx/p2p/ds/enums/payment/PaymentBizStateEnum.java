package com.yx.p2p.ds.enums.payment;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/16/15:42
 */
public enum PaymentBizStateEnum {
    NEW_ADD("1","新增"),//添加支付单
    PAY_SUC("2","支付成功"),
    PAY_FAIL("3","支付失败");

    private String state;//状态
    private String stateDesc;//状态描述

    PaymentBizStateEnum(String state,String stateDesc){
        this.state = state;
        this.stateDesc = stateDesc;
    }

    public static String getStateDesc(String state){
        PaymentBizStateEnum[] values = values();
        for( PaymentBizStateEnum ele : values){
            if(ele.getState().equals(state)){
                return ele.stateDesc;
            }
        }
        return null;
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
