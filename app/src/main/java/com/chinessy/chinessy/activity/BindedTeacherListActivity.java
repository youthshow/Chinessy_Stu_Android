package com.chinessy.chinessy.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.chinessy.chinessy.Config;
import com.chinessy.chinessy.R;
import com.chinessy.chinessy.adapter.BindedTeacherListAdapter;
import com.chinessy.chinessy.adapter.LiveRoomListAdapter;
import com.chinessy.chinessy.fragment.TutorsFragment;

import java.util.ArrayList;
import java.util.List;

public class BindedTeacherListActivity extends AppCompatActivity {
    private RecyclerView mRv_bindedtecherlists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binded_teacherlist);

        SystemSetting();

        mRv_bindedtecherlists = (RecyclerView) findViewById(R.id.rv_bindedtecherlists);
        mRv_bindedtecherlists.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRv_bindedtecherlists.setLayoutManager(layoutManager);

        List list = new ArrayList<Integer>();
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);

        BindedTeacherListAdapter mAdapter = new BindedTeacherListAdapter(BindedTeacherListActivity.this, list);
        mAdapter.setOnItemClickListener(new BindedTeacherListAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(BindedTeacherListActivity.this, "跳转老师详情", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent();
//                intent.setClass(mActivity, TutorActivity.class);
//                intent.putExtra("tutor", tutor);
//                intent.putExtra("position", position);
//                TutorsFragment.this.startActivityForResult(intent, Config.RC_MAIN_TO_TUTOR);
            }
        });
        // specify an adapter (see also next example)
        mRv_bindedtecherlists.setAdapter(mAdapter);
    }


    private void SystemSetting() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.black));
        actionBar.setTitle(R.string.Binded_Minutes);
        actionBar.setElevation(0f);
        actionBar.setDisplayHomeAsUpEnabled(true);// 设置back按钮是否可见
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
