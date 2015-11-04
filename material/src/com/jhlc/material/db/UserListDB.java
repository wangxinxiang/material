package com.jhlc.material.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jhlc.material.bean.Userlist;

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
 */
public class UserListDB {
    private UserListDBHelper helper;
    public UserListDB(Context context) {
        helper = new UserListDBHelper(context);
    }

    /**
     *  添加用户列表（上级的和下级的都有）
     *  已经有此用户的 则更新信息
     *  没有此用户则插入数据
     * */
    public void addUsers(Userlist u,int UpOrDown) {
        if (getUser(u.getName()) != null) {
            update(u);
            return;
        }
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(
                "insert into userlist (upordown,name,headimg,msgnum,onexecutenum,overtimenum,normalworkoknum,overtimeworkoknum,noreportworknum,worknum,ispass,usid,applycompletenum,userid) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                new Object[]{UpOrDown+"",u.getName(), u.getHeadimg(), u.getMsgnum(), u.getOnexecutenum(), u.getOvertimenum(), u.getNormalworkoknum(),
                        u.getOvertimeworkoknum(), u.getNoreportworknum(), u.getWorknum(),u.getIspass(),u.getUsid(),u.getApplycompletenum(),u.getUserid()});

      //  db.close();

    }

    public void update(Userlist u) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(
                "update userlist set headimg=?,msgnum=?,onexecutenum=?,overtimenum=?,normalworkoknum=?,overtimeworkoknum=?,noreportworknum=?,worknum=? ,ispass=?,usid=? ,applycompletenum=? ,userid=? where name=?",
                new Object[]{u.getHeadimg(), u.getMsgnum(), u.getOnexecutenum(), u.getOvertimenum(), u.getNormalworkoknum(),
                        u.getOvertimeworkoknum(), u.getNoreportworknum(), u.getWorknum(),u.getIspass(),u.getUsid(),u.getApplycompletenum(),u.getUserid(), u.getName()});

       // db.close();
    }

    //根据姓名得到 用户信息
    public Userlist getUser(String name) {
        Userlist u = new Userlist();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("select * from userlist where name=?",
                new String[]{name + ""});
        if (c.moveToFirst()) {
            u.setName(c.getString(c.getColumnIndex("name")));
            u.setHeadimg(c.getString(c.getColumnIndex("headimg")));
            u.setMsgnum(c.getString(c.getColumnIndex("msgnum")));
            u.setOnexecutenum(c.getString(c.getColumnIndex("onexecutenum")));
            u.setOvertimenum(c.getString(c.getColumnIndex("overtimenum")));
            u.setNormalworkoknum(c.getString(c.getColumnIndex("normalworkoknum")));
            u.setOvertimeworkoknum(c.getString(c.getColumnIndex("overtimeworkoknum")));
            u.setNoreportworknum(c.getString(c.getColumnIndex("noreportworknum")));
            u.setWorknum(c.getString(c.getColumnIndex("worknum")));
            u.setIspass(c.getString(c.getColumnIndex("ispass")));
            u.setUsid(c.getString(c.getColumnIndex("usid")));
            u.setUserid(c.getString(c.getColumnIndex("userid")));
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
    public ArrayList<Userlist> getUser(int upordown) {
        ArrayList<Userlist> userlistArrayList = new ArrayList<Userlist>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("select * from userlist where upordown=? order by ispass asc, _id desc",new String[]{upordown + ""});

        while (c.moveToNext()) {
            Userlist u = new Userlist();
            u.setName(c.getString(c.getColumnIndex("name")));
            u.setHeadimg(c.getString(c.getColumnIndex("headimg")));
            u.setMsgnum(c.getString(c.getColumnIndex("msgnum")));
            u.setOnexecutenum(c.getString(c.getColumnIndex("onexecutenum")));
            u.setOvertimenum(c.getString(c.getColumnIndex("overtimenum")));
            u.setNormalworkoknum(c.getString(c.getColumnIndex("normalworkoknum")));
            u.setOvertimeworkoknum(c.getString(c.getColumnIndex("overtimeworkoknum")));
            u.setNoreportworknum(c.getString(c.getColumnIndex("noreportworknum")));
            u.setWorknum(c.getString(c.getColumnIndex("worknum")));
            u.setIspass(c.getString(c.getColumnIndex("ispass")));
            u.setUsid(c.getString(c.getColumnIndex("usid")));
            u.setApplycompletenum(c.getString(c.getColumnIndex("applycompletenum")));
            u.setUserid(c.getString(c.getColumnIndex("userid")));
            userlistArrayList.add(u);
        }
        if (!c.isClosed()) {
            c.close();
        };
      //  db.close();
        return userlistArrayList;
    }

    //删除某个用户 根据用户名
    public void deleteByName(String name) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "delete from userlist where name='"+name+"'";//删除操作的SQL语句
        db.execSQL(sql);//执行删除操作
       // db.close();

    }

    //删除整个 userlist 中的数据
    public void delete() {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("delete from userlist");
       // db.close();
    }

    public void close(){
        SQLiteDatabase db = helper.getWritableDatabase();
        if(db.isOpen()){
            db.close();
        }
    }

}
