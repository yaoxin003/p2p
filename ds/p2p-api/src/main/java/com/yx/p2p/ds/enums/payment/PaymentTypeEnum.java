package com.yx.p2p.ds.enums.payment;

/**
 * @description:
 * @author: yx
 * @date: 2020/05/05/14:03
 */
public enum PaymentTypeEnum {
    INVEST_RECHARGE(1,"投资充值"),
    BORROW_LOAN(2,"借款放款"),
    TRANSFER_WITHDRAW(3,"转让提现");

    private Integer code;
    private String codeDesc;

    PaymentTypeEnum(Integer code,String codeDesc){
        this.code = code;
        this.codeDesc = codeDesc;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getCodeDesc() {
        return codeDesc;
    }

    public void setCodeDesc(String codeDesc) {
        this.codeDesc = codeDesc;
    }

    @Override
    public String toString() {
        return "PaymentTypeEnum{" +
                "code=" + code +
                ", codeDesc='" + codeDesc + '\'' +
                '}';
    }
}
