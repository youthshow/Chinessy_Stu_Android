package com.chinessy.chinessy.beans;

/**
 * Created by susan on 2016/11/24.
 */

public class EmailVerificationCode {

    /**
     * data : 983190
     * msg : 验证已发送
     * status : true
     */

    private int data;
    private String msg;
    private String status;

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
