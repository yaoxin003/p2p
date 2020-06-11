package com.yx.p2p.ds.timer.quartz.enums;

/**
 * @description:
 * @author: yx
 * @date: 2020/06/08/9:57
 */
public enum JobBizStateEnum {

    STOP("0","停止"),
    RUNNING("1","开始");

    private String state;
    private String stateDesc;

    JobBizStateEnum(String state,String stateDesc){
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
        return "JobBizStateEnum{" +
                "state='" + state + '\'' +
                ", stateDesc='" + stateDesc + '\'' +
                '}';
    }

}
