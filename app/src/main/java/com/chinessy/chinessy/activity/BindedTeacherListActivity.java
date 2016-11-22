package com.chinessy.chinessy.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chinessy.chinessy.Chinessy;
import com.chinessy.chinessy.R;
import com.chinessy.chinessy.adapter.BindedTeacherListAdapter;
import com.chinessy.chinessy.beans.BasicBean;
import com.chinessy.chinessy.beans.getStudentBinds;
import com.chinessy.chinessy.clients.ConstValue;
import com.chinessy.chinessy.utils.LogUtils;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BindedTeacherListActivity extends AppCompatActivity {
    private RecyclerView mRv_bindedtecherlists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binded_teacherlist);

        SystemSetting();
        webRequest();
        mRv_bindedtecherlists = (RecyclerView) findViewById(R.id.rv_bindedtecherlists);
        mRv_bindedtecherlists.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRv_bindedtecherlists.setLayoutManager(layoutManager);


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

    private void webRequest() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstValue.BasicUrl + ConstValue.getStudentBinds,
                new Response.Listener() {
                    //﻿{"data":{"teacher":[""]},"msg":"没有数据！学生端一对一绑定信息列表","status":"true"}
                    @Override
                    public void onResponse(Object response) {
                        LogUtils.d(ConstValue.getStudentBinds + " :-->" + response.toString());
                        BasicBean basicBean = new Gson().fromJson(response.toString(), BasicBean.class);
                        if ("true".equals(basicBean.getStatus().toString())) {
                            getStudentBinds Beans = new Gson().fromJson(response.toString(), getStudentBinds.class);
                            getStudentBinds.DataBean beans = Beans.getData();
                            List<getStudentBinds.DataBean.TeacherBean> teacherBeanList = Beans.getData().getTeacher();


                            BindedTeacherListAdapter mAdapter = new BindedTeacherListAdapter(BindedTeacherListActivity.this, teacherBeanList);
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


                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtils.d(ConstValue.getStudentBinds + " :error-->" + error.toString());
            }
        }) {
            @Override
            protected Map getParams() {
                //在这里设置需要post的参数
                Map map = new HashMap();
                map.put("userId", Chinessy.chinessy.getUser().getId());
                //  map.put("userId", "611");

                return map;
            }
        };

        Chinessy.requestQueue.add(stringRequest);
    }

}
