package com.jhlc.material.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 104468 on 2015/3/12.
 */
public class OfficeDBHelper_new extends SQLiteOpenHelper {


    public static final String OFFICE_USER_DBNAME = "officeuser.db";
    private static OfficeDBHelper_new instance;//这里主要解决死锁问题,是static就能解决死锁问题



    public OfficeDBHelper_new(Context context) {
        /**
         * 数据数据库版本
         * 8：增加mid,增加opcode ,updatetime 记录更新时间 数据上传时间
         *
         */
        super(context, OFFICE_USER_DBNAME, null, 1);
    }

    /**
     * 为应用程序提供一个单一的入口，保证应用程序使用同一个对象操作数据库，不会因为对象不同而使同步方法失效
     * @param context 上下文
     * @return  instance
     */
    public static OfficeDBHelper_new getHelper(Context context){
        if(instance==null)
            instance=new OfficeDBHelper_new(context);
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE table IF NOT EXISTS officeuser"
                + " (_id INTEGER PRIMARY KEY AUTOINCREMENT,officename TEXT,username TEXT,headimage TEXT, job TEXT,area TEXT)");
    }
    //第一版数据库，不用更新
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
