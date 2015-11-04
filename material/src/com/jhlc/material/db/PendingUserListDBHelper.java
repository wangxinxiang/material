package com.jhlc.material.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 "name": "5001",
 "headimg": "/UserImage/201410200951303261.JPG",
 "msgnum": "0",
 "onexecutenum": "0",
 "overtimenum": "0",
 "normalworkoknum": "0",
 "overtimeworkoknum": "0",
 "noreportworknum": "0",
 "worknum": "0"
 */
public class PendingUserListDBHelper extends SQLiteOpenHelper {
    public static final String USER_DBNAME = "pendinguserlist.db";

    public PendingUserListDBHelper(Context context) {
        super(context, USER_DBNAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE table IF NOT EXISTS puserlist"
                + " (_id INTEGER PRIMARY KEY AUTOINCREMENT,usid TEXT, ispass TEXT,username TEXT," +
                "userid TEXT,headimg TEXT,upordown TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("ALTER TABLE user ADD COLUMN other TEXT");
    }
}
