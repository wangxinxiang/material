package com.jhlc.material.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.jhlc.material.utils.LogZ;

/**
 * Created by 104468 on 2014/10/24.
 */
public class SetNewMsgDBHelper extends SQLiteOpenHelper {
    public static final String USER_DBNAME = "newmsg.db";

    public SetNewMsgDBHelper(Context context) {
        super(context, USER_DBNAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE table IF NOT EXISTS newmsg"
                + " (_id INTEGER PRIMARY KEY AUTOINCREMENT,departmentname TEXT, username TEXT,msgnum TEXT,upordown TEXT,workid TEXT,userid TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        if(oldVersion<2){//增加一个userid字段，用于新消息来的提醒
//            LogZ.d("lyjtest", "开始更新数据库!");
//            try
//            {
//                db.beginTransaction();
//
//                db.execSQL("ALTER TABLE newmsg ADD COLUMN userid TEXT");
//
//                db.setTransactionSuccessful();
//            }
//            catch (Exception e)
//            {
//                LogZ.e("zxldbupdate：",e.getMessage());
//                e.printStackTrace();
//            }
//            finally
//            {
//                LogZ.e("lyjtest","zxldbupdate finished：");
//                db.endTransaction();
//            }
//        }
    }
}
