package com.jhlc.material.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.jhlc.material.bean.Userlist;
import com.jhlc.material.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 *  用于 保存 任务 和 报销 新消息的的ＤＢ
 *  主要用于 新消息 红点的 消失和显示
 */
public class SetNewMsgDB {
    private SetNewMsgDBHelper helper;
    public SetNewMsgDB(Context context) {
        helper = new SetNewMsgDBHelper(context);
    }

    /**
     *
     * @param username
     * @param workid    任务id
     * @param upordown  0 表示是任务模块的新消息信息  1 为报销模块
     */
    public void addNewMsg(String username,String msgnum,String upordown,String workid, String departmentname) {
            SQLiteDatabase db = helper.getReadableDatabase();
        ContentValues cv = new ContentValues();//实例化一个ContentValues用来装载待插入的数据cv.put("username","Jack Johnson");//添加用户名
        cv.put("username",username);
        cv.put("msgnum",msgnum);
        cv.put("upordown",upordown);
        cv.put("workid",workid);
        if (departmentname != null) {
            cv.put("departmentname",departmentname);
        }
        db.insert("newmsg",null,cv);//执行插入操作

//        String sql = "insert into setclock(id,item_name,issetclock) values ('"+id+"','"+item_name+"','"+issetclock+"')";
//        db.execSQL(sql);//执行SQL语句
        db.close();
    }

    /**
     *
     * @param userId    邀请的id
     * @param msgnum
     * @param upordown  0 表示是任务模块的新消息信息  1 为报销模块
     * @param workid  RenWu = "RenWu",BaoXiao = "BaoXiao";
     */
    public void addNewMsgByUserId(String userId,String msgnum,String upordown,String workid, String departmentname) {
        SQLiteDatabase db = helper.getReadableDatabase();
        ContentValues cv = new ContentValues();//实例化一个ContentValues用来装载待插入的数据cv.put("username","Jack Johnson");//添加用户名
        cv.put("userid",userId);
        cv.put("msgnum",msgnum);
        cv.put("upordown",upordown);
        cv.put("workid",workid);
        if (departmentname != null) {
            cv.put("departmentname",departmentname);
        }
        db.insert("newmsg",null,cv);//执行插入操作

//        String sql = "insert into setclock(id,item_name,issetclock) values ('"+id+"','"+item_name+"','"+issetclock+"')";
//        db.execSQL(sql);//执行SQL语句
        db.close();
    }

