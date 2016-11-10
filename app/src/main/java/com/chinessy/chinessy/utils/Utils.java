package com.chinessy.chinessy.utils;

import android.content.Context;
import android.view.WindowManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by larry on 15/8/20.
 */
public class Utils {
    public static String captureName(String name) {
        //     name = name.substring(0, 1).toUpperCase() + name.substring(1);
        //        return  name;
        char[] cs=name.toCharArray();
        cs[0]-=32;
        return String.valueOf(cs);
    }

    public static void getPhoneResolution(Context context, JSONObject jsonObject) throws JSONException {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();

        jsonObject.put("phone_screen_height", height);
        jsonObject.put("phone_screen_width", width);

        // 方法2
//        DisplayMetrics dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);
//        TextView tv = (TextView)this.findViewById(R.id.tv);
//        float width=dm.widthPixels*dm.density;
//        float height=dm.heightPixels*dm.density;
//        tv.setText("First method:"+dm.toString()+"\n"+"Second method:"+"Y="+screenWidth+";X="+screenHeight);
    }

    public static void getPhoneInfo(JSONObject jsonObject) throws JSONException {
//        android.os.Build.VERSION.RELEASE获取版本号
//        android.os.Build.MODEL 获取手机型号
//        android.os.Build.MANUFACTURER 厂商
        jsonObject.put("phone_type", android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL);
        jsonObject.put("phone_os_version", android.os.Build.VERSION.RELEASE);
    }

//    public static String getIPAddress(Context context) {
//        //获取wifi服务
//        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//        //判断wifi是否开启
//        if (!wifiManager.isWifiEnabled()) {
//            return null;
//        }
//        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//        int ipAddress = wifiInfo.getIpAddress();
//
//        String ip = intToIp(ipAddress);
//        return ip;
//    }
//    public static String intToIp(int i) {
//
//        return (i & 0xFF ) + "." +
//                ((i >> 8 ) & 0xFF) + "." +
//                ((i >> 16 ) & 0xFF) + "." +
//                ( i >> 24 & 0xFF) ;
//    }
//    public String getLocalIpAddress()
//    {
//        try
//        {
//            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
//            {
//                NetworkInterface intf = en.nextElement();
//                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
//                {
//                    InetAddress inetAddress = enumIpAddr.nextElement();
//                    if (!inetAddress.isLoopbackAddress())
//                    {
//                        return inetAddress.getHostAddress().toString();
//                    }
//                }
//            }
//        } catch (SocketException ex)
//        {
//            Log.e("WifiPreferenceIpAddress", ex.toString());
//        }
//        return null;
//    }
}
