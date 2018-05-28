package com.example.asus.weather.json;

/**
 * 未来天气类
 * Created by ASUS on 2018/5/18.
 */

public class Daily {
    /* date, 日期 */
    public String dailyDate;
    /* text_day, 白天天气现象文字 */
    public String dailyTextDay;
    /* code_day, 白天天气现象代码 */
    public String dailyCodeDay;
    /* text_night, 晚间天气现象文字 */
    public String dailyTextNight;
    /* code_night, 晚间天气现象代码 */
    public String dailyCodeNight;
    /* high, 当天最高温度 */
    public String dailyHigh;
    /* low, 当天最低温度 */
    public String dailyLow;
    /* wind_direction, 风向文字 */
    public String dailyWindDirection;
    /* precip, 降水概率，范围0~100，单位百分比 */
    public String dailyPrecip;
    /* wind_speed, 风速，单位km/h（当unit=c时）、mph（当unit=f时） */
    public String dailyWindSpeed;
    /* wind_scale,风力等级 */
    public String dailyWindScale;
    /* wind_direction_degree, 风向角度，范围0~360 */
    public String getDailyWindDirectionDegree;

}
