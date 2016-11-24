package com.chinessy.chinessy.dialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.chinessy.chinessy.activity.PersonInfoActivity;
import com.chinessy.chinessy.R;
import com.chinessy.chinessy.models.ButtonData;
import com.chinessy.chinessy.models.User;
import com.rey.material.app.BottomSheetDialog;
import com.rey.material.app.SimpleDialog;

/**
 * Created by larry on 15/8/20.
 */
public class ReservationDialog extends BottomSheetDialog{

    ToggleButton mTgbtn15Min;
    ToggleButton mTgbtn30Min;
    ToggleButton mTgbtn45Min;
    ToggleButton mTgbtn60Min;

    TextView mTvName;
    TextView mTvEdit;

    Button mBtnApply;

    int mNumOf15Minutes = 1;

    Context mContext;
    public ReservationDialog(Context context, User teacher, User student) {
        super(context);

        mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_reservation, null);
        mTvName = (TextView)view.findViewById(R.id.dlgreservation_tv_name);
        TextView tvPhone = (TextView)view.findViewById(R.id.dlgreservation_tv_phone);
        TextView tvBalance = (TextView)view.findViewById(R.id.dlgreservation_tv_balance);
        mBtnApply = (Button)view.findViewById(R.id.dlgreservation_btn_apply);
        ImageView ivHeadImg = (ImageView)view.findViewById(R.id.dlgreservation_iv_headimg);
        mTgbtn15Min = (ToggleButton)view.findViewById(R.id.dlgreservation_tgbtn_15min);
        mTgbtn30Min = (ToggleButton)view.findViewById(R.id.dlgreservation_tgbtn_30min);
        mTgbtn45Min = (ToggleButton)view.findViewById(R.id.dlgreservation_tgbtn_45min);
        mTgbtn60Min = (ToggleButton)view.findViewById(R.id.dlgreservation_tgbtn_60min);
        mTvEdit = (TextView)view.findViewById(R.id.dlgreservation_tv_edit);

        mTgbtn15Min.setOnCheckedChangeListener(new TgbtnMinCheckListener());
        mTgbtn30Min.setOnCheckedChangeListener(new TgbtnMinCheckListener());
        mTgbtn45Min.setOnCheckedChangeListener(new TgbtnMinCheckListener());
        mTgbtn60Min.setOnCheckedChangeListener(new TgbtnMinCheckListener());

        mTvEdit.setOnClickListener(new TvEditOnClickListener());

        mTvName.setText("Schedule with " + teacher.getUserProfile().getName());
        tvPhone.setText(student.getUserProfile().getCountryCode() + " " + student.getUserProfile().getPhone());
        tvBalance.setText("(Your balance:" + student.getUserProfile().getNumOf15Minutes() * 15 + "min)");

        if(teacher.getUserProfile().getHeadImg().getBitmap() != null){
            ivHeadImg.setImageBitmap(teacher.getUserProfile().getHeadImg().getBitmap());
        }

        this.contentView(view);
        this.cancelable(true);
        this.inDuration(300);
    }

    class TvEditOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(mContext, PersonInfoActivity.class);
            mContext.startActivity(intent);
        }
    }

    class TgbtnMinCheckListener implements CompoundButton.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){
                switch (buttonView.getId()){
                    case R.id.dlgreservation_tgbtn_15min:
                        setNumOf15Minutes(1);

                        mTgbtn15Min.setTextColor(Color.WHITE);
                        mTgbtn30Min.setTextColor(Color.BLACK);
                        mTgbtn45Min.setTextColor(Color.BLACK);
                        mTgbtn60Min.setTextColor(Color.BLACK);

                        mTgbtn30Min.setChecked(false);
                        mTgbtn45Min.setChecked(false);
                        mTgbtn60Min.setChecked(false);
                        break;
                    case R.id.dlgreservation_tgbtn_30min:
                        setNumOf15Minutes(2);

                        mTgbtn15Min.setTextColor(Color.BLACK);
                        mTgbtn30Min.setTextColor(Color.WHITE);
                        mTgbtn45Min.setTextColor(Color.BLACK);
                        mTgbtn60Min.setTextColor(Color.BLACK);

                        mTgbtn15Min.setChecked(false);
                        mTgbtn45Min.setChecked(false);
                        mTgbtn60Min.setChecked(false);
                        break;
                    case R.id.dlgreservation_tgbtn_45min:
                        setNumOf15Minutes(3);

                        mTgbtn15Min.setTextColor(Color.BLACK);
                        mTgbtn30Min.setTextColor(Color.BLACK);
                        mTgbtn45Min.setTextColor(Color.WHITE);
                        mTgbtn60Min.setTextColor(Color.BLACK);

                        mTgbtn15Min.setChecked(false);
                        mTgbtn30Min.setChecked(false);
                        mTgbtn60Min.setChecked(false);
                        break;
                    case R.id.dlgreservation_tgbtn_60min:
                        setNumOf15Minutes(4);

                        mTgbtn15Min.setTextColor(Color.BLACK);
                        mTgbtn30Min.setTextColor(Color.BLACK);
                        mTgbtn45Min.setTextColor(Color.BLACK);
                        mTgbtn60Min.setTextColor(Color.WHITE);

                        mTgbtn15Min.setChecked(false);
                        mTgbtn30Min.setChecked(false);
                        mTgbtn45Min.setChecked(false);
                        break;
                }
            }
        }
    }

    public void setButtonData(ButtonData buttonData){
        if(buttonData != null){
            mBtnApply.setText(buttonData.getBtnText());
            mBtnApply.setOnClickListener(buttonData.getBtnClickListener());
        }
    }

    public void setTitle(String title){
        mTvName.setText(title);
    }

    public void inputUserPhone(){

        final SimpleDialog simpleDialog = new SimpleDialog(mContext);
        simpleDialog.title(R.string.dialog_reservation_no_phone_number_title);
        simpleDialog.message(R.string.dialog_reservation_no_phone_number_message);
        simpleDialog.positiveAction(R.string.Continue);
        simpleDialog.positiveActionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContext, PersonInfoActivity.class);
                mContext.startActivity(intent);
                simpleDialog.cancel();
                ReservationDialog.this.cancel();
            }
        });

        simpleDialog.show();
    }

    public int getNumOf15Minutes() {
        return mNumOf15Minutes;
    }

    public void setNumOf15Minutes(int numOf15Minutes) {
        this.mNumOf15Minutes = numOf15Minutes;
    }
}
