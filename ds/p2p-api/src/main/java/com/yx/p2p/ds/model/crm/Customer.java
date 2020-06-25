package com.yx.p2p.ds.model.crm;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yx.p2p.ds.model.base.BaseModel;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @description:客户
 * @author: yx
 * @date: 2020/04/10/9:03
 */
@Table(name="p2p_customer")
public class Customer extends BaseModel implements Serializable {

    private String name;//姓名
    private Short gender;//性别

    @DateTimeFormat(pattern = "yyyy-MM-dd")//主要是前后到后台的时间格式的转换
    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")//后台到前台的时间格式的转换
    private Date birthday;//生日
    private String idCard;//身份证号码

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
        return "Customer{" +
                " name='" + name + '\'' +
                ", gender=" + gender +
                ", birthday=" + birthday +
                ", idCard='" + idCard + '\'' +
                '}' +  super.toString();
    }
}