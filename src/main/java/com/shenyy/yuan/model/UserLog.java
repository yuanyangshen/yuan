package com.shenyy.yuan.model;

public class UserLog {
    String userName;
    String module;
    String operation;
    String userStr;

    public UserLog(){

    }

    public UserLog(String userName, String module, String operation, String userStr) {
        this.userName = userName;
        this.module = module;
        this.operation = operation;
        this.userStr = userStr;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getUserStr() {
        return userStr;
    }

    public void setUserStr(String userStr) {
        this.userStr = userStr;
    }
}
