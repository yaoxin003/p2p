package com.yx.p2p.ds.mock.thirdpay.enums;

/**
 * @description:第三方支付状态
 * @author: yx
 * @date: 2020/04/16/15:31
 */
public enum RetCodeEnum {
    PAY_SUC("0000","支付成功"),
    BALANCE_NO_ENOUGH("1001","账户余额不足"),
    ACCOUNT_CANCEL("1001","账户已注销");

    private String code;
    private String codeInfo;

    RetCodeEnum(String code,String codeInfo){
        this.code = code;
        this.codeInfo = codeInfo;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCodeInfo() {
        return codeInfo;
    }

    public void setCodeInfo(String codeInfo) {
        this.codeInfo = codeInfo;
    }

    @Override
    public String toString() {
        return "RetCodeEnum{" +
                "code='" + code + '\'' +
                ", codeInfo='" + codeInfo + '\'' +
                '}';
    }
}
