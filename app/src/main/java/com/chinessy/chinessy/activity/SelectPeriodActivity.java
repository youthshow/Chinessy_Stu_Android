package com.chinessy.chinessy.activity;

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

import com.chinessy.chinessy.R;
import com.chinessy.chinessy.adapter.PackageProductPeriodListAdapter;
import com.chinessy.chinessy.models.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SelectPeriodActivity extends AppCompatActivity {
    Activity mActivity;

    ListView mLvPeriodList;
    PackageProductPeriodListAdapter mLaPeriodAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_months);
        mActivity = this;
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.black));
        actionBar.setElevation(0f);

        mLvPeriodList = (ListView)findViewById(R.id.selectmonths_lv_list);
        mLvPeriodList.setOnItemClickListener(new LvPeriodListOnClickListener());

        Intent intent = getIntent();
        List<Integer> periodList = (List<Integer>)intent.getSerializableExtra("period_list");
        int defaultPeriodIndex = intent.getIntExtra("default_period_index", 0);
        int defaultMinutes = intent.getIntExtra("default_minutes", 0);
        List<Product> packageProductList = (List<Product>)intent.getSerializableExtra("package_product_list");
        List<Product> periodProductList = new ArrayList<>();

        int length = packageProductList.size();
        for(int i=0; i<length; i++){
            Product product = packageProductList.get(i);
            if(product.getMinutes() == defaultMinutes){
                periodProductList.add(product);
            }
        }
        Collections.sort(periodProductList, new Comparator<Product>() {
            @Override
            public int compare(Product lhs, Product rhs) {
                if(lhs.getDaysLast() > rhs.getDaysLast()){
                    return 1;
                }
                return 0;
            }
        });

        mLaPeriodAdapter = new PackageProductPeriodListAdapter(mActivity, periodList, periodProductList, defaultPeriodIndex);
        mLvPeriodList.setAdapter(mLaPeriodAdapter);
    }

    class LvPeriodListOnClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mLaPeriodAdapter.viewOnClick(position);

            Intent intent = new Intent();
//            intent.putExtra("default_product", mLaPeriodAdapter.getDefaultProduct());
            intent.putExtra("default_period_index", mLaPeriodAdapter.getDefaultIndex());
            mActivity.setResult(Activity.RESULT_OK, intent);
            mActivity.finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_months, menu);
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
            SelectPeriodActivity.this.finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
