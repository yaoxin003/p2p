package com.yx.p2p.ds.model;

import java.util.Date;

/**
 * @description:
 * @author: yx
 * @date: 2020/03/26/13:34
 */
public class BaseModel {

    private Date createTime;//创建时间
    private Date updateTime;//修改时间
    private Integer creator;//创建人
    private Integer reviser;//修改人

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getCreator() {
        return creator;
    }

    public void setCreator(Integer creator) {
        this.creator = creator;
    }

    public Integer getReviser() {
        return reviser;
    }

    public void setReviser(Integer reviser) {
        this.reviser = reviser;
    }

    @Override
    public String toString() {
        return "BaseModel{" +
                "createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", creator=" + creator +
                ", reviser=" + reviser +
                '}';
    }
}
