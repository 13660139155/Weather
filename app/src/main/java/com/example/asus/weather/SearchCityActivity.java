package com.example.asus.weather;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.weather.Temp.Temp;
import com.example.asus.weather.adapter.ListSearchAdapter;
import com.example.asus.weather.db.SQLDatabase;
import com.example.asus.weather.json.Location;
import com.example.asus.weather.unit.ActivityCollector;
import com.example.asus.weather.unit.HttpUnity;
import com.example.asus.weather.unit.JSONUnity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SearchCityActivity extends AppCompatActivity implements View.OnClickListener,
        TextWatcher,
        AdapterView.OnItemClickListener{

    private NetworkChangeReceiver networkChangeReceiver;
    private IntentFilter intentFilter;
    private boolean IS_NETWORK_AVAILABLE;
    private SQLDatabase sqlDatabase;

    EditText editTextEdit;
    ImageView imageViewSearch;
    ImageView imageViewDelete;
    ListSearchAdapter listSearchAdapter;
    ListView listView;
    TextView textViewhasCity;
    ArrayList<Location> locationArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        networkChangeReceiver = new NetworkChangeReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkChangeReceiver, intentFilter);
        setContentView(R.layout.activity_search_city);
        ActivityCollector.addActivity(this);
        editTextEdit = (EditText)findViewById(R.id.edit_edit);
        imageViewDelete = (ImageView)findViewById(R.id.image_delete);
        imageViewSearch = (ImageView)findViewById(R.id.image_search);
        textViewhasCity = (TextView)findViewById(R.id.text_has_city);
        Toolbar toolbar = (Toolbar)findViewById(R.id.search_city_toolbar);
        listView = (ListView)findViewById(R.id.list_search);

        sqlDatabase = new SQLDatabase(SearchCityActivity.this, "Weather.db", null, 1);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        imageViewSearch.setOnClickListener(this);
        imageViewDelete.setOnClickListener(this);
        editTextEdit.addTextChangedListener(this);
        listView.setOnItemClickListener(this);
        textViewhasCity.setVisibility(View.VISIBLE);


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
                if(arrayList == null || arrayList.size() == 0){
                    Temp.IS_STARTACTIVITY = 1;
                    ActivityCollector.finishAll();
                }else {
                    finish();
                }
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 启动活动
     * @param context
     */
    public static void activityStart(Context context){
        Intent intent = new Intent(context, SearchCityActivity.class);
        context.startActivity(intent);
    }

    /**
     * 控件点击
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_delete:
                if (editTextEdit.getText() != null){
                    editTextEdit.setText("");
                    imageViewDelete.setVisibility(View.INVISIBLE);
                    textViewhasCity.setText("界面空空如也");
                }
                break;
            case R.id.image_search:
                String s = editTextEdit.getText().toString();
                if(s.length() != 0){
                    if(IS_NETWORK_AVAILABLE){
                        textViewhasCity.setVisibility(View.VISIBLE);
                        textViewhasCity.setText("正在搜索");
                        String key = encode(s.toString());
                        String address = "https://api.seniverse.com/v3/location/search.json?key=v2bxdf4yegclkkns&q=" + key;
                        new MyLocationAsyncTask().execute(address);
                    }else {
                        textViewhasCity.setVisibility(View.VISIBLE);
                        textViewhasCity.setText("网络开小差");
                    }
                }else {
                    textViewhasCity.setVisibility(View.VISIBLE);
                    textViewhasCity.setText("界面空空如也");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        imageViewDelete.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(s.toString().trim().length() != 0){
            imageViewDelete.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if(s.toString().trim().length() != 0){
            if(IS_NETWORK_AVAILABLE){
                if(locationArrayList != null && locationArrayList.size() != 0){
                    listSearchAdapter.clearAll(true);
                    listSearchAdapter.notifyDataSetChanged();
                }
                textViewhasCity.setVisibility(View.VISIBLE);
                textViewhasCity.setText("正在搜索");
                String key = encode(s.toString());
                String address = "https://api.seniverse.com/v3/location/search.json?key=v2bxdf4yegclkkns&q=" + key;
                new MyLocationAsyncTask().execute(address);
            }else {
                textViewhasCity.setVisibility(View.VISIBLE);
                textViewhasCity.setText("网络开小差");
            }
        }else {
            textViewhasCity.setVisibility(View.VISIBLE);
            textViewhasCity.setText("界面空空如也");
            if(locationArrayList != null && locationArrayList.size() != 0){
                listSearchAdapter.clearAll(true);
                listSearchAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * ListView子项点击事件
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Location location = locationArrayList.get(position);
        ArrayList<String> arrayList = quryFromSQL("Address", "address");
        int k = 1;
        if(arrayList.size() != 0 && arrayList != null){
            for(String s : arrayList){
                if(s.compareTo(location.locationId) == 0){
                    k = 0;
                }
            }
        }
        if(k == 1){
            if(Temp.location.compareTo(location.locationId) == 0){
                if(arrayList.size() != 0 && arrayList != null){
                    for(String s : arrayList){
                        deleteFromSQL("Address", "address == ?", s);
                    }
                    insertInSQL("Address", "address", location.locationId);
                    for(String s : arrayList){
                        insertInSQL("Address", "address", s);
                    }
                }
            }else {
                insertInSQL("Address", "address", location.locationId);
            }
        }
        MainActivity.activityStart(SearchCityActivity.this, location.locationId);
        finish();
    }

    /**
     * 城市搜索的位置请求
     */
    private class
    MyLocationAsyncTask extends AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(String... params) {
            String address = params[0];
            String response = HttpUnity.sendHttpRequest(address);
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
           if (!TextUtils.isEmpty(s)){
               textViewhasCity.setVisibility(View.GONE);
               locationArrayList = JSONUnity.praseLocationResponse(s);
               if(locationArrayList.size() != 0){
                   listSearchAdapter = new ListSearchAdapter(SearchCityActivity.this, R.layout.item_list_search, locationArrayList);
                   listView.setAdapter(listSearchAdapter);
                   listSearchAdapter.notifyDataSetChanged();
               }else {
                   if(locationArrayList != null && locationArrayList.size() != 0){
                       listSearchAdapter.clearAll(true);
                       listSearchAdapter.notifyDataSetChanged();
                   }
                   textViewhasCity.setVisibility(View.VISIBLE);
                   textViewhasCity.setText("无匹配城市");
               }
           }
        }
    }

    /**
     * 从数据库中删除数据
     * @param bookName 表名
     * @param deleteData 要删除的数据
     */
    private void deleteFromSQL(String bookName, String where, String deleteData){
        SQLiteDatabase db = sqlDatabase.getWritableDatabase();
        db.delete(bookName, where, new String[]{deleteData});
    }

    /**
     * 向数据库中插入数据
     * @param bookName 表名
     * @param colName  列名
     * @param data 数据
     */
    private void insertInSQL(String bookName, String colName, String data){
        SQLiteDatabase db = sqlDatabase.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(colName, data);
        db.insert(bookName, null, contentValues);
        contentValues.clear();
    }

    /**
     *  从数据库中查询数据
     * @param bookName 表名
     * @param colName 列名
     * @return NewsID或ImageUrl的ArrayList
     */
    private ArrayList<String> quryFromSQL(String bookName, String colName){
        ArrayList<String> arrayList = new ArrayList<>();
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

    /**
     * 编码
     */
    private String encode(String string){
        String str = new String();
        try {
            str = URLEncoder.encode(string, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }


    /**
     * 接受系统网络广播
     * Created by asus on 2018/4/27.
     */
    class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if(networkInfo != null && networkInfo.isAvailable()){
                IS_NETWORK_AVAILABLE = true;
            }else{
                IS_NETWORK_AVAILABLE = false;
                Toast.makeText(context, "网络不可用", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ArrayList<String> arrayList = quryFromSQL("Address", "address");
        if(arrayList == null || arrayList.size() == 0){
            Temp.IS_STARTACTIVITY = 1;
            ActivityCollector.finishAll();
        }
    }

}
