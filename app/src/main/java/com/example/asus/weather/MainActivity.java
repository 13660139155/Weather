package com.example.asus.weather;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.asus.weather.Temp.Temp;
import com.example.asus.weather.adapter.CityManageAdapter;
import com.example.asus.weather.adapter.FragAdapter;
import com.example.asus.weather.db.SQLDatabase;
import com.example.asus.weather.file.SPFDatabase;
import com.example.asus.weather.fragment.WeatherFragment;
import com.example.asus.weather.json.Now;
import com.example.asus.weather.json.Weather;
import com.example.asus.weather.services.WeatherUpdataService;
import com.example.asus.weather.unit.ActivityCollector;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    ArrayList<Fragment> fragmentArrayList;//天气界面集合
    ViewPager viewPager;//页面切换sp
    FragAdapter fragAdapter;
    ImageView imageViewAdd;
    ScrollView scrollView;

    private String UPDATAALL = "com.example.asus.weather.UPDATAALL";
    private LocationClient locationClient;//定位
    public static boolean IS_NETWORK_AVAILABLE = true;
    private NetworkChangeReceiver networkChangeReceiver;
    private IntentFilter intentFilter;
    private SQLDatabase sqlDatabase;
    private int pagePosition;
    private ArrayList<String> pageId = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        sqlDatabase = new SQLDatabase(MainActivity.this, "Weather.db", null, 1);
        networkChangeReceiver = new NetworkChangeReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkChangeReceiver, intentFilter);
        locationClient = new LocationClient(getApplicationContext());
        locationClient.registerLocationListener(new MyLocationListener());

        if(Build.VERSION.SDK_INT >= 21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }else {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            ViewGroup decorViewGroup = (ViewGroup) window.getDecorView();
            View statusBarView = new View(window.getContext());
            int statusBarHeight = getStatusBarHeight(window.getContext());
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, statusBarHeight);
            params.gravity = Gravity.TOP;
            statusBarView.setLayoutParams(params);
            statusBarView.setBackgroundColor(Color.TRANSPARENT);
            decorViewGroup.addView(statusBarView);
        }
        setContentView(R.layout.activity_main);

        imageViewAdd = (ImageView) findViewById(R.id.image_add);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        scrollView = (ScrollView)findViewById(R.id.scroll_view);
        fragmentArrayList = new ArrayList<>();
        setSupportActionBar(toolbar);
        imageViewAdd.setOnClickListener(this);

        openLocation();

        Intent intent = getIntent();
        String address = intent.getStringExtra("address");
        if(pageId != null || pageId.size() != 0){
            pageId.clear();
        }
        if (!TextUtils.isEmpty(address)) {//从其他活动跳转来
            ArrayList<String> arrayList = quryFromSQL("Address", "address");
            if (address.compareTo("WS0E9D8WN298") != 0) {
                fragmentArrayList.add(WeatherFragment.newFragment(address));
                pageId.add(address);
                if (arrayList.size() != 0) {
                    for (String s : arrayList) {
                        if (s.compareTo(address) == 0) {
                            continue;
                        }
                        fragmentArrayList.add(WeatherFragment.newFragment(s));
                        pageId.add(s);
                    }
                }
            }else {
                fragmentArrayList.add(WeatherFragment.newFragment(address));
                pageId.add(address);
                if (arrayList.size() != 0) {
                    for (String s : arrayList) {
                        if (s.compareTo(address) == 0 || s.compareTo(returnData("广州")) == 0) {
                            continue;
                        }
                        fragmentArrayList.add(WeatherFragment.newFragment(s));
                        pageId.add(s);
                    }
                }
            }
        }else {//第一次进入程序
            ArrayList<String> arrayList = quryFromSQL("Address", "address");
            if(arrayList.size() != 0){
                for(String s : arrayList){
                    if(s.compareTo(returnData("广州")) == 0){
                        continue;
                    }
                    fragmentArrayList.add(WeatherFragment.newFragment(s));
                    pageId.add(s);
                }
            }
        }
        if(fragmentArrayList.size() != 0){
            fragAdapter = new FragAdapter(getSupportFragmentManager(), fragmentArrayList);
            viewPager.setAdapter(fragAdapter);
            fragAdapter.notifyDataSetChanged();
        }
        viewPager.setOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        pagePosition = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    /**
     * 定位监听器
     */
    public class MyLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation == null) {
                return;
            }

            String data = bdLocation.getCity();//中文城市
            if(data == null){
                data = "广州";
            }

            if(Temp.IS_STARTACTIVITY == 1){//第一次进入主活动
                Temp.IS_STARTACTIVITY = 0;
                ArrayList<String> arrayList = quryFromSQL("Address", "address");
                insert(arrayList, data);
                String newData = returnData(data);
                Temp.location = newData;
                SPFDatabase.preferenceData(newData, newData);
                if((pageId != null || pageId.size() != 0) && pageId.size() != 1){
                    ArrayList<String> tempList = pageId;
                    pageId.clear();
                    pageId.add(newData);
                    pageId.addAll(tempList);
                }else {
                    pageId.add(newData);
                }
                if(arrayList.size() == 0 || arrayList == null){// 程序第一次安装进入程序，数据库没有数据
                    if (fragmentArrayList.size() != 0){
                        fragmentArrayList.clear();
                    }
                    fragmentArrayList.add(WeatherFragment.newFragment(newData));
                    fragAdapter = new FragAdapter(getSupportFragmentManager(), fragmentArrayList);
                    viewPager.setAdapter(fragAdapter);
                    fragAdapter.notifyDataSetChanged();
                }else if (arrayList.size() > 1){// 数据库中已经有数据
                    fragAdapter.addItem(newData);
                    fragAdapter.notifyDataSetChanged();
                }else if(arrayList.size() == 1){//
                    if(arrayList.get(0).compareTo(newData) == 0){
                        if (fragmentArrayList.size() != 0){
                            fragmentArrayList.clear();
                        }
                        fragmentArrayList.add(WeatherFragment.newFragment(newData));
                        fragAdapter = new FragAdapter(getSupportFragmentManager(), fragmentArrayList);
                        viewPager.setAdapter(fragAdapter);
                        fragAdapter.notifyDataSetChanged();
                    }else {
                        fragAdapter.addItem(newData);
                        fragAdapter.notifyDataSetChanged();
                    }
                }

                Intent intent1 = new Intent();
                intent1.setAction(UPDATAALL);
                sendBroadcast(intent1);
            }

            if(Temp.IS_LOCATION == 1){//定位
                Temp.IS_LOCATION = 0;
                ArrayList<String> arrayList = quryFromSQL("Address", "address");
                insert(arrayList, data);
                String newData = returnData(data);
                if(arrayList.size() != 0){
                    if(fragmentArrayList.size() != 0){
                        fragmentArrayList.clear();
                    }
                    if((pageId != null || pageId.size() != 0) && pageId.size() != 1){
                        ArrayList<String> tempList = pageId;
                        pageId.clear();
                        pageId.add(newData);
                        pageId.addAll(tempList);
                    }else {
                        pageId.add(newData);
                    }
                    fragmentArrayList.add(WeatherFragment.newFragment(newData));
                    for(String s : arrayList){
                        if(s.compareTo(newData) == 0){
                            continue;
                        }
                        fragmentArrayList.add(WeatherFragment.newFragment(s));
                        pageId.add(s);
                    }
                    fragAdapter = new FragAdapter(getSupportFragmentManager(), fragmentArrayList);
                    viewPager.setAdapter(fragAdapter);
                    Toast.makeText(MainActivity.this, "定位到" + bdLocation.getDistrict() + bdLocation.getCity() + bdLocation.getCountry(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    /**
     * 各种控件点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_add:
                Intent intent = new Intent(MainActivity.this, CityManageActivity.class);
                startActivityForResult(intent, 1);
                break;
            default:
                break;
        }
    }

    /**
     * 定位设置
     */
    private void initLocationClient() {
        LocationClientOption option = new LocationClientOption();
        /* 每1小时更新一次定位 */
        option.setScanSpan(3600000);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setIsNeedAddress(true);
        option.setOpenGps(true);

        locationClient.setLocOption(option);
    }

    /**
     * 开启定位
     */
    private void openLocation() {
        initLocationClient();
        locationClient.start();
    }

    /**
     * 创建底部弹出菜单
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    /**
     * 弹出菜单选项点击事件
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_shared:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "我是标题");
                Now now = Temp.treeMapWeatherAddress.get(pageId.get(pagePosition));
                intent.putExtra(Intent.EXTRA_TEXT, now.nowText + " / "  + now.nowTemperature + "℃" + "\n" + now.update);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent.createChooser(intent, "分享到"));
                break;
            case R.id.item_location:
                Temp.IS_LOCATION = 1;
                locationClient.requestLocation();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationClient.stop();
        unregisterReceiver(networkChangeReceiver);
        Intent intentService = new Intent(this, WeatherUpdataService.class);
        startService(intentService);
    }

    /**
     * 启动活动
     *
     * @param context
     */
    public static void activityStart(Context context, String address) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("address", address);
        context.startActivity(intent);
    }

    /**
     * 接受系统网络广播
     * Created by asus on 2018/4/27.
     */
    class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
                IS_NETWORK_AVAILABLE = true;
            } else {
                IS_NETWORK_AVAILABLE = false;
                Toast.makeText(context, "网络不可用", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 向数据库中插入数据
     *
     * @param bookName 表名
     * @param colName  列名
     * @param data     数据
     */
    private void insertInSQL(String bookName, String colName, String data) {
        SQLiteDatabase db = sqlDatabase.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(colName, data);
        db.insert(bookName, null, contentValues);
        contentValues.clear();
    }

    /**
     * 从数据库中查询数据
     * @param bookName 表名
     * @param colName  列名
     * @return NewsID或ImageUrl的ArrayList
     */
    private ArrayList<String> quryFromSQL(String bookName, String colName) {
        ArrayList arrayList = new ArrayList();
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
     * 处理从上一个活动返回时的逻辑
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1:
                if(RESULT_OK == resultCode) {
                    String result = data.getStringExtra("data_return");
                    if (result.compareTo("reset") == 0) {
                        if (pageId != null || pageId.size() != 0) {
                            pageId.clear();
                        }
                        ArrayList<String> arrayList = quryFromSQL("Address", "address");
                        if (arrayList.size() != 0) {
                            if (fragmentArrayList.size() != 0) {
                                fragmentArrayList.clear();
                            }
                            for (String s : arrayList) {
                                fragmentArrayList.add(WeatherFragment.newFragment(s));
                                pageId.add(s);
                            }
                            fragAdapter = new FragAdapter(getSupportFragmentManager(), fragmentArrayList);
                            viewPager.setAdapter(fragAdapter);
                        }
                    }
                }
            break;
        }
    }

    /**
     * 插入解析过的数据到数据库
     * @param arrayList
     * @param oldAddress
     */
    private void insert(ArrayList<String> arrayList, String oldAddress){
        int k = 1;
        String data = returnData(oldAddress);
        if(arrayList.size() != 0 && arrayList != null){
            for(String s : arrayList){
                if(s.compareTo(data) == 0){
                    k = 0;
                }
            }
        }
        if(k == 1){
            insertInSQL("Address", "address", data);
        }
    }

    /**
     * 返回解析过的城市数据
     * @param oldAddress
     * @return
     */
    private String returnData(String oldAddress){
        String newAddress = oldAddress;
        if(oldAddress.contains("市")){
            newAddress = oldAddress.replace("市", "");
        }if(oldAddress.contains("区")){
            newAddress = oldAddress.replace("区", "");
        }if(oldAddress.contains("县")){
            newAddress = oldAddress.replace("县", "");
        }
        String data = encode(newAddress);
        return data;
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Temp.IS_STARTACTIVITY = 1;
        ActivityCollector.finishAll();
    }

    /**
     * 获取系统状态栏高度
     * @param context
     * @return
     */
    private static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = res.getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }
}
