package com.yx.p2p.ds.enums.match;

/**
 * @description:融资撮合请求类型
 * @author: yx
 * @date: 2020/05/11/14:47
 */
public enum FinanceMatchReqTypeEnum {
    BORROW(1,"借款"),
    TRANSFER(2,"转让");

    private Integer type;
    private String typeDesc;

    FinanceMatchReqTypeEnum(Integer type,String typeDesc){
        this.type = type;
        this.typeDesc = typeDesc;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTypeDesc() {
        return typeDesc;
    }

    public void setTypeDesc(String typeDesc) {
        this.typeDesc = typeDesc;
    }

    @Override
    public String toString() {
        return "FinanceMatchReqTypeEnum{" +
                "type=" + type +
                ", typeDesc='" + typeDesc + '\'' +
                '}';
    }
}
