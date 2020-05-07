package com.shenyy.yuan.common;


public class Result<T> {
    private String msgCode;
    private String msg;
    private T data;


    public String getMsgCode() {
        return msgCode;
    }

    public Result(ErrorCode errorCode, T data){
        this.msgCode = errorCode.getIndex();
        this.msg = errorCode.getDesc();
        this.data = data;
    }

    public void setMsgCode(String msgCode) {
        this.msgCode = msgCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
