package com.yx.p2p.ds.enums.match;

/**
 * @description:数值越大越先撮合
 * @author: yx
 * @date: 2020/04/25/14:33
 */
public enum InvestMatchReqLevelEnum {
    NEW_INVEST(2,"新投资"),
    REINVEST(4,"回款再投");

    private Integer level;
    private String levelDesc;

    InvestMatchReqLevelEnum(Integer level,String levelDesc){
        this.level = level;
        this.levelDesc = levelDesc;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getLevelDesc() {
        return levelDesc;
    }

    public void setLevelDesc(String levelDesc) {
        this.levelDesc = levelDesc;
    }
}
