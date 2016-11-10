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

import com.chinessy.chinessy.adapter.ProductPlansListAdapter;
import com.chinessy.chinessy.models.Product;

import java.util.List;

public class ChoosePlanActivity extends AppCompatActivity {
    Activity mActivity;

    ProductPlansListAdapter mLaProductPlansAdapter;
    ListView mLvProductPlansList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_plan);
        mActivity = this;
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.black));
        actionBar.setElevation(0f);

        Intent intent = getIntent();
        List<Integer> minutesList = (List<Integer>) intent.getSerializableExtra("minutes_list");
        int defaultMinutesIndex = intent.getIntExtra("default_minutes_index", 0);

        mLvProductPlansList = (ListView)findViewById(R.id.chooseplan_lv_list);
        mLaProductPlansAdapter = new ProductPlansListAdapter(mActivity, minutesList, defaultMinutesIndex);
        mLvProductPlansList.setAdapter(mLaProductPlansAdapter);
        mLvProductPlansList.setOnItemClickListener(new ProductPlansListItemOnClickListener());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choose_plan, menu);
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
            ChoosePlanActivity.this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    class ProductPlansListItemOnClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mLaProductPlansAdapter.viewOnClick(position);

            Intent intent = new Intent();
            intent.putExtra("default_minutes_index", mLaProductPlansAdapter.getDefaultIndex());
            mActivity.setResult(Activity.RESULT_OK, intent);
            mActivity.finish();
        }
    }
}
