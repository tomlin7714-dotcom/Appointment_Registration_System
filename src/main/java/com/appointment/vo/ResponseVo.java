package com.appointment.vo;

public class ResponseVo {
    private Integer code;
    private String msg;
    private Object data;

    public ResponseVo() {
    }

    public ResponseVo(Integer code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static ResponseVo success() {
        return new ResponseVo(200, "操作成功", null);
    }

    public static ResponseVo success(Object data) {
        return new ResponseVo(200, "操作成功", data);
    }

    public static ResponseVo error(Integer code, String msg) {
        return new ResponseVo(code, msg, null);
    }

    public static ResponseVo error(String msg) {
        return new ResponseVo(500, msg, null);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}