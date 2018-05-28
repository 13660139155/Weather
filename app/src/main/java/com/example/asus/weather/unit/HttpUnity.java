package com.example.asus.weather.unit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.asus.weather.Interface.HttpCallBackListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * 网络工具包
 * Created by ASUS on 2018/5/18.
 */

public class HttpUnity {

    /**
     *  发起网络请求
     * @param address 发起网络请求
     * @return 返回网络请求的数据
     */
    public static String sendHttpRequest(final String address){
        String data = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(address);
            httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(6000);
            httpURLConnection.setReadTimeout(6000);
            httpURLConnection.setDoInput(true);
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            StringBuilder response = new StringBuilder();
            String line;
            while((line = bufferedReader.readLine()) != null){
                response.append(line);
            }
            data = response.toString();
        } catch (MalformedURLException e) {

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(httpURLConnection != null){
                httpURLConnection.disconnect();
            }
        }
        return data;
    }

    /**
     * 向网络请求一张图片
     * @param imageUrl 图片的url
     * @return Bitmap
     */
    public static Bitmap getOneImageBitmap(String imageUrl){
        URL imgUrl = null;
        Bitmap bitmap = null;
        try {
            imgUrl = new URL(imageUrl);
            HttpURLConnection conn = (HttpURLConnection) imgUrl.openConnection();
            conn.setDoInput(true);
            conn.setRequestMethod("GET");
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
