package com.chinessy.chinessy.models;

import android.content.Context;
import android.content.SharedPreferences;

import com.chinessy.chinessy.Chinessy;
import com.chinessy.chinessy.Config;
import com.chinessy.chinessy.R;
import com.chinessy.chinessy.clients.InternalClient;
import com.chinessy.chinessy.utils.DateUtil;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

/**
 * Created by larry on 15/7/11.
 */
public class UserProfile implements Serializable{

    private String id = "";
    private String name = "";
    private String phone = "";
    private String status = "";

    private String role = "";
    private String referralCode = "";
    private String refereeCode = "";

    private int numOf15Minutes;
    private int balanceFree;
    private int balancePackage;

    private String introduction = "";
    private String education = "";
    private String spokenLanguages = "";
    private LocalImage headImg = new LocalImage();
    private String country = "";
    private String countryCode = "";
    private float score = 5;
    private long servedMinutes= 0;


    private Date updatedAt = new Date();
    private Date createdAt = new Date();

    public UserProfile(){

    }

    public UserProfile(JSONObject jsonObject){
        try {
            setId(jsonObject.getInt("pk") + "");
            JSONObject fields = jsonObject.getJSONObject("fields");
            setName(fields.getString("name"));
            setStatus(fields.getString("status"));
            setPhone(fields.getString("phone"));

            setRole(fields.getString("role"));
            setReferralCode(fields.getString("referral_code"));
            setRefereeCode(fields.getString("referee_code"));

            setNumOf15Minutes(fields.getInt("num_of_15_minutes"));
            setBalanceFree(fields.getInt("balance_free"));
//            setBalancePackage(fields.getInt("balance_package_left"));

            setIntroduction(fields.getString("introduction"));
            setEducation(fields.getString("education"));
            setSpokenLanguages(fields.getString("spoken_languages"));
            setHeadImg(new LocalImage(fields.getString("head_img_key")));
            setCountry(fields.getString("country"));
            setCountryCode(fields.getString("country_code"));
            setScore((float) fields.getDouble("score"));
            setServedMinutes(fields.getLong("served_minutes"));

            setUpdatedAt(DateUtil.string2Datetime(fields.getString("updated_at")));
            setCreatedAt(DateUtil.string2Datetime(fields.getString("created_at")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setName(String name, Context context) {
        this.name = name;
        SharedPreferences sp = context.getSharedPreferences(Config.SP_SETTINGS, Context.MODE_PRIVATE);
        sp.edit().putString("user_profile_name", getName()).commit();
    }

    public int getBalanceFree() {
        return balanceFree;
    }

    public void setBalanceFree(int balanceFree) {
        this.balanceFree = balanceFree;
    }

    public void setBalanceFree(int balanceFree, Context context) {
        this.balanceFree = balanceFree;
        SharedPreferences sp = context.getSharedPreferences(Config.SP_SETTINGS, Context.MODE_PRIVATE);
        sp.edit().putString("user_profile_balance_free", getBalanceFree() + "").commit();
    }

    public int getBalancePackage() {
        return balancePackage;
    }

    public void setBalancePackage(int balancePackage) {
        this.balancePackage = balancePackage;
    }

    public void setBalancePackage(int balancePackage, Context context) {
        this.balancePackage = balancePackage;
        SharedPreferences sp = context.getSharedPreferences(Config.SP_SETTINGS, Context.MODE_PRIVATE);
        sp.edit().putString("user_profile_balance_package", getBalancePackage() + "").commit();
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPhone(String phone, Context context) {
        this.phone = phone;
        SharedPreferences sp = context.getSharedPreferences(Config.SP_SETTINGS, Context.MODE_PRIVATE);
        sp.edit().putString("user_profile_phone", getPhone()).commit();
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    public String getRefereeCode() {
        return refereeCode;
    }

    public void setRefereeCode(String refereeCode) {
        this.refereeCode = refereeCode;
    }

    public int getNumOf15Minutes() {
        return numOf15Minutes;
    }

    public void setNumOf15Minutes(int numOf15Minutes) {
        this.numOf15Minutes = numOf15Minutes;
    }
    public void setNumOf15Minutes(int numOf15Minutes, Context context) {
        this.numOf15Minutes = numOf15Minutes;
        SharedPreferences sp = context.getSharedPreferences(Config.SP_SETTINGS, Context.MODE_PRIVATE);
        sp.edit().putString("user_profile_num_of_15_minutes", getNumOf15Minutes() + "").commit();
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getSpokenLanguages() {
        return spokenLanguages;
    }

    public void setSpokenLanguages(String spokenLanguages) {
        this.spokenLanguages = spokenLanguages;
    }

    public LocalImage getHeadImg() {
        return headImg;
    }

    public void setHeadImg(LocalImage headImg) {
        this.headImg = headImg;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setCountry(String country, Context context) {
        this.country = country;
        SharedPreferences sp = context.getSharedPreferences(Config.SP_SETTINGS, Context.MODE_PRIVATE);
        sp.edit().putString("user_profile_country", getCountry()).commit();
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public void setCountryCode(String countryCode, Context context) {
        this.countryCode = countryCode;
        SharedPreferences sp = context.getSharedPreferences(Config.SP_SETTINGS, Context.MODE_PRIVATE);
        sp.edit().putString("user_profile_country_code", getCountryCode()).commit();
    }

    public float getScore() {
        BigDecimal bd = new BigDecimal(this.score);
        bd.setScale(1, BigDecimal.ROUND_HALF_UP);
        float result = bd.floatValue();
        bd = null;
        return result;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public long getServedMinutes() {
        return servedMinutes;
    }

    public void setServedMinutes(long servedMinutes) {
        this.servedMinutes = servedMinutes;
    }

    public String getShareMessage(Context context){
        String shareMsg = context.getString(R.string.g_share_message) + " ";
        return shareMsg + getReferralCode();
    }

    public void localSave(Context context){
        SharedPreferences sp = context.getSharedPreferences(Config.SP_SETTINGS, Context.MODE_PRIVATE);
        sp.edit().putString("user_profile_id", getId())
                .putString("user_profile_name", getName())
                .putString("user_profile_status", getStatus())
                .putString("user_profile_phone", getPhone())

                .putString("user_profile_role", getRole())
                .putString("user_profile_referral_code", getReferralCode())
                .putString("user_profile_referee_code", getRefereeCode())
                .putString("user_profile_num_of_15_minutes", getNumOf15Minutes()+"")
                .putString("user_profile_balance_free", getBalanceFree() + "")
                .putString("user_profile_balance_package", getBalancePackage() + "")
                .putString("user_profile_introduction", getIntroduction())
                .putString("user_profile_education", getEducation())
                .putString("user_profile_spoken_languages", getSpokenLanguages())
                .putString("user_profile_head_img_key", getHeadImg().getKey())
                .putString("user_profile_country", getCountry())
                .putString("user_profile_country_code", getCountryCode())

                .putLong("user_profile_updated_at", getUpdatedAt().getTime())
                .putLong("user_profile_created_at", getCreatedAt().getTime())
                .commit();
    }
    public void localRead(Context context){
        SharedPreferences sp = context.getSharedPreferences(Config.SP_SETTINGS, Context.MODE_PRIVATE);
        setId(sp.getString("user_profile_id", ""));
        setName(sp.getString("user_profile_name", ""));
        setStatus(sp.getString("user_profile_status", ""));
        setPhone(sp.getString("user_profile_phone", ""));

        setRole(sp.getString("user_profile_role", ""));
        setReferralCode(sp.getString("user_profile_referral_code", ""));
        setRefereeCode(sp.getString("user_profile_referee_code", ""));
        setNumOf15Minutes(Integer.parseInt(sp.getString("user_profile_num_of_15_minutes", "0")));
        setBalanceFree(Integer.parseInt(sp.getString("user_profile_balance_free", "0")));
        setBalancePackage(Integer.parseInt(sp.getString("user_profile_balance_package", "0")));
        setIntroduction(sp.getString("user_profile_introduction", ""));
        setEducation(sp.getString("user_profile_education", ""));
        setSpokenLanguages(sp.getString("user_profile_spoken_languages", ""));
        setHeadImg(new LocalImage(sp.getString("user_profile_head_img_key", "")));
        setCountry(sp.getString("user_profile_country", ""));
        setCountryCode(sp.getString("user_profile_country_code", ""));

        setUpdatedAt(new Date(sp.getLong("user_profile_updated_at", 0)));
        setCreatedAt(new Date(sp.getLong("user_profile_created_at", 0)));
    }

    public int getBalance(){
        return getBalanceFree()+getBalancePackage();
    }

    public void refreshBalance(final Context context, final IAfterRefreshBalance afterRefreshBalance){
//        final ProgressDialog progressDialog = new ProgressDialog(context);
//        progressDialog.setMessage(context.getString(R.string.Waiting));
//        progressDialog.show();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("access_token", Chinessy.chinessy.getUser().getAccessToken());
            InternalClient.postInternalJson(context, "remain_minutes/get", jsonObject, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        switch (response.getInt("code")) {
                            case 10000:
                                User.updateUserBalance(context, response);
                                afterRefreshBalance.execute();
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }finally {
//                        progressDialog.dismiss();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void refreshBalanceInBackground(final Context context){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("access_token", Chinessy.chinessy.getUser().getAccessToken());
            InternalClient.postInternalJson(context, "remain_minutes/get", jsonObject, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        switch (response.getInt("code")) {
                            case 10000:
                                User.updateUserBalance(context, response);
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public interface IAfterRefreshBalance{
        public void execute();
    }
}
