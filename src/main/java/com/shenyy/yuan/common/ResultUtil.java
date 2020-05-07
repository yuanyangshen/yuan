package com.shenyy.yuan.common;


public class ResultUtil {

    /**
     * 成功返回
     *
     * @param data 数据
     * @return
     */
    public static Result success(Object data) {
        return new Result(ErrorCode.SUCCESS, data);
    }

    /**
     * 成功返回
     *
     * @return
     */
    public static Result success() {
        return new Result(ErrorCode.SUCCESS, null);
    }

    /**
     * 系统异常
     *
     * @return
     */
    public static Result systemExcp() {
        return new Result(ErrorCode.SYSTEM_EXCPTION, null);
    }

    /**
     * 返回失败
     *
     * @param errorCode 错误码
     * @param data      数据
     * @return
     */
    public static Result failed(ErrorCode errorCode, Object data) {
        return new Result(errorCode, data);
    }

    /**
     * 返回失败
     *
     * @param errorCode 错误码
     * @return
     */
    public static Result failed(ErrorCode errorCode) {
        return new Result(errorCode, null);
    }


}
