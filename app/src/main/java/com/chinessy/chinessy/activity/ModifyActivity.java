package com.chinessy.chinessy.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
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
import com.chinessy.chinessy.models.ModifyObject;
import com.rey.material.app.SimpleDialog;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class ModifyActivity extends AppCompatActivity {
    final int HANDLER_MODIFY_FINISHED = 100;
    final int HANDLER_MODIFY_FAILED = 101;
    Activity mActivity;
    ModifyObject mModifyObject = new ModifyObject();

    EditText mEtContent;

    Handler modifyHandler = new ModifyHandler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);
        mActivity = this;

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.black));
        actionBar.setElevation(0f);



        Intent intent = getIntent();
        mModifyObject = (ModifyObject)intent.getSerializableExtra("modify_object");

        mEtContent = (EditText)findViewById(R.id.modify_et_content);
        mEtContent.setHint(mModifyObject.getFieldName());
        mEtContent.setText(mModifyObject.getFieldValue());

        actionBar.setTitle(mModifyObject.getTitle());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_modify, menu);
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
        }else if(id == R.id.modify_menu_apply) {
            JSONObject json = new JSONObject();
            try {
                json.put("access_token", Chinessy.chinessy.getUser().getAccessToken());
                mModifyObject.setFieldValue(mEtContent.getText().toString());
                json.put(mModifyObject.getFieldName(), mModifyObject.getFieldValue());

                InternalClient.postInternalJson(mActivity, mModifyObject.getModifyUrl(), json, new SimpleJsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        try {
                            switch (response.getInt("code")) {
                                case 10000:
                                    final SimpleDialog simpleDialog = new SimpleDialog(mActivity);
                                    simpleDialog.title(R.string.modify_succeed_title);
                                    simpleDialog.positiveAction(R.string.OK);
                                    simpleDialog.positiveActionClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            simpleDialog.cancel();
                                            Intent intent = new Intent();
                                            intent.putExtra("modify_object", mModifyObject);
                                            mActivity.setResult(RESULT_OK, intent);
//                                            mActivity.finishActivity(Config.ACTIVITY_RESULT_MODIFY_NAME);
                                            mActivity.finish();
                                        }
                                    });
                                    simpleDialog.show();
                                    modifyHandler.obtainMessage(HANDLER_MODIFY_FINISHED).sendToTarget();
                                    break;
                                default:
                                    final SimpleDialog simpleDialog1 = new SimpleDialog(mActivity);
                                    simpleDialog1.title(R.string.modify_failed_title);
                                    simpleDialog1.message(R.string.modify_failed_message);
                                    simpleDialog1.positiveAction(R.string.OK);
                                    simpleDialog1.positiveActionClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            simpleDialog1.cancel();
                                        }
                                    });
                                    simpleDialog1.show();
                                    modifyHandler.obtainMessage(HANDLER_MODIFY_FAILED).sendToTarget();
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
                return super.onOptionsItemSelected(item);
            }
        }

        return super.onOptionsItemSelected(item);
    }
    class ModifyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case HANDLER_MODIFY_FINISHED:
                    break;
                case HANDLER_MODIFY_FAILED:
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
