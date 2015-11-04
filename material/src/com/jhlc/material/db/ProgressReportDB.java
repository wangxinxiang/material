package com.jhlc.material.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jhlc.material.bean.WorkDetailList;
import com.jhlc.material.utils.LogZ;
import com.jhlc.material.utils.TimeUtil;

import java.util.LinkedList;

/**
 */
public class ProgressReportDB {
    private ProgressReportHelper helper;
    public  ProgressReportDB(Context context) {
        helper = new ProgressReportHelper(context);
    }

    public void addWorkDetailList(String workid,LinkedList<WorkDetailList> workDetailLists) {
        SQLiteDatabase db = helper.getReadableDatabase();
        for (int i=0; i<workDetailLists.size(); i++) {
            ContentValues cv = new ContentValues();//实例化一个ContentValues用来装载待插入的数据cv.put("username","Jack Johnson");//添加用户名
            cv.put("workid", workid);
            cv.put("reportmessage", workDetailLists.get(i).getReportmessage());
            cv.put("reporttypeid", workDetailLists.get(i).getReporttypeid());
            cv.put("reporttime", workDetailLists.get(i).getReporttime());
            db.insert("progressreport", null, cv);//执行插入操作
            LogZ.d("ProgressReportActivityDB--> ", "" + workDetailLists.get(i).getReportmessage());

        }

    }

    public void addWorkDetailList(String workid,WorkDetailList workDetailList,boolean workover) {
        LinkedList<WorkDetailList> workDetailListsLimit = getWorkDetail(workid);
        LogZ.d("addWorkDetailList--> ",workDetailListsLimit.size()+"  "+workDetailList.getReporttime());
        int linklength = workDetailListsLimit.size();

        if(0 != linklength && (TimeUtil.getLongDiff(workDetailList.getReporttime().replace("/","-"),
                workDetailListsLimit.get(linklength-1).getReporttime().replace("/","-")) < 3600*1000 || workover)){
            //todo :为了测试,这里临时改成5秒,正常应该是3600,一个小时   这里逻辑不对.改成3秒,不能正常出现
            return;
        }

        SQLiteDatabase db = helper.getReadableDatabase();
            ContentValues cv = new ContentValues();//实例化一个ContentValues用来装载待插入的数据cv.put("username","Jack Johnson");//添加用户名
            cv.put("workid", workid);
            cv.put("reportmessage", workDetailList.getReportmessage());
            cv.put("reporttypeid", workDetailList.getReporttypeid());
            cv.put("reporttime", workDetailList.getReporttime());
            db.insert("progressreport", null, cv);//执行插入操作
            LogZ.d("ProgressReportActivityDB--> insert", "" + workDetailList.getReportmessage());
    }

    public LinkedList<WorkDetailList> getWorkDetail(String workid) {

        LinkedList<WorkDetailList> workDetailLists = new LinkedList<WorkDetailList>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("select * from progressreport where workid=?", new String[]{workid});
        while(c.moveToNext()) {
            WorkDetailList workDetailList = new WorkDetailList();
            workDetailList.setReportmessage(c.getString(c.getColumnIndex("reportmessage")));
            workDetailList.setReporttime(c.getString(c.getColumnIndex("reporttime")));
            workDetailList.setReporttypeid(c.getString(c.getColumnIndex("reporttypeid")));
            workDetailLists.add(workDetailList);
        }

        if (!c.isClosed()) {
            c.close();
        }

        return workDetailLists;
    }

    /**
     *  用于上删除 workid 对应的数据
     * */
    public void deleteByWorkID(String workid) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "delete from progressreport where workid='"+workid+"'";//删除操作的SQL语句
        db.execSQL(sql);//执行删除操作
        db.close();

    }

    public void delete() {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("delete from progressreport");
    }


    public void close(){
        SQLiteDatabase db = helper.getWritableDatabase();
        if(db.isOpen()){
            db.close();
        }
    }
}
