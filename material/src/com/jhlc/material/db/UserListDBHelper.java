package com.jhlc.material.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.jhlc.material.utils.LogZ;

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
public class UserListDBHelper extends SQLiteOpenHelper {
    public static final String USER_DBNAME = "userlist.db";

    public UserListDBHelper(Context context) {
        //数据版本更新到6 注意这里,更新历史：
        /**
         * 更新历史：
         * 1：初始版本
         * 6：增加用户邀请确认
         * 7:增加申请完成字段
         * 8:增加userid
         */
        super(context, USER_DBNAME, null, 8);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE table IF NOT EXISTS userlist"
                + " (_id INTEGER PRIMARY KEY AUTOINCREMENT,upordown TEXT, name TEXT,headimg TEXT," +
                "msgnum TEXT,onexecutenum TEXT,overtimenum TEXT,normalworkoknum TEXT,overtimeworkoknum TEXT,noreportworknum TEXT,worknum TEXT,ispass TEXT,usid TEXT,applycompletenum TEXT,userid TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if(oldVersion<6){//更新到邀请上下级版本
            LogZ.d("lyjtest","开始更新数据库!");
            try
            {
                db.beginTransaction();

                db.execSQL("ALTER TABLE userlist ADD COLUMN ispass TEXT");
                db.execSQL("ALTER TABLE userlist ADD COLUMN usid TEXT");
                db.execSQL("update userlist set ispass=? where ispass is null",new String[]{"3"});
                //6
                db.execSQL("ALTER TABLE userlist ADD COLUMN applycompletenum TEXT");
                db.execSQL("update userlist set applycompletenum=? where applycompletenum is null",new String[]{"0"});
                //7
                db.execSQL("ALTER TABLE userlist ADD COLUMN userid TEXT");
                db.setTransactionSuccessful();
            }
            catch (Exception e)
            {
                LogZ.e("zxldbupdate：",e.getMessage());
                e.printStackTrace();
            }
            finally
            {
                LogZ.e("lyjtest","zxldbupdate finished：");
                db.endTransaction();
            }
        }

        if(oldVersion==6){//更新到邀请上下级版本
            LogZ.d("lyjtest","开始更新数据库!");
            try
            {
                db.beginTransaction();

                db.execSQL("ALTER TABLE userlist ADD COLUMN applycompletenum TEXT");
                db.execSQL("update userlist set applycompletenum=? where applycompletenum is null",new String[]{"0"});

                //7
                db.execSQL("ALTER TABLE userlist ADD COLUMN userid TEXT");
                db.setTransactionSuccessful();
            }
            catch (Exception e)
            {
                LogZ.e("zxldbupdate：",e.getMessage());
                e.printStackTrace();
            }
            finally
            {
                LogZ.e("lyjtest","zxldbupdate finished：");
                db.endTransaction();
            }
        }

        if(oldVersion==7){//没有userid的版本
            LogZ.d("lyjtest","开始更新数据库!");
            try
            {
                db.beginTransaction();

                db.execSQL("ALTER TABLE userlist ADD COLUMN userid TEXT");

                db.setTransactionSuccessful();
            }
            catch (Exception e)
            {
                LogZ.e("zxldbupdate：",e.getMessage());
                e.printStackTrace();
            }
            finally
            {
                LogZ.e("lyjtest","zxldbupdate finished：");
                db.endTransaction();
            }
        }
    }
}
