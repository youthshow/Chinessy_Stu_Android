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

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chinessy.chinessy.Chinessy;
import com.chinessy.chinessy.MainActivity;
import com.chinessy.chinessy.R;
import com.chinessy.chinessy.beans.BasicBean;
import com.chinessy.chinessy.beans.EmailVerificationCode;
import com.chinessy.chinessy.clients.ConstValue;
import com.chinessy.chinessy.utils.LogUtils;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;

import cz.msebera.android.httpclient.util.TextUtils;


public class SignupActivity extends ActionBarActivity {
    private final int AFTER_SIGNUP_HANDLER = 10;

    private Activity mActivity = this;
    private Handler mSignupHandler = new SignupHandler();

    EditText mEtName;
    EditText mEtEmail;
    EditText mEtPassword;
    EditText mEtInvitationCode;
    Button mBtnSignup;
    Button mBtnNext;
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

        mEtName = (EditText) findViewById(R.id.signup_et_name);
        mEtEmail = (EditText) findViewById(R.id.signup_et_email);
        mEtPassword = (EditText) findViewById(R.id.signup_et_password);
        mEtInvitationCode = (EditText) findViewById(R.id.signup_et_email_code);
        mBtnNext = (Button) findViewById(R.id.signup_btn_next);
        mBtnNext.setOnClickListener(new BtnNextOnClickListener());

    }

    private String name;
    private String email;
    private String password;
    private String invitationCode;

    class BtnNextOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            name = mEtName.getText().toString();
            email = mEtEmail.getText().toString();
            password = mEtPassword.getText().toString();
            invitationCode = mEtInvitationCode.getText().toString();

            if (name.equals("")) {
                mEtName.setError(mActivity.getString(R.string.error_name_is_invalid));
                mEtName.requestFocus();
                return;
            }

            if (!isEmailInvalid(email)) {
                mEtEmail.setError(mActivity.getString(R.string.error_email_is_invalid));
                mEtEmail.requestFocus();
                return;
            }
            if (!isPasswordInvalid(password)) {
                mEtPassword.setError(mActivity.getString(R.string.error_password_is_invalid));
                mEtPassword.requestFocus();
                return;
            }

            //            if(!isInvitationCodeInvalid(invitationCode)){
            //                mEtInvitationCode.setError(mActivity.getString(R.string.error_invitation_code_is_invalid));
            //                mEtInvitationCode.requestFocus();
            //                return;
            //            }


            ToNextWebRequest();
        }
    }

    private void ToNextWebRequest() {
        showProgress(true);
        StringRequest stringRequest = new StringRequest(ConstValue.CODE + "?" + ConstValue.mail + "=" + email,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (TextUtils.isEmpty(response)) {
                            Toast.makeText(SignupActivity.this, R.string.send_code_to_email_faill, Toast.LENGTH_SHORT).show();
                        } else {

                            BasicBean basicBean = new Gson().fromJson(response.toString(), BasicBean.class);
                            if ("true".equals(basicBean.getStatus().toString())) {
                                EmailVerificationCode emailVerificationCode = new Gson().fromJson(response.toString(), EmailVerificationCode.class);
                                Intent intent = new Intent(SignupActivity.this, SignUpSucessActivity.class);
                                intent.putExtra("Mail", email);
                                intent.putExtra("password", password);
                                intent.putExtra("referee_code", invitationCode);
                                intent.putExtra("name", name);
                                startActivity(intent);
                            }

                        }
                        showProgress(false);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showProgress(false);
                LogUtils.d(ConstValue.mail + " :error-->" + error.toString());
            }
        });
        Chinessy.requestQueue.add(stringRequest);
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
        switch (item.getItemId()) {
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

    class SignupHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
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

    boolean isEmailInvalid(String email) {
        if (!email.contains("@") || !email.contains(".")) {
            return false;
        }
        return true;
    }

    boolean isPasswordInvalid(String password) {
        if (password.length() < 6) {
            return false;
        }
        return true;
    }

    boolean isInvitationCodeInvalid(String invitationCode) {
        if (invitationCode.length() < 6 && !invitationCode.equals("")) {
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
