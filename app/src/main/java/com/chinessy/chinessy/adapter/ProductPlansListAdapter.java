package com.chinessy.chinessy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinessy.chinessy.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by larry on 15/12/3.
 */
public class ProductPlansListAdapter extends BaseAdapter {
    Context mContext;
    List<Integer> mMinutesList;
    int mDefaultIndex;
    List<View> mViewList = new ArrayList<>();
    public ProductPlansListAdapter(Context context, List<Integer> minutesList, int defaultIndex){
        mContext = context;

        mDefaultIndex = defaultIndex;
        mMinutesList = minutesList;

        int length = mMinutesList.size();
        for(int i=0; i<length; i++){
            mViewList.add(makeItemView(mMinutesList.get(i), i));
        }
    }

    View makeItemView(Integer minutes, int index){
        View view = LayoutInflater.from(mContext).inflate(R.layout.productminutesplans_list_item, null);
        TextView tvMinutes = (TextView)view.findViewById(R.id.productplans_tv_minutes);
        ImageView ivChecked = (ImageView)view.findViewById(R.id.productplans_tv_checked);

        tvMinutes.setText(minutes + " mins/day");
        if(index == mDefaultIndex){
            ivChecked.setVisibility(view.VISIBLE);
        }
        return view;
    }

    public void viewOnClick(int index){
        int length = mViewList.size();
        mDefaultIndex = index;
        for(int i=0; i<length; i++){
            if(index == i){
                mViewList.get(i).findViewById(R.id.productplans_tv_checked).setVisibility(View.VISIBLE);
            }else{
                mViewList.get(i).findViewById(R.id.productplans_tv_checked).setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public int getCount() {
        return mViewList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMinutesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return mViewList.get(position);
    }

    public int getDefaultIndex() {
        return mDefaultIndex;
    }
}
