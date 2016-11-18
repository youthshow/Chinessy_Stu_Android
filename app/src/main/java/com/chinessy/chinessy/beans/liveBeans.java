package com.chinessy.chinessy.beans;

/**
 * Created by susan on 2016/11/17.
 */

public class liveBeans {


    /**
     * data : rtmp://5228.livepush.myqcloud.com/live/5228_?bizid=5228&txSecret=a120a883195716f781b291881310bdab&txTime=584E20C0
     * msg : 获取推流地址成功！
     * status : true
     */

    private String data;
    private String msg;
    private String status;

    public String getData() {
        return data;
    }

    public void setData(String data) {
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
