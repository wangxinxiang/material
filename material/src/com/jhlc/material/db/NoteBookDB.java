package com.jhlc.material.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.jhlc.material.bean.NoteBookBean;
import com.jhlc.material.bean.NoteBookServerKV;
import com.jhlc.material.bean.NoteBookServerMemo;
import com.jhlc.material.utils.PreferenceUtils;
import com.jhlc.material.utils.TimeUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

/**
 */
public class NoteBookDB {

    private NoteBookDBHelper helper;
    public NoteBookDB(Context context) {
        helper = new NoteBookDBHelper(context);
    }

    public void addNoteBook(String type,String title,String date,String istop) {
        SQLiteDatabase db = helper.getReadableDatabase();
        ContentValues cv = new ContentValues();//实例化一个ContentValues用来装载待插入的数据cv.put("username","Jack Johnson");//添加用户名
        baseCV(type, title, date, istop, cv);
        cv.put("opcode","new");
        db.insert("notebook",null,cv);//执行插入操作
        db.close();
//        String sql = "insert into setclock(id,item_name,issetclock) values ('"+id+"','"+item_name+"','"+issetclock+"')";
//        db.execSQL(sql);//执行SQL语句
//        db.close();
    }
    public void addNoteBookFromServer(String type,String title,String date,String istop,String mid,long uploadTime) {

//        String sql = "insert into setclock(id,item_name,issetclock) values ('"+id+"','"+item_name+"','"+issetclock+"')";
//        db.execSQL(sql);//执行SQL语句
//        db.close();
    }

    private void baseCV(String type, String title, String date, String istop, ContentValues cv) {
        cv.put("type",type);
        cv.put("title",title);
        cv.put("date",date);
        cv.put("istop",istop);
        cv.put("valid","1");
        cv.put("username", PreferenceUtils.getInstance().getUserName());
    }

  /*  public Boolean getByName(String username) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from newmsg where username=?",new String[]{username});
        if(cursor.moveToFirst()) {
            if(username.equals(cursor.getString(cursor.getColumnIndex("username")))){
                return true;
            }
            cursor.moveToNext();
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        db.close();
        return false;
    }

    public Boolean getByUpOrDown(String upordown) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from newmsg where upordown=?",new String[]{upordown});
        if(cursor.moveToFirst()) {
            if(upordown.equals(cursor.getString(cursor.getColumnIndex("upordown")))){
                return true;
            }
            cursor.moveToNext();
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        db.close();
        return false;
    }*/

    public ArrayList<NoteBookBean> getNoteBook(int editId ) {
        ArrayList<NoteBookBean> noteBookBeans = new ArrayList<NoteBookBean>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = null;
        if (editId>0) {
            c = db.rawQuery("select * from notebook where _id = ? and valid='1' order by type asc,date desc",new String[]{String.valueOf(editId)});
        }else{
            c = db.rawQuery("select * from notebook where valid='1' order by type asc,date desc",null);
        }
        while (c.moveToNext()) {
            NoteBookBean noteBookBean = new NoteBookBean();
            noteBookBean.setId(c.getInt(c.getColumnIndex("_id")));
            noteBookBean.setType(c.getString(c.getColumnIndex("type")));
            noteBookBean.setTitle(c.getString(c.getColumnIndex("title")));
            noteBookBean.setDate(c.getString(c.getColumnIndex("date")));
            noteBookBean.setIstop(Integer.parseInt(c.getString(c.getColumnIndex("istop"))));
            noteBookBean.setUpdateTime((c.getLong(c.getColumnIndex("updatetime"))));
            noteBookBean.setUpdateTime((c.getLong(c.getColumnIndex("uploadtime"))));
            noteBookBean.setOpcode((c.getString(c.getColumnIndex("opcode"))));
            noteBookBeans.add(noteBookBean);
        }
        if (!c.isClosed()) {
            c.close();
        }
        db.close();

        return noteBookBeans;
    }

    public ArrayList<NoteBookBean> findNeedUpdate() {
        ArrayList<NoteBookBean> noteBookBeans = new ArrayList<NoteBookBean>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = null;

            c = db.rawQuery("select * from notebook where ((uploadtime is null) or (updatetime>uploadtime))  order by type asc",null);

        while (c.moveToNext()) {
            NoteBookBean noteBookBean = new NoteBookBean();
            noteBookBean.setId(c.getInt(c.getColumnIndex("_id")));
            noteBookBean.setType(c.getString(c.getColumnIndex("type")));
            noteBookBean.setTitle(c.getString(c.getColumnIndex("title")));
            noteBookBean.setDate(c.getString(c.getColumnIndex("date")));
            noteBookBean.setIstop(Integer.parseInt(c.getString(c.getColumnIndex("istop"))));
            noteBookBean.setUpdateTime((c.getLong(c.getColumnIndex("updatetime"))));
            noteBookBean.setUpdateTime((c.getLong(c.getColumnIndex("uploadtime"))));
            noteBookBean.setMid((c.getString(c.getColumnIndex("mid"))));
            noteBookBean.setOpcode((c.getString(c.getColumnIndex("opcode"))));
            noteBookBeans.add(noteBookBean);
        }
        if (!c.isClosed()) {
            c.close();
        }
        db.close();

        return noteBookBeans;
    }

