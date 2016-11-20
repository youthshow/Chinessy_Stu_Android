package com.chinessy.chinessy.beans;

import java.util.List;

/**
 * Created by susan on 2016/11/20.
 */

public class BindTeacher {

    /**
     * data : {"teacher":[{"user_id":"412","head_img_key":"selfJessyScalled.jpg","name":"Jessy 马洁雪","binding_minutes":"30","country":"Xi'an, China","score":"5.00","served_minutes":"17","status":"offline"},{"user_id":"197","head_img_key":"13728617827.jpg","name":"Kristy 黄柳婷","binding_minutes":"15","country":"Shenzhen, China","score":"5.00","served_minutes":"3083","status":"busy"}]}
     * msg : 获取成功！学生端一对一绑定信息列表
     * status : true
     */

    private DataBean data;
    private String msg;
    private String status;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
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

    public static class DataBean {
        private List<TeacherBean> teacher;

        public List<TeacherBean> getTeacher() {
            return teacher;
        }

        public void setTeacher(List<TeacherBean> teacher) {
            this.teacher = teacher;
        }

        public static class TeacherBean {
            /**
             * user_id : 412
             * head_img_key : selfJessyScalled.jpg
             * name : Jessy 马洁雪
             * binding_minutes : 30
             * country : Xi'an, China
             * score : 5.00
             * served_minutes : 17
             * status : offline
             */

            private String user_id;
            private String head_img_key;
            private String name;
            private String binding_minutes;
            private String country;
            private String score;
            private String served_minutes;
            private String status;

            public String getUser_id() {
                return user_id;
            }

            public void setUser_id(String user_id) {
                this.user_id = user_id;
            }

            public String getHead_img_key() {
                return head_img_key;
            }

            public void setHead_img_key(String head_img_key) {
                this.head_img_key = head_img_key;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getBinding_minutes() {
                return binding_minutes;
            }

            public void setBinding_minutes(String binding_minutes) {
                this.binding_minutes = binding_minutes;
            }

            public String getCountry() {
                return country;
            }

            public void setCountry(String country) {
                this.country = country;
            }

            public String getScore() {
                return score;
            }

            public void setScore(String score) {
                this.score = score;
            }

            public String getServed_minutes() {
                return served_minutes;
            }

            public void setServed_minutes(String served_minutes) {
                this.served_minutes = served_minutes;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }
        }
    }
}
