package com.chinessy.chinessy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chinessy.chinessy.R;
import com.chinessy.chinessy.models.UserBalancePackage;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by larry on 15/12/3.
 */
public class SubscriptionsListAdapter extends BaseAdapter {

    Context mContext;
    List<View> mViewList = new ArrayList<>();
    List<UserBalancePackage> mUserBalancePackageList;
    public SubscriptionsListAdapter(Context context, List<UserBalancePackage> userBalancePackages){
        mContext = context;

        mUserBalancePackageList = userBalancePackages;
        int length = userBalancePackages.size();
        for(int i=0; i<length; i++){
            UserBalancePackage ubp = userBalancePackages.get(i);
            mViewList.add(makeItemView(ubp, i));
        }
    }

    View makeItemView(UserBalancePackage ubp, int index){
        View view = LayoutInflater.from(mContext).inflate(R.layout.subscriptions_list_item, null);
        TextView tvMinutes = (TextView)view.findViewById(R.id.subscriptions_tv_minutes);
        TextView tvExpiredAt = (TextView)view.findViewById(R.id.subscriptions_tv_expiredat);
        TextView tvActivated = (TextView)view.findViewById(R.id.subscriptions_tv_activated);

        SimpleDateFormat format = new SimpleDateFormat("MMM d,yyyy");
        tvMinutes.setText(ubp.getMinutes()+" mins/day");
        tvExpiredAt.setText(format.format(ubp.getStartAt()) + " - " + format.format(ubp.getEndAt()));

        if(index == 0){
            tvActivated.setVisibility(View.VISIBLE);
        }
        return view;
    }

    @Override
    public int getCount() {
        return mViewList.size();
    }

    @Override
    public Object getItem(int position) {
        return mUserBalancePackageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return mViewList.get(position);
    }
}
