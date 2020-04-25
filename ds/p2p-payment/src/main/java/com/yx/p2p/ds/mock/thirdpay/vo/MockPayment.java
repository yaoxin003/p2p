package com.yx.p2p.ds.mock.thirdpay.vo;

import com.yx.p2p.ds.model.payment.Payment;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/21/15:24
 */
public class MockPayment extends Payment {
    //模拟支付状态
    private String retCode;

    public String getRetCode() {
        return retCode;
    }

    public void setRetCode(String retCode) {
        this.retCode = retCode;
    }

    @Override
    public String toString() {
        return super.toString() + "MockPayment{" +
                "retCode='" + retCode + '\'' +
                '}';
    }
}
