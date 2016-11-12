package com.chinessy.chinessy.handlers;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.FileAsyncHttpResponseHandler;



import java.io.File;

import cz.msebera.android.httpclient.Header;

/**
 * Created by larry on 15/8/20.
 */
public class SimpleFileAsyncHttpResponseHandler extends FileAsyncHttpResponseHandler{
    final String tag = "SFileAsyncHttpRespH";
    public SimpleFileAsyncHttpResponseHandler(Context context) {
        super(context);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
        Log.w(tag, throwable.getMessage());
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, File file) {

    }
}
