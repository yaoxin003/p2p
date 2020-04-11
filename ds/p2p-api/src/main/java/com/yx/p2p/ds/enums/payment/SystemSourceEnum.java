package com.yx.p2p.ds.enums.payment;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/11/15:37
 */
public enum  SystemSourceEnum {
    INVEST("invest");

    private String name;

    SystemSourceEnum(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "SystemSourceEnum{" +
                "name='" + name + '\'' +
                '}';
    }
}
