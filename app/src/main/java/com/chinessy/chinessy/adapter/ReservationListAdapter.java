package com.chinessy.chinessy.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinessy.chinessy.R;
import com.chinessy.chinessy.clients.InternalClient;
import com.chinessy.chinessy.models.LocalImage;
import com.chinessy.chinessy.models.Reservation;
import com.chinessy.chinessy.utils.FileUtil;
import com.loopj.android.http.FileAsyncHttpResponseHandler;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * Created by larry on 15/8/18.
 */
public class ReservationListAdapter extends BaseAdapter{
    final String tag = "ReservationListAdapter";

    final int HANDLER_SET_HEAD_IMAGE = 1000;

    Handler mHandler = new ReservationListAdapterHandler();

    ArrayList<View> mViewList = new ArrayList<View>();
    ArrayList<Reservation> mReservationList = new ArrayList<Reservation>();

    HashMap<Integer, View> mLoadingViewMap = new HashMap<Integer, View>();
    HashMap<Integer, Reservation> mLoadingReservationMap = new HashMap<>();
    Context mContext;
    int mLoadingCount = 0;

    public ReservationListAdapter(Context context){
        mContext = context;
    }

    View makeItemView(final Reservation reservation){
        final View view = LayoutInflater.from(mContext).inflate(R.layout.reservation_item, null);
        ImageView ivHeadImg = (ImageView)view.findViewById(R.id.reservationitem_iv_headimg);
        TextView tvTime = (TextView)view.findViewById(R.id.reservationitem_tv_time);
        TextView tvDate = (TextView)view.findViewById(R.id.reservationitem_tv_date);
        TextView tvIntro = (TextView)view.findViewById(R.id.reservationitem_tv_intro);

        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mma");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd E");
        Date reservationAt = reservation.getReservedAt();
        tvTime.setText(timeFormat.format(reservationAt));
        tvDate.setText(dateFormat.format(reservationAt));
        tvIntro.setText("With " + reservation.getTeacher().getUserProfile().getName() + ", " + reservation.getDuration() + "min");


        final LocalImage localImage = reservation.getTeacher().getUserProfile().getHeadImg();
        if(localImage.getBitmap() != null){
            ivHeadImg.setImageBitmap(localImage.getBitmap());
        }else if(!localImage.getKey().equals("")){
            InternalClient.getFile(localImage.getKey(), new FileAsyncHttpResponseHandler(mContext) {
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                    Log.w(tag, throwable.getMessage());
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, File file) {
                    File f = new File(localImage.getAbsolutePath());
                    FileUtil.fileChannelCopy(file, f);
                    Message message = mHandler.obtainMessage(HANDLER_SET_HEAD_IMAGE);
                    message.arg1 = mLoadingCount;
                    mLoadingViewMap.put(mLoadingCount, view);
                    mLoadingReservationMap.put(mLoadingCount, reservation);
                    mLoadingCount++;
                    message.sendToTarget();
                }
            });
        }
        return view;
    }

    View makeHeaderItemView(String title){
        TextView view = (TextView)LayoutInflater.from(mContext).inflate(R.layout.reservation_header_item, null);
        view.setText(title);
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

    class ReservationListAdapterHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case HANDLER_SET_HEAD_IMAGE:
                    View view = mLoadingViewMap.get(msg.arg1);
                    Reservation reservation = mLoadingReservationMap.get(msg.arg1);
                    Bitmap bitmap = reservation.getTeacher().getUserProfile().getHeadImg().getBitmap();
                    if(bitmap != null){
                        ((ImageView) view.findViewById(R.id.tutoritem_iv_headimg)).setImageBitmap(bitmap);
                    }else{
                        Log.w(tag, "image still null after loading");
                    }
                    mLoadingViewMap.remove(msg.arg1);
                    mLoadingReservationMap.remove(msg.arg1);
                    break;
            }
        }
    }

    public ArrayList<Reservation> getReservationList() {
        return mReservationList;
    }

    public void setHistoryReservationList(ArrayList<Reservation> historyList, boolean isInit) {
        if(isInit){
            this.mViewList.clear();
            this.mReservationList.clear();
        }

        for(Reservation reservation : historyList){
            reservation.setListTag("history");
            View view = makeItemView(reservation);
            mViewList.add(view);
        }
        mReservationList.addAll(historyList);
    }

    public ArrayList<View> getmViewList() {
        return mViewList;
    }
}