    /**
     * 查询是否该部门有新数据
     * @param departmentname 部门名
     * @return
     */
    public Boolean getByDepartmentName(String departmentname) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from newmsg where departmentname=?", new String[]{departmentname});
        try {
            if(cursor!=null){
                while (cursor.moveToNext()){
                    if(departmentname.equals(cursor.getString(cursor.getColumnIndex("departmentname")))){
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (cursor!=null) {
                cursor.close();
            }
            db.close();
        }
        return false;
    }

    private Boolean getByName(String username) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from newmsg where username=?", new String[]{username});
        try {
            if(cursor!=null){
                while (cursor.moveToNext()){
                    if(username.equals(cursor.getString(cursor.getColumnIndex("username")))){
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (cursor!=null) {
                cursor.close();
            }
            db.close();
        }
        /*
        if(cursor.moveToFirst()) {
            if(username.equals(cursor.getString(cursor.getColumnIndex("username")))){
                return true;
            }
            cursor.moveToNext();
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        db.close();*/
        return false;
    }
    private Boolean getByUserId(String userid) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from newmsg where userid=?", new String[]{userid});
        try {
            if(cursor!=null){
                while (cursor.moveToNext()){
                    if(userid.equals(cursor.getString(cursor.getColumnIndex("userid")))){
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (cursor!=null) {
                cursor.close();
            }
            db.close();
        }
        /*
        if(cursor.moveToFirst()) {
            if(username.equals(cursor.getString(cursor.getColumnIndex("username")))){
                return true;
            }
            cursor.moveToNext();
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        db.close();*/
        return false;
    }private Boolean getByNameOrUserId(String name,String userid) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from newmsg where username=? or userid=?", new String[]{name, userid});
        try {
            if(cursor!=null){
                while (cursor.moveToNext()){
                    if(name.equals(cursor.getString(cursor.getColumnIndex("username")))){
                        return true;
                    }
                    if(userid.equals(cursor.getString(cursor.getColumnIndex("userid")))) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (cursor!=null) {
                cursor.close();
            }
            db.close();
        }
        /*
        if(cursor.moveToFirst()) {
            if(username.equals(cursor.getString(cursor.getColumnIndex("username")))){
                return true;
            }
            cursor.moveToNext();
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        db.close();*/
        return false;
    }

    public Boolean getByName(Userlist user) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String name = user.getName();
        String userid = user.getUserid();
        if(StringUtils.isNotBlank(name)&&StringUtils.isBlank(userid)){
            return getByName(name);
        }
        if(StringUtils.isBlank(name)&&StringUtils.isNotBlank(userid)){
            return getByUserId(userid);
        }
        if(StringUtils.isNotBlank(name)&&StringUtils.isNotBlank(userid)){
            return getByNameOrUserId(name, userid);
        }
        return false;

    }

    public Boolean getByUpOrDown(String upordown) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from newmsg where upordown=?", new String[]{upordown});
        try {
            if(cursor!=null){
                while (cursor.moveToNext()){
                    if(upordown.equals(cursor.getString(cursor.getColumnIndex("upordown")))){
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (cursor!=null) {
                cursor.close();
            }
            db.close();
        }

       /* if(cursor.moveToFirst()) {
            if(upordown.equals(cursor.getString(cursor.getColumnIndex("upordown")))){
                return true;
            }
            cursor.moveToNext();
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        db.close();*/
        return false;
    }

    /**
     * 列出所有的消息类型，根据upordown 新消息类型 10 任务上级 0 任务下级  11报销上级 1 报销下级
     * @return
     */
    public List<String> listNewMsgByTypeUpOrDown() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select upordown from newmsg  where (username is null and userid!='') or (username !='' and userid is null) group by upordown", null);
        List<String> result=new ArrayList<String>();
        try {
            if(cursor!=null){

                while (cursor.moveToNext()){
                    result.add(cursor.getString(cursor.getColumnIndex("upordown")));
                    }
                }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (cursor!=null) {
                cursor.close();
            }
            db.close();
        }
        return result;

       /* if(cursor.moveToFirst()) {
            if(upordown.equals(cursor.getString(cursor.getColumnIndex("upordown")))){
                return true;
            }
            cursor.moveToNext();
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        db.close();*/
    }

    public Boolean getByWorkID(String workid) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from newmsg where workid=?",new String[]{workid});
        if(cursor.moveToFirst()) {
            if(workid.equals(cursor.getString(cursor.getColumnIndex("workid")))){
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

    public ArrayList<String> getAllWorkID(String name) {
        SQLiteDatabase db = helper.getReadableDatabase();
        ArrayList<String> workids = new ArrayList<>();
        Cursor cursor = db.rawQuery("select workid from newmsg where username=? ",new String[]{name});
        try {
            if(cursor!=null){
                while (cursor.moveToNext()){
                    workids.add(cursor.getString(cursor.getColumnIndex("workid")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (cursor!=null) {
                cursor.close();
            }
            db.close();
        }
        return workids;
    }

    public void deleteByName(String username) {

        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "delete from newmsg where username='"+username+"'";//删除操作的SQL语句
        db.execSQL(sql);//执行删除操作
        db.close();
    }

    public void deleteByUserId(String userId) {

        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "delete from newmsg where userId='"+userId+"'";//删除操作的SQL语句
        db.execSQL(sql);//执行删除操作
        db.close();
    }

    public void delete() {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("delete from newmsg");
        db.close();
    }

    public void deleteByWorkID(String workid) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "delete from newmsg where workid='"+workid+"'";// gen ju workid删除操作的SQL语句
        db.execSQL(sql);//执行删除操作
        db.close();
    }

    public void delete(Userlist user) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql="delete from newmsg where username='"+user.getName()+"' or userid='"+user.getUserid()+"'";
        db.execSQL(sql);//执行删除操作
        db.close();

    }
}
