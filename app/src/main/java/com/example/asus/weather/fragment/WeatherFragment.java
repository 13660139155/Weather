package com.example.asus.weather.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.weather.R;
import com.example.asus.weather.Temp.Temp;
import com.example.asus.weather.adapter.DailyAdapter;
import com.example.asus.weather.file.SPFDatabase;
import com.example.asus.weather.json.Daily;
import com.example.asus.weather.json.Location;
import com.example.asus.weather.json.Now;
import com.example.asus.weather.json.Suggestion;
import com.example.asus.weather.json.Weather;
import com.example.asus.weather.unit.HttpUnity;
import com.example.asus.weather.unit.JSONUnity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static com.example.asus.weather.MainActivity.IS_NETWORK_AVAILABLE;

/**
 * 天气主界面
 * Created by ASUS on 2018/5/18.
 */


public class WeatherFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private static final String KEY = "key";
    private  String UPDATAALL = "com.example.asus.weather.UPDATAALL";
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    private DailyAdapter dailyAdapter;
    private View view;

    ScrollView scrollView;

    Weather weather;
    ImageView imageViewBg;

    TextView textViewNowLocation;
    ImageView imageViewNowCode;
    TextView textViewNowTemp;
    TextView textViewNowText;
    TextView textViewNowPrecip;
    TextView textViewNowWind;
    TextView textViewNowWindDirection;
    TextView textViewNowWindScale;

    ImageView imageViewCode1;
    TextView textViewText1;
    TextView textViewTemp1;

    ImageView imageViewCode2;
    TextView textViewText2;
    TextView textViewTemp2;

    ImageView imageViewCode3;
    TextView textViewText3;
    TextView textViewTemp3;

    TextView textViewDressingB;
    TextView textViewUvB;
    TextView textViewCarB;
    TextView textViewSportB;
    TextView textViewTravelB;
    TextView textViewFishingB;

    SwipeRefreshLayout swipeRefreshLayout;
    String address;

    @SuppressLint("ResourceAsColor")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_weather, container, false);
        weather = new Weather();
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        textViewNowLocation = view.findViewById(R.id.text_view_location);
        imageViewNowCode = view.findViewById(R.id.image_view_now);
        textViewNowTemp = view.findViewById(R.id.text_view_temp);
        textViewNowText = view.findViewById(R.id.text_view_now);
        textViewNowPrecip = view.findViewById(R.id.text_view_now_precip);
        textViewNowWind = view.findViewById(R.id.text_view_now_wind);
        textViewNowWindDirection = view.findViewById(R.id.text_view_now_wind_direction);
        textViewNowWindScale = view.findViewById(R.id.text_view_now_wind_scale);
        imageViewBg = view.findViewById(R.id.image_bg);

        imageViewCode1 = view.findViewById(R.id.image_now_code);
        textViewText1 = view.findViewById(R.id.text_now_text);
        textViewTemp1 = view.findViewById(R.id.text_now_temp);
        imageViewCode2 = view.findViewById(R.id.image_daily_code);
        textViewText2 = view.findViewById(R.id.text_daily_text);
        textViewTemp2 = view.findViewById(R.id.text_daily_temp);
        imageViewCode3 = view.findViewById(R.id.image_daily_code2);
        textViewText3 = view.findViewById(R.id.text_daily_text2);
        textViewTemp3 = view.findViewById(R.id.text_daily_temp2);

        textViewDressingB = view.findViewById(R.id.text_sug_dressing_brief);
        textViewUvB = view.findViewById(R.id.text_sug_uv_brief);
        textViewCarB = view.findViewById(R.id.text_sug_car_brief);
        textViewDressingB = view.findViewById(R.id.text_sug_dressing_brief);
        textViewFishingB = view.findViewById(R.id.text_sug_fishing_brief);
        textViewSportB = view.findViewById(R.id.text_sug_sport_brief);
        textViewTravelB = view.findViewById(R.id.text_sug_travel_brief);

        recyclerView = view.findViewById(R.id.recycler_view_line);

        swipeRefreshLayout.setColorSchemeColors(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(this);

        Bundle bundle = getArguments();
        if(bundle != null){
            address = bundle.getString(KEY);
        }
        Now now = SPFDatabase.extractNowData(address + "now");
        Temp.treeMapWeatherAddress.put(address, now);
        ArrayList<Daily> dailyArrayList = SPFDatabase.extractDailyData(address + "daily");
        Suggestion suggestion = SPFDatabase.extractSuggestionData(address + "suggestion");
        ArrayList<Location> locationArrayList =  SPFDatabase.extractLocationData(address + "location");
        if((dailyArrayList.size() > 0) && suggestion != null && (locationArrayList.size() > 0)){

            setWhestherDataFromSPF(now, dailyArrayList, suggestion, locationArrayList);

        }else {
            if(IS_NETWORK_AVAILABLE){
                  /* 位置 */
                requestLocationWeather(address);
                /* 天气实况 */
                requestNowWeather(address);
                /* 未来天气 */
                requestDailyWeather(address, "0");
                /* 生活指数 */
                requestSuggestionWeather(address);
            }else {
                Toast.makeText(getActivity(), "请打开网络，获取天气失败", Toast.LENGTH_SHORT).show();
            }
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        scrollView =  view.findViewById(R.id.scroll_view);
        scrollView.smoothScrollTo(0, 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        scrollView.smoothScrollTo(0, 0);
    }

    public ScrollView getScrollView() {
        return scrollView;
    }

    /**
     * 下拉刷新
     */
    @Override
    public void onRefresh() {

        /* 位置 */
        requestLocationWeather(address);
        /* 天气实况 */
        requestNowWeather(address);
        /* 未来天气 */
        requestDailyWeather(address, "0");
        /* 生活指数 */
        requestSuggestionWeather(address);

        swipeRefreshLayout.setRefreshing(false);

        Intent intent = new Intent();
        intent.setAction(UPDATAALL);
        getActivity().sendBroadcast(intent);
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
                if(dailyArrayList.size() != 0){
                    if(dailyArrayList.get(0).dailyPrecip.equals("")){
                        textViewNowPrecip.setText("11%");
                    }else {
                        textViewNowPrecip.setText(dailyArrayList.get(0).dailyPrecip + "%");
                    }
                    textViewNowWind.setText(dailyArrayList.get(0).dailyWindSpeed + "km/h");
                    textViewNowWindDirection.setText(dailyArrayList.get(0).dailyWindDirection + "风");
                    textViewNowWindScale.setText(dailyArrayList.get(0).dailyWindScale + "级");

                    textViewTemp1.setText(dailyArrayList.get(0).dailyLow + "℃" + "/" + dailyArrayList.get(0).dailyHigh + "℃");
                    textViewText2.setText(dailyArrayList.get(1).dailyTextDay);
                    textViewTemp2.setText(dailyArrayList.get(1).dailyLow + "℃" + "/" + dailyArrayList.get(0).dailyHigh + "℃");
                    setNowTabImageByCode(dailyArrayList.get(1).dailyCodeDay, 2);
                    textViewText3.setText(dailyArrayList.get(2).dailyTextDay);
                    textViewTemp3.setText(dailyArrayList.get(2).dailyLow + "℃" + "/" + dailyArrayList.get(0).dailyHigh + "℃");
                    setNowTabImageByCode(dailyArrayList.get(2).dailyCodeDay, 3);
                    weather.dailyArrayList = dailyArrayList;

                    List<Integer> lowData = new ArrayList<>();
                    List<Integer> highData = new ArrayList<>();
                    int low;
                    int high;
                    for(int i = 0; i < dailyArrayList.size(); i++){
                        lowData.add(Integer.parseInt(dailyArrayList.get(i).dailyLow));
                        highData.add(Integer.parseInt(dailyArrayList.get(i).dailyHigh));
                    }
                    Collections.sort(lowData);
                    low = lowData.get(0);
                    Collections.sort(highData);
                    high = highData.get(2);
                    linearLayoutManager = new FullyLinearLayoutManager(getActivity());
                    linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setNestedScrollingEnabled(false);
                    dailyAdapter = new DailyAdapter(dailyArrayList, low, high);
                    recyclerView.setAdapter(dailyAdapter);
                }
            }
        }

    }
    /**
     * 异步请求天气实况
     */
    private class NowAsyncTask extends AsyncTask<String, Integer, String>{

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
                textViewNowTemp.setText(now.nowTemperature + "℃");
                textViewText1.setText(now.nowText);
                textViewNowText.setText(now.nowText);
                setNowTabImageByCode(now.nowCode, 0);
                setNowTabImageByCode(now.nowCode, 1);
                Temp.treeMapWeatherAddress.put(address, now);
                weather.now = now;
            }
        }
    }

    /**
     * 异步请求天气生活建议
     */
    private class SuggestionAsyncTask extends AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(String... params) {
            String address = params[0];
            String response = HttpUnity.sendHttpRequest(address);
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            if(!TextUtils.isEmpty(s)){
                SPFDatabase.preferenceData(address + "suggestion", s);
                Suggestion suggestion = JSONUnity.praseSuggestionResponse(s);
                textViewDressingB.setText("穿衣指数：" + suggestion.sugDressing.dressingBrief);
                textViewCarB.setText("洗车指数：" + suggestion.sugCarWashing.washingBrief);
                textViewUvB.setText("紫外线强度：" + suggestion.sugUv.uvBrief);
                textViewSportB.setText("运动指数：" + suggestion.sugSport.sportBrief);
                textViewTravelB.setText("旅游指数：" + suggestion.sugTravel.travelBrief);
                textViewFishingB.setText("感冒指数：" + suggestion.sugFishing.fishingBrief);
                weather.suggestion = suggestion;
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
                if(locationArrayList.size() != 0){
                    textViewNowLocation.setText(locationArrayList.get(0).locationPath);weather.location = locationArrayList.get(0);
                }
            }
        }
    }

    /**
     * 设置天气现象的图片
     * @param code 天气代码
     */
    private void setNowTabImageByCode(String code, int requestcode){
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

        switch (requestcode){
            case 0:
                imageViewNowCode.setImageResource(id);
                break;
            case 1:
                imageViewCode1.setImageResource(id);
                break;
            case 2:
                imageViewCode2.setImageResource(id);
                break;
            case 3:
                imageViewCode3.setImageResource(id);
                break;
            default:
                break;
        }
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
     * 请求天气建议
     * @param address
     */
    private void requestSuggestionWeather(String address){
        String url = "https://api.seniverse.com/v3/life/suggestion.json?key=v2bxdf4yegclkkns&location=" + address + "&language=zh-Hans";
        new SuggestionAsyncTask().execute(url);
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
     * 启动Fragment
     * @param data 值
     * @return 碎片
     */
    public static Fragment newFragment(String data){
        Fragment fragment = new WeatherFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY, data);
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * 从本地设置天气数据
     */
    private void setWhestherDataFromSPF(Now now, ArrayList<Daily> dailyArrayList, Suggestion suggestion, ArrayList<Location> locationArrayList){

        textViewNowTemp.setText(now.nowTemperature + "℃");
        textViewNowText.setText(now.nowText);
        setNowTabImageByCode(now.nowCode, 0);
        textViewText1.setText(now.nowText);
        setNowTabImageByCode(now.nowCode, 0);
        weather.now = now;

        if(dailyArrayList.get(0).dailyPrecip.equals("")){
            textViewNowPrecip.setText("11%");
        }else {
            textViewNowPrecip.setText(dailyArrayList.get(0).dailyPrecip + "%");
        }
        textViewNowWind.setText(dailyArrayList.get(0).dailyWindSpeed + "km/h");
        textViewNowWindDirection.setText(dailyArrayList.get(0).dailyWindDirection + "风");
        textViewNowWindScale.setText(dailyArrayList.get(0).dailyWindScale + "级");
        textViewTemp1.setText(dailyArrayList.get(0).dailyLow + "℃" + "/" + dailyArrayList.get(0).dailyHigh + "℃");
        textViewText2.setText(dailyArrayList.get(1).dailyTextDay);
        textViewTemp2.setText(dailyArrayList.get(1).dailyLow + "℃" + "/" + dailyArrayList.get(0).dailyHigh + "℃");
        setNowTabImageByCode(dailyArrayList.get(1).dailyCodeDay, 2);
        textViewText3.setText(dailyArrayList.get(2).dailyTextDay);
        textViewTemp3.setText(dailyArrayList.get(2).dailyLow + "℃" + "/" + dailyArrayList.get(0).dailyHigh + "℃");
        setNowTabImageByCode(dailyArrayList.get(2).dailyCodeDay, 3);
        weather.dailyArrayList = dailyArrayList;

        List<Integer> lowData = new ArrayList<>();
        List<Integer> highData = new ArrayList<>();
        int low;
        int high;
        for(int i = 0; i < dailyArrayList.size(); i++){
            lowData.add(Integer.parseInt(dailyArrayList.get(i).dailyLow));
            highData.add(Integer.parseInt(dailyArrayList.get(i).dailyHigh));
        }
        Collections.sort(lowData);
        low = lowData.get(0);
        Collections.sort(highData);
        high = highData.get(2);
        linearLayoutManager = new FullyLinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);//禁止rcyc嵌套滑动
        dailyAdapter = new DailyAdapter(dailyArrayList, low, high);
        recyclerView.setAdapter(dailyAdapter);

        textViewDressingB.setText("穿衣指数：" + suggestion.sugDressing.dressingBrief);
        textViewCarB.setText("洗车指数：" + suggestion.sugCarWashing.washingBrief);
        textViewUvB.setText("紫外线强度：" + suggestion.sugUv.uvBrief);
        textViewSportB.setText("运动指数：" + suggestion.sugSport.sportBrief);
        textViewTravelB.setText("旅游指数：" + suggestion.sugTravel.travelBrief);
        textViewFishingB.setText("感冒指数：" + suggestion.sugFishing.fishingBrief);
        weather.suggestion = suggestion;

        textViewNowLocation.setText(locationArrayList.get(0).locationPath);
        weather.location = locationArrayList.get(0);


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
     * 测量出每个RecycleView的item大小
     */
    public class FullyLinearLayoutManager extends LinearLayoutManager {

        private final String TAG = FullyLinearLayoutManager.class.getSimpleName();

        public FullyLinearLayoutManager(Context context) {
            super(context);
        }

        public FullyLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        private int[] mMeasuredDimension = new int[2];

        @Override
        public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state,
                              int widthSpec, int heightSpec) {

            final int widthMode = View.MeasureSpec.getMode(widthSpec);
            final int heightMode = View.MeasureSpec.getMode(heightSpec);
            final int widthSize = View.MeasureSpec.getSize(widthSpec);
            final int heightSize = View.MeasureSpec.getSize(heightSpec);

            Log.i(TAG, "onMeasure called. \nwidthMode " + widthMode
                    + " \nheightMode " + heightSpec
                    + " \nwidthSize " + widthSize
                    + " \nheightSize " + heightSize
                    + " \ngetItemCount() " + getItemCount());

            int width = 0;
            int height = 0;
            for (int i = 0; i < getItemCount(); i++) {
                measureScrapChild(recycler, i,
                        View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED),
                        mMeasuredDimension);

                if (getOrientation() == HORIZONTAL) {
                    width = width + mMeasuredDimension[0];
                    if (i == 0) {
                        height = mMeasuredDimension[1];
                    }
                } else {
                    height = height + mMeasuredDimension[1];
                    if (i == 0) {
                        width = mMeasuredDimension[0];
                    }
                }
            }
            switch (widthMode) {
                case View.MeasureSpec.EXACTLY:
                    width = widthSize;
                case View.MeasureSpec.AT_MOST:
                case View.MeasureSpec.UNSPECIFIED:
            }

            switch (heightMode) {
                case View.MeasureSpec.EXACTLY:
                    height = heightSize;
                case View.MeasureSpec.AT_MOST:
                case View.MeasureSpec.UNSPECIFIED:
            }

            setMeasuredDimension(width, height);
        }

        private void measureScrapChild(RecyclerView.Recycler recycler, int position, int widthSpec,
                                       int heightSpec, int[] measuredDimension) {
            try {
                View view = recycler.getViewForPosition(0);//fix 动态添加时报IndexOutOfBoundsException

                if (view != null) {
                    RecyclerView.LayoutParams p = (RecyclerView.LayoutParams) view.getLayoutParams();

                    int childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec,
                            getPaddingLeft() + getPaddingRight(), p.width);

                    int childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec,
                            getPaddingTop() + getPaddingBottom(), p.height);

                    view.measure(childWidthSpec, childHeightSpec);
                    measuredDimension[0] = view.getMeasuredWidth() + p.leftMargin + p.rightMargin;
                    measuredDimension[1] = view.getMeasuredHeight() + p.bottomMargin + p.topMargin;
                    recycler.recycleView(view);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }
        }
    }

}



