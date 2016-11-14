package com.chinessy.chinessy.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.chinessy.chinessy.R;
import com.chinessy.chinessy.Views.CustomRoundView;

import java.util.List;

/**
 * Created by susan on 16/2/29.
 */
public class LiveRoomListAdapter extends RecyclerView.Adapter<LiveRoomListAdapter.ViewHolder> {
    private Context mContext;
    private List<Integer> mList;

    public LiveRoomListAdapter(Context context, List<Integer> mData) {
        super();
        this.mContext = context;
        this.mList = mData;
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
//            holder.mName.setText("userName");
//            holder.mName.setText("client.email");
//            holder.mName.setText("client.phone");
//        holder.mAddress.setText("address");
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CustomRoundView roundview_avatar;
        TextView mName;
        TextView tv_score;
        TextView tv_location;
        TextView tv_country;
        TextView tv_onlive;
        ImageView iv_screenshot;



        public ViewHolder(View itemView) {
            super(itemView);
            this.roundview_avatar = (CustomRoundView) itemView.findViewById(R.id.roundview_avatar);
            this.mName = (TextView) itemView.findViewById(R.id.tv_name);
            this.tv_score = (TextView) itemView.findViewById(R.id.tv_score);
            this.tv_location = (TextView) itemView.findViewById(R.id.tv_location);
            this.tv_country = (TextView) itemView.findViewById(R.id.tv_country);
            this.iv_screenshot = (ImageView) itemView.findViewById(R.id.iv_screenshot);
            this.tv_onlive = (TextView) itemView.findViewById(R.id.tv_onlive);



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
