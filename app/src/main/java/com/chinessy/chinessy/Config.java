package com.chinessy.chinessy;

import com.paypal.android.sdk.payments.PayPalConfiguration;

import java.util.TimeZone;

/**
 * Created by larry on 15/7/10.
 */
public class Config {
    public static final int SPLASH_SCREEN_TIMEOUT = 3000;

    public static final String CLIENT_TYPE = "cns_app";
    public static final String FOLDER_MAIN = "Chinessy/";
    public static final String FOLDER_HEAD_IMG = "Chinessy/img/head/";
    public static final String SP_SETTINGS = "settings";

    public static final int ACTIVITY_RESULT_MODIFY_NAME = 10001;
    public static final int ACTIVITY_RESULT_MODIFY_PHONE = 10002;

    public static final TimeZone TIMEZONE_GMT0 = TimeZone.getTimeZone("GMT+0");

    // paypal sendbox
    public static final String PAYPAL_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    public static final String PAYPAL_CLIENT_ID = "AaWa4nRlx7zHzfXIXp0HerToo9eoc6ZATbkJidIJQPeLPGpwdGjEfWAv6Uh6yUaacrDLuFHLzOHroN-D";

    // paypal live
//    public static final String PAYPAL_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_PRODUCTION;
//    public static final String PAYPAL_CLIENT_ID = "AR11TkEohrArOPf3zFrDm_IrBzinWv6u5EJKB0PWdnRW7mV_4yY-8rB_mDCyC_nSUwBgrhFu7QZXqSUK";

    public static final String JUSTALK_CLOUD_APPKEY = "d0778f188a0a55b9f2505097";
    public static final String JUSTALK_CLOUD_SERVER_ADDRESS = "sudp:dev.ae.justalkcloud.com:9851";
//    public static final String JUSTALK_CLOUD_SERVER_ADDRESS = "sudp:ae.justalkcloud.com:9851";

    //   public static final String BASE_URL = "http://apidev.chinessy.com:9090/"; //测试服务器
    public static final String BASE_URL = "http://api.chinessy.com:8090/"; //生产环境的端口

    public static final int RC_MAIN_TO_TUTOR = 11;

}
