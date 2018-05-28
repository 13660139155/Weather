package com.example.asus.weather.unit;

import android.app.Activity;

import java.util.ArrayList;

/**
 * 活动管理器
 * Created by ASUS on 2018/5/24.
 */

public class ActivityCollector {

    public static ArrayList<Activity> activityArrayList = new ArrayList<>();

    public static void addActivity(Activity activity){
        activityArrayList.add(activity);
    }

    public static void removeActivity(Activity activity){
        activityArrayList.remove(activity);
    }


    public static  void finishAll(){
        for(Activity activity : activityArrayList){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
        activityArrayList.clear();
    }
}
