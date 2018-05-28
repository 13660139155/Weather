package com.example.asus.weather;

import android.app.Application;
import android.content.Context;

/**
 * Created by ASUS on 2018/5/18.
 */

public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        context = getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }

}
