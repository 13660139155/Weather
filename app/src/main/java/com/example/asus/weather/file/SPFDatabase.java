package com.example.asus.weather.file;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.asus.weather.MyApplication;
import com.example.asus.weather.json.Daily;
import com.example.asus.weather.json.Location;
import com.example.asus.weather.json.Now;
import com.example.asus.weather.json.Suggestion;
import com.example.asus.weather.unit.JSONUnity;

import java.util.ArrayList;

/**
 * Created by ASUS on 2018/5/18.
 */

public class SPFDatabase {

    /**
     * 储存天气数据
     * @param fileName 文件名
     * @param data 要储存的数据
     */
    public static void preferenceData(String fileName, String data){
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences(fileName, Context.MODE_PRIVATE).edit();
        editor.putString(fileName, data);
        editor.apply();
    }

    /**
     * 取出天气数据
     * @param fileName 文件名
     * @return 要取出的数据
     */
    public static Now extractNowData(String fileName){
        SharedPreferences preferences = MyApplication.getContext().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return JSONUnity.praseNowResponse(preferences.getString(fileName, ""));
    }

    /**
     * 取出天气数据
     * @param fileName 文件名
     * @return 要取出的数据
     */
    public static ArrayList<Daily> extractDailyData(String fileName){
        SharedPreferences preferences = MyApplication.getContext().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return JSONUnity.praseDailyResponse(preferences.getString(fileName, ""));
    }

    /**
     * 取出天气数据
     * @param fileName 文件名
     * @return 要取出的数据
     */
    public static Suggestion extractSuggestionData(String fileName){
        SharedPreferences preferences = MyApplication.getContext().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return JSONUnity.praseSuggestionResponse(preferences.getString(fileName, ""));
    }


    /**
     * 取出天气数据
     * @param fileName 文件名
     * @return 要取出的数据
     */
    public static ArrayList<Location> extractLocationData(String fileName){
        SharedPreferences preferences = MyApplication.getContext().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return JSONUnity.praseLocationResponse(preferences.getString(fileName, ""));
    }

    public static String extractData(String fileName){
        SharedPreferences sharedPreferences = MyApplication.getContext().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return sharedPreferences.getString(fileName, "");
    }
}
