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
    //todo 头像
    public static final String getStudentBinds = "getStudentBinds";

    /**
     * 功能点：直播间列表展示
     * 接口类型：http
     * 接口地址：http://192.168.3.239:8090/Chinessy/index.php/Home/Index/getStudioList
     * 接口方式：POST
     * 功能描述：查询学生端里直播间列表
     * 传递参数：
     * --不用
     */
    public static final String getStudioList = "getStudioList";

    /**
     * 功能点：查看购买及充值的虚拟币、总时间和绑定给单个教师1对1视频学习的总时间
     * 接口地址：http://192.168.3.239:8090/Chinessy/index.php/Home/Index/getMoneyInfo
     * 接口方式：POST
     * 功能描述：查询虚拟币、时间和一对一绑定视频总时长
     * 传递参数：
     * 1)userId		学生的用户ID
     */
    public static final String getMoneyInfo = "getMoneyInfo";

    //todo +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    /*
    * 注册邮箱验证码发送
    *  接口方式：get
    *  接口地址：http://lande88.com/live/home/live/code?mail=465070308@qq.com
    * */
    public static final String CODE = "http://lande88.com/live/home/live/code";
    public static final String mail = "mail";
    public static final String code = "code";
    /*
    * 注册邮箱验证码校验
    *  接口方式：get
    *  接口地址：http://lande88.com/live/home/live/valid?mail=465070308@qq.com&code=788430
    * */
    public static final String VAILD = "http://lande88.com/live/home/live/valid";



}
