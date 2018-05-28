package com.example.asus.weather.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.weather.MainActivity;
import com.example.asus.weather.MyApplication;
import com.example.asus.weather.R;
import com.example.asus.weather.Temp.Temp;
import com.example.asus.weather.db.SQLDatabase;
import com.example.asus.weather.file.SPFDatabase;
import com.example.asus.weather.json.Daily;
import com.example.asus.weather.json.Location;
import com.example.asus.weather.json.Now;
import com.example.asus.weather.json.Suggestion;
import com.example.asus.weather.json.Weather;

import java.util.ArrayList;
import java.util.Vector;

import static android.content.ContentValues.TAG;


/**
 * Implementation of App Widget functionality.
 */
public class WeatherAppWidget extends AppWidgetProvider {
    
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        
        for (int appWidgetId : appWidgetIds) {

            RemoteViews views = upData(context);
            
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }

    /**
     *  从数据库中查询数据
     * @param bookName 表名
     * @param colName 列名
     * @return NewsID或ImageUrl的ArrayList
     */
    private static ArrayList<String> quryFromSQL(String bookName, String colName){
        ArrayList<String> arrayList = new ArrayList<>();
        SQLDatabase sqlDatabase = new SQLDatabase(MyApplication.getContext(), "Weather.db", null, 1);
        SQLiteDatabase db = sqlDatabase.getReadableDatabase();
        Cursor cursor = db.query(bookName, null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            do{
                String record = cursor.getString(cursor.getColumnIndex(colName));
                arrayList.add(record);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return arrayList;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().compareTo("com.example.asus.weather.UPDATAALL") == 0){
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName componentName = new ComponentName(context,WeatherAppWidget.class);
            RemoteViews views = upData(context);
            appWidgetManager.updateAppWidget(componentName, views);
        }else if(intent.getAction().compareTo("com.example.asus.weather.DELETE") == 0) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName componentName = new ComponentName(context,WeatherAppWidget.class);
            RemoteViews views = deleteAll(context);
            appWidgetManager.updateAppWidget(componentName, views);
        }else {
            super.onReceive(context, intent);
        }
    }

    /**
     * 更新数据
     */
    private RemoteViews upData(Context context){

        ArrayList<String> dbAddressList = quryFromSQL("Address", "address");


        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_app_widget);

        views.setTextViewText(R.id.text_widget_today, "今天");
        views.setTextViewText(R.id.text_widget_tomorrow, "明天");
        views.setTextViewText(R.id.text_widget_next_day, "后天");

        String location = SPFDatabase.extractData(Temp.location);

