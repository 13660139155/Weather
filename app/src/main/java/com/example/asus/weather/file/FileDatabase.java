package com.example.asus.weather.file;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.asus.weather.MyApplication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by ASUS on 2018/6/7.
 */

public class FileDatabase {

    /**
     * 在本地文件保存图片
     * @param fileName 文件名
     * @param bitmap 图片
     */
    public static void saveBitmap(String fileName, Bitmap bitmap){
        String newUrl = fileName.replace("/", "");
        BufferedOutputStream bufferedOutputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = MyApplication.getContext().openFileOutput(newUrl, Context.MODE_PRIVATE);
            bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, bufferedOutputStream);
            bufferedOutputStream.flush();
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 在本地文件取出保存的图片
     * @param fileName 文件名
     * @return 图片
     */
    public static Bitmap loadBitmap(String fileName){
        String newUrl = fileName.replace("/", "");
        BufferedInputStream bufferedInputStream = null;
        Bitmap bitmap = null;
        FileInputStream fileInputStream;
        try {
            fileInputStream = MyApplication.getContext().openFileInput(newUrl);
            bufferedInputStream = new BufferedInputStream(fileInputStream);
            bitmap = BitmapFactory.decodeStream(bufferedInputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 在本地文件删除图片
     * @param fileName
     * @return
     */
    public static boolean deleteBitmap(String fileName){
        String newUrl = fileName.replace("/", "");
        File file = new File(MyApplication.getContext().getFilesDir(), newUrl);
        if(file.exists()){
            file.delete();
            return true;
        }
        return false;
    }
}
