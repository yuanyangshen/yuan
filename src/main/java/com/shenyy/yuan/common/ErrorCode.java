package com.shenyy.yuan.common;

public enum ErrorCode {

    SUCCESS("0", "success"),
    SYSTEM_EXCPTION("-1", "system exception"),
    NO_AUTHORIZATION("100", "no authorization"),
    DB_ACCESS_ERROR("200", "db access error"),
    QUERY_CONDITION_IS_EMPTY("400", "query condition is empty"),
    THE_USER_NOT_FOUND("500", "the user not found"), //用户不存在
    THE_USER_ID_IS_NULL("600", "the user id is null"), //用户ID为空
    THE_USERNAME_IS_NULL("700", "the username is null"), //用户名称为空
    THE_PASSWORD_IS_NULL("800", "the password is null"), //密码为空

    /**
     * 任务模块
     */
    ;

    private String index;
    private String desc;

    public String getIndex() {
        return index;
    }

    public String getDesc() {
        return desc;
    }

    ErrorCode(String index, String desc) {
        this.index = index;
        this.desc = desc;
    }


}
