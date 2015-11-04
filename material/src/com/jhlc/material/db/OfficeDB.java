package com.jhlc.material.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.jhlc.material.bean.OfficeBean;
import com.jhlc.material.bean.OfficeUserBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 104468 on 2015/3/12.
 */
public class OfficeDB {
    private OfficeDBHelper helper;
    public OfficeDB(Context context){
        helper = OfficeDBHelper.getHelper(context);
    }
    public void addOffce(String officename,String username,String headimage,String job){
        SQLiteDatabase db = helper.getReadableDatabase();
        ContentValues cv = new ContentValues();
        baseCV(officename, username, headimage, job, cv);
        db.insert("officeuser", null, cv);//执行插入操作
        db.close();

    }
    private void baseCV(String officename, String username, String headimage, String job, ContentValues cv) {
        //baseCV是负责向 用于写入数据库数据类的ContentValues 的实例cv中写入实际数据的
        cv.put("officename",officename);
        cv.put("username",username);
        cv.put("headimage", headimage);
        cv.put("job", job);
    }

    public void delete(String title, String data) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "delete from officeuser where ?=?";//删除操作的SQL语句
        db.execSQL(sql,new Object[]{title,data});//执行删除操作
        db.close();
    }
    //参数title是要查找的字段名，参数data是要查找的值，参数updateTitle是要修改的字段名，参数updateData是要修改的值
    public void update(String title,String data,String updateTitle,String updateData){
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "update officeuser set ?=? where ?=?";
        db.execSQL(sql,new Object[]{title,data,updateTitle,updateData});//执行修改操作
        db.close();
    }

    //数据更新操作，当人物的职位和头像发生变化时更新
    public void updateDataByUsername(String username,String headimage,String job,SQLiteDatabase db){
        String sql = "update officeuser set headimage=?,job=? where username=?";
        db.execSQL(sql, new Object[]{headimage, job, username});//执行修改操作
    }

    //获取所有数据
    public List<OfficeBean> getData(){
        SQLiteDatabase db = helper.getReadableDatabase();
        List<OfficeBean> list = new ArrayList<OfficeBean>();
        String sql = "select * from officeuser";
        Cursor c = db.rawQuery(sql,new String[]{});
        //rawQuery用于执行select查询语句
        while (c.moveToNext()) {
            OfficeUserBean bean = new OfficeUserBean();
            bean.setUsername(c.getString(c.getColumnIndex("username")));
            bean.setHeadimage(c.getString(c.getColumnIndex("headimage")));
            bean.setJob(c.getString(c.getColumnIndex("job")));
            String officename = c.getString(c.getColumnIndex("officename"));
            //判断list中是否已经有OfficeBean，有就直接将bean加入到OfficeBean的list中,没有就新建一个OfficeBean加入到list中
            boolean f = true;
            for (int i = 0; i< list.size(); i++){
                OfficeBean ob = list.get(i);
                //如果
                if(officename.equals(ob.getOfficename())){
                    ob.getList().add(bean);
                    f = false;
                    break;
                }
            }
            if(f){
                List<OfficeUserBean> oblist = new ArrayList<OfficeUserBean>();
                OfficeBean ob = new OfficeBean();
                ob.setOfficename(officename);
                oblist.add(bean);
                ob.setList(oblist);
                list.add(ob);
            }
        }
        if (!c.isClosed()) {
            c.close();
        }
        db.close();
        return  list;
    }
    //获取搜索数据
    // officename表示办公室名称 officeuser表示表名
    public List<OfficeBean> getDataBySearch(String search){
        SQLiteDatabase db = helper.getReadableDatabase();
        List<OfficeBean> list = new ArrayList<OfficeBean>();
        String sql = "select * from officeuser where officename like ? or username like ? or job like ?";
        Cursor c = db.rawQuery(sql,new String[]{"%"+search+"%","%"+search+"%","%"+search+"%"});
        while (c.moveToNext()) {
            OfficeUserBean bean = new OfficeUserBean();
            bean.setUsername(c.getString(c.getColumnIndex("username")));
            bean.setHeadimage(c.getString(c.getColumnIndex("headimage")));
            bean.setJob(c.getString(c.getColumnIndex("job")));
            String officename = c.getString(c.getColumnIndex("officename"));
            //判断list中是否已经有OfficeBean，有就直接将bean加入到OfficeBean的list中,没有就新建一个OfficeBean加入到list中
            boolean f = true;
            for (int i = 0; i< list.size(); i++){
                OfficeBean ob = list.get(i);
                if(officename.equals(ob.getOfficename())){
                    ob.getList().add(bean);
                    f = false;
                    break;
                }
            }
            if(f){
                List<OfficeUserBean> oblist = new ArrayList<OfficeUserBean>();
                OfficeBean ob = new OfficeBean();
                ob.setOfficename(officename);
                oblist.add(bean);
                ob.setList(oblist);
                list.add(ob);
            }
        }
        if (!c.isClosed()) {
            c.close();
        }
        db.close();
        return list;
    }

    //用于判断该username是否存在于officeuser表中
    public boolean isDataByUsername(String username,SQLiteDatabase db){
        Cursor c = null;
        c = db.rawQuery("select * from officeuser where username=?",new String[]{username});
        boolean f = false;
        while (c.moveToNext()) {
            //officeuser表中非重名，读到数据就返回ture
            f = true;
            break;
        }
        if (!c.isClosed()) {
            c.close();
        }
        //读不到数据就返回false
        return f;
    }
    //用于数据更新的操作
    public void updateOffice(List<OfficeBean> list){
       // = System.currentTimeMillis()
        SQLiteDatabase db = helper.getReadableDatabase();
            for (int i = 0; i < list.size(); i++) {
                OfficeBean ob = list.get(i);
                for (int j = 0; j < ob.getList().size(); j++) {
                    OfficeUserBean bean = ob.getList().get(j);
                    if(!isDataByUsername(bean.getUsername(),db)){
                        //如果该username不存在表中就插入数据
                        ContentValues cv = new ContentValues();
                        baseCV(ob.getOfficename(), bean.getUsername(), bean.getHeadimage(),bean.getJob(),cv);
                        db.insert("officeuser",null,cv);
                    }else{
                        //如果该username存在就update更新该username中的数据
                        updateDataByUsername(bean.getUsername(),bean.getHeadimage(),bean.getJob(),db);
                    }
                }
            }
        db.close();
    }
    public void delete() {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("delete from officeuser");
        db.close();
    }
}
