package com.chinessy.chinessy.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.chinessy.chinessy.R;
import com.chinessy.chinessy.rtmp.LivePlayerActivity;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

public class LiveRoomActivity extends AppCompatActivity {
    private FrameLayout frameLayout;
    LivePlayerActivity mPlayerVodFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_live_room);
        SystemSetting();
        frameLayout = (FrameLayout) findViewById(R.id.content_layout);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        mPlayerVodFragment = new LivePlayerActivity();
        transaction.replace(R.id.content_layout, mPlayerVodFragment);
        transaction.commit();
        findViewById(R.id.tv_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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
