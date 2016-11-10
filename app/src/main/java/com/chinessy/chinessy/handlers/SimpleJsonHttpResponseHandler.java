package com.chinessy.chinessy.handlers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by lingeng on 2015/7/20.
 */
public class SimpleJsonHttpResponseHandler extends JsonHttpResponseHandler {
    final String tag = "SJsonHttpResHandler";

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        super.onSuccess(statusCode, headers, response);
        Log.w(tag, "success, json object: " + response.toString());
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
        super.onSuccess(statusCode, headers, response);
        Log.w(tag, "success, json array: " + response.toString());
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        super.onFailure(statusCode, headers, throwable, errorResponse);
        if(errorResponse != null){
            Log.w(tag, "failure, json object: " + errorResponse.toString());
        }else{
            Log.w(tag, "failure, json object: " + null);
        }

    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
        super.onFailure(statusCode, headers, throwable, errorResponse);
        Log.w(tag, "failure, json array: " + errorResponse.toString());

    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        super.onFailure(statusCode, headers, responseString, throwable);
        Log.w(tag, "failure, string: " + responseString);

    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, String responseString) {
        super.onSuccess(statusCode, headers, responseString);
        Log.w(tag, "success, string: " + responseString);

    }

    public static void defaultHandler(Context context, String message){
//        final SimpleDialog promptDialog = new SimpleDialog(context);
//        promptDialog.message(message);
//        promptDialog.positiveAction(R.string.OK);
//        promptDialog.positiveActionClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                promptDialog.dismiss();
//            }
//        });
//        promptDialog.show();
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
