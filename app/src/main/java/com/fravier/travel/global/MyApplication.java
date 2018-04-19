package com.fravier.travel.global;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


/**
 * Created by francis on 13/09/2015.
 */
public class MyApplication extends android.support.multidex.MultiDexApplication {
    public static MyApplication sInstance;
    public static MyDatabaseHelper dbHelper;
    public static SQLiteDatabase db;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        dbHelper = new MyDatabaseHelper(this.getApplicationContext());
        db = dbHelper.getWritableDatabase();

    }

    public static SQLiteDatabase getDbHelper() {
        return db;
    }

    public static MyApplication getInstance() {
        return sInstance;
    }

    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }


}
