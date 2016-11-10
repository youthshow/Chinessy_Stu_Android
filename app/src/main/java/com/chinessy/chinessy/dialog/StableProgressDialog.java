package com.chinessy.chinessy.dialog;

import android.app.ProgressDialog;
import android.content.Context;

import com.chinessy.chinessy.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by larry on 15/11/10.
 */
public class StableProgressDialog {
    static final int STABLE_TIME = 2000;
    ProgressDialog mProgressDialog;

    Context mContext;

    Boolean isTaskFinished = false;
    Boolean isTimeEnded = false;
    Timer mTimer = new Timer();
    TimerTask mTimerTask = new TimerTask(){
        @Override
        public void run() {
            isTimeEnded = true;
            dismissProgressDialog();
        }
    };
    IAfterDismiss mAfterDismiss = null;
    public StableProgressDialog(Context context, IAfterDismiss afterDismiss){
        mContext = context;
        mTimer.schedule(mTimerTask, STABLE_TIME);
        mAfterDismiss = afterDismiss;
    }

    public void showProgressDialog(){
        if(mProgressDialog == null){
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setMessage(mContext.getString(R.string.Waiting));
        }
        mProgressDialog.show();
    }

    public void dismissProgressDialog(Boolean taskFinished){
        isTaskFinished = taskFinished;
        dismissProgressDialog();
    }

    private void dismissProgressDialog(){
        if(mProgressDialog != null && isTaskFinished && isTimeEnded){
            mProgressDialog.dismiss();
            if(mAfterDismiss != null){
                mAfterDismiss.execute();
            }
        }
    }

    public interface IAfterDismiss{
        void execute();
    }
}
