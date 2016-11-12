package com.chinessy.chinessy.activity;

import android.app.Activity;
import android.support.v7.app.ActionBar;
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
import com.chinessy.chinessy.models.User;
import com.rey.material.app.SimpleDialog;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class PromotionActivity extends AppCompatActivity {
    Activity mActivity;
    EditText mEtPromotionCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion);
        mActivity = this;
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.black));
        actionBar.setElevation(0f);


        mEtPromotionCode = (EditText)findViewById(R.id.promotion_et_promotioncode);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_promotion, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == android.R.id.home){
            mActivity.finish();
        }else if(id == R.id.promotion_menu_apply){
            String code = mEtPromotionCode.getText().toString();
            if(code.equals("")){
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

                InternalClient.postJson(mActivity, "promotion_code/use", jsonParams, new SimpleJsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            switch (response.getInt("code")){
                                case 10000:
                                    User.updateUserBalance(mActivity, response);
                                    final SimpleDialog promptDialog = new SimpleDialog(mActivity);
                                    promptDialog.title(R.string.promotion_code_redeem_succeed_title);
                                    promptDialog.message(R.string.promotion_code_redeem_succeed_message);
                                    promptDialog.positiveAction(R.string.OK);
                                    promptDialog.positiveActionClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            promptDialog.cancel();
                                            PromotionActivity.this.finish();
                                        }
                                    });
                                    promptDialog.show();
                                    break;
                                case 10006:
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
                                    break;
                                case 30001:
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
                                    break;
                                case 30002:
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
                                    break;
                                case 30003:
                                    final SimpleDialog promptDialog4 = new SimpleDialog(mActivity);
                                    promptDialog4.message(R.string.promotion_only_for_discount);
                                    promptDialog4.positiveAction(R.string.OK);
                                    promptDialog4.positiveActionClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            promptDialog4.cancel();
                                        }
                                    });
                                    promptDialog4.show();
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
