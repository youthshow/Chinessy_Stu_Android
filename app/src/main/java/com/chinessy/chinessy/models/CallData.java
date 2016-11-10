package com.chinessy.chinessy.models;

import java.io.Serializable;

/**
 * Created by larry on 15/11/7.
 */
public class CallData implements Serializable {
    User callee;
    boolean isConnected = false;
    String callId;
    int duration;
    public CallData(User callee){
        setCallee(callee);
    }
    public User getCallee() {
        return callee;
    }
    public void setCallee(User callee) {
        this.callee = callee;
    }
    public boolean isConnected() {
        return isConnected;
    }
    public void setIsConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }
    public String getCallId() {
        return callId;
    }
    public void setCallId(String callId) {
        this.callId = callId;
    }
    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }
}
