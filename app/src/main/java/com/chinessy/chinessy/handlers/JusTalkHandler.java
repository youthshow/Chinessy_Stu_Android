package com.chinessy.chinessy.handlers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.chinessy.chinessy.Chinessy;
import com.chinessy.chinessy.Config;
import com.chinessy.chinessy.clients.InternalClient;
import com.chinessy.chinessy.models.CallData;
import com.chinessy.chinessy.models.User;
import com.justalk.cloud.juscall.MtcCallDelegate;
import com.justalk.cloud.lemon.MtcApi;
import com.justalk.cloud.lemon.MtcCall;
import com.justalk.cloud.lemon.MtcCallConstants;
import com.justalk.cloud.lemon.MtcCallExt;
import com.justalk.cloud.lemon.MtcConstants;
import com.justalk.cloud.lemon.MtcUe;
import com.justalk.cloud.lemon.MtcUeConstants;
import com.justalk.cloud.zmf.ZmfAudio;
import com.justalk.cloud.zmf.ZmfVideo;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import cz.msebera.android.httpclient.Header;

/**
 * Created by larry on 15/11/4.
 */
public class JusTalkHandler {
    final String tag = "JusTalkHandler";
    Context mContext;
    CallData mCallData = null;
    BroadcastReceiver mAuthRequiredReceiver = new AuthRequiredBroadcastReceiver();

    BroadcastReceiver mLoginOkReceiver;
    BroadcastReceiver mLoginDidFailReceiver;

    BroadcastReceiver mAuthExpiredReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "auth expired", Toast.LENGTH_SHORT).show();
            MtcUe.Mtc_UeRefreshAuth();
        }
    };

    BroadcastReceiver mDidLogoutNotification;
    BroadcastReceiver mLogoutedNotification;

    BroadcastReceiver mCallConnectingBroadcastReceiver = new CallConnectingBroadcastReceiver();
//    BroadcastReceiver mCallTalkingBroadcastReceiver = new CallTalkingBroadcastReceiver();
    BroadcastReceiver mCallDidTermBroadcastReceiver = new CallDidTermBroadcastReceiver();
    BroadcastReceiver mCallTermedBroadcastReceiver = new CallTermedBroadcastReceiver();
