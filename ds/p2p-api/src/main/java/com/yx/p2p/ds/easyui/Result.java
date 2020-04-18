package com.yx.p2p.ds.easyui;

import java.io.Serializable;

/**
 * @description:
 * @author: yx
 * @date: 2020/03/31/8:08
 */
public class Result<T> implements Serializable{

    private static final String STATUS_OK = "ok";
    private static final String STATUS_ERROR = "error";

    private static final String DEFAULT_MSG_SUC = "操作成功！";
    private static final String DEFAULT_MSG_ERROR = "操作失败！";

    private String status;//ok,error
    private Integer count;//数据库insert/update/delete操作条数
    private String defaultMsg;//操作成功,操作失败
    private String selfMsg;//自定义输入信息
    private T target;//自定义返回对象
    private String retCode;//编码

    public static boolean checkStatus(Result result){
        if(STATUS_OK.equals(result.getStatus())){
            return true;
        }
        return false;
    }

    public static Result success(){
        Result result = new Result();
        result.setStatus(STATUS_OK);
        result.setCount(1);
        result.setDefaultMsg(DEFAULT_MSG_SUC);
        result.setSelfMsg("");
        return result;
    }

    public static Result success(String selfMessage){
        Result result = success();
        result.setSelfMsg(selfMessage);
        return result;
    }

    public static Result success(Integer count,String selfMsg){
        Result result = success();
        result.setSelfMsg(selfMsg);
        result.setCount(count);
        return result;
    }

    public static Result error(){
        Result result = new Result();
        result.setStatus(STATUS_ERROR);
        result.setCount(0);
        result.setDefaultMsg(DEFAULT_MSG_ERROR);
        result.setSelfMsg("");
        return result;
    }

    public static Result error(Integer count,String selfMsg){
        Result result = error();
        result.setSelfMsg(selfMsg);
        result.setCount(count);
        return result;
    }

    public static Result error(String selfMsg){
        Result result = error();
        result.setSelfMsg(selfMsg);
        return result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getDefaultMsg() {
        return defaultMsg;
    }

    public void setDefaultMsg(String defaultMsg) {
        this.defaultMsg = defaultMsg;
    }

    public String getSelfMsg() {
        return selfMsg;
    }

    public void setSelfMsg(String selfMsg) {
        this.selfMsg = selfMsg;
    }

    public T getTarget() {
        return target;
    }

    public void setTarget(T target) {
        this.target = target;
    }

    public String getRetCode() {
        return retCode;
    }

    public void setRetCode(String retCode) {
        this.retCode = retCode;
    }

    @Override
    public String toString() {
        return "Result{" +
                "status='" + status + '\'' +
                ", count=" + count +
                ", defaultMsg='" + defaultMsg + '\'' +
                ", selfMsg='" + selfMsg + '\'' +
                ", target=" + target +
                ", retCode='" + retCode + '\'' +
                '}';
    }
}
