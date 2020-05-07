package com.yx.p2p.ds.enums.match;

/**
 * @description:数值越大越先撮合
 * @author: yx
 * @date: 2020/04/25/14:35
 */
public enum BorrowMatchReqLevelEnum {
    BORROW(2,"借款"),
    TRANSFER(4,"转让");

    private Integer level;
    private String levelDesc;

    BorrowMatchReqLevelEnum(Integer level,String levelDesc){
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

    @Override
    public String toString() {
        return "BorrowMatchReqLevelEnum{" +
                "level=" + level +
                ", levelDesc='" + levelDesc + '\'' +
                '}';
    }
}
