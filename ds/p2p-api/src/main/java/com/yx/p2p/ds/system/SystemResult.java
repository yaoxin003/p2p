package com.yx.p2p.ds.system;

import java.util.Map;

/**
 * @description: 系统返回结果
 * @author: yx
 * @date: 2020/04/12/15:05
 */
public class SystemResult {

    private String retCode;
    private String retInfo;
    private Map<String,Object> map;

    public String getRetCode() {
        return retCode;
    }

    public void setRetCode(String retCode) {
        this.retCode = retCode;
    }

    public String getRetInfo() {
        return retInfo;
    }

    public void setRetInfo(String retInfo) {
        this.retInfo = retInfo;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }

    @Override
    public String toString() {
        return "SystemResult{" +
                "retCode='" + retCode + '\'' +
                ", retInfo='" + retInfo + '\'' +
                ", map=" + map +
                '}';
    }
}
