package com.yx.p2p.ds.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * @description:
 * @author: yx
 * @date: 2020/03/26/13:34
 */
public class BaseModel {

    @JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;//创建时间
    @JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime;//修改时间
    private Integer creator;//创建人
    private Integer reviser;//修改人
    private String logicState;//逻辑状态（暂未使用）： 1-有效，0-无效
    private String bizState; //业务状态：1-新增

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

    public String getLogicState() {
        return logicState;
    }

    public void setLogicState(String logicState) {
        this.logicState = logicState;
    }

    public String getBizState() {
        return bizState;
    }

    public void setBizState(String bizState) {
        this.bizState = bizState;
    }

    @Override
    public String toString() {
        return "BaseModel{" +
                "createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", creator=" + creator +
                ", reviser=" + reviser +
                ", logicState='" + logicState + '\'' +
                ", bizState='" + bizState + '\'' +
                '}';
    }
}
