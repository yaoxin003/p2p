package com.yx.p2p.ds.enums.invest;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/16/15:39
 */
public enum InvestBizStateEnum {
    NEW_ADD("1","新增"),//添加新投资
    INVEST_SUC("2","投资成功"),//接收支付结果：支付成功
    INVEST_CANCEL("3","投资撤销"),//接收支付结果：支付失效
    MATCHING("4","撮合中"),//发送撮合系统，暂时没有上
    FULL_AMT("5","满额"),//全额出借
    TRANSFERING("6","转让中"),//发起转让
    TRANSFER_SUC("7","转让成功");//转让交割后

    private String state;//状态
    private String stateDesc;//状态描述

    InvestBizStateEnum(String state, String stateDesc) {
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
