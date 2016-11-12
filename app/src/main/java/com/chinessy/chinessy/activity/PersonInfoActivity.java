package com.chinessy.chinessy.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chinessy.chinessy.Chinessy;
import com.chinessy.chinessy.Config;
import com.chinessy.chinessy.R;
import com.chinessy.chinessy.clients.InternalClient;
import com.chinessy.chinessy.handlers.SimpleJsonHttpResponseHandler;
import com.chinessy.chinessy.models.ModifyObject;
import com.chinessy.chinessy.models.User;
import com.chinessy.chinessy.models.UserProfile;
import com.countrypicker.CountryPicker;
import com.countrypicker.CountryPickerListener;
import com.rey.material.app.SimpleDialog;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class PersonInfoActivity extends AppCompatActivity {
    final String tag = "PersonInfoActivity";
    final String mEgPhone = "e.g +1 xxxxxxxxx";

    EditText mEtName;
//    EditText mEtPhone;
//    EditText mEtCountryCode;
    TextView mTvCountry;
    TextView mTvEmail;
    TextView mTvCountryCode;

    LinearLayout mRlName;
//    LinearLayout mRlPhone;
//    LinearLayout mLlPhoneAndCountryCode;
    LinearLayout mLlCountry;
//    LinearLayout mLlCountryCode;
    RelativeLayout mRlEmail;

    MenuItem mMiSave;

    ProgressDialog mProgressDialog;

    Activity mActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_info);
        mActivity = this;

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.black));
        actionBar.setElevation(0f);



        mEtName = (EditText)findViewById(R.id.pinfo_et_name);
//        mEtPhone = (EditText)findViewById(R.id.pinfo_et_phone);
//        mEtCountryCode = (EditText)findViewById(R.id.pinfo_et_countrycode);
        mTvEmail = (TextView)findViewById(R.id.pinfo_tv_email);
        mTvCountry = (TextView)findViewById(R.id.pinfo_et_countrydetail);
        mTvCountryCode = (TextView)findViewById(R.id.pinfo_tv_countrycode);

        mRlName = (LinearLayout)findViewById(R.id.pinfo_rl_name);
//        mRlPhone = (LinearLayout)findViewById(R.id.pinfo_rl_phone);
        mLlCountry = (LinearLayout)findViewById(R.id.pinfo_rl_country);
        mRlEmail = (RelativeLayout)findViewById(R.id.pinfo_rl_email);
//        mLlPhoneAndCountryCode = (LinearLayout)findViewById(R.id.pinfo_ll_phoneandcountrycode);
//        mLlCountryCode = (LinearLayout)findViewById(R.id.pinfo_rl_countrycode);

        mRlName.setOnClickListener(new RlOnClickListener());
//        mRlPhone.setOnClickListener(new RlOnClickListener());
        mLlCountry.setOnClickListener(new RlOnClickListener());
//        mLlCountryCode.setOnClickListener(new RlOnClickListener());

        User user = Chinessy.chinessy.getUser();
        if(user != null){
            mEtName.setText(user.getUserProfile().getName());
            String phone = user.getUserProfile().getPhone();
            phone = phone.equals("") ? mEgPhone : phone;
//            mEtPhone.setText(phone);
            mTvEmail.setText(user.getEmail());
            mTvCountry.setText(user.getUserProfile().getCountry());
//            mEtCountryCode.setText(user.getUserProfile().getCountryCode());
            mTvCountryCode.setText(user.getUserProfile().getCountryCode());
        }else{
            Log.w(tag, "user is:" + user);
        }

        setUnEditable();
    }

    void setEditable(){
        mEtName.setKeyListener((KeyListener) mEtName.getTag());
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)mEtName.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        mEtName.setLayoutParams(layoutParams);

//        mEtPhone.setKeyListener((KeyListener) mEtPhone.getTag());
//        layoutParams = (RelativeLayout.LayoutParams)mLlPhoneAndCountryCode.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//        mLlPhoneAndCountryCode.setLayoutParams(layoutParams);
//        if(mEtPhone.getText().toString().equals(mEgPhone)){
//            mEtPhone.setText("");
//        }

//        mEtCountryCode.setKeyListener((KeyListener) mEtCountryCode.getTag());
//        layoutParams = (RelativeLayout.LayoutParams)mEtCountryCode.getLayoutParams();
//        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//        mEtCountryCode.setLayoutParams(layoutParams);

        mMiSave.setVisible(true);
    }

    public void setEtNameEditable(){
        setEditable();

        mEtName.requestFocus();
        mEtName.setCursorVisible(true);
        mEtName.setSelection(mEtName.getText().toString().length());
    }

//    public void setEtPhoneEditable(){
//        setEditable();
//
//        mEtPhone.requestFocus();
//        mEtPhone.setCursorVisible(true);
//        mEtPhone.setSelection(mEtPhone.getText().toString().length());
//    }

//    public void setEtCountryCodeEditable(){
//        setEditable();
//
//        mEtCountryCode.requestFocus();
//        mEtCountryCode.setCursorVisible(true);
//        mEtCountryCode.setSelection(mEtCountryCode.getText().toString().length());
//    }

    public void setUnEditable(){
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)mEtName.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
        mEtName.setLayoutParams(layoutParams);
        mEtName.setTag(mEtName.getKeyListener());
        mEtName.setKeyListener(null);

//        layoutParams = (RelativeLayout.LayoutParams)mLlPhoneAndCountryCode.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
//        mLlPhoneAndCountryCode.setLayoutParams(layoutParams);
//        mEtPhone.setTag(mEtPhone.getKeyListener());
//        mEtPhone.setKeyListener(null);
//        if(mEtPhone.getText().toString().equals("")){
//            mEtPhone.setText(mEgPhone);
//        }

