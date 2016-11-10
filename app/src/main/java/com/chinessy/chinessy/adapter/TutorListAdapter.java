package com.chinessy.chinessy.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.chinessy.chinessy.models.User;
import com.chinessy.chinessy.utils.FileUtil;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import org.apache.http.Header;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by larry on 15/8/18.
 */
public class TutorListAdapter extends BaseAdapter {
    final String tag = "TutorListAdapter";

    final int HANDLER_SET_HEAD_IMAGE = 1000;

    Handler mHandler = new TutorListAdapterHandler();

    ArrayList<User> mTutorList = new ArrayList<User>();
    ArrayList<View> mViewList = new ArrayList<View>();

    HashMap<Integer, View> mLoadingViewMap = new HashMap<Integer, View>();
    HashMap<Integer, User> mLoadingUserMap = new HashMap<Integer, User>();
    Context mContext;
    int mLoadingCount = 0;

    public TutorListAdapter(Context context){
        mContext = context;

    }

//    public TutorListAdapter(Context context, ArrayList<User> tutorList){
//        mContext = context;
//
//        mTutorList = tutorList;
//
//        for(User tutor : tutorList){
//            View view = makeItemView(tutor);
//            mAvailableViewList.add(view);
//        }
//    }

    View makeItemView(final User user){
        final View view = LayoutInflater.from(mContext).inflate(R.layout.tutor_item, null);
        ImageView ivHeadImg = (ImageView)view.findViewById(R.id.tutoritem_iv_headimg);
        TextView tvName = (TextView)view.findViewById(R.id.tutoritem_tv_name);
        TextView tvAddress = (TextView)view.findViewById(R.id.tutoritem_tv_address);
        TextView tvScore = (TextView)view.findViewById(R.id.tutoritem_tv_score);
        TextView tvServedMinutes = (TextView)view.findViewById(R.id.tutoritem_tv_servedminutes);
        ImageView ivStatus = (ImageView)view.findViewById(R.id.tutoritem_iv_status);
        ImageView ivFavouritesIcon = (ImageView)view.findViewById(R.id.tutoritem_iv_favouritesicon);

        changeFavouritesStatus(ivFavouritesIcon, user);

        tvName.setText(user.getUserProfile().getName());
        tvAddress.setText(user.getUserProfile().getCountry());
        if(user.getUserProfile().getStatus().equals(User.STATUS_AVAILABLE)){
            ivStatus.setImageResource(R.mipmap.dot_available);
        }else if(user.getUserProfile().getStatus().equals(User.STATUS_BUSY)){
            ivStatus.setImageResource(R.mipmap.dot_busy);
        }else if(user.getUserProfile().getStatus().equals(User.STATUS_OFFLINE)){
            ivStatus.setImageResource(R.mipmap.dot_offline);
        }
        tvScore.setText(user.getUserProfile().getScore()+"");
        long minutes = user.getUserProfile().getServedMinutes();
        tvServedMinutes.setText(((int)Math.floor(minutes/60))+"h"+minutes%60+"mins");

        final LocalImage localImage = user.getUserProfile().getHeadImg();
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
                    mLoadingUserMap.put(mLoadingCount, user);
                    mLoadingCount++;
                    message.sendToTarget();
                }
            });
        }
        return view;
    }

    View makeHeaderItemView(String header){
        TextView view = (TextView)LayoutInflater.from(mContext).inflate(R.layout.tutor_header_item, null);
        view.setText(header);
        view.setClickable(false);
        return view;
    }

    public class TutorListAdapterHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case HANDLER_SET_HEAD_IMAGE:
                    View view = mLoadingViewMap.get(msg.arg1);
                    User user = mLoadingUserMap.get(msg.arg1);
                    Bitmap bitmap = user.getUserProfile().getHeadImg().getBitmap();
                    if(bitmap != null){
                        ((ImageView) view.findViewById(R.id.tutoritem_iv_headimg)).setImageBitmap(bitmap);
                    }else{
                        Log.w(tag, "image still null after loading");
                    }
                    mLoadingViewMap.remove(msg.arg1);
                    mLoadingUserMap.remove(msg.arg1);
                    break;
            }
        }
    }

    @Override
    public int getCount() {
//        return mAvailableViewList.size()+mBusyViewList.size()+mOfflineViewList.size()+mHeaderViewList.size();
        return mViewList.size();
    }

    @Override
    public Object getItem(int position) {
        return mTutorList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        Log.d(tag, position + " : " + mTutorList.get(position).getEmail());
        return mViewList.get(position);

//        int avaiSize = mTutorList.size()+1;
//        int busySize = mBusyTutorList.size() + avaiSize +1;
//        int offlSize = mOfflineTutorList.size() + busySize +1;
//
//        if(position == 0){
//            return mHeaderViewList.get(0);
//        }else if(position>0 && position<avaiSize){
//            return mAvailableViewList.get(position-1);
//        }else if(position == avaiSize){
//            return mHeaderViewList.get(1);
//        }else if(position>avaiSize && position<busySize){
//            return mBusyViewList.get(position-avaiSize-1);
//        }else if(position == busySize){
//            return mHeaderViewList.get(2);
//        }else if(position>busySize && position<offlSize){
//            return mOfflineViewList.get(position-busySize-1);
//        }else{
//            return null;
//        }
    }

    public ArrayList<User> getTutorList() {
        return mTutorList;
    }

    public void setTutorList(ArrayList<User> availableList, ArrayList<User> busyList, ArrayList<User> offlineList) {
        mViewList.clear();
        mTutorList.clear();

//        if(availableList.size() > 0){
//            mViewList.add(makeHeaderItemView("Available"));
//            mTutorList.add(new User("available"));
//        }
        for(User tutor : availableList){
            View view = makeItemView(tutor);
            mViewList.add(view);
        }
        mTutorList.addAll(availableList);

//        if(busyList.size() > 0){
//            mViewList.add(makeHeaderItemView("Busy"));
//            mTutorList.add(new User("busy"));
//        }
        for(User tutor: busyList){
            View view = makeItemView(tutor);
            mViewList.add(view);
        }
        mTutorList.addAll(busyList);

//        if(offlineList.size() > 0){
//            mViewList.add(makeHeaderItemView("Offline"));
//            mTutorList.add(new User("offline"));
//        }
        for(User tutor: offlineList){
            View view = makeItemView(tutor);
            mViewList.add(view);
        }
        mTutorList.addAll(offlineList);
    }

    public void changeFavouritesStatus(ImageView view, User tutor){
        if(tutor.isFavourites()){
//            if(tutor.getUserProfile().getStatus().equals(User.STATUS_AVAILABLE)){
//                view.setImageResource(R.mipmap.listicon_star_green);
//                view.setVisibility(View.VISIBLE);
//            }else if(tutor.getUserProfile().getStatus().equals(User.STATUS_BUSY)){
//                view.setImageResource(R.mipmap.listicon_star_green);
//                view.setVisibility(View.VISIBLE);
//            }else if(tutor.getUserProfile().getStatus().equals(User.STATUS_OFFLINE)){
//                view.setImageResource(R.mipmap.listicon_star_green);
//            }
            if(tutor.getUserProfile().getStatus().equals(User.STATUS_AVAILABLE)){
                view.setVisibility(View.VISIBLE);
                view.setImageResource(R.mipmap.listicon_star_green);
            }else{
                view.setVisibility(View.INVISIBLE);
            }
        }else{
            view.setVisibility(View.INVISIBLE);
        }
    }

    public void onFavouritesChanged(Intent data, int position){
        User tutor = mTutorList.get(position);
        View view = mViewList.get(position).findViewById(R.id.tutoritem_iv_favouritesicon);
        tutor.setIsFavourites(data.getBooleanExtra("is_favourites", tutor.isFavourites()));

        changeFavouritesStatus((ImageView)view, tutor);
    }
}
