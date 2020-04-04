package com.yx.p2p.ds.vo;

import com.yx.p2p.ds.model.Crm;

import java.io.Serializable;

/**
 * @description:
 * @author: yx
 * @date: 2020/03/28/13:15
 */

public class CrmVo extends Crm implements Serializable{//CrmVo作为Dubbo服务参数必须实现Serializable

   private String birthdayStr;

    public String getBirthdayStr() {
        return birthdayStr;
    }

    public void setBirthdayStr(String birthdayStr) {
        this.birthdayStr = birthdayStr;
    }

    @Override
    public String toString() {
        return super.toString() + ",CrmVo{" +
                "birthdayStr='" + birthdayStr + '\'' +
                '}';
    }
}
