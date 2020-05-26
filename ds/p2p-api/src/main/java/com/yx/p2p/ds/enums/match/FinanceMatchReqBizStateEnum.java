package com.yx.p2p.ds.enums.match;

/**
 * @description:融资撮合请求业务状态
 * @author: yx
 * @date: 2020/04/25/14:35
 */
public enum FinanceMatchReqBizStateEnum {
    NEW_ADD("1","新增"),
    BORROW_MATCH_CONFIRM("2","借款撮合确认"),
    BORROW_MATCH_RE("3","借款撮合撤销"),
    TRANSFER_MATCH_CONFIRM("4","转让撮合确认");

    private String bizState;
    private String bizStateDesc;

    FinanceMatchReqBizStateEnum(String bizState, String bizStateDesc) {
        this.bizState = bizState;
        this.bizStateDesc = bizStateDesc;
    }

    public String getBizState() {
        return bizState;
    }

    public void setBizState(String bizState) {
        this.bizState = bizState;
    }

    public String getBizStateDesc() {
        return bizStateDesc;
    }

    public void setBizStateDesc(String bizStateDesc) {
        this.bizStateDesc = bizStateDesc;
    }
}
