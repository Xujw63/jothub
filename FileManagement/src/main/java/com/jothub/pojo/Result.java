package com.jothub.pojo;

@lombok.Data
public class Result {
    private Integer code; //1 成功， 0 失败
    private String message; //提示信息
    private Object data; //数据data

    public Result() {}
    public Result(Integer code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static Result success(Object data) {
        return new Result(1, "success", data);
    }
    public static Result success() {
        return new Result(1, "success", null);
    }

    public static Result error(String msg) {
        return new Result(0, msg, null);
    }

    @Override
    public String toString() {
        return "Result {code=" + code + ", message=" + message + ", data=" + data + "}";
    }
}
