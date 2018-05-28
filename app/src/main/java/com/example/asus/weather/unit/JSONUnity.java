package com.example.asus.weather.unit;

import com.example.asus.weather.json.Daily;
import com.example.asus.weather.json.Location;
import com.example.asus.weather.json.Now;
import com.example.asus.weather.json.Suggestion;
import com.example.asus.weather.json.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 解析json数据
 * Created by ASUS on 2018/5/18.
 */

public class JSONUnity {

    /**
     *  把JSON数据解析成Now
     * @param jsonResponse 网络请求返回的JSON数据
     * @return 返回Data
     */
    public static Now praseNowResponse(String jsonResponse) {
        JSONObject jsonObject = null;
        JSONObject jsonObject2 = null;
        JSONObject jsonObject3 = null;
        JSONArray jsonArray = null;
        Now now = null;
        try {
            now = new Now();
            jsonObject = new JSONObject(jsonResponse);
            jsonArray = jsonObject.getJSONArray("results");
            jsonObject2 = jsonArray.getJSONObject(0);
            jsonObject3 = jsonObject2.getJSONObject("now");
            now.nowCode = jsonObject3.getString("code");
            now.nowTemperature = jsonObject3.getString("temperature");
            now.nowText = jsonObject3.getString("text");
            now.update = jsonObject2.getString("last_update");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return now;
    }

    /**
     *  把JSON数据解析成Daily集合
     * @param jsonResponse 网络请求返回的JSON数据
     * @return 返回Daily集合
     */
    public static ArrayList<Daily> praseDailyResponse(String jsonResponse) {
        JSONObject jsonObject = null;
        JSONObject jsonObject2 = null;
        JSONObject jsonObject3 = null;
        ArrayList<Daily> dailyArrayList = null;
        JSONArray jsonArray = null;
        JSONArray jsonArray2 = null;
        try {
            dailyArrayList = new ArrayList<>();
            jsonObject = new JSONObject(jsonResponse);
            jsonArray = jsonObject.getJSONArray("results");
            jsonObject2 = jsonArray.getJSONObject(0);
            jsonArray2 = jsonObject2.getJSONArray("daily");
            for(int i = 0; i < jsonArray2.length(); i++){
                Daily daily = new Daily();
                JSONObject object = jsonArray2.getJSONObject(i);
                daily.dailyCodeDay = object.getString("code_day");
                daily.dailyCodeNight = object.getString("code_night");
                daily.dailyDate = object.getString("date");
                daily.dailyHigh = object.getString("high");
                daily.dailyLow = object.getString("low");
                daily.dailyTextDay = object.getString("text_day");
                daily.dailyTextNight = object.getString("text_night");
                daily.dailyWindDirection = object.getString("wind_direction");
                daily.dailyPrecip = object.getString("precip");
                daily.getDailyWindDirectionDegree = object.getString("wind_direction_degree");
                daily.dailyWindScale = object.getString("wind_scale");
                daily.dailyWindSpeed = object.getString("wind_speed");
                dailyArrayList.add(daily);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dailyArrayList;
    }

    /**
     *  把JSON数据解析成Suggestion
     * @param jsonResponse 网络请求返回的JSON数据
     * @return 返回Suggestion
     */
    public static Suggestion praseSuggestionResponse(String jsonResponse) {
        JSONObject jsonObject = null;
        JSONObject jsonObject2 = null;
        JSONObject jsonObject3 = null;
        JSONObject jsonObject4 = null;
        Suggestion suggestion = null;
        JSONArray jsonArray = null;
        JSONArray jsonArray2 = null;
        try {
            suggestion = new Suggestion();
            jsonObject = new JSONObject(jsonResponse);
            jsonArray = jsonObject.getJSONArray("results");
            jsonObject2 = jsonArray.getJSONObject(0);
            jsonObject3 = jsonObject2.getJSONObject("suggestion");

            jsonObject4 = jsonObject3.getJSONObject("dressing");
            Suggestion.SugDressing sugDressing = suggestion.new SugDressing();
            sugDressing.dressingBrief = jsonObject4.getString("brief");
            suggestion.sugDressing = sugDressing;

            jsonObject4 = jsonObject3.getJSONObject("uv");
            Suggestion.SugUv sugUv = suggestion.new SugUv();
            sugUv.uvBrief = jsonObject4.getString("brief");
            suggestion.sugUv = sugUv;

            jsonObject4 = jsonObject3.getJSONObject("car_washing");
            Suggestion.SugCarWashing sugCarWashing = suggestion.new SugCarWashing();
            sugCarWashing.washingBrief = jsonObject4.getString("brief");
            suggestion.sugCarWashing = sugCarWashing;

            jsonObject4 = jsonObject3.getJSONObject("travel");
            Suggestion.SugTravel sugTravel = suggestion.new SugTravel();
            sugTravel.travelBrief = jsonObject4.getString("brief");
            suggestion.sugTravel = sugTravel;

            jsonObject4 = jsonObject3.getJSONObject("flu");
            Suggestion.SugFishing sugFishing = suggestion.new SugFishing();
            sugFishing.fishingBrief = jsonObject4.getString("brief");
            suggestion.sugFishing = sugFishing;

            jsonObject4 = jsonObject3.getJSONObject("sport");
            Suggestion.SugSport sugSport = suggestion.new SugSport();
            sugSport.sportBrief = jsonObject4.getString("brief");
            suggestion.sugSport = sugSport;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return suggestion;
    }


    /**
     *  把JSON数据解析成Location集合
     * @param jsonResponse 网络请求返回的JSON数据
     * @return 返回Daily集合
     */
    public static ArrayList<Location> praseLocationResponse(String jsonResponse) {
        JSONObject jsonObject = null;
        ArrayList<Location> locationArrayList = null;
        JSONArray jsonArray = null;
        try {
            locationArrayList = new ArrayList<>();
            jsonObject = new JSONObject(jsonResponse);
            jsonArray = jsonObject.getJSONArray("results");
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject object = jsonArray.getJSONObject(i);
                Location location = new Location();
                location.locationCountry = object.getString("country");
                location.locationId = object.getString("id");
                location.locationName = object.getString("name");
                location.locationPath = object.getString("path");
                locationArrayList.add(location);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return locationArrayList;
    }

}
