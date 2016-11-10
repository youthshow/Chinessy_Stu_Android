package com.chinessy.chinessy.clients;

import android.content.Context;

import com.chinessy.chinessy.Config;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by larry on 15/7/11.
 */
public class InternalClient {
    private static final String BASE_URL = Config.BASE_URL;
    private static final String BASE_INTERNAL_URL = BASE_URL + "internal/";

    private static final String BASE_STATIC_URL = "http://7xl3jz.com1.z0.glb.clouddn.com/";

    private static AsyncHttpClient client = new AsyncHttpClient();
    {
        client.addHeader("Content-Type", "application/json");
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void postInternal(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteInternalUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void getFile(String key, FileAsyncHttpResponseHandler fileAsyncHttpResponseHandler) {
        client.get(getBaseStaticUrl(key), fileAsyncHttpResponseHandler);
    }

    public static void postInternalJson(Context context, String url, JSONObject jsonParams, AsyncHttpResponseHandler responseHandler){
        StringEntity entity = null;
        try {
            entity = new StringEntity(jsonParams.toString());
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            client.post(context, getAbsoluteInternalUrl(url), entity, "application/json", responseHandler);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void postJson(Context context, String url, JSONObject jsonParams, AsyncHttpResponseHandler responseHandler){
        StringEntity entity = null;
        try {
            entity = new StringEntity(jsonParams.toString());
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            client.post(context, getAbsoluteUrl(url), entity, "application/json", responseHandler);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private static String getAbsoluteInternalUrl(String relativeUrl) {
        return BASE_INTERNAL_URL + relativeUrl;
    }
    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
    public static String getBaseStaticUrl(String key){
        return BASE_STATIC_URL + key;
    }
}
