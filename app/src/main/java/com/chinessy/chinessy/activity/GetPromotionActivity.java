package com.chinessy.chinessy.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.chinessy.chinessy.Chinessy;
import com.chinessy.chinessy.R;
import com.chinessy.chinessy.clients.InternalClient;
import com.chinessy.chinessy.handlers.SimpleJsonHttpResponseHandler;
import com.chinessy.chinessy.models.PromotionCode;
import com.rey.material.app.SimpleDialog;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class GetPromotionActivity extends AppCompatActivity {
    Activity mActivity;
    EditText mEtPromotionCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_promotion);
        mActivity = this;
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.black));
        actionBar.setElevation(0f);

        mEtPromotionCode = (EditText) findViewById(R.id.getpromotion_et_promotioncode);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_get_promotion, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            mActivity.finish();
        } else if (id == R.id.getpromotion_menu_apply) {
            String code = mEtPromotionCode.getText().toString();
            if (code.equals("")) {


                final SimpleDialog promptDialog1 = new SimpleDialog(mActivity);
                promptDialog1.title(R.string.promotion_code_invalid_title);
                promptDialog1.message(R.string.promotion_code_invalid_message);
                promptDialog1.positiveAction(R.string.OK);
                promptDialog1.positiveActionClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        promptDialog1.cancel();
                    }
                });
                promptDialog1.show();
                return super.onOptionsItemSelected(item);
            }
            JSONObject jsonParams = new JSONObject();
            try {
                jsonParams.put("access_token", Chinessy.chinessy.getUser().getAccessToken());
                jsonParams.put("promotion_code", code);

                InternalClient.postJson(mActivity, "promotion_code/get", jsonParams, new SimpleJsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            switch (response.getInt("code")) {
                                case 10000:
                                    PromotionCode promotionCode = new PromotionCode(response.getJSONObject("data"));
                                    Intent intent = new Intent();
                                    intent.putExtra("promotion_code", promotionCode);
                                    GetPromotionActivity.this.setResult(Activity.RESULT_OK, intent);
                                    GetPromotionActivity.this.finish();
                                    break;
                                case 10006:
                                    AlertDialog.Builder promptDialog1 = new AlertDialog.Builder(mActivity);  //先得到构造器
                                    promptDialog1.setTitle(R.string.promotion_code_invalid_title);
                                    promptDialog1.setMessage(R.string.promotion_code_invalid_message); //设置内容
                                    promptDialog1.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() { //设置确定按钮
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss(); //关闭dialog
                                        }
                                    });

                                    //参数都设置完成了，创建并显示出来
                                    promptDialog1.create().show();
/*
                                    final SimpleDialog promptDialog1 = new SimpleDialog(mActivity);
                                    promptDialog1.title(R.string.promotion_code_invalid_title);
                                    promptDialog1.message(R.string.promotion_code_invalid_message);
                                    promptDialog1.positiveAction(R.string.OK);
                                    promptDialog1.positiveActionClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            promptDialog1.cancel();
                                        }
                                    });
                                    promptDialog1.show();
                                    */
                                    break;
                                case 30001:
                                    AlertDialog.Builder promptDialog2 = new AlertDialog.Builder(mActivity);  //先得到构造器
                                    promptDialog2.setTitle(R.string.promotion_code_expired_title);
                                    promptDialog2.setMessage(R.string.promotion_code_expired_message); //设置内容
                                    promptDialog2.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() { //设置确定按钮
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss(); //关闭dialog
                                        }
                                    });

                                    //参数都设置完成了，创建并显示出来
                                    promptDialog2.create().show();
/*
                                    final SimpleDialog promptDialog2 = new SimpleDialog(mActivity);
                                    promptDialog2.title(R.string.promotion_code_expired_title);
                                    promptDialog2.message(R.string.promotion_code_expired_message);
                                    promptDialog2.positiveAction(R.string.OK);
                                    promptDialog2.positiveActionClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            promptDialog2.cancel();
                                        }
                                    });
                                    promptDialog2.show();
                                    */
                                    break;
                                case 30002:
                                    AlertDialog.Builder promptDialog3 = new AlertDialog.Builder(mActivity);  //先得到构造器
                                    promptDialog3.setTitle(R.string.promotion_code_already_used_title);
                                    promptDialog3.setMessage(R.string.promotion_code_already_used_message); //设置内容
                                    promptDialog3.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() { //设置确定按钮
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss(); //关闭dialog
                                        }
                                    });

                                    //参数都设置完成了，创建并显示出来
                                    promptDialog3.create().show();
/*
                                    final SimpleDialog promptDialog3 = new SimpleDialog(mActivity);
                                    promptDialog3.title(R.string.promotion_code_already_used_title);
                                    promptDialog3.message(R.string.promotion_code_already_used_message);
                                    promptDialog3.positiveAction(R.string.OK);
                                    promptDialog3.positiveActionClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            promptDialog3.cancel();
                                        }
                                    });
                                    promptDialog3.show();
                                    */
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
}
