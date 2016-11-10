package com.chinessy.chinessy.models;

import android.content.Context;
import android.content.SharedPreferences;

import com.chinessy.chinessy.Config;
import com.chinessy.chinessy.utils.DateUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by larry on 15/10/12.
 */
public class UserBalancePackage implements Serializable{
//    private Product product = null;
    private Date startAt = new Date(0);
    private Date endAt = new Date(0);
    private int daysLast = 0;
    private int minutes = 0;

    private Date updatedAt = new Date();
    private Date createdAt = new Date();

    public UserBalancePackage(){

    }

    public UserBalancePackage(JSONObject jsonObject){
        try {
            JSONObject fields = jsonObject.getJSONObject("fields");
            setStartAt(DateUtil.string2Date(fields.getString("start_at"), TimeZone.getDefault()));
            this.setEndAt(DateUtil.string2Date(fields.getString("end_at"), TimeZone.getDefault()));
            setDaysLast(fields.getInt("days_last"));
            setMinutes(fields.getInt("minutes"));
            setUpdatedAt(DateUtil.string2Datetime(fields.getString("updated_at")));
            setCreatedAt(DateUtil.string2Datetime(fields.getString("created_at")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public int getDaysLast() {
        return daysLast;
    }

    public void setDaysLast(int daysLast) {
        this.daysLast = daysLast;
    }

    public Date getEndAt() {
        return endAt;
    }

    public void setEndAt(Date endAt) {
        this.endAt = endAt;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public Date getStartAt() {
        return startAt;
    }

    public void setStartAt(Date startAt) {
        this.startAt = startAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void localSave(Context context){
        SharedPreferences sp = context.getSharedPreferences(Config.SP_SETTINGS, Context.MODE_PRIVATE);
        sp.edit().putLong("ubp_start_at", getStartAt().getTime())
                .putLong("ubp_end_at", getEndAt().getTime())
                .putInt("ubp_days_last", getDaysLast())
                .putInt("ubp_minutes", getMinutes())

                .putLong("ubp_updated_at", getUpdatedAt().getTime())
                .putLong("ubp_created_at", getCreatedAt().getTime())
                .commit();
    }
    public void localRead(Context context){
        SharedPreferences sp = context.getSharedPreferences(Config.SP_SETTINGS, Context.MODE_PRIVATE);
        setStartAt(new Date(sp.getLong("ubp_start_at", 0)));
        setEndAt(new Date(sp.getLong("ubp_end_at", 0)));
        setDaysLast(sp.getInt("ubp_days_last", 0));
        setMinutes(sp.getInt("ubp_minutes", 0));

        setUpdatedAt(new Date(sp.getLong("ubp_updated_at", 0)));
        setCreatedAt(new Date(sp.getLong("ubp_created_at", 0)));
    }
}
