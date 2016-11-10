package com.chinessy.chinessy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chinessy.chinessy.R;
import com.chinessy.chinessy.models.Reservation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by larry on 15/8/21.
 */
public class ReservationAvoidListAdapter extends BaseAdapter {
    final String tag = "ReservationAvoidListAdapter";

    Context mContext;

    ArrayList<View> mViewList = new ArrayList<View>();
    ArrayList<Reservation> mReservationList = new ArrayList<Reservation>();

    public ReservationAvoidListAdapter(Context context){
        mContext = context;

    }

    View makeItemView(Reservation reservation){
        View view = LayoutInflater.from(mContext).inflate(R.layout.avoid_item, null);
        TextView tvDate = (TextView)view.findViewById(R.id.avoiditem_tv_date);
        TextView tvDescription = (TextView)view.findViewById(R.id.avoiditem_tv_description);

        Date reservedAt = reservation.getReservedAt();
        Date reservedTo = new Date(reservedAt.getTime()+900000*reservation.getNumOf15Minutes());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd, HH:mm");
        SimpleDateFormat toDateFormat = new SimpleDateFormat("HH:mm");
        tvDate.setText(simpleDateFormat.format(reservedAt) + " - " + toDateFormat.format(reservedTo));

        if(reservation.getPurpose() == "others"){
            tvDescription.setText("n/a");
        }else{
            tvDescription.setText(R.string.Reserved);
        }

        return view;
    }

    @Override
    public int getCount() {
        return mViewList.size();
    }

    @Override
    public Object getItem(int position) {
        return mReservationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return mViewList.get(position);
    }

    public ArrayList<Reservation> getReservationList() {
        return mReservationList;
    }

    public void setReservationList(ArrayList<Reservation> reservationList) {
        this.mReservationList = reservationList;
        mViewList.clear();
        for(Reservation reservation : reservationList){
            View view = makeItemView(reservation);
            mViewList.add(view);
        }
    }
}
