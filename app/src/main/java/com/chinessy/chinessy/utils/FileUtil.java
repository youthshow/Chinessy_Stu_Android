package com.chinessy.chinessy.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.os.Environment;
import com.chinessy.chinessy.Chinessy;

/**
 * Created by larry on 15/7/10.
 */
public class FileUtil {
    public static String SDCARD_DIR = "/sdcard/";
    public static String NOSDCARD_DIR = "/data/data/" + Chinessy.chinessy.getPackageName();


    public static boolean hasSDCard() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    public static String getDirPrefix(){
        String dir;
        if (FileUtil.hasSDCard()) {
            dir = SDCARD_DIR;
        } else {
            dir = NOSDCARD_DIR;
        }
        return dir;
    }

    public static String getAbsolutePath(String relativePath){
        return getDirPrefix() + relativePath;
    }

    public static String createFolder(String folderName){
        String dir;
        if (FileUtil.hasSDCard()) {
            dir = SDCARD_DIR;
        } else {
            dir = NOSDCARD_DIR;
        }
        String path = dir + folderName;
        File destDir = new File(path);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        return path;
    }

    public static boolean ifExists(String relativePath){
        String dir;
        if (FileUtil.hasSDCard()) {
            dir = SDCARD_DIR;
        } else {
            dir = NOSDCARD_DIR;
        }
        relativePath = dir + relativePath;

        File file = new File(relativePath);
        return file.exists();
    }

    public static void fileChannelCopy(File from, File to) {
        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;
        try {
            fi = new FileInputStream(from);
            fo = new FileOutputStream(to);
            in = fi.getChannel();//得到对应的文件通道
            out = fo.getChannel();//得到对应的文件通道
            in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fi.close();
                in.close();
                fo.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
