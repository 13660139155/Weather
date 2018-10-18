package com.example.asus.weather.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.asus.weather.MyApplication;
import com.example.asus.weather.R;
import com.example.asus.weather.db.SQLDatabase;
import com.example.asus.weather.json.Location;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 搜索到的城市列表的适配器
 * Created by ASUS on 2018/5/21.
 */

public class ListSearchAdapter extends ArrayAdapter<Location> {

    private ArrayList<Location> locationArrayList;
    private int resourceId;
    private SQLDatabase sqlDatabase;

    public ListSearchAdapter(Context context, int resource, List<Location> objects) {
        super(context, resource);
        this.locationArrayList = (ArrayList)objects;
        this.resourceId = resource;
        sqlDatabase = new SQLDatabase(MyApplication.getContext(), "Weather.db", null, 1);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       Location location = getItem(position);
        MyViewHolder myViewHolder;
        View view;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            myViewHolder = new MyViewHolder();
            myViewHolder.textView = (TextView)view.findViewById(R.id.text_list_search_item);
            myViewHolder.textViewIsAdd = (TextView)view.findViewById(R.id.text_is_add);
            view.setTag(myViewHolder);
        }else {
            view = convertView;
            myViewHolder = (MyViewHolder)view.getTag();
        }
        myViewHolder.textView.setText(location.locationPath);
        ArrayList<String> arrayList = quryFromSQL("Address", "address");
        if(arrayList.size() != 0){
            for(String s : arrayList){
                if(s.compareTo(location.locationId) == 0){
                    myViewHolder.textViewIsAdd.setText("曾添加");
                }
            }
        }
        return view;
    }

    @Override
    public int getCount() {
        return locationArrayList.size();
    }


    @Nullable
    @Override
    public Location getItem(int position) {
        return locationArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return  0;
    }

    class MyViewHolder{
        TextView textView;
        TextView textViewIsAdd;
    }

   public void clearAll(boolean isRemove){
       if (isRemove){
           if (locationArrayList.size() != 0){
               locationArrayList.clear();
           }
       }
       notifyDataSetChanged();
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
}
