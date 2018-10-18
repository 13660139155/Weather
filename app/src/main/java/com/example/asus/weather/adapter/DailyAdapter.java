package com.example.asus.weather.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.asus.weather.R;
import com.example.asus.weather.json.Daily;
import com.example.asus.weather.view.WeatherLineView;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by ASUS on 2018/5/30.
 */

public class DailyAdapter extends RecyclerView.Adapter<DailyAdapter.MyViewHolder>{

    private ArrayList<Daily> mDailyArrayList;
    private int mLowestTemp;
    private int mHighestTemp;

    public DailyAdapter(ArrayList<Daily> mDailyArrayList, int lowestTemp, int highestTemp) {
        this.mDailyArrayList = mDailyArrayList;
        this.mHighestTemp = highestTemp;
        this.mLowestTemp = lowestTemp;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_daily_weather, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Daily daily = mDailyArrayList.get(position);
        holder.textViewDayText.setText(daily.dailyTextDay);
        holder.imageViewDayCode.setImageResource(setNowTabImageByCode(daily.dailyCodeDay));
        holder.textViewNightText.setText(daily.dailyTextNight);
        holder.imageViewNightCode.setImageResource(setNowTabImageByCode(daily.dailyCodeNight));
        String newDate = daily.dailyDate.substring(5);
        holder.textViewDate.setText(newDate);

        holder.weatherLineView.setLowHighestData(mLowestTemp, mHighestTemp);
        int[] lowData = new int[3];
        int[] highData = new int[3];
        if(position <= 0){
            lowData[0] = 0;
            highData[0] = 0;
        }else {
            lowData[0] = (Integer.parseInt(mDailyArrayList.get(position - 1).dailyLow) + Integer.parseInt(daily.dailyLow)) / 2;
            highData[0] = (Integer.parseInt(mDailyArrayList.get(position - 1).dailyHigh) + Integer.parseInt(daily.dailyHigh)) / 2;
        }
        if(position >= (mDailyArrayList.size() - 1)){
            lowData[2] = 0;
            highData[2] = 0;
        }else {
            lowData[2] = (Integer.parseInt(mDailyArrayList.get(position + 1).dailyLow) + Integer.parseInt(daily.dailyLow)) / 2;
            highData[2] = (Integer.parseInt(mDailyArrayList.get(position + 1).dailyHigh) + Integer.parseInt(daily.dailyHigh)) / 2;
        }
        lowData[1] = Integer.parseInt(daily.dailyLow);
        highData[1] = Integer.parseInt(daily.dailyHigh);
        holder.weatherLineView.setLowHighData(lowData, highData);
    }

    @Override
    public int getItemCount() {
        return mDailyArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView textViewDate;
        private TextView textViewDayText;
        private ImageView imageViewDayCode;
        private TextView textViewNightText;
        private ImageView imageViewNightCode;
        private WeatherLineView weatherLineView;

        public MyViewHolder(View itemView) {
            super(itemView);
            textViewDate = (TextView) itemView.findViewById(R.id.text_line_date);
            textViewDayText = (TextView) itemView.findViewById(R.id.text_line_day_text);
            imageViewDayCode = (ImageView)itemView.findViewById(R.id.image_line_day_code);
            textViewNightText = (TextView)itemView.findViewById(R.id.text_line_night_text);
            imageViewNightCode = (ImageView)itemView.findViewById(R.id.image_line_night_code);
            weatherLineView = (WeatherLineView)itemView.findViewById(R.id.weather_line_view);
        }
    }

    /**
     * 设置天气现象的图片
     * @param code 天气代码
     */
    private int setNowTabImageByCode(String code){
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
