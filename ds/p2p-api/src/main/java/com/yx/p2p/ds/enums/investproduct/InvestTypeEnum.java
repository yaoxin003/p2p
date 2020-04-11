package com.yx.p2p.ds.enums.investproduct;

/**
 * @description: //投资类型:1-固定期限,2-非固定期限
 * @author: yx
 * @date: 2020/04/10/13:36
 */
public enum InvestTypeEnum {
    FIXED("1","固定期限"),
    NO_FIXED("2","非固定期限");

    private String state;

    private String stateInfo;

    InvestTypeEnum(String state, String stateInfo){
        this.state = state;
        this.stateInfo = stateInfo;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }
}
