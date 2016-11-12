package com.chinessy.chinessy.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.chinessy.chinessy.Chinessy;
import com.chinessy.chinessy.Config;
import com.chinessy.chinessy.R;
import com.chinessy.chinessy.adapter.ReservationAvoidListAdapter;
import com.chinessy.chinessy.clients.InternalClient;
import com.chinessy.chinessy.dialog.CustomTimePickerDialog;
import com.chinessy.chinessy.fragment.ReservationFragment;
import com.chinessy.chinessy.models.Reservation;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.rey.material.app.DatePickerDialog;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.widget.ProgressView;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TimeslotsActivity extends AppCompatActivity {
    final String tag = "TimeslotsActivity";

    final int HANDLER_AFTER_LOAD_TEACHER_UPCOMING = 100;

    Activity mActivity;

    RelativeLayout mRlTime;
    RelativeLayout mRlDate;
    TextView mTvTime;
    TextView mTvDate;
    ListView mLvAvoidReservation;

    ReservationAvoidListAdapter mLaAvoidReservation;

    ProgressView mPvLoading;

    String mTeacherID;
    int mNumOf15Minutes;
    Date mReservedAt = new Date((new Date()).getTime() + 86400000);

    Handler mHandler = new TimeslotsHandler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeslots);
        mActivity = this;

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.black));
        actionBar.setElevation(0f);



        mRlTime = (RelativeLayout)findViewById(R.id.timeslots_rl_time);
        mRlDate = (RelativeLayout)findViewById(R.id.timeslots_rl_date);
        mTvTime = (TextView)findViewById(R.id.timeslots_tv_time);
        mTvDate = (TextView)findViewById(R.id.timeslots_tv_date);
        mPvLoading = (ProgressView)findViewById(R.id.timeslots_pv_loading);
        mLvAvoidReservation = (ListView)findViewById(R.id.timeslots_lv_teacherupcoming);

        mReservedAt.setMinutes(((int)Math.floor(mReservedAt.getMinutes()/15))*15);
        mReservedAt.setSeconds(0);
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("HH:mm");
        mTvTime.setText(dateFormat1.format(mReservedAt));
//        Date nextDay = new Date(mReservedAt.getTime() + 86400000);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        mTvDate.setText(dateFormat.format(mReservedAt));

        mLaAvoidReservation = new ReservationAvoidListAdapter(mActivity);
        mLvAvoidReservation.setAdapter(mLaAvoidReservation);

        mRlTime.setOnClickListener(new RlTimeClickListener());
        mRlDate.setOnClickListener(new RlDateClickListener());

        Intent intent = getIntent();
        mTeacherID = intent.getStringExtra("teacher_id");
        mNumOf15Minutes = intent.getIntExtra("num_of_15_minutes", -1);

        if(mTeacherID == null || mTeacherID.equals("") || mNumOf15Minutes == -1){
            Log.w(tag, "wrong teacher_id or num_of_15_minutes: " + mTeacherID + ", " + mNumOf15Minutes);
            mActivity.finish();
        }

        iniData();
    }

    void iniData(){
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("access_token", Chinessy.chinessy.getUser().getAccessToken());
            jsonParams.put("teacher_id", Integer.parseInt(mTeacherID));
            InternalClient.postInternalJson(mActivity, "reservation/find_teachers_upcoming", jsonParams, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                    super.onSuccess(statusCode, headers, response);
                    try {
                        switch (response.getInt("code")) {
                            case 10000:
                                JSONArray jsonArray = response.getJSONArray("data");
                                ArrayList<Reservation> reservationList = Reservation.loadReservationFromJsonArray(jsonArray);
                                mLaAvoidReservation.setReservationList(reservationList);

                                mHandler.obtainMessage(HANDLER_AFTER_LOAD_TEACHER_UPCOMING).sendToTarget();
                                break;
                            default:
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

    void checkProgress(){
        if(mPvLoading == null){
            mPvLoading = (ProgressView)mActivity.findViewById(R.id.timeslots_pv_loading);
        }
    }
    public void showProgress(){
        checkProgress();
        mPvLoading.setVisibility(View.VISIBLE);
    }
    public void hideProgress(){
        checkProgress();
        mPvLoading.setVisibility(View.INVISIBLE);
    }

    class RlTimeClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
//            Dialog.Builder builder = new TimePickerDialog.Builder(R.style.TimePickerStyle, mReservedAt.getHours(), mReservedAt.getMinutes()){
//                @Override
//                public void onPositiveActionClicked(DialogFragment fragment) {
//                    TimePickerDialog dialog = (TimePickerDialog)fragment.getDialog();
//                    Toast.makeText(mActivity, "Time is " + dialog.getFormattedTime(SimpleDateFormat.getTimeInstance()), Toast.LENGTH_SHORT).show();
//                    mReservedAt.setHours(dialog.getHour());
//                    mReservedAt.setMinutes(dialog.getMinute());
//                    mReservedAt.setSeconds(0);
//
//                    SimpleDateFormat dateFormat1 = new SimpleDateFormat("HH:mm");
//                    mTvTime.setText(dateFormat1.format(mReservedAt));
//                    super.onPositiveActionClicked(fragment);
//                }
//
//                @Override
//                public void onNegativeActionClicked(DialogFragment fragment) {
////                    Toast.makeText(mActivity, "Cancelled" , Toast.LENGTH_SHORT).show();
//                    super.onNegativeActionClicked(fragment);
//                }
//            };
//
//            builder.positiveAction("OK")
//                    .negativeAction("CANCEL");
//            DialogFragment fragment = DialogFragment.newInstance(builder);
//            fragment.show(TimeslotsActivity.this.getSupportFragmentManager(), null);

            CustomTimePickerDialog customTimePickerDialog = new CustomTimePickerDialog(mActivity, new android.app.TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    Toast.makeText(mActivity, hourOfDay + ":" + minute, Toast.LENGTH_SHORT).show();
                    mReservedAt.setHours(hourOfDay);
                    mReservedAt.setMinutes(minute);
                    mReservedAt.setSeconds(0);

                    SimpleDateFormat dateFormat1 = new SimpleDateFormat("hh:mma");
                    mTvTime.setText(dateFormat1.format(mReservedAt));
                }
            }, mReservedAt.getHours(), mReservedAt.getMinutes(), false);
            customTimePickerDialog.show();
        }
    }

    class RlDateClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Date date = new Date();
            Date nextDay = new Date(date.getTime() + 86400000);
            Dialog.Builder builder = null;
