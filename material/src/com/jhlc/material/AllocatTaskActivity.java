package com.jhlc.material;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.jhlc.material.bean.PostBackDataBean;
import com.jhlc.material.utils.Constant;
import com.jhlc.material.utils.LogZ;
import com.jhlc.material.utils.MYURL;
import com.jhlc.material.utils.PreferenceUtils;
import com.jhlc.material.view.wheelview.WheelMain;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *  分配任务
 *
 * */
public class AllocatTaskActivity extends Activity{
    private RelativeLayout rl_limit_time;
    private TextView tv_limit_num;
    private EditText  et_edit_task;
    private Button ibtn_submit_edit;

    private View      timePicker;
    private WheelMain wheelMain;
    private CheckBox ck_everyday_report;
    private String    message,receptname;
    private int       UpOrDown;
    private int       enddate = 1;

    private TextView tv_title_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allocat_task_dialog);
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

        ibtn_submit_edit = (Button)findViewById(R.id.ibtn_submit_edit);
        ibtn_submit_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 得到 当前 日期 加上任务的时间 的日期
                if(wheelMain.getDay() >= 1){
                    enddate = wheelMain.getDay();
                }
               // ZXLApplication.getInstance().showTextToast("11111111111111111111111111111enddate:"+enddate);
                message = et_edit_task.getText().toString().trim();
                if(null != message &&  !("".equals(message))) {
                    ibtn_submit_edit.setEnabled(false);
                    allocatTask();
                } else {
                    ZXLApplication.getInstance().showTextToast("添加的任务不能为空！");
                }

            }
        });

        rl_limit_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //隐藏键盘
                InputMethodManager imm = (InputMethodManager) AllocatTaskActivity.this
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                // imm.hideSoftInputFromWindow(myEditText.getWindowToken(), 0);
                if (imm.isActive())  //一直是true
                    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
                            InputMethodManager.HIDE_NOT_ALWAYS);

                if(timePicker.getVisibility()==View.GONE) {
                    timePicker.setVisibility(View.VISIBLE);
                }else{
                    timePicker.setVisibility(View.GONE);
                }
            }
        });

        et_edit_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               timePicker.setVisibility(View.GONE);
            }
        });



    }

    private void initView() {
        ck_everyday_report= (CheckBox) findViewById(R.id.ck_everyday_report);
        rl_limit_time = (RelativeLayout) findViewById(R.id.rl_limit_time);
        tv_limit_num = (TextView) findViewById(R.id.tv_limit_num);

        tv_title_name= (TextView) findViewById(R.id.zxl_title).findViewById(R.id.tv_title_name);


        et_edit_task  = (EditText)findViewById(R.id.et_edit_task);
        et_edit_task.requestFocus();
        et_edit_task.setSelection(0);

        Intent intent = getIntent();
        receptname    = intent.getStringExtra("receptname");
        UpOrDown  = intent.getIntExtra("UpOrDown", -1);

        if(Constant.ZXL_UP_USER == UpOrDown) {
            tv_title_name.setText("代"+receptname+"发起任务");
//            tv_title_name.setHint("我代"+receptname+"发起任务");
        } else if(Constant.ZXL_DOWN_USER == UpOrDown){
//            tv_title_name.setHint("给"+receptname+"布置任务");
            tv_title_name.setText("给"+receptname+"布置任务");
        }

        timePicker = findViewById(R.id.ll_timePicker);
        wheelMain = new WheelMain(timePicker);
        wheelMain.initDateTimePicker();
        wheelMain.setTv_limit_num(tv_limit_num);
    }

    private void allocatTask( ) {
        String dayreport="1";
        if(!ck_everyday_report.isChecked()){
            dayreport="0";
        }
        // 发送请求到服务器
        AsyncHttpClient client = new AsyncHttpClient();
        // 创建请求参数
        RequestParams params = new RequestParams();
        params.put("opcode", "addwork");
        params.put("eid", Constant.EID);
        params.put("Message", message);
        params.put("enddate", enddate);
        params.put("DayReport", dayreport);
        if(Constant.ZXL_UP_USER == UpOrDown){
            // 帮上级添加任务
            params.put("identity", "ReceptUserName");
            params.put("Username", receptname);
            params.put("ReceptUserName", PreferenceUtils.getInstance().getUserName());
        }else if(Constant.ZXL_DOWN_USER == UpOrDown){
            // 给下级添加任务
            params.put("identity", "Username");
            params.put("Username", PreferenceUtils.getInstance().getUserName());
            params.put("ReceptUserName", receptname);
        }

        // 执行post方法
        client.post(MYURL.URL_HEAD, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // 设置值
                LogZ.d("getUpUserData--> ", new String(responseBody));
                try {
                    PostBackDataBean postBackDataBean = new GsonBuilder().create().fromJson(new String(responseBody), PostBackDataBean.class);
                    if(postBackDataBean.getCode() == 100){
                        ZXLApplication.getInstance().showTextToast("操作成功");
                        setResult(100);
                        finish();
                    }
                } catch (JsonSyntaxException exception){
                    exception.printStackTrace();
                    ZXLApplication.getInstance().showTextToast("数据解析错误");
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // 打印错误信息
                error.printStackTrace();

            }

        });
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
