package com.gawilive.common.server;

public class BaseResult<T> {
    private int code;

    private String msg;

    private T data;

    public int getCode() {
        return code;
    }

    public BaseResult<T> setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public BaseResult<T> setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public T getData() {
        return data;
    }

    public BaseResult<T> setData(T data) {
        this.data = data;
        return this;
    }
}
