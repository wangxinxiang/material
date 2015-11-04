package com.jhlc.material;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jhlc.material.adapter.WorkDetailAdapter;
import com.jhlc.material.bean.AlarmBean;
import com.jhlc.material.bean.Executelist;
import com.jhlc.material.bean.WorksBean;
import com.jhlc.material.db.SetNewMsgDB;
import com.jhlc.material.service.MyAlarmManager;
import com.jhlc.material.utils.*;
import com.jhlc.material.view.MyRadioGroup;
import com.jhlc.material.view.PullToRefreshView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *  任务列表界面，带统计数字的
 */
public class WorkItemsDetailActivity extends Activity implements PullToRefreshView.OnHeaderRefreshListener {


private ImageButton    ibtn_add_task;
    private RelativeLayout rl_close_actvity;
    private MyRadioGroup   myRadioGroup;
    private ListView       ls_up_user;
    private CheckBox       cb_detail_info;
    private WorkDetailAdapter workDetailAdapter;
    private Intent intent;

    private ZXLApplication  application = ZXLApplication.getInstance();
    private PreferenceUtils preferenceUtils = PreferenceUtils.getInstance();

    private TextView tv_which_task;
    // 工作完成情况的五中状态
    private TextView tv_running,tv_timeout,tv_ontime,tv_timeout_complete,tv_all_count;
    private TextView tv_title1,tv_title2,tv_title3,tv_title4,tv_title5;

    private SetNewMsgDB newMsgDB = new SetNewMsgDB(this);
    private ArrayList<Executelist>  showExecutelists = new ArrayList<Executelist>();
    private int UpOrDown = -1;
    private String    workusername;
    private WorksBean worksBean = new WorksBean();
    private int checkedIdFlag   = 0;
    private ArrayList<AlarmBean> alarmBeans;
    //用于显示提示信息的字段
    private RelativeLayout insertll;
    private ImageView no_task;
    private TextView tv_addtask_tip;
    private Button btn_addtask;

    private PullToRefreshView   mPullToRefreshView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.task_items_detail);

        /**
         *  注册广播： 用于接收新消息出发，
         * */
        IntentFilter filter = new IntentFilter(Constant.WorkItemsNewMsgAction);
        registerReceiver(broadcastReceiver, filter);

        initTitleView();        //初始化标题
        setRadioGroup();        //设置内容选择选项
        setContent();           //设置内容列表


    }

    @Override
    protected void onResume() {
        super.onResume();
        getMyWorks(true);
    }

    //监听到有 新消息 则刷新列表 进行未读标识
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // 查询新来的消息 的任务ID 是否在本界面
            String workname = intent.getStringExtra("workname");
            if(workname.equals(worksBean.getWorkusername())){
                getMyWorks(true);
            }
        }
    };

    private void initTitleView() {

        mPullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_refresh_view);
        mPullToRefreshView.setOnHeaderRefreshListener(this);
