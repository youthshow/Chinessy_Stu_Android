package com.chinessy.chinessy.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinessy.chinessy.Chinessy;
import com.chinessy.chinessy.R;
import com.chinessy.chinessy.clients.InternalClient;
import com.chinessy.chinessy.handlers.SimpleJsonHttpResponseHandler;
import com.chinessy.chinessy.models.CallData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class RatingsActivity extends AppCompatActivity {
    static final int HANDLER_GOT_CALL_INFO = 101;
    Activity mActivity;

    CallData mCallData;

    ImageView mIvHeadImg;
    TextView mTvName;
    TextView mTvDuration;
    Button mBtnSubmit;

    List<ImageView> connectionQualityList = new ArrayList<>();
    List<ImageView> tutorQualityList = new ArrayList<>();

    int mConnectionQuality = 0;
    int mTutorQuality = 0;
    ProgressDialog mProgressDialog = null;
    Handler mHandler = new RatingsActivityHandler();
    boolean isConnectionReviewed = false;
    boolean isTutorReviewed = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ratings);
        mActivity = this;
        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.black));
        actionBar.setElevation(0f);

        Intent intent = getIntent();
        mCallData = (CallData)intent.getSerializableExtra("call_data");

        mTvDuration = (TextView)findViewById(R.id.ratings_tv_duration);
        mBtnSubmit = (Button)findViewById(R.id.ratings_btn_submit);

        getCallInfo();

        ImageView ivStar00 = (ImageView)findViewById(R.id.ratings_iv_star00);
        ImageView ivStar01 = (ImageView)findViewById(R.id.ratings_iv_star01);
        ImageView ivStar02 = (ImageView)findViewById(R.id.ratings_iv_star02);
        ImageView ivStar03 = (ImageView)findViewById(R.id.ratings_iv_star03);
        ImageView ivStar04 = (ImageView)findViewById(R.id.ratings_iv_star04);
        ivStar00.setOnClickListener(new ConnectionQualityStarOnClickListener());
        ivStar01.setOnClickListener(new ConnectionQualityStarOnClickListener());
        ivStar02.setOnClickListener(new ConnectionQualityStarOnClickListener());
        ivStar03.setOnClickListener(new ConnectionQualityStarOnClickListener());
        ivStar04.setOnClickListener(new ConnectionQualityStarOnClickListener());
        connectionQualityList.add(ivStar00);
        connectionQualityList.add(ivStar01);
        connectionQualityList.add(ivStar02);
        connectionQualityList.add(ivStar03);
        connectionQualityList.add(ivStar04);

        ImageView ivStar10 = (ImageView)findViewById(R.id.ratings_iv_star10);
        ImageView ivStar11 = (ImageView)findViewById(R.id.ratings_iv_star11);
        ImageView ivStar12 = (ImageView)findViewById(R.id.ratings_iv_star12);
        ImageView ivStar13 = (ImageView)findViewById(R.id.ratings_iv_star13);
        ImageView ivStar14 = (ImageView)findViewById(R.id.ratings_iv_star14);
        ivStar10.setOnClickListener(new TutorQualityStarOnClickListener());
        ivStar11.setOnClickListener(new TutorQualityStarOnClickListener());
        ivStar12.setOnClickListener(new TutorQualityStarOnClickListener());
        ivStar13.setOnClickListener(new TutorQualityStarOnClickListener());
        ivStar14.setOnClickListener(new TutorQualityStarOnClickListener());
        tutorQualityList.add(ivStar10);
        tutorQualityList.add(ivStar11);
        tutorQualityList.add(ivStar12);
        tutorQualityList.add(ivStar13);
        tutorQualityList.add(ivStar14);

        mIvHeadImg = (ImageView)findViewById(R.id.ratings_iv_headimg);
        mTvName = (TextView)findViewById(R.id.ratings_tv_name);
        mIvHeadImg.setImageBitmap(mCallData.getCallee().getUserProfile().getHeadImg().getBitmap());
        mTvName.setText(mCallData.getCallee().getUserProfile().getName());

        mBtnSubmit.setOnClickListener(new BtnSubmitClickListener());
        mBtnSubmit.setEnabled(false);
        mBtnSubmit.setTextColor(Color.WHITE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ratings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mActivity.finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getCallInfo(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("access_token", Chinessy.chinessy.getUser().getAccessToken());
            jsonObject.put("call_id", mCallData.getCallId());
            mProgressDialog = new ProgressDialog(mActivity);
            mProgressDialog.setMessage(getString(R.string.Waiting));
            mProgressDialog.show();
            InternalClient.postJson(mActivity, "video/get", jsonObject, new SimpleJsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        switch (response.getInt("code")){
                            case 10000:
                                mCallData.setDuration(response.getJSONObject("data").getJSONObject("video_call").getJSONObject("fields").getInt("duration"));
                                mHandler.sendEmptyMessage(RatingsActivity.HANDLER_GOT_CALL_INFO);
                                break;
                            default:
                                SimpleJsonHttpResponseHandler.defaultHandler(mActivity, response.getString("message"));
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }finally {
                        mProgressDialog.dismiss();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    class ConnectionQualityStarOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ratings_iv_star00:
                    onStarClick(0);
                    break;
                case R.id.ratings_iv_star01:
                    onStarClick(1);
                    break;
                case R.id.ratings_iv_star02:
                    onStarClick(2);
                    break;
                case R.id.ratings_iv_star03:
                    onStarClick(3);
                    break;
                case R.id.ratings_iv_star04:
                    onStarClick(4);
                    break;
            }
        }
        void onStarClick(int index){
            for(int i=0; i<=index; i++){
                connectionQualityList.get(i).setImageResource(R.mipmap.ratings_on);
            }
            for(int i=4; i>index; i--){
                connectionQualityList.get(i).setImageResource(R.mipmap.ratings_off);
            }
            mConnectionQuality = index+1;

            isConnectionReviewed = true;
            if(isConnectionReviewed && isTutorReviewed){
                mBtnSubmit.setEnabled(true);
            }
        }
    }
    class TutorQualityStarOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ratings_iv_star10:
                    onStarClick(0);
                    break;
                case R.id.ratings_iv_star11:
                    onStarClick(1);
                    break;
                case R.id.ratings_iv_star12:
                    onStarClick(2);
                    break;
                case R.id.ratings_iv_star13:
                    onStarClick(3);
                    break;
                case R.id.ratings_iv_star14:
                    onStarClick(4);
                    break;
            }
        }
        void onStarClick(int index){
            for(int i=0; i<=index; i++){
                tutorQualityList.get(i).setImageResource(R.mipmap.ratings_on);
            }
            for(int i=4; i>index; i--){
                tutorQualityList.get(i).setImageResource(R.mipmap.ratings_off);
            }
            mTutorQuality = index+1;

            isTutorReviewed = true;
            if(isConnectionReviewed && isTutorReviewed){
                mBtnSubmit.setEnabled(true);
            }
        }
    }
    class BtnSubmitClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("access_token", Chinessy.chinessy.getUser().getAccessToken());
                jsonObject.put("call_id", mCallData.getCallId());
                jsonObject.put("score", mTutorQuality);
                jsonObject.put("call_quality_score", mConnectionQuality);
                mProgressDialog = new ProgressDialog(mActivity);
                mProgressDialog.setMessage(getString(R.string.Waiting));
                mProgressDialog.show();
                InternalClient.postJson(mActivity, "internal/reservation/review", jsonObject, new SimpleJsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        try {
                            switch (response.getInt("code")){
                                case 10000:
//                                    Toast.makeText(mActivity, getString(R.string.Review_Succeed), Toast.LENGTH_SHORT).show();
                                    RatingsActivity.this.finish();
                                    break;
                                default:
                                    SimpleJsonHttpResponseHandler.defaultHandler(mActivity, response.getString("message"));
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }finally {
                            mProgressDialog.dismiss();
                        }
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    class RatingsActivityHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case RatingsActivity.HANDLER_GOT_CALL_INFO:
                    mTvDuration.setText((int)Math.ceil(mCallData.getDuration()/60.0)+ getString(R.string.mins));
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        // do nothing
    }
}
