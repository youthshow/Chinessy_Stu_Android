package com.chinessy.chinessy.clients;

/**
 * Created by susan on 2016/11/16.
 */

public class ConstValue {
    public static final String BasicUrl = "http://120.76.223.83:8090/Chinessy/index.php/Home/Index/";

    /**
     * 功能点：获取播放地址
     * 接口方式：POST
     * 功能描述：直播间的播放地址
     * 传递参数：
     * 1)roomId		直播间号
     */
    public static final String getPlayUrl = "getPlayUrl";

    /**
     * 功能点学生端个人中心：一对一绑定信息列表展示
     * 接口地址：http://192.168.3.239:8090/Chinessy/index.php/Home/Index/getStudentBinds
     * 接口方式：POST
     * 功能描述：查询学生端里一对一绑定信息教师列表
     * 传递参数：
     * 1)userId		学生的用户ID
     */
    public static final String getStudentBinds = "getStudentBinds";
}
