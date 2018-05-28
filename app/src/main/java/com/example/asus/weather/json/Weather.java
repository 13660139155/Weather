package com.example.asus.weather.json;

import java.util.ArrayList;

/**
 * Created by ASUS on 2018/5/18.
 */

public class Weather {

    /* 位置 */
    public Location location;
    /* 天气实况 */
    public Now now;
    /* 天气预报 */
    public ArrayList<Daily> dailyArrayList;
    /* 生活建议 */
    public Suggestion suggestion;
    /* last_update， 数据更新时间 */
    public String update;

}
