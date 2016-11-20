package com.chinessy.chinessy.beans;

/**
 * Created by susan on 2016/11/20.
 */

public class getMoneyInfo {

    /**
     * data : {"userId":"611","beans":"12360","allTime":"10","allBindingTime":"85"}
     * status : true
     * msg : 获取虚拟币、时间和一对一绑定视频总时长成功！
     */

    private DataBean data;
    private String status;
    private String msg;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class DataBean {
        /**
         * userId : 611
         * beans : 12360
         * allTime : 10
         * allBindingTime : 85
         */

        private String userId;
        private String beans;
        private String allTime;
        private String allBindingTime;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getBeans() {
            return beans;
        }

        public void setBeans(String beans) {
            this.beans = beans;
        }

        public String getAllTime() {
            return allTime;
        }

        public void setAllTime(String allTime) {
            this.allTime = allTime;
        }

        public String getAllBindingTime() {
            return allBindingTime;
        }

        public void setAllBindingTime(String allBindingTime) {
            this.allBindingTime = allBindingTime;
        }
    }
}
