package com.chinessy.chinessy;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;

import com.chinessy.chinessy.clients.InternalClient;
import com.chinessy.chinessy.handlers.SimpleJsonHttpResponseHandler;
import com.chinessy.chinessy.utils.FileUtil;
import com.chinessy.chinessy.utils.Utils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengDialogButtonListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UpdateStatus;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;


public class SplashActivity extends Activity {
    public final static String KEY_FIRST_START = "first_start";
    public final static int HANDLER_APP_ACTIVE = 100;

    final int duration = 3000;
    LoadTask mLoadTask;
    Timer mTimer = new Timer();
    boolean mFinish = false;

    TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            endLoading();
        }
    };

    boolean mAutoLogin = false;
    Activity mActivity;

    Handler mHandler = new SplashActivityHandler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // umeng force update
        mActivity = this;
        UmengUpdateAgent.setUpdateOnlyWifi(false);
        UmengUpdateAgent.setDialogListener(new UmengDialogButtonListener() {
            @Override
            public void onClick(int i) {
                switch (i) {
                    case UpdateStatus.NotNow:
//                        Toast.makeText(SplashActivity.this, "not now", Toast.LENGTH_SHORT).show();
                        Chinessy.chinessy.onTerminate();
                        break;
                }
            }
        });
        UmengUpdateAgent.forceUpdate(SplashActivity.this);

        checkAutoLogin();

        if(Chinessy.chinessy.isLogined()){
            Chinessy.chinessy.getJusTalkHandler().login(null, null);
        }

        mLoadTask = new LoadTask();
        mLoadTask.execute();
        mTimer.schedule(mTimerTask, duration);
    }

    private void endLoading(){
        if (mFinish) {
            if(mAutoLogin){
                Intent i = new Intent();
                i.setClass(SplashActivity.this, MainActivity.class);
                this.startActivity(i);
            } else {
                Intent i = new Intent();
                i.setClass(SplashActivity.this, GuideActivity.class);
                this.startActivity(i);
            }
            mFinish = false;
            this.finish();
        } else {
            mFinish = true;
        }
    }

    class LoadTask extends AsyncTask<Void, Void, Void> {
        Dialog promptDlg;
        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            SharedPreferences sp = SplashActivity.this
                    .getPreferences(MODE_PRIVATE);
            if (sp.getBoolean(SplashActivity.KEY_FIRST_START, true)) {
                doFirst(sp);
            }
            doEachTime(sp);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            endLoading();
        }
        private void doFirst(SharedPreferences sp) {
            makeDirs(sp);
            mHandler.sendEmptyMessage(HANDLER_APP_ACTIVE);
            sp.edit().putBoolean(SplashActivity.KEY_FIRST_START, false).commit();
        }

        private void makeDirs(SharedPreferences sp) {
            FileUtil.createFolder(Config.FOLDER_MAIN);
            FileUtil.createFolder(Config.FOLDER_HEAD_IMG);
        }

        private int doEachTime(SharedPreferences sp) {

            return 0;
        }
    }

    private void checkAutoLogin(){
//        SharedPreferences sp = SplashActivity.this.getSharedPreferences(Config.SP_SETTINGS, MODE_PRIVATE);
        if(Chinessy.chinessy.isLogined()){
            Chinessy.chinessy.autoLogin();
            mAutoLogin = true;

//            temp
            mFinish = true;
        } else {
            mAutoLogin = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    class SplashActivityHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case HANDLER_APP_ACTIVE:
                    appActive();
                    break;
            }
        }
    }

    private void appActive(){
        JSONObject jsonObject = new JSONObject();
        try {
            Utils.getPhoneInfo(jsonObject);
            Utils.getPhoneResolution(mActivity, jsonObject);
            jsonObject.put("time_zone", TimeZone.getDefault().getRawOffset() / 3600000);
            jsonObject.put("client_type", Config.CLIENT_TYPE);
            InternalClient.postJson(mActivity, "internal/app/active", jsonObject, new SimpleJsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        switch (response.getInt("code")){
                            case 10000:
//                                Toast.makeText(mActivity, "app activited", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                SimpleJsonHttpResponseHandler.defaultHandler(mActivity, response.getString("message"));
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
