package com.jhlc.material.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.jhlc.material.bean.Pendingrelation;

import java.util.ArrayList;

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
 用于记录邀请添加的用户
 */
public class PendingUserListDB {
    private PendingUserListDBHelper helper;
    public PendingUserListDB(Context context) {
        helper = new PendingUserListDBHelper(context);
    }

    /**
     *  添加用户列表（上级的和下级的都有）
     *  已经有此用户的 则更新信息
     *  没有此用户则插入数据
     * */
    public void addPendingUsers(Pendingrelation u,int UpOrDown) {
        if (getPUser(u.getUserid()) != null) {
            update(u);
            return;
        }
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(
                "insert into puserlist (upordown,usid,ispass,username,userid,headimg) values(?,?,?,?,?,?)",
                new Object[]{UpOrDown+"",u.getUsid(), u.getIsPass(), u.getUsername(), u.getUserid(), u.getHeadImg()});

      //  db.close();

    }

    public void update(Pendingrelation u) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(
                "update puserlist set upordown=?,usid=?,ispass=?,username=?,headimg=? where userid=?",
                new Object[]{u.getUpordown(), u.getUsid(), u.getIsPass(), u.getUsername(), u.getHeadImg(),
                        u.getUserid()});

       // db.close();
    }

    //根据姓名得到 用户信息
    public Pendingrelation getPUser(String userid) {
        Pendingrelation u = new Pendingrelation();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("select * from puserlist where userid=?",
                new String[]{userid});
        if (c.moveToFirst()) {
            u.setUsername(c.getString(c.getColumnIndex("username")));
            u.setUserid(c.getString(c.getColumnIndex("userid")));
            u.setUsid(c.getString(c.getColumnIndex("usid")));
            u.setHeadImg(c.getString(c.getColumnIndex("headimg")));
            u.setIsPass(c.getString(c.getColumnIndex("ispass")));;
            u.setUpordown(c.getString(c.getColumnIndex("upordown")));
        } else {
            return null;
        }
        if (!c.isClosed()) {
            c.close();
        }
       // db.close();
        return u;
    }

    // 根据上下级 upordown 取得用户列表
    public ArrayList<Pendingrelation> getPUser(int upordown) {
        ArrayList<Pendingrelation> userlistArrayList = new ArrayList<Pendingrelation>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("select * from puserlist where upordown=? order by _id desc",new String[]{upordown + ""});

        while (c.moveToNext()) {
            Pendingrelation u = new Pendingrelation();
            u.setUsername(c.getString(c.getColumnIndex("username")));
            u.setUserid(c.getString(c.getColumnIndex("userid")));
            u.setUsid(c.getString(c.getColumnIndex("usid")));
            u.setHeadImg(c.getString(c.getColumnIndex("headimg")));
            u.setIsPass(c.getString(c.getColumnIndex("ispass")));;
            u.setUpordown(c.getString(c.getColumnIndex("upordown")));
            userlistArrayList.add(u);
        }
        if (!c.isClosed()) {
            c.close();
        };
      //  db.close();
        return userlistArrayList;
    }

    //删除某个用户 根据用户名
    public void deleteByUserId(String userId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "delete from puserlist where userid='"+userId+"'";//删除操作的SQL语句
        db.execSQL(sql);//执行删除操作
       // db.close();

    }

    //删除整个 userlist 中的数据
    public void delete() {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("delete from puserlist");
       // db.close();
    }

    public void close(){
        SQLiteDatabase db = helper.getWritableDatabase();
        if(db.isOpen()){
            db.close();
        }
    }

}