        if(dbAddressList.size() != 0){
            for(final String s : dbAddressList){
                if(s.compareTo(location) == 0){
                    String address = location;
                    Now now = SPFDatabase.extractNowData(address + "now");
                    ArrayList<Daily> dailyArrayList = SPFDatabase.extractDailyData(address + "daily");
                    ArrayList<Location> locationArrayList = SPFDatabase.extractLocationData(address + "location");
                    if(now != null){
                        views.setTextViewText(R.id.text_widget_now_text, now.nowText);
                        views.setImageViewResource(R.id.image_widget_now_code, setNowTabImageByCode(now.nowCode));
                        views.setTextViewText(R.id.text_widget_now_temp, now.nowTemperature + "℃");
                        views.setImageViewResource(R.id.image_widget_daily_text1, setNowTabImageByCode(now.nowCode));
                    }if(locationArrayList.size() != 0 && locationArrayList != null){
                        views.setTextViewText(R.id.text_widget_location, locationArrayList.get(0).locationName);
                    }if(dailyArrayList.size() != 0 && dailyArrayList != null){
                        views.setTextViewText(R.id.text_widget_daily_temp1, dailyArrayList.get(0).dailyLow + "℃/" + dailyArrayList.get(0).dailyHigh + "℃");
                        views.setTextViewText(R.id.text_widget_now_wind, dailyArrayList.get(0).dailyWindDirection + "风");
                        views.setImageViewResource(R.id.image_widget_daily_text2, setNowTabImageByCode(dailyArrayList.get(1).dailyCodeDay));
                        views.setTextViewText(R.id.text_widget_daily_temp2, dailyArrayList.get(1).dailyLow + "℃/" + dailyArrayList.get(1).dailyHigh + "℃");
                        views.setImageViewResource(R.id.image_widget_daily_text3, setNowTabImageByCode(dailyArrayList.get(2).dailyCodeDay));
                        views.setTextViewText(R.id.text_widget_daily_temp3, dailyArrayList.get(2).dailyLow + "℃/" + dailyArrayList.get(2).dailyHigh + "℃");
                    }
                }
            }
        }
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 ,intent, 0);
        views.setOnClickPendingIntent(R.id.widget_linear_layout, pendingIntent);
        
        return views;
    }

    /**
     * 删除所有数据
     */
    private RemoteViews deleteAll(Context context){

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_app_widget);

        views.setTextViewText(R.id.text_widget_now_text, "");
        views.setImageViewResource(R.id.image_widget_now_code, 0);
        views.setTextViewText(R.id.text_widget_now_temp, "");
        views.setImageViewResource(R.id.image_widget_daily_text1, 0);
        views.setTextViewText(R.id.text_widget_location, "");
        views.setTextViewText(R.id.text_widget_daily_temp1, "");
        views.setTextViewText(R.id.text_widget_now_wind, "");
        views.setImageViewResource(R.id.image_widget_daily_text2, 0);
        views.setTextViewText(R.id.text_widget_daily_temp2, "");
        views.setImageViewResource(R.id.image_widget_daily_text3, 0);
        views.setTextViewText(R.id.text_widget_daily_temp3,"");

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 ,intent, 0);
        views.setOnClickPendingIntent(R.id.widget_linear_layout, pendingIntent);

        return views;
    }


    /**
     * 设置天气现象的图片
     * @param code 天气代码
     */
    private static int setNowTabImageByCode(String code){
        int id = R.drawable.nine;
        if(code != null){
            switch (code){
                case "0":
                    id = R.drawable.zero;
                    break;
                case "1":
                    id = R.drawable.one;
                    break;
                case "2":
                    id = R.drawable.two;
                    break;
                case "3":
                    id = R.drawable.three;
                    break;
                case "4":
                    id = R.drawable.four;
                    break;
                case "5":
                    id = R.drawable.five;
                    break;
                case "6":
                    id = R.drawable.six;
                    break;
                case "7":
                    id = R.drawable.seven;
                    break;
                case "8":
                    id = R.drawable.eight;
                    break;
                case "9":
                    id = R.drawable.nine;
                    break;
                case "10":
                    id = R.drawable.ten;
                    break;
                case "11":
                    id = R.drawable.eleven;
                    break;
                case "12":
                    id = R.drawable.twelve;
                    break;
                case "13":
                    id = R.drawable.thirteen;
                    break;
                case "14":
                    id = R.drawable.fourteen;
                    break;
                case "15":
                    id = R.drawable.fifteen;
                    break;
                case "16":
                    id = R.drawable.sixteen;
                    break;
                case "17":
                    id = R.drawable.seventeen;
                    break;
                case "18":
                    id = R.drawable.eighteen;
                    break;
                case "19":
                    id = R.drawable.nineteen;
                    break;
                case "20":
                    id = R.drawable.twenty;
                    break;
                case "21":
                    id = R.drawable.twenty_one;
                    break;
                case "22":
                    id = R.drawable.twenty_two;
                    break;
                case "23":
                    id = R.drawable.twenty_three;
                    break;
                case "24":
                    id = R.drawable.twenty_four;
                    break;
                case "25":
                    id = R.drawable.twenty_five;
                    break;
                case "26":
                    id = R.drawable.twenty_six;
                    break;
                case "27":
                    id = R.drawable.twenty_seven;
                    break;
                case "28":
                    id = R.drawable.twent_eight;
                    break;
                case "29":
                    id = R.drawable.twenty_nine;
                    break;
                case "30":
                    id = R.drawable.thirty;
                    break;
                case "31":
                    id = R.drawable.thirty_one;
                    break;
                case "32":
                    id = R.drawable.thirty_two;
                    break;
                case "33":
                    id = R.drawable.thirty_three;
                    break;
                case "34":
                    id = R.drawable.thirty_four;
                    break;
                case "35":
                    id = R.drawable.thirty_five;
                    break;
                case "36":
                    id = R.drawable.thirty_six;
                    break;
                case "37":
                    id = R.drawable.thirty_seven;
                    break;
                case "99":
                    id = R.drawable.ninety_nine;
                    break;
                default:
                    break;
            }
        }
        return id;
    }
}

