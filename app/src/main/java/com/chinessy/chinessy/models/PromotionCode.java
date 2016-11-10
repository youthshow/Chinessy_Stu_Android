package com.chinessy.chinessy.models;

import com.chinessy.chinessy.utils.DateUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by larry on 15/12/2.
 */
public class PromotionCode implements Serializable{
    String id;
    Product product;
    String code;
    Date startAt;
    Date endAt;
    boolean disposable;
    boolean disposableByPerson;
    int usedCount;

    public PromotionCode(JSONObject jsonObject){
        try {
            JSONObject productJson = jsonObject.getJSONObject("product");
            JSONObject promotionCodeJson = jsonObject.getJSONObject("promotion_code");
            setProduct(new Product(productJson));
            setId(promotionCodeJson.getInt("pk") + "");

            JSONObject fieldsJson = promotionCodeJson.getJSONObject("fields");
            setCode(fieldsJson.getString("code"));
            setStartAt(DateUtil.string2Date("start_at", TimeZone.getDefault()));
            setEndAt(DateUtil.string2Date("end_at", TimeZone.getDefault()));
            setDisposable(fieldsJson.getBoolean("disposable"));
            setDisposableByPerson(fieldsJson.getBoolean("disposable_by_person"));
            setUsedCount(fieldsJson.getInt("used_count"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isDisposable() {
        return disposable;
    }

    public void setDisposable(boolean disposable) {
        this.disposable = disposable;
    }

    public boolean isDisposableByPerson() {
        return disposableByPerson;
    }

    public void setDisposableByPerson(boolean disposableByPerson) {
        this.disposableByPerson = disposableByPerson;
    }

    public Date getEndAt() {
        return endAt;
    }

    public void setEndAt(Date endAt) {
        this.endAt = endAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Date getStartAt() {
        return startAt;
    }

    public void setStartAt(Date startAt) {
        this.startAt = startAt;
    }

    public int getUsedCount() {
        return usedCount;
    }

    public void setUsedCount(int usedCount) {
        this.usedCount = usedCount;
    }


}
