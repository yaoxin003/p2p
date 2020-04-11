package com.yx.p2p.ds.easyui;

import java.io.Serializable;

/**
 * @description:
 * @author: yx
 * @date: 2020/03/31/8:08
 */
public class Result<T> implements Serializable{

    public static final String STATUS_OK = "ok";
    public static final String STATUS_ERROR = "error";

    private String status;
    private Integer count;
    private String defualtMsg;
    private String selfMsg;
    private T target;

    public static Result success(){
        Result result = new Result();
        result.setStatus("ok");
        result.setCount(1);
        result.setDefualtMsg("操作成功！");
        result.setSelfMsg("");
        return result;
    }

    public static Result success(String selfMessage){
        Result result = success();
        result.setSelfMsg(selfMessage);
        return result;
    }

    public static Result success(Integer count,String selfMessage){
        Result result = success();
        result.setSelfMsg(selfMessage);
        result.setCount(count);
        return result;
    }

    public static Result error(){
        Result result = new Result();
        result.setStatus("error");
        result.setCount(0);
        result.setDefualtMsg("操作失败！");
        result.setSelfMsg("");
        return result;
    }

    public static Result error(Integer count,String selfMessage){
        Result result = error();
        result.setSelfMsg(selfMessage);
        result.setCount(count);
        return result;
    }

    public static Result error(String selfMessage){
        Result result = error();
        result.setSelfMsg(selfMessage);
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

    public String getDefualtMsg() {
        return defualtMsg;
    }

    public void setDefualtMsg(String defualtMsg) {
        this.defualtMsg = defualtMsg;
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

    @Override
    public String toString() {
        return "Result{" +
                "status='" + status + '\'' +
                ", count=" + count +
                ", defualtMsg='" + defualtMsg + '\'' +
                ", selfMsg='" + selfMsg + '\'' +
                ", target=" + target +
                '}';
    }
}
