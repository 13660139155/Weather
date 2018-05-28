package com.example.asus.weather;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.example.asus.weather.Temp.Temp;
import com.example.asus.weather.adapter.CityManageAdapter;
import com.example.asus.weather.db.SQLDatabase;
import com.example.asus.weather.file.SPFDatabase;
import com.example.asus.weather.json.Daily;
import com.example.asus.weather.json.Location;
import com.example.asus.weather.json.Now;
import com.example.asus.weather.json.Suggestion;
import com.example.asus.weather.json.Weather;
import com.example.asus.weather.unit.ActivityCollector;
import com.example.asus.weather.unit.HttpUnity;
import com.example.asus.weather.unit.JSONUnity;
import java.util.ArrayList;

public class CityManageActivity extends AppCompatActivity implements View.OnClickListener{

    FloatingActionButton floatingActionButton;
    CityManageAdapter cityManageAdapter;
    RecyclerView recyclerView;
    ArrayList<Weather> weatherArrayList;
    LinearLayoutManager linearLayoutManager;
    MenuItem menuItem;
    ArrayList<String> dbAddressList;
    Weather weather;
    private String address;
    private boolean IS_EDIT = false;
    private SQLDatabase sqlDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_city);
        ActivityCollector.addActivity(this);
        floatingActionButton = (FloatingActionButton)findViewById(R.id.float_button);
        Toolbar toolbar = (Toolbar)findViewById(R.id.city_manage_toolbar);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        weatherArrayList = new ArrayList<>();
        sqlDatabase = new SQLDatabase(this, "Weather.db", null, 1);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        floatingActionButton.setOnClickListener(this);

        dbAddressList = quryFromSQL("Address", "address");
        if(dbAddressList.size() != 0){
            for(final String s : dbAddressList){
                address = s;
                weather = new Weather();
                Now now = SPFDatabase.extractNowData(s + "now");
                ArrayList<Daily> dailyArrayList = SPFDatabase.extractDailyData(s + "daily");
                Suggestion suggestion = SPFDatabase.extractSuggestionData(s + "suggestion");
                ArrayList<Location> locationArrayList = SPFDatabase.extractLocationData(s + "location");
                if(now != null && locationArrayList.size() != 0 && locationArrayList != null && dailyArrayList.size() != 0 && dailyArrayList != null){
                    weather.now = now;
                    weather.location = locationArrayList.get(0);
                    weather.suggestion = suggestion;
                    weather.dailyArrayList = dailyArrayList;
                    weatherArrayList.add(weather);
                }else if(MainActivity.IS_NETWORK_AVAILABLE){
                    requestNowWeather(s);
                    requestLocationWeather(s);
                    requestDailyWeather(s, "0");
                }
            }
        }
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        cityManageAdapter = new CityManageAdapter(weatherArrayList);
        recyclerView.setAdapter(cityManageAdapter);
    }



    /**
     * 控件点击事件监听
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.float_button:
                SearchCityActivity.activityStart(CityManageActivity.this);
                break;
            default:
                break;
        }
    }

    /**
     * 创建菜单
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_city_manage, menu);
        menuItem = menu.findItem(R.id.item_edit);
        if (dbAddressList.size() == 0){
            menuItem.setVisible(false);
        }else {
            menuItem.setVisible(true);
        }
        return true;
    }

    /**
     * 菜单选项点击事件
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                ArrayList<String> arrayList = quryFromSQL("Address", "address");
                if(arrayList.size() == 0){
                    SearchCityActivity.activityStart(CityManageActivity.this);
                    finish();
                }else {
                    Intent intent = new Intent();
                    if(Temp.IS_DELETE == 1){
                        Temp.IS_DELETE = 0;
                        intent.putExtra("data_return", "reset");
                    }else {
                        intent.putExtra("data_return", "no");
                    }
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
            case R.id.item_edit:
                if(!IS_EDIT){
                    IS_EDIT = true;
                    cityManageAdapter.tag(true);
                    menuItem.setIcon(R.drawable.back);
                }else {
                    IS_EDIT = false;
                    cityManageAdapter.tag(false);
                    menuItem.setIcon(R.drawable.edit);
                }
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //TODO something
        ArrayList<String> arrayList = quryFromSQL("Address", "address");
        if(arrayList.size() == 0){
            SearchCityActivity.activityStart(CityManageActivity.this);
            finish();
        }else {
            Intent intent = new Intent();
            if(Temp.IS_DELETE == 1){
                Temp.IS_DELETE = 0;
                intent.putExtra("data_return", "reset");
            }else {
                intent.putExtra("data_return", "no");
            }
            setResult(RESULT_OK, intent);
        }
        super.onBackPressed();
    }

    /**
     * 启动活动
     * @param context
     */
    public static void activityStart(Context context){
        Intent intent = new Intent(context, CityManageActivity.class);
        context.startActivity(intent);
    }

    /**
     * 从数据库中查询数据
     *
     * @param bookName 表名
     * @param colName  列名
     * @return NewsID或ImageUrl的ArrayList
     */
    private ArrayList<String> quryFromSQL(String bookName, String colName) {
        ArrayList<String> arrayList = new ArrayList<>();
        SQLiteDatabase db = sqlDatabase.getReadableDatabase();
        Cursor cursor = db.query(bookName, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String record = cursor.getString(cursor.getColumnIndex(colName));
                arrayList.add(record);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return arrayList;
    }

    /**
     * 请求天气预报
     * @param address
     */
    private void requestDailyWeather(String address, String start){
        String url = "https://api.seniverse.com/v3/weather/daily.json?key=v2bxdf4yegclkkns&location=" + address + "&language=zh-Hans&unit=c&start=" + start + "&days=5";
        new DailyAsyncTask().execute(url);
    }


    /**
     * 请求天气实况
     * @param address
     */
    private void requestNowWeather(String address){
        String url = "https://api.seniverse.com/v3/weather/now.json?key=v2bxdf4yegclkkns&location=" + address + "&language=zh-Hans&unit=c";
        new NowAsyncTask().execute(url);
    }

    /**
     * 请求天气地址
     * @param address
     */
    private void requestLocationWeather(String address){
        String url = "https://api.seniverse.com/v3/location/search.json?key=v2bxdf4yegclkkns&q=" + address;
        new LocationAsyncTask().execute(url);
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
            if(!TextUtils.isEmpty(s)){
                SPFDatabase.preferenceData(address + "now", s);
                Now now = JSONUnity.praseNowResponse(s);
                weather.now = now;
            }
        }
    }

    /**
     * 异步请求天气预报
     */
    private class DailyAsyncTask extends AsyncTask<String, Integer, String>{

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
                ArrayList<Daily> dailyArrayList = JSONUnity.praseDailyResponse(s);
                weather.dailyArrayList = dailyArrayList;
            }
        }

    }

    /**
     * 城市搜索的位置请求
     */
    private class LocationAsyncTask extends AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(String... params) {
            String address = params[0];
            String response = HttpUnity.sendHttpRequest(address);
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            if(!TextUtils.isEmpty(s)){
                SPFDatabase.preferenceData(address + "location", s);
                ArrayList<Location> locationArrayList = JSONUnity.praseLocationResponse(s);
               weather.location = locationArrayList.get(0);
            }
        }
    }
}
