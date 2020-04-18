package com.yx.p2p.ds.enums;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/11/15:37
 */
public enum SystemSourceEnum {
    MANAGE("manage","后台管理"),
    CRM("crm","客户关系管理"),
    INVEST_SALE("invest_sale","投资销售管理"),
    INVEST("invest","投资管理"),
    MATCH("match","撮合管理"),
    BORROW_SALE("borrow_sale","借款销售管理"),
    BORROW("borrow","借款管理"),
    PAYMENT("payment","支付管理"),
    ACCOUNT("account","账户管理");

    public String name;
    public String nameDesc;

    SystemSourceEnum(String name, String nameDesc){
        this.name = name;
        this.nameDesc = nameDesc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameDesc() {
        return nameDesc;
    }

    public void setNameDesc(String nameDesc) {
        this.nameDesc = nameDesc;
    }

    @Override
    public String toString() {
        return "SystemSourceEnum{" +
                "name='" + name + '\'' +
                ", nameDesc='" + nameDesc + '\'' +
                '}';
    }
}
