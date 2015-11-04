package com.jhlc.material.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Administrator on 2015/5/2.
 */
public class OfficeDBManager extends SQLiteOpenHelper{

    private AtomicInteger mOpenCounter = new AtomicInteger();

    private static OfficeDBManager instance;
    private static SQLiteOpenHelper mDatabaseHelper;
    private SQLiteDatabase mDatabase;

    public static final String OFFICE_USER_DBNAME = "officeuser.db";

    public OfficeDBManager(Context context) {
        super(context, OFFICE_USER_DBNAME, null, 1);
    }

    public static synchronized void initializeInstance(SQLiteOpenHelper helper,Context context) {
        if (instance == null) {
            instance = new OfficeDBManager(context);
            mDatabaseHelper = helper;
        }
    }

    public static synchronized OfficeDBManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException(OfficeDBManager.class.getSimpleName() +
                    " is not initialized, call initializeInstance(..) method first.");
        }

        return instance;
    }

    public synchronized SQLiteDatabase openDatabase() {
        //incrementAndget 表示递增一个数后
        //即mOpenCounter 递增一个数后才等于1 ，表示之前没有，所以可以打开数据库
        if(mOpenCounter.incrementAndGet() == 1) {
            // Opening new database
            mDatabase = mDatabaseHelper.getWritableDatabase();
        }
        return mDatabase;
    }

    public synchronized void closeDatabase() {
        if(mOpenCounter.decrementAndGet() == 0) {
            // Closing database
            mDatabase.close();

        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE table IF NOT EXISTS officeuser"
                + " (_id INTEGER PRIMARY KEY AUTOINCREMENT,officename TEXT,username TEXT,headimage TEXT, job TEXT,area TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
