package com.yx.p2p.ds.model;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @description: 客户信息
 * @author: yx
 * @date: 2020/03/26/13:52
 */
@Table(name="p2p_crm")
public class Crm extends BaseModel implements Serializable {
    @Id//使用tkmybatis.selectByPrimaryKey方法时需要该字段，否则会将所有字段都当做where条件
    private Integer id;
    private String name;//姓名
    private Short gender;//性别
    private Date birthday;//生日
    private String idCard;//身份证号码

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Short getGender() {
        return gender;
    }

    public void setGender(Short gender) {
        this.gender = gender;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    @Override
    public String toString() {
        return super.toString()+"Crm{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", gender=" + gender +
                ", birthday=" + birthday +
                ", idCard='" + idCard + '\'' +
                '}';
    }
}