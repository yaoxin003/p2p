package com.yx.p2p.ds.easyui;

/**
 * @description:
 * @author: yx
 * @date: 2020/03/31/8:08
 */
public class Result {
    private String status;
    private String message;

    public static Result success(){
        Result result = new Result();
        result.setStatus("ok");
        result.setMessage("操作成功！");
        return result;
    }
    public static Result success(String message){
        Result result = success();
        if(message != null && !"".equals(message)){
            result.setMessage(message);
        }
        return result;
    }

    public static Result error(){
        Result result = new Result();
        result.setStatus("error");
        result.setMessage("操作失败！");
        return result;
    }

    public static Result error(String message){
        Result result = error();
        if(message != null && !"".equals(message)){
            result.setMessage(message);
        }
        return result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Result{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
