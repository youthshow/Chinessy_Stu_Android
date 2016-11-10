package com.chinessy.chinessy.models;

import android.view.View;

import java.io.Serializable;

/**
 * Created by larry on 15/8/20.
 */
public class ButtonData implements Serializable{
    String btnText;
    View.OnClickListener btnClickListener;

    public String getBtnText() {
        return btnText;
    }

    public void setBtnText(String btnText) {
        this.btnText = btnText;
    }

    public View.OnClickListener getBtnClickListener() {
        return btnClickListener;
    }

    public void setBtnClickListener(View.OnClickListener btnClickListener) {
        this.btnClickListener = btnClickListener;
    }
}
