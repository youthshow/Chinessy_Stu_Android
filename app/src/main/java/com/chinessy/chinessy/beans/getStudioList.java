package com.chinessy.chinessy.beans;

import java.util.List;

/**
 * Created by susan on 2016/11/20.
 */

public class getStudioList {


    /**
     * data : {"studio":[{"room_id":"109","user_id":"109","head_img_key":"self1.pic_hd.jpg","name":"Ryan 洪俊杰","country":"Shenzhen, China","spoken_languages":"Mandarin(Native), Cantonese(Native), English(Fluent)","online_num":"29523","status":"inactive","cover":"192.168.3.239:8090/Chinessy/image/006brYTkgw1f5py4hafwoj30j60y10x7.jpg"},{"room_id":"267","user_id":"267","head_img_key":"xiaoqian.jpg","name":"Sue 袁小倩","country":"Shanghai, China","spoken_languages":"Fluent English ,Basic Dutch , Low Spanish .","online_num":"24456","status":"inactive","cover":"192.168.3.239:8090/Chinessy/image/0060KzfCgw1f3114tza5mj30dw0jugod.jpg"}]}
     * msg : 获取成功！直播间列表
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
        private List<StudioBean> studio;

        public List<StudioBean> getStudio() {
            return studio;
        }

        public void setStudio(List<StudioBean> studio) {
            this.studio = studio;
        }

        public static class StudioBean {
            /**
             * room_id : 109
             * user_id : 109
             * head_img_key : self1.pic_hd.jpg
             * name : Ryan 洪俊杰
             * country : Shenzhen, China
             * spoken_languages : Mandarin(Native), Cantonese(Native), English(Fluent)
             * online_num : 29523
             * status : inactive
             * cover : 192.168.3.239:8090/Chinessy/image/006brYTkgw1f5py4hafwoj30j60y10x7.jpg
             */

            private String room_id;
            private String user_id;
            private String head_img_key;
            private String name;
            private String country;
            private String spoken_languages;
            private String online_num;
            private String status;
            private String cover;

            public String getRoom_id() {
                return room_id;
            }

            public void setRoom_id(String room_id) {
                this.room_id = room_id;
            }

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

            public String getCountry() {
                return country;
            }

            public void setCountry(String country) {
                this.country = country;
            }

            public String getSpoken_languages() {
                return spoken_languages;
            }

            public void setSpoken_languages(String spoken_languages) {
                this.spoken_languages = spoken_languages;
            }

            public String getOnline_num() {
                return online_num;
            }

            public void setOnline_num(String online_num) {
                this.online_num = online_num;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getCover() {
                return cover;
            }

            public void setCover(String cover) {
                this.cover = cover;
            }
        }
    }
}
