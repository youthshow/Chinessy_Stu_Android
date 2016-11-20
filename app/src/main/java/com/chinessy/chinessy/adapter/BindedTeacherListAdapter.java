package com.chinessy.chinessy.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chinessy.chinessy.R;
import com.chinessy.chinessy.activity.BindedTeacherListActivity;
import com.chinessy.chinessy.beans.BindTeacher;
import com.chinessy.chinessy.rtmp.CustomRoundView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by susan on 16/2/29.
 */
public class BindedTeacherListAdapter extends RecyclerView.Adapter<BindedTeacherListAdapter.ViewHolder> {
    private Context mContext;
    private List<BindTeacher.DataBean.TeacherBean> mList;


    public BindedTeacherListAdapter(BindedTeacherListActivity context, List<BindTeacher.DataBean.TeacherBean> teacherBeanList) {
        super();
        this.mContext = context;
        this.mList = teacherBeanList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.item_bindteacherlist, null);
        //创建一个VIewHolder
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BindTeacher.DataBean.TeacherBean teacher = mList.get(position);
        holder.tutoritem_tv_name.setText(teacher.getName());
        holder.tutoritem_tv_address.setText(teacher.getCountry());
        holder.tutoritem_tv_score.setText(teacher.getScore());
        holder.tv_binding_min.setText(teacher.getBinding_minutes());
        holder.served_minutes.setText(teacher.getServed_minutes());
        if ("available".equals(teacher.getStatus())) {
            holder.tutoritem_iv_status.setImageResource(R.mipmap.dot_available);
        } else if ("offline".equals(teacher.getStatus())) {
            holder.tutoritem_iv_status.setImageResource(R.mipmap.dot_offline);
        }else if("busy".equals(teacher.getStatus())){
            holder.tutoritem_iv_status.setImageResource(R.mipmap.dot_busy);
        }
        Glide.with(mContext)
                .load("http://inthecheesefactory.com/uploads/source/glidepicasso/cover.jpg")
                .into(holder.tutoritem_iv_headimg);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView tutoritem_iv_status;
        CircleImageView tutoritem_iv_headimg;
        TextView tutoritem_tv_name;
        TextView tutoritem_tv_address;
        TextView tutoritem_tv_score;
        TextView tv_binding_min;
        TextView served_minutes;


        public ViewHolder(View itemView) {
            super(itemView);
            this.tutoritem_iv_headimg = (CircleImageView) itemView.findViewById(R.id.tutoritem_iv_headimg);
            this.tutoritem_tv_name = (TextView) itemView.findViewById(R.id.tutoritem_tv_name);
            this.tutoritem_tv_address = (TextView) itemView.findViewById(R.id.tutoritem_tv_address);
            this.tutoritem_tv_score = (TextView) itemView.findViewById(R.id.tutoritem_tv_score);
            this.tv_binding_min = (TextView) itemView.findViewById(R.id.tv_binding_min);
            this.tutoritem_iv_status = (ImageView) itemView.findViewById(R.id.tutoritem_iv_status);
            this.served_minutes = (TextView) itemView.findViewById(R.id.tutoritem_tv_servedminutes);


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
