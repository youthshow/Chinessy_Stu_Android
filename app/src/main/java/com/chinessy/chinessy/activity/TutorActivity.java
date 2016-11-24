package com.chinessy.chinessy.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chinessy.chinessy.Chinessy;
import com.chinessy.chinessy.R;
import com.chinessy.chinessy.clients.InternalClient;
import com.chinessy.chinessy.handlers.SimpleFileAsyncHttpResponseHandler;
import com.chinessy.chinessy.handlers.SimpleJsonHttpResponseHandler;
import com.chinessy.chinessy.models.CallData;
import com.chinessy.chinessy.models.User;
import com.chinessy.chinessy.models.UserProfile;
import com.chinessy.chinessy.utils.FileUtil;
import com.chinessy.chinessy.utils.PictureUtil;
import com.chinessy.chinessy.utils.Utils;
import com.rey.material.app.SimpleDialog;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import cz.msebera.android.httpclient.Header;

public class TutorActivity extends AppCompatActivity {
    final String tag = "TutorActivity";
    final int HANDLER_HEAD_IMG_DOWNLOADED = 100;

    Activity mActivity;

    User mTutor;

    RelativeLayout mRlProfile;
    TextView mTvName;
    TextView mTvStatus;
    TextView mTvIntroduction;
    TextView mTvEducation;
    TextView mTvSpokenLanguages;
    TextView mTvAddress;
    TextView mTvScore;
    TextView mTvServedTime;
    ImageView mIvHeadImg;
    ImageView mIvProfileBg;

    ImageView mIvStar0;
    ImageView mIvStar1;
    ImageView mIvStar2;
    ImageView mIvStar3;
    ImageView mIvStar4;
//    ImageView mIvStatus;
//todo +++++++++++++++++++++++++
//    Button mBtnPracticeNow;
    //todo +++++++++++++++++++++++++
//    Button mBtnScheduleaReservation;

    private TextView mTvRemainTime;
    private Button mBtnResrve;
    private Button mBtnLive;

    Handler mHandler = new TutorHandler();

    boolean mPracticeNow = false;
    int mListPosition = -1;
    CallData mCallData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor);
        mActivity = this;

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.black));
        actionBar.setElevation(0f);


        Intent intent = getIntent();
        mTutor = (User) intent.getSerializableExtra("tutor");
        mPracticeNow = intent.getBooleanExtra("practice_now", false);
        mListPosition = intent.getIntExtra("position", -1);

        mRlProfile = (RelativeLayout) findViewById(R.id.tutor_rl_profile);
        mTvName = (TextView) findViewById(R.id.tutor_tv_name);
        mTvStatus = (TextView) findViewById(R.id.tutor_tv_status);
        mTvIntroduction = (TextView) findViewById(R.id.tutor_tv_aboutme);
        mTvEducation = (TextView) findViewById(R.id.tutor_tv_education);
        mTvSpokenLanguages = (TextView) findViewById(R.id.tutor_tv_spokenlanguages);
        mTvAddress = (TextView) findViewById(R.id.tutor_tv_address);
        mTvScore = (TextView) findViewById(R.id.tutor_tv_score);
        mTvServedTime = (TextView) findViewById(R.id.tutor_tv_servedtime);
        mIvHeadImg = (ImageView) findViewById(R.id.tutor_iv_headimg);
        mIvProfileBg = (ImageView) findViewById(R.id.tutor_iv_profilebg);
        mIvStar0 = (ImageView) findViewById(R.id.tutor_iv_star0);
        mIvStar1 = (ImageView) findViewById(R.id.tutor_iv_star1);
        mIvStar2 = (ImageView) findViewById(R.id.tutor_iv_star2);
        mIvStar3 = (ImageView) findViewById(R.id.tutor_iv_star3);
        mIvStar4 = (ImageView) findViewById(R.id.tutor_iv_star4);
        fillTutor(mTutor);
