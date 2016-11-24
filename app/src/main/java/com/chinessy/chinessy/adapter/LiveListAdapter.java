package com.chinessy.chinessy.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chinessy.chinessy.R;
import com.chinessy.chinessy.beans.getStudioList;
import com.chinessy.chinessy.clients.InternalClient;
import com.chinessy.chinessy.models.LocalImage;
import com.chinessy.chinessy.models.User;
import com.chinessy.chinessy.rtmp.CustomRoundView;
import com.chinessy.chinessy.utils.FileUtil;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by larry on 15/8/18.
 */
public class LiveListAdapter extends BaseAdapter {

    private Context mContext;
    private List<getStudioList.DataBean.StudioBean> mList;

    public LiveListAdapter(Context context, List<getStudioList.DataBean.StudioBean> dataList) {
        this.mContext = context;
        this.mList = dataList;
    }


    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_liveroomlist, null);
            holder = new ViewHolder();
            convertView.setTag(holder);

            holder.roundview_avatar = (CustomRoundView) convertView.findViewById(R.id.roundview_avatar);
            holder.mName = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_online_num = (TextView) convertView.findViewById(R.id.tv_online_num);
            holder.tv_location = (TextView) convertView.findViewById(R.id.tv_location);
            holder.tv_language = (TextView) convertView.findViewById(R.id.tv_language);
            holder.iv_screenshot = (ImageView) convertView.findViewById(R.id.iv_screenshot);
            holder.tv_onlive = (TextView) convertView.findViewById(R.id.tv_onlive);
            holder.tv_statue = (TextView) convertView.findViewById(R.id.tv_statue);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        getStudioList.DataBean.StudioBean studioBean = mList.get(position);
        holder.mName.setText(studioBean.getName());
        holder.tv_online_num.setText(studioBean.getOnline_num());
        holder.tv_location.setText(studioBean.getCountry());
        holder.tv_language.setText(studioBean.getSpoken_languages());
        if ("inactive".equals(studioBean.getStatus())) {
            holder.tv_onlive.setVisibility(View.VISIBLE);
            holder.tv_statue.setText("Online");
        } else if ("offline".equals(studioBean.getStatus())) {
            holder.tv_onlive.setVisibility(View.GONE);
            holder.tv_statue.setText("Offline");
        }

        Glide.with(mContext)
                .load(studioBean.getCover())
                .placeholder(R.mipmap.profile_square)
                .into(holder.iv_screenshot);
        Glide.with(mContext)  //head_img_key
                .load("http://inthecheesefactory.com/uploads/source/glidepicasso/cover.jpg")
                .placeholder(R.mipmap.profile_head)
                .into(holder.roundview_avatar);

        return convertView;
    }

    static class ViewHolder {
        CustomRoundView roundview_avatar;
        TextView mName;
        TextView tv_online_num;
        TextView tv_location;
        TextView tv_language;
        TextView tv_onlive;
        TextView tv_statue;
        ImageView iv_screenshot;
    }

}