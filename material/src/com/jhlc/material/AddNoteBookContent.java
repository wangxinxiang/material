package com.jhlc.material;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jhlc.material.bean.NoteBookBean;
import com.jhlc.material.db.NoteBookDB;
import com.jhlc.material.utils.*;
import com.jhlc.material.view.wheelview.WheelMain;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 *  分配任务
 *
 * */
public class AddNoteBookContent extends Activity{
    private RelativeLayout rl_limit_time;
    private EditText   et_edit_task;
    private View       timePicker;
    private WheelMain  wheelMain;
    private String     message,type;
    private String     enddate;
    private int editId;
    private NoteBookDB noteBookDB = new NoteBookDB(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_notebook_content);
        initView();
        setListener();
    }

    private void setListener() {
        findViewById(R.id.ibtn_close_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.ibtn_submit_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 得到 当前 日期 加上任务的时间 的日期
                enddate =createEnddate(type);

                message = et_edit_task.getText().toString().trim();
                if(null != message &&  !("".equals(message))) {
                   // ZXLApplication.getInstance().showTextToast("editId:"+editId);
                    if(editId>0){
                        editNoteBook(message,editId);
                    }else {
                        addNoteBookItems();
                    }
                    finish();
                } else {
                    ZXLApplication.getInstance().showTextToast("内容不能为空！");
                }

            }
        });



    }

    private String createEnddate(String type) {
        String result=null;
        Date date = new Date();
        if(Constant.notebook_today.equals(type)){
            result= TimeUtil.sdf.format(date);
        }else if(Constant.notebook_tomorrow.equals(type)){
            Calendar temp=Calendar.getInstance();
            temp.set(Calendar.DATE,temp.get(Calendar.DATE)+1);
            result=TimeUtil.sdf.format(temp.getTime());
        }else if(Constant.notebook_recent.equals(type)){
            Calendar temp=Calendar.getInstance();
            temp.set(Calendar.DATE,temp.get(Calendar.DATE)+5);
            result=TimeUtil.sdf.format(temp.getTime());
        }else{
            result= TimeUtil.sdf.format(date);
        }
        return result;
    }

    private void editNoteBook(String message, int editId) {
            noteBookDB.updateNoteBookTitle(message,editId);
    }

    private void initView() {
        rl_limit_time = (RelativeLayout) findViewById(R.id.rl_limit_time);

        et_edit_task  = (EditText)findViewById(R.id.et_edit_task);
        et_edit_task.requestFocus();
        et_edit_task.setSelection(0);

        TextView tv_title_name    = (TextView) findViewById(R.id.tv_title_name);


        Intent intent = getIntent();
        type    = intent.getStringExtra("type");
        editId    = intent.getIntExtra("editId",0);
        initNoteBookTitle(type);
        if(editId>0){
           // tv_title_name.setText("修改备忘录");
            ArrayList<NoteBookBean> result= noteBookDB.getNoteBook(editId);
            if(result.size()==0){
                ZXLApplication.getInstance().showTextToast("本地数据库错误");
            }else{
                NoteBookBean noteBookBean=result.get(0);
                et_edit_task.setText(noteBookBean.getTitle());
            }
        }else {
           // tv_title_name.setText("新建备忘录");
        }

    }

    private void initNoteBookTitle(String type) {
//        ZXLApplication.getInstance().showTextToast("测试type:"+type);
        TextView tv= (TextView) findViewById(R.id.zxl_title).findViewById(R.id.tv_title_name);
        if(Constant.notebook_today.equals(type)){
            tv.setText("今天");
        }else if(Constant.notebook_tomorrow.equals(type)){
            tv.setText("明天");
        }else if(Constant.notebook_recent.equals(type)){
           tv.setText("近期");
        }else if(Constant.notebook_later.equals(type)){
           tv.setText("以后");
        }
    }

    private void addNoteBookItems( ) {
        LogZ.d("AddNoteBookContent--> ", "type-> " + type + " message-> " + message + " enddate-> " + enddate);
        noteBookDB.addNoteBook(type,message,enddate,"0");
    }

    /**
     *  暂时不用
     * */
    private String getStopTime(){
        // 得到 当前 日期 加上任务的时间 的日期
        long time = 0L;
        if(wheelMain.getDay() == 0 && wheelMain.getHour() == 0){
            time = new Date().getTime() + 24 * 60 * 60 * 1000;
        } else {
            time = new Date().getTime() + wheelMain.getDay() * 24 * 60 * 60 * 1000 + wheelMain.getHour() * 60 * 60 * 1000;
        }
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        LogZ.d("getUpUserData--> ", "" + sd.format(time));
        return sd.format(time);
    }

}
