package com.yx.p2p.ds.enums.mq;

/**
 * @description:
 * @author: yx
 * @date: 2020/05/05/16:26
 */
public enum MQStatusEnum {
    OK("ok","成功"),
    FAIL("fail","失败");
    private String status;
    private String statusDesc;

    MQStatusEnum(String status,String statusDesc){
        this.status = status;
        this.statusDesc = statusDesc;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    @Override
    public String toString() {
        return "MQStatusEnum{" +
                "status='" + status + '\'' +
                ", statusDesc='" + statusDesc + '\'' +
                '}';
    }
}
