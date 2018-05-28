package com.example.asus.weather.adapter;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.asus.weather.MainActivity;
import com.example.asus.weather.MyApplication;
import com.example.asus.weather.R;
import com.example.asus.weather.Temp.Temp;
import com.example.asus.weather.db.SQLDatabase;
import com.example.asus.weather.json.Weather;
import com.example.asus.weather.unit.ActivityCollector;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by ASUS on 2018/5/22.
 */

public class CityManageAdapter extends RecyclerView.Adapter<CityManageAdapter.MyViewHolder> {

    private ArrayList<Weather> arrayList;
    private boolean IS_VISIBLE = false;
    private SQLDatabase sqlDatabase;
    private String DELETE = "com.example.asus.weather.DELETE";

    public CityManageAdapter(ArrayList<Weather> weatherArrayList) {
        this.arrayList = weatherArrayList;
        sqlDatabase = new SQLDatabase(MyApplication.getContext(), "Weather.db", null , 1);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_city_manage, parent, false);
        final MyViewHolder myViewHolder = new MyViewHolder(view);
        myViewHolder.relativeCityLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Weather weather = arrayList.get(myViewHolder.getAdapterPosition());
                if(R.id.relative_city_layout == v.getId()){
                    MainActivity.activityStart(v.getContext(), weather.location.locationId);
                    ActivityCollector.finishAll();
                }
            }
        });
        myViewHolder.textViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(R.id.text_cancel == v.getId()){
                    int i = myViewHolder.getAdapterPosition();
                    String deleteS = new String();
                    ArrayList<String> arrayListAddress = quryFromSQL("Address", "address");
                    for(String s : arrayListAddress){
                        if (arrayList.get(i).location != null){
                            if(s.compareTo(arrayList.get(i).location.locationId) == 0 || s.compareTo(encode(arrayList.get(i).location.locationName)) == 0){
                                deleteS = s;
                            }
                        }
                    }
                    if(deleteS.compareTo(Temp.location) == 0){
                        Intent intent = new Intent(DELETE);
                        MyApplication.getContext().sendBroadcast(intent);
                    }
                    deleteFromSQL("Address", "address == ?", deleteS);
                    Temp.IS_DELETE = 1;
                    arrayList.remove(i);
                    notifyItemRemoved(i);
                    notifyDataSetChanged();
                }
            }
        });
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Weather weather = arrayList.get(position);
        if(!IS_VISIBLE){
            holder.relativeCancelLayout.setVisibility(View.GONE);
        }else {
            holder.relativeCancelLayout.setVisibility(View.VISIBLE);
        }
        if(weather.now != null){
            holder.imageViewCityCode.setImageResource(setNowTabImageByCode(weather.now.nowCode));
            holder.textViewCityTemp.setText(weather.now.nowTemperature + "℃");
        }if(weather.location != null){
            holder.textViewCityLocation.setText(weather.location.locationName);
        }if(weather.dailyArrayList != null && weather.dailyArrayList.size() != 0){
            holder.textViewCityWind.setText(weather.dailyArrayList.get(0).dailyWindDirection + "风");
            holder.textViewCityWindDegree.setText(weather.dailyArrayList.get(0).dailyWindScale + "级");
            holder.textViewCityLH.setText(weather.dailyArrayList.get(0).dailyLow + "℃/" + weather.dailyArrayList.get(0).dailyHigh + "℃");
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    /**
     * 标记
     * @param bool
     */
    public void tag(boolean bool){
        this.IS_VISIBLE = bool;
        notifyDataSetChanged();
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


    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textViewCancel;
        TextView textViewCityLocation;
        ImageView imageViewCityCode;
        TextView textViewCityTemp;
        TextView textViewCityWind;
        TextView textViewCityLH;
        TextView textViewCityWindDegree;
        RelativeLayout relativeCancelLayout;
        RelativeLayout relativeCityLayout;

        public MyViewHolder(View itemView) {
            super(itemView);
            textViewCancel = (TextView)itemView.findViewById(R.id.text_cancel);
            textViewCityLocation = (TextView)itemView.findViewById(R.id.text_city_location);
            imageViewCityCode = (ImageView)itemView.findViewById(R.id.image_city_code);
            textViewCityTemp = (TextView)itemView.findViewById(R.id.text_city_temp);
            textViewCityWind = (TextView)itemView.findViewById(R.id.text_city_wind);
            textViewCityLH = (TextView)itemView.findViewById(R.id.text_low_high);
            textViewCityWindDegree = (TextView)itemView.findViewById(R.id.text_city_wind_degree);
            relativeCancelLayout = (RelativeLayout)itemView.findViewById(R.id.relative_cancel_layout);
            relativeCityLayout = (RelativeLayout)itemView.findViewById(R.id.relative_city_layout);
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

}
