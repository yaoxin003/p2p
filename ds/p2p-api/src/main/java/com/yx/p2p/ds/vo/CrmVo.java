package com.yx.p2p.ds.vo;

import com.yx.p2p.ds.model.Crm;

/**
 * @description:
 * @author: yx
 * @date: 2020/03/28/13:15
 */
public class CrmVo extends Crm {

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