    public void deleteByID(int id) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "update notebook set valid=?,updatetime=?,opcode=? where _id=?";//删除操作的SQL语句
        db.execSQL(sql,new Object[]{"0",System.currentTimeMillis(),"delete",id});//执行删除操作
        db.close();
    }

    public void delete() {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("delete from notebook");
        db.close();
    }

    public void resumeByName(String userName) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("update notebook set valid=? where userName=?",new Object[]{"1",userName});
        db.close();
    }


    public void changeName(String oldname,String newUserName) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("update notebook set username=? where userName=?",new Object[]{newUserName,oldname});
        db.close();
    }

    public void updateNoteBookTitle(String message, int editId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(
                "update notebook set title=?,opcode=?,updatetime=? where _id=?",
                new Object[]{message,"update",System.currentTimeMillis(), editId});

        db.close();
    }

    public void updateNoteBookTop(int topStatus, int editId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(
                "update notebook set istop=?,opcode=?,updatetime=?  where _id=?",
                new Object[]{topStatus,"update",System.currentTimeMillis(), editId});

        db.close();
    }

    public void updateMid(NoteBookServerKV[] list) {


        SQLiteDatabase db = helper.getReadableDatabase();

        for (int i = 0; i < list.length; i++) {
            NoteBookServerKV noteBookServerKV = list[i];
            db.execSQL(
                    "update notebook set mid=?,uploadtime=?,updatetime=?  where _id=?",
                    new Object[]{noteBookServerKV.getValue(),System.currentTimeMillis(), System.currentTimeMillis()-1000,noteBookServerKV.getKey()});
            //当mid对应的记录有delete op时要物理删除记录
            db.execSQL(
                    "delete from notebook where mid=? and opcode=? and _id=?",
                    new Object[]{noteBookServerKV.getValue(),"delete",noteBookServerKV.getKey()});
        }

        db.close();
    }

    public void newFromServer(NoteBookServerMemo[] list) {
        SQLiteDatabase db = helper.getReadableDatabase();


        for (int i = 0; i < list.length; i++) {
            NoteBookServerMemo memo = list[i];
            String s = null;
            try {
                s = TimeUtil.sdf.format(TimeUtil.serverTimeSdf.parse(memo.getCreateTime()));
            } catch (ParseException e) {//如果报错，只要要给一个今天的时间
                e.printStackTrace();
                s=TimeUtil.sdf.format(new Date());
            }
            ContentValues cv = new ContentValues();//实例化一个ContentValues用来装载待插入的数据cv.put("username","Jack Johnson");//添加用户名
            baseCV(getLocalType(memo.getMType()),memo.getMemoContent(), s,memo.getTop(), cv);
            cv.put("opcode","new");
            cv.put("mid",memo.getID());
            cv.put("uploadtime",System.currentTimeMillis());
            db.insert("notebook",null,cv);//执行插入操作

        }
        db.close();
    }

    public void updateFromServer(NoteBookServerMemo[] list) {
        SQLiteDatabase db = helper.getReadableDatabase();


        for (int i = 0; i < list.length; i++) {
            NoteBookServerMemo memo = list[i];
            String s = null;
            try {
                s = TimeUtil.sdf.format(TimeUtil.serverTimeSdf.parse(memo.getCreateTime()));
            } catch (ParseException e) {//如果报错，只要要给一个今天的时间
                e.printStackTrace();
                s=TimeUtil.sdf.format(new Date());
            }

            if (!getNoteBookByMId(memo.getID(),db)) {
                ContentValues cv = new ContentValues();//实例化一个ContentValues用来装载待插入的数据cv.put("username","Jack Johnson");//添加用户名
                baseCV(getLocalType(memo.getMType()),memo.getMemoContent(), s,memo.getTop(), cv);
                cv.put("opcode","new");
                cv.put("mid",memo.getID());
                cv.put("uploadtime",System.currentTimeMillis());
                db.insert("notebook",null,cv);//执行插入操作
            }else{
                db.execSQL(
                        "update notebook set title=?  where mid=?",
                        new Object[]{memo.getMemoContent(),memo.getID()});
            }

        }
        db.close();
    }

    private boolean getNoteBookByMId(String id, SQLiteDatabase db) {
        boolean flag=false;
        Cursor c = null;

        c = db.rawQuery("select * from notebook where mid=?  order by type asc",new String[]{id});

        while (c.moveToNext()) {
            flag=true;
        }
        return flag;
    }

    private String getLocalType(String mType) {
        if("today".equals(mType)){
            return "1";
        }else if("tomorrow".equals(mType)){
            return "2";
        }else if("recent".equals(mType)){
            return "3";
        }else if("later".equals(mType)){
            return "4";
        }else{
            return "later";
        }
    }
}
