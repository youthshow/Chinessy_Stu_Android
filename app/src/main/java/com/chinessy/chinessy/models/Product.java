package com.chinessy.chinessy.models;

import com.chinessy.chinessy.utils.DateUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by larry on 15/10/12.
 */
public class Product implements Serializable{
    public static final String UNIT_MONTH = "month";
    public static final String UNIT_WEEK = "week";
    public static final String UNIT_DAY = "day";

    String id;
    List<Price> priceJson = new ArrayList<Price>();
    String productType;
    String name;
    boolean isDefault;
    double price;
    Date updatedAt;
    int daysLast;
    String currency;
    boolean saleable;
    String enId;
    int minutes;
    Date createdAt;
    String description;

    String unit;
    int unitNum;

    public class Price implements Serializable{
        public boolean isDefault;
        public double price;
        public int quantity;
    }

    public Product(JSONObject jsonObject){
        try {
            setId(jsonObject.getInt("pk") + "");
            JSONObject fieldsJson = jsonObject.getJSONObject("fields");
//            setPriceJson(new JSONArray(fieldsJson.getString("price_json")));
            setProductType(fieldsJson.getString("product_type"));
            setName(fieldsJson.getString("name"));
            setPrice(fieldsJson.getDouble("price"));
            setDaysLast(fieldsJson.getInt("days_last"));
            setCurrency(fieldsJson.getString("currency"));
            setSaleable(fieldsJson.getBoolean("saleable"));
            setEnId(fieldsJson.getString("en_id"));
            setMinutes(fieldsJson.getInt("minutes"));
            setDescription(fieldsJson.getString("description"));
            setUpdatedAt(DateUtil.string2Datetime(fieldsJson.getString("updated_at")));
            setCreatedAt(DateUtil.string2Datetime(fieldsJson.getString("created_at")));

            if(getDaysLast()%30 == 0){
                setUnit(Product.UNIT_MONTH);
                setUnitNum(getDaysLast()/30);
            }else if(getDaysLast()%7 == 0){
                setUnit(Product.UNIT_WEEK);
                setUnitNum(getDaysLast()/7);
            }else{
                setUnit(Product.UNIT_DAY);
                setUnitNum(getDaysLast());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getDaysLast() {
        return daysLast;
    }

    public void setDaysLast(int daysLast) {
        this.daysLast = daysLast;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEnId() {
        return enId;
    }

    public void setEnId(String enId) {
        this.enId = enId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<Price> getPriceJson() {
        return priceJson;
    }

    public void setPriceJson(JSONArray jsonArray) {
        int length = jsonArray.length();
        try {
            List<Price> priceList = new ArrayList<Price>();
            for(int i=0; i<length; i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Price price = new Price();
                price.isDefault = (jsonObject.getInt("default")==1);
                price.price = jsonObject.getDouble("price");
                price.quantity = jsonObject.getInt("quantity");
                priceList.add(price);
            }
            this.priceJson = priceList;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public boolean isSaleable() {
        return saleable;
    }

    public void setSaleable(boolean saleable) {
        this.saleable = saleable;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getUnitNum() {
        return unitNum;
    }

    public void setUnitNum(int unitNum) {
        this.unitNum = unitNum;
    }

    public String getHumanReadPeriod(){
        if(getUnitNum()==1){
            return getUnitNum()+getUnit();
        }else{
            return getUnitNum()+" "+getUnit()+"s";
        }
    }
}
