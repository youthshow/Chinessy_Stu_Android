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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by larry on 15/12/4.
 */
public class PackageProductPeriodListAdapter extends BaseAdapter{
    Context mContext;

    List<Integer> mPeriodList;
    List<Product> mPeriodProductList;
    List<View> mViewList = new ArrayList<>();
    int mDefaultIndex;

    public PackageProductPeriodListAdapter(Context context, List<Integer> periodList, List<Product> periodProductList, int defaultIndex){
        mContext = context;
        mPeriodList = periodList;
        mDefaultIndex = defaultIndex;
        mPeriodProductList = periodProductList;

        int length = mPeriodList.size();
        for(int i=0; i<length; i++){
            mViewList.add(makeItemView(periodProductList.get(i), defaultIndex==i));
        }
    }

    View makeItemView(Product product, boolean isChecked){
        View view = LayoutInflater.from(mContext).inflate(R.layout.packageproductperiod_list_item, null);
        TextView tvQuantity = (TextView)view.findViewById(R.id.packageproductquantity_tv_quantity);
        TextView tvPrice = (TextView)view.findViewById(R.id.packageproductquantity_tv_price);
        TextView tvDesc = (TextView)view.findViewById(R.id.packageproductquantity_tv_desc);
        ImageView ivChacked = (ImageView)view.findViewById(R.id.packageproductquantity_tv_checked);

        StringBuilder sb = new StringBuilder(product.getUnit());
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        String quantityStr = product.getUnitNum() + " " + sb.toString();
        if(product.getUnitNum() > 1){
            quantityStr += "s";
        }
        tvQuantity.setText(quantityStr);

        BigDecimal bdPrice = new BigDecimal(product.getPrice()+"");
        bdPrice = bdPrice.setScale(2, BigDecimal.ROUND_HALF_UP);
        tvPrice.setText("US$ " + bdPrice.toString());
        tvDesc.setText("Per " + product.getUnit() +" * " + product.getUnitNum());

        if(isChecked){
            ivChacked.setVisibility(View.VISIBLE);
        }
        return view;
    }

    public void viewOnClick(int position){
        if(position == mDefaultIndex){
            return;
        }
        mViewList.get(mDefaultIndex).findViewById(R.id.packageproductquantity_tv_checked).setVisibility(View.INVISIBLE);
        mDefaultIndex = position;
        mViewList.get(mDefaultIndex).findViewById(R.id.packageproductquantity_tv_checked).setVisibility(View.VISIBLE);
    }

    @Override
    public int getCount() {
        return mViewList.size();
    }

    @Override
    public Object getItem(int position) {
        return mPeriodProductList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return mViewList.get(position);
    }

    public Product getDefaultProduct(){
        return mPeriodProductList.get(mDefaultIndex);
    }

    public int getDefaultIndex() {
        return mDefaultIndex;
    }
}