//            builder = new DatePickerDialog.Builder(isLightTheme ? R.style.Material_App_Dialog_DatePicker_Light :  R.style.Material_App_Dialog_DatePicker){
            builder = new DatePickerDialog.Builder(R.style.Material_App_Dialog_DatePicker_Light,
                    nextDay.getDate(), nextDay.getMonth(), nextDay.getYear()+1900,
                    31, 11, date.getYear()+1901,
                    nextDay.getDate(), nextDay.getMonth(), nextDay.getYear()+1900){
                @Override
                public void onPositiveActionClicked(DialogFragment fragment) {
                    DatePickerDialog dialog = (DatePickerDialog)fragment.getDialog();
                    String date = dialog.getFormattedDate(SimpleDateFormat.getDateInstance());
                    Toast.makeText(mActivity, "Date is " + date, Toast.LENGTH_SHORT).show();
                    mReservedAt.setDate(dialog.getDay());
                    mReservedAt.setMonth(dialog.getMonth());
                    mReservedAt.setYear(dialog.getYear()-1900);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                    mTvDate.setText(dateFormat.format(mReservedAt));
                    super.onPositiveActionClicked(fragment);
                }

                @Override
                public void onNegativeActionClicked(DialogFragment fragment) {
//                    Toast.makeText(mActivity, "Cancelled" , Toast.LENGTH_SHORT).show();
                    super.onNegativeActionClicked(fragment);
                }
            };
            builder.positiveAction("OK")
                    .negativeAction("CANCEL");
            DialogFragment fragment = DialogFragment.newInstance(builder);
            fragment.show(TimeslotsActivity.this.getSupportFragmentManager(), null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeslots, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        if(id == android.R.id.home){
            TimeslotsActivity.this.finish();
        }

        if(id == R.id.timeslots_menu_done){
            JSONObject jsonParams = new JSONObject();

            try {
                jsonParams.put("num_of_15_minutes", mNumOf15Minutes);
                jsonParams.put("access_token", Chinessy.chinessy.getUser().getAccessToken());
                jsonParams.put("teacher_id", mTeacherID);

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat dateFormat1 = new SimpleDateFormat("HH:mm:00");
                dateFormat.setTimeZone(Config.TIMEZONE_GMT0);
                dateFormat1.setTimeZone(Config.TIMEZONE_GMT0);

                String strReservedAt = dateFormat.format(mReservedAt) + "T" + dateFormat1.format(mReservedAt) + "Z";
                jsonParams.put("reserved_at", strReservedAt);

                InternalClient.postInternalJson(mActivity, "reservation/create", jsonParams, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        try {
                            switch (response.getInt("code")) {
                                case 10000:
                                    final SimpleDialog promptDialog = new SimpleDialog(mActivity);
                                    promptDialog.title(R.string.reservation_succeed_title);
                                    promptDialog.message(R.string.reservation_succeed_message);
                                    promptDialog.positiveAction(R.string.OK);
                                    promptDialog.positiveActionClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            promptDialog.cancel();
                                            mActivity.finish();
                                            ReservationFragment.setIsNeed2Refresh(true);
                                        }
                                    });
                                    promptDialog.show();
                                    break;
                                case 20001:
                                    final SimpleDialog promptDialog1 = new SimpleDialog(mActivity);
                                    promptDialog1.title(R.string.reservation_need_to_pay_title);
                                    promptDialog1.message(R.string.reservation_need_to_pay_message);
                                    promptDialog1.positiveAction(R.string.OK);
                                    promptDialog1.positiveActionClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            promptDialog1.cancel();
                                        }
                                    });
                                    promptDialog1.show();
                                    break;
                                case 20002:
                                    final SimpleDialog promptDialog2 = new SimpleDialog(mActivity);
                                    promptDialog2.title(R.string.reservation_conflict_title);
                                    promptDialog2.message(R.string.reservation_conflict_message);
                                    promptDialog2.positiveAction(R.string.OK);
                                    promptDialog2.positiveActionClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            promptDialog2.cancel();
                                        }
                                    });
                                    promptDialog2.show();
                                    break;
                                default:
                                    final SimpleDialog promptDialog3 = new SimpleDialog(mActivity);
                                    promptDialog3.title(R.string.practice_now_failed_title);
                                    promptDialog3.message(R.string.practice_now_failed_message);
                                    promptDialog3.positiveAction(R.string.OK);
                                    promptDialog3.positiveActionClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            promptDialog3.cancel();
                                        }
                                    });
                                    promptDialog3.show();
                                    Log.w(tag, response.toString());
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

        return super.onOptionsItemSelected(item);
    }

    class TimeslotsHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case HANDLER_AFTER_LOAD_TEACHER_UPCOMING:
                    mLaAvoidReservation.notifyDataSetChanged();
                    int count = mLaAvoidReservation.getCount();
                    hideProgress();
                    break;
            }
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
