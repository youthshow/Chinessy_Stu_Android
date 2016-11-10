package com.chinessy.chinessy.models;

import java.io.Serializable;

/**
 * Created by larry on 15/8/5.
 */
public class ModifyObject implements Serializable{
    String title;
    String fieldName;
    String fieldValue;
    String modifyUrl;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public String getModifyUrl() {
        return modifyUrl;
    }

    public void setModifyUrl(String modifyUrl) {
        this.modifyUrl = modifyUrl;
    }
}
