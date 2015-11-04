package com.jhlc.material.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 */
public class ProgressReportHelper  extends SQLiteOpenHelper {
    public static final String USER_DBNAME = "progressreport.db";

    public ProgressReportHelper(Context context) {
        super(context, USER_DBNAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE table IF NOT EXISTS progressreport"
                + " (_id INTEGER PRIMARY KEY AUTOINCREMENT,workid TEXT, reportmessage TEXT,reporttypeid TEXT,reporttime TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       // db.execSQL("ALTER TABLE user ADD COLUMN other TEXT");
    }
}
