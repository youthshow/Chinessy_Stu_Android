package com.chinessy.chinessy.models;

import android.graphics.Bitmap;

import com.chinessy.chinessy.Config;
import com.chinessy.chinessy.utils.FileUtil;
import com.chinessy.chinessy.utils.PictureUtil;

import java.io.Serializable;

/**
 * Created by lingeng on 2015/7/20.
 */
public class LocalImage implements Serializable{
    private String key = "";
//    private Bitmap bitmap = null;

    public LocalImage(){}

    public LocalImage(String key){
        setKey(key);
    }
    boolean isImgExists(){
        return FileUtil.ifExists(Config.FOLDER_HEAD_IMG + getKey());
    }

    public String getAbsolutePath(){
        return FileUtil.getAbsolutePath(Config.FOLDER_HEAD_IMG + getKey());
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Bitmap getBitmap() {
//        if(null==bitmap && isImgExists()){
//            bitmap = PictureUtil.readPurgeableBmp(getAbsolutePath());
//        }
        Bitmap bitmap = PictureUtil.readPurgeableBmp(getAbsolutePath());
        return bitmap;
    }

    public Bitmap getBitmap(boolean isMutable){
        if(isMutable){
            Bitmap bitmap = PictureUtil.readMutableBmp(getAbsolutePath());
            return bitmap;
        }else{
            return getBitmap();
        }
    }

}
