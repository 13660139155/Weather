package com.example.asus.weather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.asus.weather.widget.WeatherAppWidget;

/**
 * sql数据库
 * Created by ASUS on 2018/5/18.
 */

public class SQLDatabase extends SQLiteOpenHelper{

    private static final String CREATE_RECORDS = "create table Records("
            + "id integer primary key autoincrement, "
            + "records text)";

    private static final String CREATER_ADDRESS = "create table Address("
            + "id integer primary key autoincrement, "
            + "address text)";


    public SQLDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATER_ADDRESS);
        db.execSQL(CREATE_RECORDS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists Records");
        db.execSQL("drop table if exists Weather");
        onCreate(db);
    }

}
