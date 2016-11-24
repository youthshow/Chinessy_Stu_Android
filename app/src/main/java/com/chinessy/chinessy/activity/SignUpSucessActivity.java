package com.chinessy.chinessy.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
import com.chinessy.chinessy.clients.InternalClient;
import com.chinessy.chinessy.handlers.SimpleJsonHttpResponseHandler;
import com.chinessy.chinessy.models.User;
import com.chinessy.chinessy.utils.LogUtils;
import com.chinessy.chinessy.utils.Utils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class SignUpSucessActivity extends AppCompatActivity {
    private EditText mEtEmailCode;
    private Button mBtnDone;
    View mProgressView;

    private String Mail;
    private String password;
    private String invitationCode;
    private String name;
    private String emailCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_sucess);

        Intent intent = getIntent();
        Mail = intent.getStringExtra("Mail");
        password = intent.getStringExtra("password");
        invitationCode = intent.getStringExtra("referee_code");
        name = intent.getStringExtra("name");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.black));
        actionBar.setElevation(0f);


        mProgressView = findViewById(R.id.signup_progress);
        mEtEmailCode = (EditText) findViewById(R.id.signupsucess_et_email_code);
        mBtnDone = (Button) findViewById(R.id.signup_btn_done);
        mBtnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getEmailCodeWebRequest();

            }
        });
    }


    private void getEmailCodeWebRequest() {
        emailCode = mEtEmailCode.getText().toString();
        showProgress(true);
        StringRequest stringRequest = new StringRequest(ConstValue.VAILD + "?" + ConstValue.mail + "=" + Mail + "&" + ConstValue.code + "=" + emailCode,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        LogUtils.d(ConstValue.VAILD + " :-->" + response.toString());
                        BasicBean basicBean = new Gson().fromJson(response.toString(), BasicBean.class);
                        if ("true".equals(basicBean.getStatus().toString())) {
                            signup();
                        } else {
                            Toast.makeText(SignUpSucessActivity.this, R.string.WrongCode, Toast.LENGTH_SHORT).show();
                            showProgress(false);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showProgress(false);
                LogUtils.d(ConstValue.VAILD + " code:error-->" + error.toString());
            }
        });
        Chinessy.requestQueue.add(stringRequest);
    }


    private void signup() {
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("name", name);
            jsonParams.put("email", Mail);
            jsonParams.put("password", password);
            jsonParams.put("referee_code", invitationCode);

            Utils.getPhoneInfo(jsonParams);
            Utils.getPhoneResolution(SignUpSucessActivity.this, jsonParams);


        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(SignUpSucessActivity.this, R.string.params_error, Toast.LENGTH_SHORT).show();
            showProgress(false);
            return;
        }
        showProgress(true);
        InternalClient.postInternalJson(SignUpSucessActivity.this, "signup", jsonParams, new SimpleJsonHttpResponseHandler() {
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
                            Toast.makeText(SignUpSucessActivity.this, R.string.error_invalid_password, Toast.LENGTH_SHORT).show();
                            break;
                        case 10005:
                            Toast.makeText(SignUpSucessActivity.this, R.string.error_account_already_exists, Toast.LENGTH_SHORT).show();
                            break;
                        case 10009:
                            Toast.makeText(SignUpSucessActivity.this, R.string.error_invitation_code_is_invalid, Toast.LENGTH_SHORT).show();
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

    private Handler mSignupHandler = new SignupHandler();
    private final int AFTER_SIGNUP_HANDLER = 10;

    class SignupHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case AFTER_SIGNUP_HANDLER:
                    Intent intent = new Intent();
                    intent.setClass(SignUpSucessActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
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
        }
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
                SignUpSucessActivity.this.finish();
                break;
            case R.id.signup_menu_login:
                Intent intent = new Intent();
                intent.setClass(SignUpSucessActivity.this, LoginActivity.class);
                SignUpSucessActivity.this.startActivity(intent);
                SignUpSucessActivity.this.finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
