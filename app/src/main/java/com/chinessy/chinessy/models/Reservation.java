package com.chinessy.chinessy.models;

import com.chinessy.chinessy.utils.DateUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by larry on 15/8/18.
 */
public class Reservation implements Serializable{
    int id;
    User student;
    User teacher;
    Date reservedAt;
    int numOf15Minutes;
    String purpose;
    String status;
    Date updatedAt;
    Date createdAt;

    int duration;

    String listTag = "";

    public Reservation(){

    }
    public Reservation(String listTag){
        this.listTag = listTag;
    }
    public Reservation(JSONObject jsonObject){
        try {
            JSONObject reservationJson = jsonObject.getJSONObject("reservation");

            setId(reservationJson.getInt("pk"));
            User teacher = User.generateTeacher(jsonObject);
            setTeacher(teacher);
            JSONObject reservationFields = reservationJson.getJSONObject("fields");
            setReservedAt(DateUtil.string2Datetime(reservationFields.getString("reserved_at")));
            setNumOf15Minutes(reservationFields.getInt("num_of_15_minutes"));
            setPurpose(reservationFields.getString("purpose"));
            setStatus(reservationFields.getString("status"));
            setUpdatedAt(DateUtil.string2Datetime(reservationFields.getString("updated_at")));
            setCreatedAt(DateUtil.string2Datetime(reservationFields.getString("created_at")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        int temp_duration = 0;
        try {
            temp_duration = jsonObject.getJSONObject("twilio_call").getJSONObject("fields").getInt("duration");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            temp_duration = jsonObject.getJSONObject("video_call").getJSONObject("fields").getInt("duration");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setDuration((int)Math.ceil(temp_duration/60.0));
    }

    public static ArrayList<Reservation> loadReservationFromJsonArray(JSONArray jsonArray){
        ArrayList<Reservation> reservationList = new ArrayList<Reservation>();
        int length = jsonArray.length();
        for(int i=0; i<length; i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Reservation reservation = new Reservation(jsonObject);
                reservationList.add(reservation);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return reservationList;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public User getTeacher() {
        return teacher;
    }

    public void setTeacher(User teacher) {
        this.teacher = teacher;
    }

    public Date getReservedAt() {
        return reservedAt;
    }

    public void setReservedAt(Date reservedAt) {
        this.reservedAt = reservedAt;
    }

    public int getNumOf15Minutes() {
        return numOf15Minutes;
    }

    public void setNumOf15Minutes(int numOf15Minutes) {
        this.numOf15Minutes = numOf15Minutes;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getListTag() {
        return listTag;
    }

    public void setListTag(String listTag) {
        this.listTag = listTag;
    }
}
