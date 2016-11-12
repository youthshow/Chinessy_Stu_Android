package com.chinessy.chinessy.activity;

import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.chinessy.chinessy.Chinessy;
import com.chinessy.chinessy.R;
import com.chinessy.chinessy.adapter.SubscriptionsListAdapter;
import com.chinessy.chinessy.clients.InternalClient;
import com.chinessy.chinessy.handlers.SimpleJsonHttpResponseHandler;
import com.chinessy.chinessy.models.UserBalancePackage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class SubscriptionsActivity extends AppCompatActivity {
    Activity mActivity;
    ListView mLvSubscriptionsList;
    SubscriptionsListAdapter mLaSubscriptionsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscriptions);
        mActivity = this;ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.black));
        actionBar.setElevation(0f);

        mLvSubscriptionsList = (ListView)findViewById(R.id.subscriptions_lv_list);

        iniList();
    }

    void iniList(){
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("access_token", Chinessy.chinessy.getUser().getAccessToken());

            InternalClient.postJson(mActivity, "internal/user/get_balance_package", jsonObject, new SimpleJsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        switch (response.getInt("code")){
                            case 10000:
                                JSONArray jsonArray = response.getJSONArray("data");
                                int length = jsonArray.length();
                                List<UserBalancePackage> ubpList = new ArrayList<UserBalancePackage>();
                                for(int i=0; i<length; i++){
                                    JSONObject jsonObject = jsonArray.getJSONObject(i).getJSONObject("user_balance_package");
                                    UserBalancePackage ubp = new UserBalancePackage(jsonObject);
                                    ubpList.add(ubp);
                                }
                                mLaSubscriptionsAdapter = new SubscriptionsListAdapter(mActivity, ubpList);
                                mLvSubscriptionsList.setAdapter(mLaSubscriptionsAdapter);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_subscriptions, menu);
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
            SubscriptionsActivity.this.finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
