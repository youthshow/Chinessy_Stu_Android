package com.chinessy.chinessy.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chinessy.chinessy.R;
import com.chinessy.chinessy.beans.getStudioList;
import com.chinessy.chinessy.rtmp.CustomRoundView;

import java.util.List;

/**
 * Created by susan on 16/2/29.
 */
public class LiveRoomListAdapter extends RecyclerView.Adapter<LiveRoomListAdapter.ViewHolder> {
    private Context mContext;
    private List<getStudioList.DataBean.StudioBean> mList;


    public LiveRoomListAdapter(Context context, List<getStudioList.DataBean.StudioBean> teacher) {
        super();
        this.mContext = context;
        this.mList = teacher;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.item_liveroomlist, null);
        //创建一个VIewHolder
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
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
                .placeholder(R.mipmap.me_profilepic)
                .into(holder.iv_screenshot);
        Glide.with(mContext)  //head_img_key
                .load("http://inthecheesefactory.com/uploads/source/glidepicasso/cover.jpg")
                .placeholder(R.mipmap.me_profilepic)
                .into(holder.roundview_avatar);

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CustomRoundView roundview_avatar;
        TextView mName;
        TextView tv_online_num;
        TextView tv_location;
        TextView tv_language;
        TextView tv_onlive;
        TextView tv_statue;
        ImageView iv_screenshot;


        public ViewHolder(View itemView) {
            super(itemView);
            this.roundview_avatar = (CustomRoundView) itemView.findViewById(R.id.roundview_avatar);
            this.mName = (TextView) itemView.findViewById(R.id.tv_name);
            this.tv_online_num = (TextView) itemView.findViewById(R.id.tv_online_num);
            this.tv_location = (TextView) itemView.findViewById(R.id.tv_location);
            this.tv_language = (TextView) itemView.findViewById(R.id.tv_language);
            this.iv_screenshot = (ImageView) itemView.findViewById(R.id.iv_screenshot);
            this.tv_onlive = (TextView) itemView.findViewById(R.id.tv_onlive);
            this.tv_statue = (TextView) itemView.findViewById(R.id.tv_statue);


            //为item添加普通点击回调
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (null != mItemClickListener) {
                        mItemClickListener.onItemClick(v, getPosition());
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    private ItemClickListener mItemClickListener;

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    //为RecycleView 添加点击事件
    public interface ItemClickListener {
        //普通点击
        public void onItemClick(View view, int position);
    }
}
