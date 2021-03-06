package com.yx.p2p.ds.vo;

import com.yx.p2p.ds.model.crm.Customer;

/**
 * @description:前台页面使用
 * @author: yx
 * @date: 2020/04/10/9:08
 */
public class CustomerVo extends Customer {

    private String idCardOld;//修改功能使用idCardOld字段，添加不使用该字段

    public String getIdCardOld() {
        return idCardOld;
    }

    public void setIdCardOld(String idCardOld) {
        this.idCardOld = idCardOld;
    }

    @Override
    public String toString() {
        return "CustomerVo{" +
                ", idCardOld='" + idCardOld + '\'' +
                '}' + super.toString();
    }
}
