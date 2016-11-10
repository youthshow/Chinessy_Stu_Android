package com.chinessy.chinessy;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinessy.chinessy.adapter.TabPagerAdapter;
import com.chinessy.chinessy.fragment.HomeFragment;
import com.chinessy.chinessy.fragment.TutorsFragment;
import com.chinessy.chinessy.fragment.MyFragment;
import com.chinessy.chinessy.fragment.ReservationFragment;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements HomeFragment.OnFragmentInteractionListener,
        TutorsFragment.OnFragmentInteractionListener,
        ReservationFragment.OnFragmentInteractionListener,
        MyFragment.OnFragmentInteractionListener
{

    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapter;
    private List<Fragment> mFragments = new ArrayList<Fragment>();

    private LinearLayout mTabBtnHome;
    private LinearLayout mTabBtnTutors;
    private LinearLayout mTabBtnReservation;
    private LinearLayout mTabBtnMe;

    private ImageView mIvHome;
    private ImageView mIvTutors;
    private ImageView mIvReservation;
    private ImageView mIvMe;

    private TextView mTvHome;
    private TextView mTvTutors;
    private TextView mTvReservation;
    private TextView mTvMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.black));
        actionBar.setElevation(0f);

        initView();

        mAdapter = new TabPagerAdapter(getSupportFragmentManager(), mFragments);

        mViewPager.setAdapter(mAdapter);

        mViewPager.setOnPageChangeListener(new TabChangeListener());
        mViewPager.setCurrentItem(0);
        mTvHome.setTextColor(getResources().getColor(R.color.main_color));
        mIvHome.setImageResource(R.mipmap.tabicon_home_on);

        mTabBtnHome.setOnClickListener(new TabClickListener());
        mTabBtnTutors.setOnClickListener(new TabClickListener());
        mTabBtnReservation.setOnClickListener(new TabClickListener());
        mTabBtnMe.setOnClickListener(new TabClickListener());
    }

    class TabClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.main_ll_home:
                    mViewPager.setCurrentItem(0, true);
                    break;
                case R.id.main_ll_tutors:
                    mViewPager.setCurrentItem(1, true);
                    break;
                case R.id.main_ll_reservation:
                    mViewPager.setCurrentItem(2, true);
                    break;
                case R.id.main_ll_me:
                    mViewPager.setCurrentItem(3, true);
                    break;
            }
//            Toast.makeText(MainActivity.this, mViewPager.getCurrentItem()+"", Toast.LENGTH_SHORT).show();
        }
    }

    class TabChangeListener implements ViewPager.OnPageChangeListener {
        private int currentIndex;

        @Override
        public void onPageSelected(int position)
        {
            resetTabBtn();
            switch (position)
            {
                case 0:
                    mTvHome.setTextColor(getResources().getColor(R.color.main_color));
                    mIvHome.setImageResource(R.mipmap.tabicon_home_on);
//                    fragmentManager.beginTransaction().replace(R.id.content_frame, mFragments.get(0)).addToBackStack(null).commit();
                    break;
                case 1:
                    mTvTutors.setTextColor(getResources().getColor(R.color.main_color));
                    mIvTutors.setImageResource(R.mipmap.tabicon_tutor_on);
                    break;
                case 2:
                    mTvReservation.setTextColor((getResources().getColor((R.color.main_color))));
                    mIvReservation.setImageResource(R.mipmap.tabicon_reservation_on);
                    break;
                case 3:
                    mTvMe.setTextColor(getResources().getColor(R.color.main_color));
                    mIvMe.setImageResource(R.mipmap.tabicon_me_on);
                    break;
            }

            currentIndex = position;
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2)
        {

        }

        @Override
        public void onPageScrollStateChanged(int arg0)
        {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    protected void resetTabBtn()
    {
        mIvHome.setImageResource(R.mipmap.tabicon_home_off);
        mIvTutors.setImageResource(R.mipmap.tabicon_tutor_off);
        mIvReservation.setImageResource(R.mipmap.tabicon_reservation_off);
        mIvMe.setImageResource(R.mipmap.tabicon_me_off);

        mTvHome.setTextColor(Color.rgb(204, 204, 204));
        mTvTutors.setTextColor(Color.rgb(204, 204, 204));
        mTvReservation.setTextColor(Color.rgb(204, 204, 204));
        mTvMe.setTextColor(Color.rgb(204, 204, 204));
    }

    private void initView()
    {

        mTabBtnHome = (LinearLayout) findViewById(R.id.main_ll_home);
        mTabBtnTutors = (LinearLayout) findViewById(R.id.main_ll_tutors);
        mTabBtnReservation = (LinearLayout) findViewById(R.id.main_ll_reservation);
        mTabBtnMe = (LinearLayout) findViewById(R.id.main_ll_me);

        mIvHome = (ImageView) findViewById(R.id.main_iv_home);
        mIvTutors = (ImageView) findViewById(R.id.main_iv_tutors);
        mIvReservation = (ImageView) findViewById(R.id.main_iv_reservation);
        mIvMe = (ImageView) findViewById(R.id.main_iv_me);

        mTvHome = (TextView) findViewById(R.id.main_tv_home);
        mTvTutors = (TextView) findViewById(R.id.main_tv_tutors);
        mTvReservation = (TextView) findViewById(R.id.main_tv_reservation);
        mTvMe = (TextView) findViewById(R.id.main_tv_me);

        HomeFragment tabDebt = new HomeFragment();
        TutorsFragment tabMessage = new TutorsFragment();
        ReservationFragment tabReservation = new ReservationFragment();
        MyFragment tabMy = new MyFragment();
        mFragments.add(tabDebt);
        mFragments.add(tabMessage);
        mFragments.add(tabReservation);
        mFragments.add(tabMy);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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