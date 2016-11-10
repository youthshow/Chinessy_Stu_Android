package com.chinessy.chinessy;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.chinessy.chinessy.adapter.ExtraProductAmountChooseAdapter;
import com.chinessy.chinessy.models.Product;

import java.util.ArrayList;
import java.util.List;

public class ChooseAmountActivity extends AppCompatActivity {
    Activity mActivity;

    ListView mLvList;
    ExtraProductAmountChooseAdapter mLaAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_amount);
        mActivity = this;
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.black));
        actionBar.setElevation(0f);

        mLvList = (ListView)findViewById(R.id.chooseamount_lv_list);

        Intent intent = getIntent();
        List<Product> productList = (List<Product>)intent.getSerializableExtra("extra_product_list");
        int defaultIndex = intent.getIntExtra("default_product_index", 0);

        mLaAdapter = new ExtraProductAmountChooseAdapter(mActivity, productList, defaultIndex);
        mLvList.setOnItemClickListener(new LvAmountListOnClickListener());
        mLvList.setAdapter(mLaAdapter);
    }

    class LvAmountListOnClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mLaAdapter.viewOnClick(position);

            Intent intent = new Intent();
            intent.putExtra("default_product_index", mLaAdapter.getDefaultIndex());
            mActivity.setResult(Activity.RESULT_OK, intent);
            mActivity.finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choose_amount, menu);
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
            ChooseAmountActivity.this.finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
