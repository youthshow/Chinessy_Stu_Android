package com.chinessy.chinessy.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.chinessy.chinessy.R;
import com.upyun.upplayer.widget.UpVideoView;


public class LiveRoomActivity extends AppCompatActivity {

    private UpVideoView upVideoView;

    String path = "rtmp://live.hkstv.hk.lxdns.com/live/hks";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_live_room);


        SystemSetting();

        upVideoView = (UpVideoView) findViewById(R.id.uvv_vido);
        // todo 设置背景图片
        //upVideoView.setImage(R.mipmap.startpage);
        //设置播放地址
        upVideoView.setVideoPath(path);
        //开始播放
        upVideoView.start();


        findViewById(R.id.tv_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upVideoView.releaseWithoutStop();
                finish();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        upVideoView.setVideoPath(path);
        // 重新开始播放器
        upVideoView.resume();
        upVideoView.start();

    }

    private void SystemSetting() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

}