//        mPullToRefreshView.setOnFooterRefreshListener(this);

        //初始化无内容数据的信息：
        insertll= (RelativeLayout) findViewById(R.id.insertll);
        no_task= (ImageView) findViewById(R.id.no_task);
        tv_addtask_tip= (TextView) findViewById(R.id.tv_addtask_tip);
        btn_addtask= (Button) findViewById(R.id.btn_addtask);

        btn_addtask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(WorkItemsDetailActivity.this, AllocatTaskActivity.class);
                intent.putExtra("UpOrDown", UpOrDown);
                intent.putExtra("receptname", workusername);
                startActivityForResult(intent, Constant.RequestCode);
            }
        });

        rl_close_actvity = (RelativeLayout) findViewById(R.id.rl_close_actvity);
        rl_close_actvity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Constant.ResultCode);
                finish();
            }
        });

        tv_which_task = (TextView) findViewById(R.id.tv_which_task);

        ibtn_add_task = (ImageButton) findViewById(R.id.ibtn_add_task);
        ibtn_add_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(WorkItemsDetailActivity.this, AllocatTaskActivity.class);
                intent.putExtra("UpOrDown", UpOrDown);
                intent.putExtra("receptname", workusername);
                startActivityForResult(intent, Constant.RequestCode);
            }
        });

        cb_detail_info = (CheckBox) findViewById(R.id.cb_detail_info);
        cb_detail_info.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) { //显示VIEW
                    myRadioGroup.setVisibility(View.VISIBLE);
                    findViewById(R.id.title_part_line).setVisibility(View.GONE);
                    PreferenceUtils.getInstance().setNeedShowReport(true);
                } else {
                    myRadioGroup.setVisibility(View.GONE);
                    PreferenceUtils.getInstance().setNeedShowReport(false);
                    findViewById(R.id.title_part_line).setVisibility(View.VISIBLE);
                }
            }
        });



    }

    private void setContent() {
        intent = this.getIntent();
        workusername = intent.getStringExtra("workusername");
        UpOrDown = intent.getIntExtra("UpOrDown",-1);

        if(Constant.ZXL_UP_USER == UpOrDown){
            tv_which_task.setText(workusername+"交给我的任务");
        } else if(Constant.ZXL_DOWN_USER == UpOrDown){
            tv_which_task.setText(workusername+"的任务");
        }

        ls_up_user = (ListView)findViewById(R.id.ls_up_user);
        workDetailAdapter = new WorkDetailAdapter(showExecutelists,WorkItemsDetailActivity.this,UpOrDown);
        ls_up_user.setAdapter(workDetailAdapter);
        ls_up_user.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent = new Intent(WorkItemsDetailActivity.this, ProgressReportActivity.class);
                //todo 这里测试refreshreport
//                intent = new Intent(WorkItemsDetailActivity.this, RefreshProgressReportActivity.class);
                intent.putExtra("UpOrDown", UpOrDown);
                intent.putExtra("workID", showExecutelists.get(position).getWorkid());
                intent.putExtra("receptname", worksBean.getWorkusername());
                intent.putExtra("imageurl", worksBean.getWorkuserhead());
                newMsgDB.deleteByWorkID(showExecutelists.get(position).getWorkid());
                startActivityForResult(intent, Constant.RequestCode);
            }
        });
        findViewById(R.id.loading_bar_rl).setVisibility(View.VISIBLE);

    }

    private void setRadioGroup() {
        tv_running = (TextView) findViewById(R.id.tv_running);
        tv_timeout = (TextView) findViewById(R.id.tv_timeout);
        tv_ontime  = (TextView) findViewById(R.id.tv_ontime);
        tv_timeout_complete = (TextView) findViewById(R.id.tv_timeout_complete);
        tv_all_count = (TextView) findViewById(R.id.tv_all_count);

        tv_title1 = (TextView) findViewById(R.id.tv_title1);
        tv_title2 = (TextView) findViewById(R.id.tv_title2);
        tv_title3 = (TextView) findViewById(R.id.tv_title3);
        tv_title4 = (TextView) findViewById(R.id.tv_title4);
        tv_title5 = (TextView) findViewById(R.id.tv_title5);

        myRadioGroup = (MyRadioGroup)findViewById(R.id.select_title);
        myRadioGroup.setOnCheckedChangeListener(new MyRadioGroupOnCheckedChangedListener());

        //增加判断，是否显示根据用户的配置文件来
        if(PreferenceUtils.isNeedShowReport()){
            cb_detail_info.setChecked(true);
        }else{
            cb_detail_info.setChecked(false);
        }

    }

    private void setStatuCount(WorksBean worksBean){
        tv_running.setText(worksBean.getOnexecutenum());
        tv_timeout.setText(worksBean.getOvertimenum());
        tv_ontime.setText(worksBean.getWorkOknum());
        tv_timeout_complete.setText(worksBean.getAllworknum());
        tv_all_count.setText(worksBean.getAllnodayreportworknum());

    }

    private void getMyWorks(final boolean showLoadingFlag) {
        // 发送请求到服务器
        AsyncHttpClient client = new AsyncHttpClient();
        // 创建请求参数
        final RequestParams params = new RequestParams();
        params.put("opcode", "get_work_list");
        params.put("Username", preferenceUtils.getUserName());
        params.put("workusername", workusername);
        params.put("eid", Constant.EID);
        params.put("typeid", UpOrDown);

        // 执行post方法
        client.post(MYURL.URL_HEAD, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                if (showLoadingFlag) {
                    findViewById(R.id.loading_bar_rl).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (showLoadingFlag) {
                    findViewById(R.id.loading_bar_rl).setVisibility(View.GONE);
                }

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                showExecutelists.clear();
                // 设置值
                LogZ.d("getUpUserData--> ", "username==" + preferenceUtils.getUserName() + "   " + new String(responseBody));
                worksBean = new GsonBuilder().create().fromJson(new String(responseBody), WorksBean.class);
                if (worksBean == null || worksBean.getCode() == -1) return;
                deleteNullInfo();
                // 如果正在进行中的有值 这默认显示 进行中的，如果没有值 则显示总数中的
                initRedioBtn();
                setStatuCount(worksBean);
                workDetailAdapter.setUserHead(worksBean.getWorkuserhead());
                workDetailAdapter.notifyDataSetChanged();
                inItRefreshView();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // 打印错误信息
                error.printStackTrace();
                inItRefreshView();
            }

        });
    }

    /**
     * 删除workId不存在的本地数据
     */
    private void deleteNullInfo() {
        ArrayList<String> workIDs = newMsgDB.getAllWorkID(workusername);
        Log.d("new message workid--->", workIDs.toString());
        Executelist[] executelists = worksBean.getAllworknumlist();
        Iterator<String> iterator = workIDs.iterator();
        if (iterator.hasNext()) {
            String workid = iterator.next();
            for (Executelist executelist : executelists) {
                if ( workid.equals( executelist.getWorkid())) {
                    iterator.remove();
                }
            }
        }
        Log.d("new message workid--->", workIDs.toString());
        for (String workid :workIDs) {
            newMsgDB.deleteByWorkID(workid);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        //可以根据多个请求代码来作相应的操作
        if(Constant.ResultCode == resultCode) {
           // showExecutelists.clear();
            getMyWorks(true);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            setResult(Constant.ResultCode);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initRedioBtn() {
        // 如果正在执行的 没有数据
        if(0 != worksBean.getOnexecuteLists().length && checkedIdFlag==0) {
            changeWorkItems(R.id.rb_running);
            //TODO：不设置本地闹钟  提醒统一改成服务器端发push推动提醒
          //  setClock();
        } else {
            changeWorkItems(checkedIdFlag);
            if (showExecutelists.size()==0) {
                showNoTaskTip(R.id.rb_running,UpOrDown);
            }else{
                insertll.setVisibility(View.GONE);
            }
        }
    }

    private void setClock() {
        //条件
        //1.正在执行的未完成的
        //2.只有上级列表
        //3.没有超时的  （结束时间 < 当前时间）
        Log.e("getAlarmConfig-->  ",PreferenceUtils.getInstance().getAlarmConfig());

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type type = new TypeToken<List<AlarmBean>>(){}.getType();  //指定集合对象属性
        alarmBeans = gson.fromJson(PreferenceUtils.getInstance().getAlarmConfig(), type);
        for(int i=0; i<showExecutelists.size(); i++) {
            Log.e("WorkItemsDetailActivity",""+TimeUtil.getMilliSecond(showExecutelists.get(i).getEndtime().replace("/","-"),showExecutelists.get(i).getCurrenttime().replace("/","-")));
            //如果这个Workid，在对象中是不存在的 这添加进去 ，Soleid 在最后一个基础上+1
            //如果 之前添加的信息 已经被删除，要更新 闹钟的配置信息
            updateConfig();
            if(!isExist(showExecutelists.get(i).getWorkid())){
                int index;
                if(alarmBeans.size()==0){
                    index = 0;
                } else {
                    index = alarmBeans.get(alarmBeans.size()-1).getSoleid()+1;
                }
                AlarmBean alarmBean = new AlarmBean();
                alarmBean.setSoleid(index);
                alarmBean.setWorkid(showExecutelists.get(i).getWorkid());
                alarmBean.setStatu(showExecutelists.get(i).getStatu());
                alarmBean.setCurrenttime(showExecutelists.get(i).getCurrenttime());
                alarmBean.setEndtime(showExecutelists.get(i).getEndtime());
                alarmBeans.add(alarmBean);
            }

           /* if(Constant.ZXL_UP_USER == UpOrDown && "0".equals(showExecutelists.get(i).getStatu())
                    && TimeUtil.getMilliSecond(showExecutelists.get(i).getEndtime().replace("/","-"),showExecutelists.get(i).getCurrenttime().replace("/","-")) < 0) {
                MyAlarmManager.addAlarm(WorkItemsDetailActivity.this,
                        TimeUtil.getMilliSecond(showExecutelists.get(i).getEndtime().replace("/","-")),
                        Integer.valueOf(showExecutelists.get(i).getWorkid()),
                        worksBean.getWorkusername(),
                        UpOrDown);
            }*/

          /*  //测试代码---------------------------------------------------------------
            switch (i){
                case 0:
                    MyAlarmManager.addAlarm(WorkItemsDetailActivity.this,
                            System.currentTimeMillis()+5000,
                            Integer.valueOf(showExecutelists.get(i).getWorkid()));
                    break;
                case 1:
                    MyAlarmManager.addAlarm(WorkItemsDetailActivity.this,
                            System.currentTimeMillis()+10000,
                            Integer.valueOf(showExecutelists.get(i).getWorkid()));
                    break;
                case 2:
                    MyAlarmManager.addAlarm(WorkItemsDetailActivity.this,
                            System.currentTimeMillis()+15000,
                            Integer.valueOf(showExecutelists.get(i).getWorkid()));
                    break;
            }*/

        }
        String alarmConfig = gson.toJson(alarmBeans, type);
        Log.e("getAlarmConfig-->  ",alarmConfig+"  "+alarmBeans.get(0).getWorkid());
        PreferenceUtils.getInstance().setAlarmConfig(alarmConfig);

        for(int i=0; i<alarmBeans.size(); i++){
            if(Constant.ZXL_UP_USER == UpOrDown && "0".equals(alarmBeans.get(i).getStatu())
                    && TimeUtil.getMilliSecond(alarmBeans.get(i).getEndtime().replace("/","-"),alarmBeans.get(i).getCurrenttime().replace("/","-")) < 0) {
                MyAlarmManager.addAlarm(WorkItemsDetailActivity.this,
                        TimeUtil.getMilliSecond(alarmBeans.get(i).getEndtime().replace("/","-")),
                        alarmBeans.get(i).getSoleid(),
                        worksBean.getWorkusername(),
                        UpOrDown);
            }
        }

    }

    private void updateConfig() {

        boolean isdelete ;
        for(int i=0; i<alarmBeans.size(); i++){
            isdelete = false;
            for(int j=0; j<showExecutelists.size(); j++){
                Log.e("getAlarmConfig-->  ",alarmBeans.get(i).getWorkid()+"  "+showExecutelists.get(j).getWorkid());

                if(alarmBeans.get(i).getWorkid().equals(showExecutelists.get(j).getWorkid())){
                    isdelete = true;
                    break;
                }
            }
            if(!isdelete){
                MyAlarmManager.cancleAlarm(this,alarmBeans.get(i).getSoleid());
                alarmBeans.remove(i);
            }
        }
    }

    private boolean isExist(String workid) {
        for(int i=0; i<alarmBeans.size(); i++){
            if(workid.equals(alarmBeans.get(i).getWorkid())){
                return true;
            }
        }
        return false;
    }

    private void inItRefreshView(){
        findViewById(R.id.loading_bar_rl).setVisibility(View.GONE);

        mPullToRefreshView.onHeaderRefreshComplete(TimeUtil.getRefreshTimeStr());
        mPullToRefreshView.onFooterRefreshComplete();
        mPullToRefreshView.onHeaderRefreshComplete();
    }



    @Override
    public void onHeaderRefresh(PullToRefreshView view) {

        getMyWorks(false);
    }

    class MyRadioGroupOnCheckedChangedListener implements MyRadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(MyRadioGroup group, int checkedId) {
            changeWorkItems(checkedId);

        }

    }

    private void changeWorkItems(int checkedId) {
        showExecutelists.clear();
        // if(!"0".equals(worksBean.getWorknum())) {
        switch (checkedId) {
            case R.id.rb_running:
                checkedIdFlag = R.id.rb_running;
                if (worksBean.getOnexecuteLists()!=null) {
                    for (int i = 0; i < worksBean.getOnexecuteLists().length; i++) {
                        showExecutelists.add(worksBean.getOnexecuteLists()[i]);
                    }
                }
                if (showExecutelists.size()==0) {
                    showNoTaskTip(R.id.rb_running,UpOrDown);
                }else{
                    insertll.setVisibility(View.GONE);
                }
                tv_running.setTextColor(getResources().getColor(R.color.main_blue));
                tv_title1.setTextColor(getResources().getColor(R.color.main_blue));

                tv_timeout.setTextColor(getResources().getColor(R.color.gray));
                tv_title2.setTextColor(getResources().getColor(R.color.gray));

                tv_ontime.setTextColor(getResources().getColor(R.color.gray));
                tv_title3.setTextColor(getResources().getColor(R.color.gray));

                tv_timeout_complete.setTextColor(getResources().getColor(R.color.gray));
                tv_title4.setTextColor(getResources().getColor(R.color.gray));

                tv_all_count.setTextColor(getResources().getColor(R.color.gray));
                tv_title5.setTextColor(getResources().getColor(R.color.gray));

                break;
            case R.id.rb_timeout://超时
                checkedIdFlag = R.id.rb_timeout;

                if (worksBean.getOvertimenumList()!=null) {
                    for (int i = 0; i < worksBean.getOvertimenumList().length; i++) {
                        showExecutelists.add(worksBean.getOvertimenumList()[i]);
                    }
                }
                if (showExecutelists.size()==0) {
                    showNoTaskTip(R.id.rb_timeout, UpOrDown);
                }else{
                    insertll.setVisibility(View.GONE);
                }
                tv_timeout.setTextColor(getResources().getColor(R.color.main_blue));
                tv_title2.setTextColor(getResources().getColor(R.color.main_blue));

                tv_running.setTextColor(getResources().getColor(R.color.gray));
                tv_title1.setTextColor(getResources().getColor(R.color.gray));

                tv_ontime.setTextColor(getResources().getColor(R.color.gray));
                tv_title3.setTextColor(getResources().getColor(R.color.gray));

                tv_timeout_complete.setTextColor(getResources().getColor(R.color.gray));
                tv_title4.setTextColor(getResources().getColor(R.color.gray));

                tv_all_count.setTextColor(getResources().getColor(R.color.gray));
                tv_title5.setTextColor(getResources().getColor(R.color.gray));
                break;
            case R.id.rb_ontime://完成
                checkedIdFlag = R.id.rb_ontime;
                if (worksBean.getWorkOknumlist()!=null) {
                    for (int i = 0; i < worksBean.getWorkOknumlist().length; i++) {
                        showExecutelists.add(worksBean.getWorkOknumlist()[i]);
                    }
                }
                if (showExecutelists.size()==0) {
                    showNoTaskTip(R.id.rb_ontime, UpOrDown);
                }else{
                    insertll.setVisibility(View.GONE);
                }
                tv_ontime.setTextColor(getResources().getColor(R.color.main_blue));
                tv_title3.setTextColor(getResources().getColor(R.color.main_blue));

                tv_running.setTextColor(getResources().getColor(R.color.gray));
                tv_title1.setTextColor(getResources().getColor(R.color.gray));

                tv_timeout.setTextColor(getResources().getColor(R.color.gray));
                tv_title2.setTextColor(getResources().getColor(R.color.gray));

                tv_timeout_complete.setTextColor(getResources().getColor(R.color.gray));
                tv_title4.setTextColor(getResources().getColor(R.color.gray));

                tv_all_count.setTextColor(getResources().getColor(R.color.gray));
                tv_title5.setTextColor(getResources().getColor(R.color.gray));
                break;
            case R.id.rb_timeout_complete://总数
                checkedIdFlag = R.id.rb_timeout_complete;
                if (worksBean.getAllworknumlist()!=null) {
                    for (int i = 0; i < worksBean.getAllworknumlist().length; i++) {
                        showExecutelists.add(worksBean.getAllworknumlist()[i]);
                    }
                }
                if (showExecutelists.size()==0) {
                    showNoTaskTip(R.id.rb_timeout_complete, UpOrDown);
                }else{
                    insertll.setVisibility(View.GONE);
                }
                tv_timeout_complete.setTextColor(getResources().getColor(R.color.main_blue));
                tv_title4.setTextColor(getResources().getColor(R.color.main_blue));

                tv_running.setTextColor(getResources().getColor(R.color.gray));
                tv_title1.setTextColor(getResources().getColor(R.color.gray));

                tv_timeout.setTextColor(getResources().getColor(R.color.gray));
                tv_title2.setTextColor(getResources().getColor(R.color.gray));

                tv_ontime.setTextColor(getResources().getColor(R.color.gray));
                tv_title3.setTextColor(getResources().getColor(R.color.gray));

                tv_all_count.setTextColor(getResources().getColor(R.color.gray));
                tv_title5.setTextColor(getResources().getColor(R.color.gray));

                break;
            case R.id.rb_all://这里改成未汇报
                checkedIdFlag = R.id.rb_all;

                if (worksBean.getAllnodayreportworknumlist()!=null) {
                    for (int i = 0; i < worksBean.getAllnodayreportworknumlist().length; i++) {
                        showExecutelists.add(worksBean.getAllnodayreportworknumlist()[i]);
                    }
                }

                if (showExecutelists.size()==0) {
                    showNoTaskTip(R.id.rb_all, UpOrDown);
                }else{
                    insertll.setVisibility(View.GONE);
                }
                tv_all_count.setTextColor(getResources().getColor(R.color.main_blue));
                tv_title5.setTextColor(getResources().getColor(R.color.main_blue));

                tv_running.setTextColor(getResources().getColor(R.color.gray));
                tv_title1.setTextColor(getResources().getColor(R.color.gray));

                tv_timeout.setTextColor(getResources().getColor(R.color.gray));
                tv_title2.setTextColor(getResources().getColor(R.color.gray));

                tv_ontime.setTextColor(getResources().getColor(R.color.gray));
                tv_title3.setTextColor(getResources().getColor(R.color.gray));

                tv_timeout_complete.setTextColor(getResources().getColor(R.color.gray));
                tv_title4.setTextColor(getResources().getColor(R.color.gray));

                break;

        }
        // }
        workDetailAdapter.notifyDataSetChanged();
    }


    private void showNoTaskTip(int checkedId, int upOrDown) {
        switch (checkedId) {
            case R.id.rb_running:
                 insertll.setVisibility(View.VISIBLE);
                tv_addtask_tip.setText("目前没有执行中的任务");
                if (Constant.ZXL_DOWN_USER==upOrDown) {
                    btn_addtask.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.rb_timeout:
                insertll.setVisibility(View.VISIBLE);
                tv_addtask_tip.setText("目前没有超时的任务");
                btn_addtask.setVisibility(View.GONE);
                break;
            case R.id.rb_ontime:
                insertll.setVisibility(View.VISIBLE);
                tv_addtask_tip.setText("目前没有完成的任务");
                btn_addtask.setVisibility(View.GONE);
                break;
            case R.id.rb_timeout_complete:
                insertll.setVisibility(View.VISIBLE);
                tv_addtask_tip.setText("目前没有任务");
                if (Constant.ZXL_DOWN_USER==upOrDown) {
                    btn_addtask.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.rb_all:
                insertll.setVisibility(View.VISIBLE);
                tv_addtask_tip.setText("目前没有未汇报任务");
                btn_addtask.setVisibility(View.GONE);
                break;
        }
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

}
