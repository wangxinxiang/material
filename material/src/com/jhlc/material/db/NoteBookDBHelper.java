package com.jhlc.material.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.jhlc.material.utils.LogZ;

/**
 * Created by 104468 on 2014/10/24.
 */
public class NoteBookDBHelper extends SQLiteOpenHelper {
    public static final String USER_DBNAME = "notebook.db";

    public NoteBookDBHelper(Context context) {
        /**
         * 数据数据库版本
         * 8：增加mid,增加opcode ,updatetime 记录更新时间 数据上传时间
         *
         */
        super(context, USER_DBNAME, null, 11);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE table IF NOT EXISTS notebook"
                + " (_id INTEGER PRIMARY KEY AUTOINCREMENT,username TEXT,valid TEXT,type TEXT, title TEXT,date TEXT,istop TEXT,mid TEXT,opcode TEXT,updatetime INTEGER,uploadtime INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion<10){//更新到邀请上下级版本
            LogZ.d("lyjtest", "开始更新数据库!");
            try
            {
                db.beginTransaction();

                db.execSQL("ALTER TABLE notebook ADD COLUMN mid TEXT");
                db.execSQL("ALTER TABLE notebook ADD COLUMN opcode TEXT");
                db.execSQL("ALTER TABLE notebook ADD COLUMN updatetime INTEGER");
                db.execSQL("ALTER TABLE notebook ADD COLUMN uploadtime INTEGER");
                //删除所有无效的数据，避免备份的时候将无效的数据提交上去
                db.execSQL("delete from notebook where valid='0'");

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
