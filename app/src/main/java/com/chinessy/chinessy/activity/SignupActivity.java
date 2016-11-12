package com.chinessy.chinessy.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chinessy.chinessy.Chinessy;
import com.chinessy.chinessy.MainActivity;
import com.chinessy.chinessy.R;
import com.chinessy.chinessy.clients.InternalClient;
import com.chinessy.chinessy.handlers.SimpleJsonHttpResponseHandler;
import com.chinessy.chinessy.models.User;
import com.chinessy.chinessy.utils.Utils;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;


public class SignupActivity extends ActionBarActivity {
    private final int AFTER_SIGNUP_HANDLER = 10;

    private Activity mActivity = this;
    private Handler mSignupHandler = new SignupHandler();

    EditText mEtName;
    EditText mEtPhone;
    EditText mEtPassword;
    EditText mEtInvitationCode;
    Button mBtnSignup;

    View mLoginFormView;
    View mProgressView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.black));
        actionBar.setElevation(0f);



        mLoginFormView = findViewById(R.id.signup_form);
        mProgressView = findViewById(R.id.signup_progress);

        mEtName = (EditText)findViewById(R.id.signup_et_name);
        mEtPhone = (EditText)findViewById(R.id.signup_et_phone);
        mEtPassword = (EditText)findViewById(R.id.signup_et_password);
        mEtInvitationCode = (EditText)findViewById(R.id.signup_et_invitation_code);

        mBtnSignup = (Button)findViewById(R.id.signup_btn_signup);
        mBtnSignup.setOnClickListener(new SignupClickListner());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_signup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case android.R.id.home:
                SignupActivity.this.finish();
                break;
            case R.id.signup_menu_login:
                Intent intent = new Intent();
                intent.setClass(SignupActivity.this, LoginActivity.class);
                SignupActivity.this.startActivity(intent);
                SignupActivity.this.finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    class SignupHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case AFTER_SIGNUP_HANDLER:
                    Intent intent = new Intent();
                    intent.setClass(mActivity, MainActivity.class);
                    mActivity.startActivity(intent);
                    mActivity.finish();
                    GuideActivity.finishGuideActivity();
                    break;
            }
        }
    }

    class SignupClickListner implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            String name = mEtName.getText().toString();
            String email = mEtPhone.getText().toString();
            String password = mEtPassword.getText().toString();
            String invitationCode = mEtInvitationCode.getText().toString();

            if(name.equals("")){
                mEtName.setError(mActivity.getString(R.string.error_name_is_invalid));
                mEtName.requestFocus();
                return;
            }

            if(!isEmailInvalid(email)){
                mEtPhone.setError(mActivity.getString(R.string.error_email_is_invalid));
                mEtPhone.requestFocus();
                return;
            }
            if(!isPasswordInvalid(password)){
                mEtPassword.setError(mActivity.getString(R.string.error_password_is_invalid));
                mEtPassword.requestFocus();
                return;
            }
//            if(!isInvitationCodeInvalid(invitationCode)){
//                mEtInvitationCode.setError(mActivity.getString(R.string.error_invitation_code_is_invalid));
//                mEtInvitationCode.requestFocus();
//                return;
//            }

            JSONObject jsonParams = new JSONObject();
            try {
                jsonParams.put("name", name);
                jsonParams.put("email", email);
                jsonParams.put("password", password);
                jsonParams.put("referee_code", invitationCode);

                Utils.getPhoneInfo(jsonParams);
                Utils.getPhoneResolution(mActivity, jsonParams);
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(mActivity, R.string.params_error, Toast.LENGTH_SHORT).show();
                return;
            }
            showProgress(true);
            InternalClient.postInternalJson(mActivity, "signup", jsonParams, new SimpleJsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        switch (response.getInt("code")) {
                            case 10000:
                                JSONObject data = response.getJSONObject("data");
                                User user = new User(data);
                                Chinessy.chinessy.afterLogin(user);
                                mSignupHandler.sendEmptyMessage(AFTER_SIGNUP_HANDLER);
                                break;
                            case 10003:
                                Toast.makeText(mActivity, R.string.error_invalid_password, Toast.LENGTH_SHORT).show();
                                break;
                            case 10005:
                                Toast.makeText(mActivity, R.string.error_account_already_exists, Toast.LENGTH_SHORT).show();
                                break;
                            case 10009:
                                Toast.makeText(mActivity, R.string.error_invitation_code_is_invalid, Toast.LENGTH_SHORT).show();
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        showProgress(false);
                    }
                }
            });
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    boolean isEmailInvalid(String email){
        if(!email.contains("@") || !email.contains(".")){
            return false;
        }
        return true;
    }
    boolean isPasswordInvalid(String password){
        if(password.length() < 6){
            return false;
        }
        return true;
    }
    boolean isInvitationCodeInvalid(String invitationCode){
        if(invitationCode.length() < 6 && !invitationCode.equals("")){
            return false;
        }
        return true;
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