//todo +++++++++++++++++++++++++

        mTvRemainTime = (TextView) findViewById(R.id.tv_remain_time);
        mBtnResrve = (Button) findViewById(R.id.tutor_btn_resrve);
        mBtnLive = (Button) findViewById(R.id.tutor_btn_live);






  /*      mBtnPracticeNow = (Button)findViewById(R.id.tutor_btn_practicenow);

         mBtnPracticeNow.setOnClickListener(new BtnPracticeNowClickListener());



        if(mPracticeNow){
            mBtnPracticeNow.performClick();
        }

        */
        //todo +++++++++++++++++++++++++
    }

    void fillTutor(final User tutor) {
        mTvName.setText(tutor.getUserProfile().getName());
        changeTutorStatus(tutor, tutor.getUserProfile().getStatus());
        mTvIntroduction.setText(tutor.getUserProfile().getIntroduction());
        mTvEducation.setText(tutor.getUserProfile().getEducation());
        mTvAddress.setText(tutor.getUserProfile().getCountry());
        mTvScore.setText(tutor.getUserProfile().getScore() + "");
        long minutes = tutor.getUserProfile().getServedMinutes();
        mTvServedTime.setText(((int) Math.floor(minutes / 60)) + "h" + minutes % 60 + "mins");
        mTvSpokenLanguages.setText(tutor.getUserProfile().getSpokenLanguages());
        fillScore(tutor);

//        if(!tutor.getUserProfile().getStatus().equals("available")){
//            mBtnPracticeNow.setEnabled(false);
//        }

        if (tutor.getUserProfile().getHeadImg().getBitmap() != null) {
            mIvHeadImg.setImageBitmap(tutor.getUserProfile().getHeadImg().getBitmap());
            blurHeadImage(tutor);
        } else if (!tutor.getUserProfile().getHeadImg().getKey().equals("")) {
            InternalClient.getFile(tutor.getUserProfile().getHeadImg().getKey(), new SimpleFileAsyncHttpResponseHandler(mActivity) {
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                    super.onFailure(statusCode, headers, throwable, file);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, File file) {
                    super.onSuccess(statusCode, headers, file);
                    File f = new File(tutor.getUserProfile().getHeadImg().getAbsolutePath());
                    FileUtil.fileChannelCopy(file, f);
                    Message message = mHandler.obtainMessage(HANDLER_HEAD_IMG_DOWNLOADED);
                    message.sendToTarget();
                }
            });
        }
    }

    void fillScore(final User tutor) {
        float score = tutor.getUserProfile().getScore();
        if (score < 0.25) {
            //do nothing
        } else if (score >= 0.25 && score < 0.75) {
            mIvStar0.setImageResource(R.mipmap.star_half);
        } else if (score >= 0.75 && score < 1.25) {
            mIvStar0.setImageResource(R.mipmap.star_on);
        } else if (score >= 1.25 && score < 1.75) {
            mIvStar0.setImageResource(R.mipmap.star_on);
            mIvStar1.setImageResource(R.mipmap.star_half);
        } else if (score >= 1.75 && score < 2.25) {
            mIvStar0.setImageResource(R.mipmap.star_on);
            mIvStar1.setImageResource(R.mipmap.star_on);
        } else if (score >= 2.25 && score < 2.75) {
            mIvStar0.setImageResource(R.mipmap.star_on);
            mIvStar1.setImageResource(R.mipmap.star_on);
            mIvStar2.setImageResource(R.mipmap.star_half);
        } else if (score >= 2.75 && score < 3.25) {
            mIvStar0.setImageResource(R.mipmap.star_on);
            mIvStar1.setImageResource(R.mipmap.star_on);
            mIvStar2.setImageResource(R.mipmap.star_on);
        } else if (score >= 3.25 && score < 3.75) {
            mIvStar0.setImageResource(R.mipmap.star_on);
            mIvStar1.setImageResource(R.mipmap.star_on);
            mIvStar2.setImageResource(R.mipmap.star_on);
            mIvStar3.setImageResource(R.mipmap.star_half);
        } else if (score >= 3.75 && score < 4.25) {
            mIvStar0.setImageResource(R.mipmap.star_on);
            mIvStar1.setImageResource(R.mipmap.star_on);
            mIvStar2.setImageResource(R.mipmap.star_on);
            mIvStar3.setImageResource(R.mipmap.star_on);
        } else if (score >= 4.25 && score < 4.75) {
            mIvStar0.setImageResource(R.mipmap.star_on);
            mIvStar1.setImageResource(R.mipmap.star_on);
            mIvStar2.setImageResource(R.mipmap.star_on);
            mIvStar3.setImageResource(R.mipmap.star_on);
            mIvStar4.setImageResource(R.mipmap.star_half);
        } else if (score >= 4.75) {
            mIvStar0.setImageResource(R.mipmap.star_on);
            mIvStar1.setImageResource(R.mipmap.star_on);
            mIvStar2.setImageResource(R.mipmap.star_on);
            mIvStar3.setImageResource(R.mipmap.star_on);
            mIvStar4.setImageResource(R.mipmap.star_on);
        }
    }

    void blurHeadImage(User tutor) {
        Bitmap bitmap = tutor.getUserProfile().getHeadImg().getBitmap(true);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(0.5f, 0.5f);
//        Bitmap resizedBmp = Bitmap.createBitmap(bitmap, width/5, height/5, width/5*3, height/5*3, matrix, true);
        Bitmap resizedBmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        resizedBmp = PictureUtil.doBlur(resizedBmp, 10, true);
//        mRlProfile.setBackgroundDrawable(new BitmapDrawable(resizedBmp));
        mIvProfileBg.setImageBitmap(resizedBmp);
    }

    void changeTutorStatus(User tutor, String status) {
        tutor.getUserProfile().setStatus(status);

        mTvStatus.setText(Utils.captureName(status));
        if (status.equals(User.STATUS_AVAILABLE)) {
            mTvStatus.setBackgroundResource(R.color.main_color);

            //todo +++++++++++++++++++++++++
            //  mBtnPracticeNow.setBackgroundResource(R.drawable.btn_long_main);

            //todo +++++++++++++++++++++++++
        } else if (status.equals(User.STATUS_BUSY)) {
            mTvStatus.setBackgroundResource(R.color.busy_red);
            //todo +++++++++++++++++++++++++
//            mBtnPracticeNow.setBackgroundResource(R.drawable.btn_long_disable);
            //todo +++++++++++++++++++++++++
        } else if (status.equals(User.STATUS_OFFLINE)) {
            mTvStatus.setBackgroundResource(R.color.gray);
            //todo +++++++++++++++++++++++++
//            mBtnPracticeNow.setBackgroundResource(R.drawable.btn_long_disable);
            //todo +++++++++++++++++++++++++
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tutor, menu);

        MenuItem miFavourites = menu.findItem(R.id.tutor_menu_favourite);
        setFavouritesStatus(miFavourites);
        return true;
    }

    void setFavouritesStatus(MenuItem miFavourites) {
        if (mTutor.isFavourites()) {
            miFavourites.setIcon(R.mipmap.profile_starred);
        } else {
            miFavourites.setIcon(R.mipmap.profile_unstarred);
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        if (id == android.R.id.home) {
            Intent intent = new Intent();
            intent.putExtra("is_favourites", mTutor.isFavourites());
            intent.putExtra("position", mListPosition);
            TutorActivity.this.setResult(RESULT_OK, intent);
            TutorActivity.this.finish();
        } else if (id == R.id.tutor_menu_favourite) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("access_token", Chinessy.chinessy.getUser().getAccessToken());
                jsonObject.put("tutor_id", mTutor.getId());
                if (mTutor.isFavourites()) {
                    InternalClient.postJson(mActivity, "internal/user/cancel_favourite_tutor", jsonObject, new SimpleJsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            try {
                                switch (response.getInt("code")) {
                                    case 10000:
                                        mTutor.setIsFavourites(false);
                                        setFavouritesStatus(item);
                                        Toast.makeText(mActivity, R.string.Removed_from_favourites, Toast.LENGTH_SHORT).show();
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
                } else {
                    InternalClient.postJson(mActivity, "internal/user/add_favourite_tutor", jsonObject, new SimpleJsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            try {
                                switch (response.getInt("code")) {
                                    case 10000:
                                        mTutor.setIsFavourites(true);
                                        setFavouritesStatus(item);
                                        Toast.makeText(mActivity, R.string.Added_to_favourites, Toast.LENGTH_SHORT).show();
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
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    class TutorHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_HEAD_IMG_DOWNLOADED:
                    Bitmap bitmap = mTutor.getUserProfile().getHeadImg().getBitmap();
                    if (bitmap != null) {
                        mIvHeadImg.setImageBitmap(bitmap);
                        blurHeadImage(mTutor);
                    } else {
                        Log.w(tag, "image still null after loading");
                    }
                    break;
            }
        }
    }

    void changeButtonMode(int status) {
        switch (status) {
            case 0:
//                final Timer timer = new Timer();
//                final TimerTask timerTask = new TimerTask() {
//                    @Override
//                    public void run() {
//                        mBtnPracticeNow.setText(R.string.Practice_Now);
//                        mBtnPracticeNow.setClickable(true);
//                        timer.cancel();
//                    }
//                };
//                timer.schedule(timerTask, 1000);
                //todo +++++++++++++++++++++++++
//                mBtnPracticeNow.setText(R.string.Practice_Now);
//                mBtnPracticeNow.setEnabled(true);
                //todo +++++++++++++++++++++++++
                break;
            case 1:
                //todo +++++++++++++++++++++++++
//                mBtnPracticeNow.setText(R.string.Connecting);
//                mBtnPracticeNow.setEnabled(false);
//                mBtnPracticeNow.setTextColor(Color.WHITE);
                //todo +++++++++++++++++++++++++
                break;
        }
    }

    class BtnPracticeNowClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (mTutor.getUserProfile().getStatus().equals(User.STATUS_AVAILABLE)) {
                changeButtonMode(1);
                UserProfile userProfile = Chinessy.chinessy.getUser().getUserProfile();
                userProfile.refreshBalance(mActivity, new AfterRefreshBalance());
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(TutorActivity.this);  //先得到构造器
                builder.setTitle(R.string.practice_now_tutor_unavailable_title); //设置标题
                builder.setMessage(R.string.practice_now_tutor_unavailable_message); //设置内容
//builder.setIcon(R.mipmap.ic_launcher);//设置图标，图片id即可
                builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() { //设置确定按钮
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); //关闭dialog
                    }
                });

//参数都设置完成了，创建并显示出来
                builder.create().show();
/*
                final SimpleDialog simpleDialog = new SimpleDialog(mActivity);
                simpleDialog.title(R.string.practice_now_tutor_unavailable_title);
                simpleDialog.message(R.string.practice_now_tutor_unavailable_message);
                simpleDialog.positiveAction(R.string.OK);
                simpleDialog.positiveActionClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        simpleDialog.dismiss();
                    }
                });
                simpleDialog.show();
                */
            }
        }

        class AfterRefreshBalance implements UserProfile.IAfterRefreshBalance {

            @Override
            public void execute() {
                changeButtonMode(0);
                if (Chinessy.chinessy.getUser().getUserProfile().getBalance() <= 0) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(TutorActivity.this);  //先得到构造器
                    builder.setTitle(R.string.practice_now_need_to_pay_title); //设置标题
                    builder.setMessage(R.string.practice_now_need_to_pay_message); //设置内容
//builder.setIcon(R.mipmap.ic_launcher);//设置图标，图片id即可
                    builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() { //设置确定按钮
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss(); //关闭dialog
                            Intent intent = new Intent();
                            intent.setClass(mActivity, GuideActivity.class);
                            mActivity.startActivity(intent);
                            mActivity.finish();
                            Chinessy.chinessy.logout();
                        }
                    });

                    //参数都设置完成了，创建并显示出来
                    builder.create().show();

/*
                    final SimpleDialog simpleDialog = new SimpleDialog(mActivity);
                    simpleDialog.title(R.string.practice_now_need_to_pay_title);
                    simpleDialog.message(R.string.practice_now_need_to_pay_message);
                    simpleDialog.positiveAction(R.string.OK);
                    simpleDialog.positiveActionClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            simpleDialog.dismiss();
                        }
                    });
                    simpleDialog.show();
                    */
                } else {
                    practiceNow2();
                }
            }
        }
    }

    void practiceNow2() {
        Chinessy.chinessy.getJusTalkHandler().justLogin();
        HistoryActivity.setIsNeed2Refresh(true);
        mCallData = new CallData(mTutor);
        Chinessy.chinessy.getJusTalkHandler().call(mCallData, Chinessy.chinessy.getUser().getUserProfile().getName());
    }

    void onCallFinished() {
        if (mCallData != null && mCallData.isConnected()) {
            Intent intent = new Intent();
            intent.setClass(mActivity, RatingsActivity.class);
            intent.putExtra("call_data", mCallData);
            mActivity.startActivity(intent);
            mCallData = null;
            Chinessy.chinessy.getUser().getUserProfile().refreshBalanceInBackground(mActivity);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        onCallFinished();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
