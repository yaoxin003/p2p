package com.yx.p2p.ds.enums.lending;

/**
 * @description: 出借单类型
 * @author: yx
 * @date: 2020/04/21/12:43
 */
public enum LendingTypeEnum {
    NEW_LEND("1","新出借"),
    RETURN_LEND("2","回款再出借");

    private String code;
    private String codeDesc;

    LendingTypeEnum(String code,String codeDesc){
        this.code = code;
        this.codeDesc = codeDesc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
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
        return "LendingTypeEnum{" +
                "code='" + code + '\'' +
                ", codeDesc='" + codeDesc + '\'' +
                '}';
    }
}