//    BroadcastReceiver mCallTransferEndedBroadcastReceiver = new CallTransferEndedBroadcastReceiver();
//    BroadcastReceiver mCallUnholdOkBroadcastReceiver = new CallUnholdOkBroadcastReceiver();
//    BroadcastReceiver mCallUnheldBroadcastReceiver = new CallUnheldBroadcastReceiver();

    public JusTalkHandler(Context context){
        mContext = context;
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
        localBroadcastManager.registerReceiver(mAuthExpiredReceiver, new IntentFilter(MtcUe.MtcUeAuthorizationExpiredNotification));
        localBroadcastManager.registerReceiver(mAuthRequiredReceiver, new IntentFilter(MtcUe.MtcUeAuthorizationRequireNotification));
    }

    public void login(IOnBroadCastReceived loginOkCallback, IOnBroadCastReceived loginDidFailCallback){
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
        mLoginOkReceiver = new LoginOkBroadcastReceiver(loginOkCallback);
        mLoginDidFailReceiver = new LoginDidFailBroadcastReceiver(loginDidFailCallback);
        localBroadcastManager.registerReceiver(mLoginOkReceiver, new IntentFilter(MtcApi.MtcLoginOkNotification));
        localBroadcastManager.registerReceiver(mLoginDidFailReceiver, new IntentFilter(MtcApi.MtcLoginDidFailNotification));
        localBroadcastManager.registerReceiver(mCallConnectingBroadcastReceiver, new IntentFilter(MtcCall.MtcCallConnectingNotification));
//        localBroadcastManager.registerReceiver(mCallTalkingBroadcastReceiver, new IntentFilter(MtcCall.MtcCallTalkingNotification));
        localBroadcastManager.registerReceiver(mCallDidTermBroadcastReceiver, new IntentFilter(MtcCall.MtcCallDidTermNotification));
        localBroadcastManager.registerReceiver(mCallTermedBroadcastReceiver, new IntentFilter(MtcCall.MtcCallTermedNotification));
//        localBroadcastManager.registerReceiver(mCallTransferEndedBroadcastReceiver, new IntentFilter(MtcCall.MtcCallTransferEndedNotification));
//        localBroadcastManager.registerReceiver(mCallUnholdOkBroadcastReceiver, new IntentFilter(MtcCall.MtcCallUnholdOkNotification));
//        localBroadcastManager.registerReceiver(mCallUnheldBroadcastReceiver, new IntentFilter(MtcCall.MtcCallUnheldNotification));

        justLogin();
    }

    public void justLogin(){
        String accessToken = Chinessy.chinessy.getUser().getAccessToken();
        if(accessToken!=null && !accessToken.equals("")){
            ZmfAudio.initialize(mContext);
            ZmfVideo.initialize(mContext);
            MtcApi.init(mContext, Config.JUSTALK_CLOUD_APPKEY);
            MtcCallDelegate.init(mContext);

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(MtcApi.KEY_PASSWORD, "123");
                jsonObject.put(MtcApi.KEY_SERVER_ADDRESS, Config.JUSTALK_CLOUD_SERVER_ADDRESS);
                MtcApi.login(Chinessy.chinessy.getUser().getAccountAlias(), jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void logout(IOnBroadCastReceived didLogoutCallback, IOnBroadCastReceived logoutedCallback){
        justLogout(didLogoutCallback, logoutedCallback);

        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
        localBroadcastManager.unregisterReceiver(mLoginOkReceiver);
        localBroadcastManager.unregisterReceiver(mLoginDidFailReceiver);
        localBroadcastManager.unregisterReceiver(mCallConnectingBroadcastReceiver);
//        localBroadcastManager.unregisterReceiver(mCallTalkingBroadcastReceiver);
        localBroadcastManager.unregisterReceiver(mCallDidTermBroadcastReceiver);
        localBroadcastManager.unregisterReceiver(mCallTermedBroadcastReceiver);
//        localBroadcastManager.unregisterReceiver(mCallTransferEndedBroadcastReceiver);
//        localBroadcastManager.unregisterReceiver(mCallUnholdOkBroadcastReceiver);
//        localBroadcastManager.unregisterReceiver(mCallUnheldBroadcastReceiver);
    }

    public void justLogout(IOnBroadCastReceived didLogoutCallback, IOnBroadCastReceived logoutedCallback){
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
        mDidLogoutNotification = new DidLogoutBroadcastReceiver(didLogoutCallback);
        mLogoutedNotification = new LogoutedBroadcastReceiver(logoutedCallback);
        localBroadcastManager.registerReceiver(mDidLogoutNotification, new IntentFilter(MtcApi.MtcDidLogoutNotification));
        localBroadcastManager.registerReceiver(mLogoutedNotification, new IntentFilter(MtcApi.MtcLogoutedNotification));

        MtcApi.logout();
    }

    public void call(CallData callData, String callerName){
        mCallData = callData;
        User callee = callData.getCallee();
        MtcCallDelegate.call(callee.getAccountAlias(), callerName, callee.getUserProfile().getName(), true);
    }

    public void destroy(){
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
        localBroadcastManager.unregisterReceiver(mAuthExpiredReceiver);
        localBroadcastManager.unregisterReceiver(mAuthRequiredReceiver);

        MtcApi.destroy();
        ZmfVideo.terminate();
        ZmfAudio.terminate();
    }

    class AuthRequiredBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String id;
            String nonce;
            try {
                String info = intent.getStringExtra(MtcApi.EXTRA_INFO);
                JSONObject json = (JSONObject) new JSONTokener(info).nextValue();
                id = json.getString(MtcUeConstants.MtcUeUriKey);
                nonce = json.getString(MtcUeConstants.MtcUeAuthNonceKey);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            // 发送授权请求到客户服务器,请求中应该包括 id 和 nonce ...
            // authCode 是客户服务器返回的授权信息
            JSONObject jsonParams = new JSONObject();
            try {
                jsonParams.put("access_token", Chinessy.chinessy.getUser().getAccessToken());
                jsonParams.put("nonce", nonce);
                InternalClient.postJson(context, "video/authorize", jsonParams, new SimpleJsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        try {
                            switch (response.getInt("code")) {
                                case 10000:
                                    String authCode = response.getJSONObject("data").getString("auth_code");
                                    MtcUe.Mtc_UePromptAuthCode(authCode);
                                    break;
                                default:
                                    SimpleJsonHttpResponseHandler.defaultHandler(mContext, response.getString("message"));
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
    }
    class LoginOkBroadcastReceiver extends BroadcastReceiver{
        IOnBroadCastReceived mIOnBroadCastReceived;
        public LoginOkBroadcastReceiver(IOnBroadCastReceived iOnBroadCastReceived){
            mIOnBroadCastReceived = iOnBroadCastReceived;
        }
        @Override
        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(mContext, "auth suceed", Toast.LENGTH_SHORT).show();
            if(mIOnBroadCastReceived != null){
                mIOnBroadCastReceived.callBack();
            }
        }
    }
    class LoginDidFailBroadcastReceiver extends BroadcastReceiver{
        IOnBroadCastReceived mIOnBroadCastReceived;
        public LoginDidFailBroadcastReceiver(IOnBroadCastReceived iOnBroadCastReceived){
            mIOnBroadCastReceived = iOnBroadCastReceived;
        }
        @Override
        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(mContext, "auth failed", Toast.LENGTH_SHORT).show();
            if(mIOnBroadCastReceived != null) {
                mIOnBroadCastReceived.callBack();
            }
        }
    }
    class DidLogoutBroadcastReceiver extends BroadcastReceiver{
        IOnBroadCastReceived mIOnBroadCastReceived;
        public DidLogoutBroadcastReceiver(IOnBroadCastReceived iOnBroadCastReceived){
            mIOnBroadCastReceived = iOnBroadCastReceived;
        }
        @Override
        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(context, "did logout", Toast.LENGTH_SHORT).show();
            if(mIOnBroadCastReceived != null) {
                mIOnBroadCastReceived.callBack();
            }
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
            localBroadcastManager.unregisterReceiver(mDidLogoutNotification);
        }
    }
    class LogoutedBroadcastReceiver extends BroadcastReceiver {
        IOnBroadCastReceived mIOnBroadCastReceived;
        public LogoutedBroadcastReceiver(IOnBroadCastReceived iOnBroadCastReceived){
            mIOnBroadCastReceived = iOnBroadCastReceived;
        }
        @Override
        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(context, "logouted", Toast.LENGTH_SHORT).show();
            if(mIOnBroadCastReceived != null) {
                mIOnBroadCastReceived.callBack();
            }
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
            localBroadcastManager.unregisterReceiver(mLogoutedNotification);
        }
    }
    class CallConnectingBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "call connecting", Toast.LENGTH_SHORT).show();
            int callIdKey = MtcConstants.INVALIDID;
            try {
                String info = intent.getStringExtra(MtcApi.EXTRA_INFO);
                JSONObject json = (JSONObject) new JSONTokener(info)
                        .nextValue();
                callIdKey = json.getInt(MtcCallConstants.MtcCallIdKey);
                String callId = MtcCallExt.Mtc_CallGetSipCallId(callIdKey);
                mCallData.setCallId(callId);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            mCallData.setIsConnected(true);
        }
    }
    class CallTalkingBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "call talking", Toast.LENGTH_SHORT).show();
            int callIdKey = MtcConstants.INVALIDID;
            try {
                String info = intent.getStringExtra(MtcApi.EXTRA_INFO);
                JSONObject json = (JSONObject) new JSONTokener(info)
                        .nextValue();
                callIdKey = json.getInt(MtcCallConstants.MtcCallIdKey);
                Log.e(tag, callIdKey + "1----------------------------------------------");
                String x = MtcCallExt.Mtc_CallGetSipCallId(callIdKey);
                Log.e(tag, x + "1----------------------------------------------" + x);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
//            MtcCallExt.Mtc_CallGetSipCallId()
        }
    }
    class CallDidTermBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "call ended", Toast.LENGTH_SHORT).show();
        }
    }
    class CallTermedBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "call ended", Toast.LENGTH_SHORT).show();
        }
    }
    class CallTransferEndedBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "call transfer ended", Toast.LENGTH_SHORT).show();
            Log.e(tag, "call transfer ended");
        }
    }
    class CallUnholdOkBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "call unhold ok", Toast.LENGTH_SHORT).show();
            Log.e(tag, "call unhold ok");
        }
    }
    class CallUnheldBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "call unheld", Toast.LENGTH_SHORT).show();
            Log.e(tag, "call unheld");
        }
    }
    public interface IOnBroadCastReceived{
        void callBack();
    }
}
