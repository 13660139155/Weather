package com.example.asus.weather.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import com.example.asus.weather.db.SQLDatabase;
import com.example.asus.weather.file.SPFDatabase;
import com.example.asus.weather.unit.HttpUnity;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;

import static android.content.ContentValues.TAG;

/**
 * 后台更新天气
 */
public class WeatherUpdataService extends Service {

    private SQLDatabase sqlDatabase;
    private String address;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        sqlDatabase = new SQLDatabase(WeatherUpdataService.this, "Weather.db", null, 1);
        final HashSet<String> hashSet = quryFromSQL("Address", "address");

        for (String s : hashSet) {
            address = s;
            requestDailyWeather(s, "0");
            requestNowWeather(s);
            requestLocationWeather(s);
            requestSuggestionWeather(s);
        }
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int hour = 1000 * 60 * 60;//一小时
        long triggerAtTime = SystemClock.elapsedRealtime() + hour;
        Intent intent2 = new Intent(this, WeatherUpdataService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent2, 0);
        alarmManager.cancel(pendingIntent);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendingIntent);
        return super.onStartCommand(intent, flags, startId);

    }

    /**
     * 从数据库中查询数据
     *
     * @param bookName 表名
     * @param colName  列名
     * @return NewsID或ImageUrl的ArrayList
     */
    private HashSet<String> quryFromSQL(String bookName, String colName) {
        HashSet<String> hashSet = new HashSet<>();
        SQLiteDatabase db = sqlDatabase.getReadableDatabase();
        Cursor cursor = db.query(bookName, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String record = cursor.getString(cursor.getColumnIndex(colName));
                hashSet.add(record);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return hashSet;
    }


    /**
     * 异步请求天气预报
     */
    private class DailyAsyncTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String address = params[0];
            String response = HttpUnity.sendHttpRequest(address);
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            if (!TextUtils.isEmpty(s)) {
                SPFDatabase.preferenceData(address + "daily", s);
            }

        }
    }

    /**
     * 异步请求天气实况
     */
    private class NowAsyncTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String address = params[0];
            String response = HttpUnity.sendHttpRequest(address);
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            if (!TextUtils.isEmpty(s)) {
                SPFDatabase.preferenceData(address + "now", s);
            }
        }
    }

    /**
     * 异步请求天气生活建议
     */
    private class SuggestionAsyncTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String address = params[0];
            String response = HttpUnity.sendHttpRequest(address);
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            if (!TextUtils.isEmpty(s)) {
                SPFDatabase.preferenceData(address + "suggestion", s);
            }
        }
    }

    /**
     * 城市搜索的位置请求
     */
    private class LocationAsyncTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String address = params[0];
            String response = HttpUnity.sendHttpRequest(address);
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            if (!TextUtils.isEmpty(s)) {
                SPFDatabase.preferenceData(address + "location", s);
            }
        }
    }

    /**
     * 请求天气预报
     *
     * @param address
     */
    private void requestDailyWeather(String address, String start) {
        String url = "https://api.seniverse.com/v3/weather/daily.json?key=v2bxdf4yegclkkns&location=" + address + "&language=zh-Hans&unit=c&start=" + start + "&days=5";
        new DailyAsyncTask().execute(url);
    }

    /**
     * 请求天气实况
     *
     * @param address
     */
    private void requestNowWeather(String address) {
        String url = "https://api.seniverse.com/v3/weather/now.json?key=v2bxdf4yegclkkns&location=" + address + "&language=zh-Hans&unit=c";
        new NowAsyncTask().execute(url);
    }

    /**
     * 请求天气建议
     *
     * @param address
     */
    private void requestSuggestionWeather(String address) {
        String url = "https://api.seniverse.com/v3/life/suggestion.json?key=v2bxdf4yegclkkns&location=" + address + "&language=zh-Hans";
        new SuggestionAsyncTask().execute(url);
    }

    /**
     * 请求天气地址
     *
     * @param address
     */
    private void requestLocationWeather(String address) {
        String url = "https://api.seniverse.com/v3/location/search.json?key=v2bxdf4yegclkkns&q=" + address;
        new LocationAsyncTask().execute(url);
    }

    /**
     * 编码
     */
    private String encode(String string) {
        String str = new String();
        try {
            str = URLEncoder.encode(string, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

}
