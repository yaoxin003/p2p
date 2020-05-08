package com.yx.p2p.ds.enums.borrow;

import com.yx.p2p.ds.enums.payment.PaymentBizStateEnum;

/**
 * @description:借款业务状态
 * @author: yx
 * @date: 2020/05/02/14:17
 */
public enum BorrowBizStateEnum {
    NEW_ADD("1","新增"),//添加借款
    SIGNED("2","已签约"),//借款撮合成功
    BORROW_SUCCESS("3","借款成功"),//借款撮合成功
    BORROW_FAIL("4","借款失败");//借款撮合成功

    private String state;//状态
    private String stateDesc;//状态描述

    BorrowBizStateEnum(String state,String stateDesc){
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
