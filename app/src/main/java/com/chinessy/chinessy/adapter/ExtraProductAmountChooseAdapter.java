package com.chinessy.chinessy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinessy.chinessy.R;
import com.chinessy.chinessy.models.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by larry on 15/12/8.
 */
public class ExtraProductAmountChooseAdapter extends BaseAdapter {

    Context mContext;
    List<View> mViewList = new ArrayList<>();
    List<Product> mProductList;
    int mDefaultIndex;
    public ExtraProductAmountChooseAdapter(Context context, List<Product> productList, int defaultIndex){
        mContext = context;
        mProductList = productList;
        mDefaultIndex = defaultIndex;

        int length = mProductList.size();
        for(int i=0; i<length; i++){
            View view = makeItemView(mProductList.get(i), i);
            mViewList.add(view);
        }
    }

    public View makeItemView(Product product, int index){
        View view = LayoutInflater.from(mContext).inflate(R.layout.extraproductamount_list_item, null);
        TextView tvPrice = (TextView)view.findViewById(R.id.extraproductamount_tv_price);
        TextView tvMinutes = (TextView)view.findViewById(R.id.extraproductamount_tv_minutes);
        ImageView ivChecked = (ImageView)view.findViewById(R.id.extraproductamount_tv_checked);

        tvPrice.setText(getShowPrice(product.getPrice()+""));
        tvMinutes.setText(product.getMinutes() + " mins");
        if(mDefaultIndex == index){
            ivChecked.setVisibility(View.VISIBLE);
        }
        return view;
    }

    private String getShowPrice(String price){
        return "US$ "+price;
    }

    public void viewOnClick(int position){
        mViewList.get(mDefaultIndex).findViewById(R.id.extraproductamount_tv_checked).setVisibility(View.INVISIBLE);
        mDefaultIndex = position;
        mViewList.get(mDefaultIndex).findViewById(R.id.extraproductamount_tv_checked).setVisibility(View.VISIBLE);
    }

    @Override
    public int getCount() {
        return mViewList.size();
    }

    @Override
    public Object getItem(int position) {
        return mProductList.get(position);
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
