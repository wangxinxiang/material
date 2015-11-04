package com.jhlc.material;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.GsonBuilder;
import com.jhlc.material.bean.PostBackDataBean;
import com.jhlc.material.utils.*;
import com.jhlc.material.view.wheelview.WheelMain;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *  分配任务
 *
 * */
public class AddLogSystem extends Activity{
    private RelativeLayout rl_limit_time;
    private EditText   et_edit_task;
    private View       timePicker;
    private WheelMain  wheelMain;
    private String     message,type;
    private String     enddate;
    private String editId;
    private String content;
    private String title;

    private ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_notebook_content);
        content=getIntent().getStringExtra("content");
        title=getIntent().getStringExtra("title");
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
                enddate = createEnddate(type);

                message = et_edit_task.getText().toString().trim();
                if (null != message && !("".equals(message))) {
                    // ZXLApplication.getInstance().showTextToast("editId:"+editId);
                    if (StringUtils.isNotBlank(editId)) {
                        editNoteBook2server(message, editId);
                    } else {
                        addNoteBook2server(message);
                    }
                    //finish();
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

    private void addNoteBook2server(String message) {
        AsyncHttpClient client = new AsyncHttpClient();




        RequestParams params = new RequestParams();
        try {
            //String m_szAndroidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

            params.put("opcode", "ls_addmemo");
            String userName = PreferenceUtils.getUserName();
            params.put("username", userName);
            params.put("eid", Constant.EID);
            Log.d("addLogSystem------>", enddate);
            params.put("ot",enddate);
            params.put("msg",message);
            params.put("type","today");
            params.put("top","0");
        } catch (Exception e) {
            e.printStackTrace();
        }

        client.post(MYURL.URL_HEAD, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                pd = ProgressDialog.show(AddLogSystem.this, "正在提交...", "请等待", true);

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                LogZ.d("add_memo_success--> ", new String(responseBody));
                //{"code":100,"msg":"success","opcode":"reguser_eid_success","headimg":"/UserImage/201411080338542207.JPG"}

                PostBackDataBean noteBookServerReturn = new GsonBuilder().create().fromJson(new String(responseBody), PostBackDataBean.class);
                if(noteBookServerReturn==null){
                    ZXLApplication.getInstance().showTextToast("连接出错");
                }
                if ("add_memo_success".equals(noteBookServerReturn.getOpcode())) {
                    //  ZXLApplication.getInstance().showTextToast("获得成功:"+noteBookServerReturn.getList().length);
                    ZXLApplication.getInstance().showTextToast("添加成功");
                   finish();
                }else if ("not_allow".equals(noteBookServerReturn.getOpcode())) {
                    ZXLApplication.getInstance().showTextToast("只允许添加"+noteBookServerReturn.getMsg()+"天内的日志");

                }else{
                    ZXLApplication.getInstance().showTextToast(noteBookServerReturn.getMsg());
                }
                pd.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                ZXLApplication.getInstance().showNetWorkingErrorTextToast();
                pd.dismiss();
            }
        });
    }

    private void editNoteBook2server(String message, String editId) {
        AsyncHttpClient client = new AsyncHttpClient();




        RequestParams params = new RequestParams();
        try {
            //String m_szAndroidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            params.put("opcode", "ls_update_memo");
            String userName = PreferenceUtils.getUserName();
            params.put("Username", userName);
            params.put("eid", Constant.EID);
            params.put("msg",message);
            params.put("type","today");
            params.put("top","0");
            params.put("mid",editId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        client.post(MYURL.URL_HEAD, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                pd = ProgressDialog.show(AddLogSystem.this, "正在提交...", "请等待", true);

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                LogZ.d("update_memo_success--> ", new String(responseBody));
                //{"code":100,"msg":"success","opcode":"reguser_eid_success","headimg":"/UserImage/201411080338542207.JPG"}

                PostBackDataBean noteBookServerReturn = new GsonBuilder().create().fromJson(new String(responseBody), PostBackDataBean.class);
                if (noteBookServerReturn!=null&&"update_memo_success".equals(noteBookServerReturn.getOpcode())) {
                      ZXLApplication.getInstance().showTextToast("修改成功");
                    finish();
                }else if ("not_allow".equals(noteBookServerReturn.getOpcode())) {
                    ZXLApplication.getInstance().showTextToast("只允许修改"+noteBookServerReturn.getMsg()+"天内的日志");
                }else{
                    ZXLApplication.getInstance().showTextToast(noteBookServerReturn.getMsg());
                }
                pd.dismiss();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                ZXLApplication.getInstance().showNetWorkingErrorTextToast();
                pd.dismiss();
            }
        });
    }



    private void initView() {
        rl_limit_time = (RelativeLayout) findViewById(R.id.rl_limit_time);

        et_edit_task  = (EditText)findViewById(R.id.et_edit_task);
        et_edit_task.requestFocus();
        et_edit_task.setSelection(0);

        TextView tv_title_name    = (TextView) findViewById(R.id.tv_title_name);
        tv_title_name.setText(title);

        Intent intent = getIntent();
        type    = intent.getStringExtra("type");
        editId    = intent.getStringExtra("editId");
        initNoteBookTitle(type);
        if(StringUtils.isNotBlank(editId)){
                et_edit_task.setText(content);
        }else {
           // tv_title_name.setText("新建备忘录");
        }
        String date = getIntent().getStringExtra("date");


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
      //  noteBookDB.addNoteBook(type,message,enddate,"0");
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
    private String getDate(Time time) {
        StringBuffer sb = new StringBuffer();
        sb.append(time.year + "/");
        if(time.month + 1 < 10){
            sb.append("0"+(time.month + 1)+"/");
        }else{
            sb.append((time.month + 1)+"/");
        }
        if(time.monthDay < 10){
            sb.append("0"+time.monthDay);
        }else{
            sb.append(time.monthDay);
        }
        return sb.toString();
    }
}
