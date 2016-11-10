package com.chinessy.chinessy.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.chinessy.chinessy.Chinessy;
import com.chinessy.chinessy.Config;
import com.chinessy.chinessy.clients.InternalClient;
import com.chinessy.chinessy.handlers.SimpleJsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.TimeZone;

/**
 * Created by larry on 15/7/11.
 */
public class User implements Serializable{
    static final String tag = "User";

    public static final String STATUS_AVAILABLE = "available";
    public static final String STATUS_BUSY = "busy";
    public static final String STATUS_OFFLINE = "offline";


    private String id = "";
    private String mEmail = "";
    private String mAccessToken = "";
    private String listTag = "";
    private UserProfile userProfile = new UserProfile();
    private UserBalancePackage userBalancePackage = new UserBalancePackage();

    private boolean isFavourites = false;

    public User(){

    }

    public User(String listTag){
        this.listTag = listTag;
    }

    public User(JSONObject jsonObject){
        JSONObject user = null;
        JSONObject userProfile = null;
        try {
            user = jsonObject.getJSONObject("user");
            userProfile = jsonObject.getJSONObject("user_profile");
            setEmail(user.getJSONObject("fields").getString("email"));
            setId(user.getInt("pk") + "");
            if(jsonObject.has("access_token")){
                setAccessToken(jsonObject.getString("access_token"));
            }
            setUserProfile(new UserProfile(userProfile));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
//            Log.e(tag, isFavourites()+"_______"+getId()+jsonObject.getJSONObject("favourite_tutor"));
            JSONObject favouriteTutor = jsonObject.getJSONObject("favourite_tutor");
            if(favouriteTutor != null){
                String favouriteStatus = favouriteTutor.getJSONObject("fields").getString("status");
                if(favouriteStatus.equals("valid")){
                    setIsFavourites(true);
                }
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    public static User generateTeacher(JSONObject jsonObject){
        User teacher = new User();

        JSONObject user = null;
        JSONObject userProfile = null;
        try {
            user = jsonObject.getJSONObject("teacher");
            userProfile = jsonObject.getJSONObject("teacher_profile");
            teacher.setEmail(user.getJSONObject("fields").getString("email"));
            teacher.setId(user.getInt("pk") + "");
            if(jsonObject.has("access_token")){
                teacher.setAccessToken(jsonObject.getString("access_token"));
            }
            teacher.setUserProfile(new UserProfile(userProfile));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
//            Log.e(tag, isFavourites()+"_______"+getId()+jsonObject.getJSONObject("favourite_tutor"));
            JSONObject favouriteTutor = jsonObject.getJSONObject("favourite_teacher");
            if(favouriteTutor != null){
                String favouriteStatus = favouriteTutor.getJSONObject("fields").getString("status");
                if(favouriteStatus.equals("valid")){
                    teacher.setIsFavourites(true);
                }
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
        return teacher;
    }

    public void activeUser(final Context context){
        JSONObject jsonParams = new JSONObject();
        TimeZone tz = TimeZone.getDefault();
//        Toast.makeText(context, tz.getRawOffset() + "  " + tz.getRawOffset()/3600000, Toast.LENGTH_SHORT).show();
        int timeZone = tz.getRawOffset()/3600000;
        try {
            jsonParams.put("time_zone", timeZone);
            jsonParams.put("access_token", Chinessy.chinessy.getUser().getAccessToken());
            InternalClient.postJson(context, "internal/user/active", jsonParams, new SimpleJsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
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

    public static void updateUserBalance(Context context, JSONObject jsonObject){
        JSONObject data = null;
        try {
            data = jsonObject.getJSONObject("data");
            int numOf15Minutes = data.getInt("num_of_15_minutes");
            int balanceFree = data.getInt("balance_free");
            int balancePackage = data.getInt("balance_package");

            UserProfile userProfile = Chinessy.chinessy.getUser().getUserProfile();
            userProfile.setNumOf15Minutes(numOf15Minutes, context);
            userProfile.setBalanceFree(balanceFree, context);
            userProfile.setBalancePackage(balancePackage, context);

            JSONObject jsonUserBalancePackage = data.getJSONObject("user_balance_package");
            UserBalancePackage userBalancePackage = new UserBalancePackage(jsonUserBalancePackage);
            Chinessy.chinessy.getUser().setUserBalancePackage(userBalancePackage, context);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public UserBalancePackage getUserBalancePackage() {
        return userBalancePackage;
    }

    public void setUserBalancePackage(UserBalancePackage userBalancePackage) {
        this.userBalancePackage = userBalancePackage;
    }

    public void setUserBalancePackage(UserBalancePackage userBalancePackage, Context context) {
        this.userBalancePackage = userBalancePackage;
        this.userBalancePackage.localSave(context);
    }

    public String getAccessToken() {
        return mAccessToken;
    }

    public void setAccessToken(String accessToken) {
        this.mAccessToken = accessToken;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void localSave(Context context){
        SharedPreferences sp = context.getSharedPreferences(Config.SP_SETTINGS, Context.MODE_PRIVATE);
        sp.edit().putString("user_id", getId())
                .putString("user_access_token", getAccessToken())
                .putString("user_email", getEmail()).commit();
        getUserProfile().localSave(context);
        getUserBalancePackage().localSave(context);
    }
    public void localRead(Context context){
        SharedPreferences sp = context.getSharedPreferences(Config.SP_SETTINGS, Context.MODE_PRIVATE);
        setId(sp.getString("user_id", ""));
        setAccessToken(sp.getString("user_access_token", ""));
        setEmail(sp.getString("user_email", ""));

        UserProfile userProfile = new UserProfile();
        userProfile.localRead(context);
        setUserProfile(userProfile);

        UserBalancePackage userBalancePackage = new UserBalancePackage();
        userBalancePackage.localRead(context);
        setUserBalancePackage(userBalancePackage);
    }

    public static ArrayList<User> loadTeachersFromJsonArray(JSONArray jsonArray){
        ArrayList<User> userList = new ArrayList<User>();
        try{
            int length = jsonArray.length();
            for(int i=0; i<length; i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                User user = new User(jsonObject);
                userList.add(user);
            }
        }catch (JSONException e){
            Log.w(tag, e.getMessage());
        }

        return userList;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        this.mEmail = email;
    }

    public boolean isFavourites() {
        return isFavourites;
    }

    public void setIsFavourites(boolean isFavourites) {
        this.isFavourites = isFavourites;
    }

    public String getListTag() {
        return listTag;
    }

    public void setListTag(String listTag) {
        this.listTag = listTag;
    }

    public String getAccountAlias(){
        return getEmail().replace('@', '_');
    }
}
