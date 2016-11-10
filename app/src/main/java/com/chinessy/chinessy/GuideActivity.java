package com.chinessy.chinessy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.umeng.analytics.MobclickAgent;


public class GuideActivity extends Activity {
    private static GuideActivity guideActivity = null;

    Button mBtnSignup;
    Button mBtnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        mBtnLogin = (Button) findViewById(R.id.guide_btn_login);
        mBtnSignup = (Button) findViewById(R.id.guide_btn_signup);

        mBtnLogin.setOnClickListener(new LoginClickListener());
        mBtnSignup.setOnClickListener(new SignupClickListener());

        guideActivity = this;
    }

    class LoginClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            intent.setClass(GuideActivity.this, LoginActivity.class);
            GuideActivity.this.startActivity(intent);
        }
    }

    class SignupClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            intent.setClass(GuideActivity.this, SignupActivity.class);
            GuideActivity.this.startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_guide, menu);
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

        return super.onOptionsItemSelected(item);
    }
    public static void finishGuideActivity(){
        if(guideActivity != null){
            guideActivity.finish();
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
