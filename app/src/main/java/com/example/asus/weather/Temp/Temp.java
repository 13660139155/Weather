package com.example.asus.weather.Temp;

import com.example.asus.weather.MainActivity;
import com.example.asus.weather.fragment.WeatherFragment;
import com.example.asus.weather.json.Now;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeMap;

/**
 * Created by ASUS on 2018/5/21.
 */

public class Temp {
    /** 天气Id与值的图 */
    public static TreeMap<String, Now> treeMapWeatherAddress = new TreeMap<>();
    public static TreeMap<String, WeatherFragment> weatherFragmentTreeMap = new TreeMap<>();
    /** 是否第一次进入主活动, 默认是 */
    public static int IS_STARTACTIVITY = 1;
    /**  是否定位，默认否*/
    public static int IS_LOCATION = 0;
    /** 有没有删除 */
    public static int IS_DELETE = 0;
    /** 位置 */
    public static String location;
    /** 被删除的天气集合 */
    public static ArrayList<String> deleteArrayList = new ArrayList<>();
}
