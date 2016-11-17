package com.chinessy.chinessy;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.multidex.MultiDexApplication;

import com.chinessy.chinessy.handlers.JusTalkHandler;
import com.chinessy.chinessy.models.User;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalService;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by larry on 15/7/10.
 */
public class Chinessy extends MultiDexApplication implements Application.ActivityLifecycleCallbacks {
    final static String tag = "ChinessyApplication";

    public static Chinessy chinessy;
    private List<Activity> activityList = new ArrayList<Activity>();

    private static PayPalConfiguration config = new PayPalConfiguration()
            // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
            // or live (ENVIRONMENT_PRODUCTION)
            .environment(Config.PAYPAL_ENVIRONMENT)
            .clientId(Config.PAYPAL_CLIENT_ID);

    private User user;

    int activityCount = 0;
    Timer mTimer = new Timer(true);
    boolean ifInBackground = false;

    JusTalkHandler jusTalkHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        Chinessy.chinessy = this;
        jusTalkHandler = new JusTalkHandler(this.getApplicationContext());
        registerActivityLifecycleCallbacks(this);

      //  MobclickAgent.updateOnlineConfig(getApplicationContext());
        MobclickAgent.openActivityDurationTrack(false);
     //   AnalyticsConfig.enableEncrypt(true);

        initPaypal();
    }


    void initPaypal() {
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);
    }

    void stopPaypal() {
        stopService(new Intent(this, PayPalService.class));
    }

    public static PayPalConfiguration getPaypalConfig() {
        return config;
    }

    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    public void finishActivitys() {
        for (Activity activity : activityList) {
            if (null != activity) {
                activity.finish();
            }
        }
    }

    @Override
    public void onTerminate() {
        // TODO Auto-generated method stub
        super.onTerminate();
        getJusTalkHandler().logout(null, null);
        getJusTalkHandler().destroy();

        stopPaypal();
        finishActivitys();
        System.exit(0);
    }

    public boolean isLogined() {
        SharedPreferences sp = getSharedPreferences(Config.SP_SETTINGS, MODE_PRIVATE);
        return !sp.getString("user_access_token", "").equals("");
    }

    public void autoLogin() {
        User user = new User();
        user.localRead(chinessy.getApplicationContext());
        setUser(user);
    }

    public void afterLogin(User user) {
        setUser(user);
        user.localSave(chinessy.getApplicationContext());
        getJusTalkHandler().login(null, null);
    }

    public void logout() {
        setUser(new User());
        getUser().localSave(chinessy.getApplicationContext());
        getJusTalkHandler().logout(null, null);
    }

    public static void shareReferralCode(Activity activity) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, chinessy.getUser().getUserProfile().getShareMessage(activity));
        sendIntent.setType("text/plain");
//        activity.startActivity(sendIntent);
        activity.startActivity(Intent.createChooser(sendIntent, activity.getTitle()));
    }

    public User getUser() {
        if (user == null) {
            autoLogin();
        }
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public JusTalkHandler getJusTalkHandler() {
        return jusTalkHandler;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
//        Log.d(tag, "on app create ");
        addActivity(activity);
    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {
        activityCount--;
//        Log.d(tag, "on app paused " + activityPaused);
//        Log.d(tag, "App in background " + (activityPaused == activityCount));
//        Date now = new Date();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (activityCount == 0) {
//                    Log.d(tag, "In Background");
                    ifInBackground = true;
                } else {
//                    Log.d(tag, "In Foreground");
                    ifInBackground = false;
                }
                this.cancel();
            }
        }, 1000);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        activityCount++;
        if (ifInBackground) {
            Chinessy.chinessy.getUser().activeUser(getApplicationContext());
        }
//        Log.d(tag, "on app resume "+ activityCount);

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }
}