//        layoutParams = (RelativeLayout.LayoutParams)mEtCountryCode.getLayoutParams();
//        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
//        mEtCountryCode.setLayoutParams(layoutParams);
//        mEtCountryCode.setTag(mEtCountryCode.getKeyListener());
//        mEtCountryCode.setKeyListener(null);

        if(mMiSave != null){
            mMiSave.setVisible(false);
        }
    }

    void showProgressDialog(){
        if(mProgressDialog == null){
            mProgressDialog = new ProgressDialog(mActivity);
        }
        mProgressDialog.setMessage(mActivity.getString(R.string.Waiting));
        mProgressDialog.show();
    }

    void hideProgressDialog(){
        mProgressDialog.cancel();
    }

    class RlOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.pinfo_rl_name:
                    setEtNameEditable();
                    break;
//                case R.id.pinfo_rl_phone:
//                    setEtPhoneEditable();
//                    break;
                case R.id.pinfo_rl_country:
                    final CountryPicker picker = CountryPicker.newInstance("Select Country");
                    picker.setListener(new CountryPickerListener() {

                        @Override
                        public void onSelectCountry(String name, String code) {
                            // Invoke your function here
                            Toast.makeText(mActivity, name + " " + code, Toast.LENGTH_SHORT).show();
                            mTvCountryCode.setText(code);
                            mTvCountry.setText(name);
                            picker.dismiss();
                        }
                    });
                    picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
                    setEtNameEditable();
//                    setEtPhoneEditable();
//                    setEtCountryCodeEditable();
                    break;
//                case R.id.pinfo_rl_countrycode:
//                    setEtCountryCodeEditable();
//                    break;
            }
        }
    }

    class RlNameClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(mActivity, ModifyActivity.class);
            ModifyObject modifyObject = new ModifyObject();
            modifyObject.setFieldName("name");
            modifyObject.setFieldValue(mEtName.getText().toString());
            modifyObject.setTitle(mActivity.getString(R.string.Full_Name));
            modifyObject.setModifyUrl("user_profile/update");
            intent.putExtra("modify_object", modifyObject);
            mActivity.startActivityForResult(intent, Config.ACTIVITY_RESULT_MODIFY_NAME);
        }
    }

    class RlPhoneClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(mActivity, ModifyActivity.class);
            ModifyObject modifyObject = new ModifyObject();
            modifyObject.setFieldName("phone");
            modifyObject.setFieldValue(Chinessy.chinessy.getUser().getUserProfile().getPhone());
            modifyObject.setTitle(mActivity.getString(R.string.Phone_Number));
            modifyObject.setModifyUrl("user_profile/update");
            intent.putExtra("modify_object", modifyObject);
            mActivity.startActivityForResult(intent, Config.ACTIVITY_RESULT_MODIFY_PHONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_person_info, menu);

        mMiSave = menu.findItem(R.id.pinfo_menu_save);
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
            PersonInfoActivity.this.finish();
        }else if(id == R.id.pinfo_menu_save){
            if(mEtName.getText().toString().equals("")){
                final SimpleDialog simpleDialog = new SimpleDialog(mActivity);
                simpleDialog.message(R.string.dialog_info_not_complete_message);
                simpleDialog.positiveAction(R.string.OK);
                simpleDialog.positiveActionClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        simpleDialog.cancel();
                    }
                });
                simpleDialog.show();
                return super.onOptionsItemSelected(item);
            }
            showProgressDialog();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("access_token", Chinessy.chinessy.getUser().getAccessToken());
                jsonObject.put("country", mTvCountry.getText());
                jsonObject.put("country_code", mTvCountryCode.getText().toString());
                jsonObject.put("name", mEtName.getText().toString());
//                jsonObject.put("phone", mEtPhone.getText().toString());

                InternalClient.postInternalJson(mActivity, "user_profile/update", jsonObject, new SimpleJsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            switch (response.getInt("code")) {
                                case 10000:
                                    Toast.makeText(mActivity, R.string.Save_succeed, Toast.LENGTH_SHORT).show();
                                    setUnEditable();
                                    afterModify();
                                    hideProgressDialog();

                                    mActivity.finish();
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case Config.ACTIVITY_RESULT_MODIFY_NAME:
                if(resultCode == RESULT_OK){
//                    Toast.makeText(mActivity, "RESULT_OK", Toast.LENGTH_SHORT).show();
                    ModifyObject modifyObject = (ModifyObject)data.getSerializableExtra("modify_object");
                    Chinessy.chinessy.getUser().getUserProfile().setName(modifyObject.getFieldValue(), mActivity);
                    mEtName.setText(modifyObject.getFieldValue());
                }else{
//                    Toast.makeText(mActivity, "NOT_OK", Toast.LENGTH_SHORT).show();
                }
                break;
            case Config.ACTIVITY_RESULT_MODIFY_PHONE:
                if(resultCode == RESULT_OK){
                    ModifyObject modifyObject = (ModifyObject)data.getSerializableExtra("modify_object");
                    Chinessy.chinessy.getUser().getUserProfile().setPhone(modifyObject.getFieldValue(), mActivity);
//                    mEtPhone.setText(modifyObject.getFieldValue());
                }else{

                }
                break;
        }
    }

    void afterModify(){
        UserProfile userProfile = Chinessy.chinessy.getUser().getUserProfile();

//        userProfile.setPhone(mEtPhone.getText().toString(), mActivity);
        userProfile.setName(mEtName.getText().toString(), mActivity);
        userProfile.setCountry(mTvCountry.getText().toString(), mActivity);
        userProfile.setCountryCode(mTvCountryCode.getText().toString(), mActivity);
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
