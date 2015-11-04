package com.jhlc.material;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import com.google.gson.GsonBuilder;
import com.jhlc.material.adapter.ChatMsgViewAdapter;
import com.jhlc.material.bean.NewReportMessage;
import com.jhlc.material.bean.PostBackDataBean;
import com.jhlc.material.bean.ReportDetailBean;
import com.jhlc.material.bean.WorkDetailList;
import com.jhlc.material.db.ProgressReportDB;
import com.jhlc.material.utils.*;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * 重新刷新progressReport，不再直接读取
 */
public class RefreshProgressReportActivity extends Activity{
    private LinearLayout ll_upuser_report,ll_downuser_operate;

    private String    workID,message,reportname,headimage;
    private EditText  et_progress_report;
    private TextView  tv_which_task;
    private ImageButton ibtn_expression,rl_confirm_complete,rl_delete_task;
    private CheckBox ck_everyday_report;
    private int UpOrDown;

    private ListView mListView;
    private GridView gridview;
    private ChatMsgViewAdapter mAdapter;
    private LinkedList<WorkDetailList> workDetailLists = new LinkedList<WorkDetailList>();
    private LinkedList<WorkDetailList> workDetailListsDB = new LinkedList<WorkDetailList>();

    private ReportDetailBean reportDetailBean;
    private Intent intent;
    private ProgressReportDB progressReportDB = new ProgressReportDB(this);

    private ProgressDialog pd;

    private Timer timer = new Timer();
    //用于处理刷新消息的handler
    Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if(ZXLApplication.getInstance().isconnected()){
                        //当前登录用户名不为空才去获得请求
                        if(!PreferenceUtils.getInstance().getUserName().equals("")){
                            getNewReportMsg();
                        }

                    }
                    break;
            }
            super.handleMessage(msg);
        }

    };

    private void startGetNewReportMsg() {
        TimerTask task = new TimerTask() {
            public void run() {
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        };
        timer.schedule(task, 0, 3000);

    }
    private void getNewReportMsg() {

        ZXLApplication.getInstance().showTextToast("开始刷新");
        // 发送请求到服务器
        AsyncHttpClient client = new AsyncHttpClient();
        // 创建请求参数
        RequestParams params = new RequestParams();
        params.put("opcode", "getnewreportmessage");
        params.put("workid", workID);
        params.put("typeid", UpOrDown);
        params.put("eid", Constant.EID);

        // 执行post方法
        client.post(MYURL.URL_HEAD, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //标示 上下级
                int  UpOrDown = -1;
                //标示 任务的姓名信息
                StringBuilder sb_newmsg = new StringBuilder("");
                //报销的姓名信息
                StringBuilder sb_paynewmsg = new StringBuilder("");

                LogZ.d("RefreshProgressReportActivity",new String(responseBody));
                NewReportMessage newMessageBean = new GsonBuilder().create().fromJson(new String(responseBody), NewReportMessage.class);

                if(newMessageBean.getList()!=null&&newMessageBean.getList().length>0){
                    for (int i = 0; i < newMessageBean.getList().length; i++) {
                        WorkDetailList workDetailList = newMessageBean.getList()[i];
                        workDetailLists.add(workDetailList);
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // 打印错误信息
                error.printStackTrace();
            }

        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress_report_dialog);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getIntentData();
        getWorkChat();
        initView();
        setListener();
        AddExpression();

//        /**
//         *  注册广播： 用于接收新消息出发，
//         * */
//        IntentFilter filter = new IntentFilter(Constant.WorkItemsNewMsgAction);
//        registerReceiver(broadcastReceiver, filter);

    }

    //监听到有 新消息 则刷新列表 进行未读标识
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // 查询新来的消息 的任务ID 是否在本界面
            String workIdstr = intent.getStringExtra("workId");
           // ZXLApplication.getInstance().showTextToast("已经获得广播："+workIdstr+"|"+reportDetailBean.getWorkid());
            if(workIdstr!=null&& workIdstr.contains(reportDetailBean.getWorkid())){
                getWorkChat();
            }
        }
    };

    private void initView() {
        ck_everyday_report= (CheckBox) findViewById(R.id.ck_everyday_report);
        ll_upuser_report    = (LinearLayout) findViewById(R.id.ll_upuser_report);
        ll_downuser_operate = (LinearLayout) findViewById(R.id.ll_downuser_operate);

        ibtn_expression = (ImageButton) findViewById(R.id.ibtn_expression);
        rl_confirm_complete = (ImageButton) findViewById(R.id.rl_confirm_complete);
        rl_delete_task      = (ImageButton) findViewById(R.id.rl_delete_task);

        et_progress_report  = (EditText) findViewById(R.id.et_progress_report);
        et_progress_report.requestFocus();
        et_progress_report.setSelection(0);
        tv_which_task       = (TextView) findViewById(R.id.tv_which_task);

        mListView = (ListView) findViewById(R.id.listview);
        mAdapter = new ChatMsgViewAdapter(RefreshProgressReportActivity.this, workDetailLists,UpOrDown,headimage);
        mListView.setAdapter(mAdapter);




//      mAdapter = new ChatMsgViewAdapter(ProgressReportActivity.this, workDetailLists,UpOrDown,headimage);
//      mListView.setAdapter(mAdapter);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        UpOrDown      = intent.getIntExtra("UpOrDown",-1);
        workID        = intent.getStringExtra("workID");
        reportname    = intent.getStringExtra("receptname");
        headimage     = intent.getStringExtra("imageurl");
        LogZ.d("getintent",workID+"  "+reportname+"  "+headimage);
        if(Constant.ZXL_DOWN_USER == UpOrDown){//下级界面不显示提交完成按钮
            findViewById(R.id.ibtn_submit_edit).setVisibility(View.GONE);
            //看我的下属界面要显示日报开关
            findViewById(R.id.tv_switch).setVisibility(View.VISIBLE);
            findViewById(R.id.ck_everyday_report).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.ibtn_submit_edit).setVisibility(View.VISIBLE);
            //看我的上级界面不要显示日报开关
            findViewById(R.id.tv_switch).setVisibility(View.GONE);
            findViewById(R.id.ck_everyday_report).setVisibility(View.GONE);
        }

    }

    private void setListener() {
        //给拍照添加动作
        findViewById(R.id.ib_take_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RefreshProgressReportActivity.this, SettingAddHeadImageActivity.class);
                intent.putExtra(Constant.uploadPic, Constant.uploadPic);
                startActivityForResult(intent, Constant.progressReport_upload);
            }
        });

        findViewById(R.id.rl_close_actvity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Constant.ResultCode);
                finish();
            }
        });

        findViewById(R.id.rl_confirm_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = et_progress_report.getText().toString().trim();
                if(null != message &&  !("".equals(message))) {
                    progressReport(message);
                } else {
                    ZXLApplication.getInstance().showTextToast("汇报内容不能为空！");
                }
            }
        });

        findViewById(R.id.ibtn_submit_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RefreshProgressReportActivity.this);
                builder.setMessage("是否申请完成任务？");
                builder.setTitle("提示");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        //    ZXLApplication.getInstance().showTextToast("确认任务完成：optype"+optype+"workid:"+workid);
                        requestFinish(workID);

                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });

        ibtn_expression.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(View.VISIBLE == gridview.getVisibility()){
                    gridview.setVisibility(View.GONE);
                } else {
                    gridview.setVisibility(View.VISIBLE);
                }

            }
        });

        rl_confirm_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                intent = new Intent(ProgressReportActivity.this,NoticeDialog.class);
//                intent.putExtra("optype",1);
//                intent.putExtra("workID",workID);
//                intent.putExtra("notic_content","是否确认完成？");
               // startActivityForResult(intent,Constant.RequestCode);
                dialog("本任务是否确认完成?",1,workID);


            }
        });

        rl_delete_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                intent = new Intent(ProgressReportActivity.this,NoticeDialog.class);
//                intent.putExtra("optype",2);
//                intent.putExtra("workID",workID);
//                intent.putExtra("notic_content","是否确认删除？");
//                startActivityForResult(intent,Constant.RequestCode);
                dialog("是否确认删除该任务?",2,workID);

            }
        });

        findViewById(R.id.ib_confirm_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                intent = new Intent(ProgressReportActivity.this,NoticeDialog.class);
//                intent.putExtra("optype",2);
//                intent.putExtra("workID",workID);
//                intent.putExtra("notic_content","是否确认删除？");
//                startActivityForResult(intent,Constant.RequestCode);
                dialog("是否拒绝完成任务申请?", 3, workID);

            }
        });
        //不在这里处理onchange逻辑，否则，网络加载成功后会处罚onchange逻辑
        /*ck_everyday_report.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    sw_dialog("是否开启每日汇报?",4,workID,"1");
                }else{
                    sw_dialog("是否关闭每日汇报?",4,workID, "0");
                }
            }
        });*/

    }

    //申请完成任务
    private void requestFinish(String workID) {
        // 发送请求到服务器
        AsyncHttpClient client = new AsyncHttpClient();
        // 创建请求参数
        RequestParams params = new RequestParams();
        params.put("opcode", "apply_work_complete");
        params.put("Username", PreferenceUtils.getInstance().getUserName());
        params.put("eid", Constant.EID);
        params.put("workid", workID);
        LogZ.d("workID：" , workID);
        // 执行post方法
        client.post(MYURL.URL_HEAD, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                pd = ProgressDialog.show(RefreshProgressReportActivity.this, "正在提交数据,请稍后...", "请等待", true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // 设置值
                LogZ.d("getUpUserData-->upUserOpt ", new String(responseBody));
                PostBackDataBean postBackDataBean = new GsonBuilder().create().fromJson(new String(responseBody), PostBackDataBean.class);
                if (pd!=null&&pd.isShowing()) {
                    pd.dismiss();
                }
                if(postBackDataBean.getCode() == 100){
//                    Intent intent=new Intent();
//                    intent.putExtra("optype", optype);
//                    setResult(100, intent);
                    if ("apply_work_complete_success".equals(postBackDataBean.getOpcode())) {
                        ZXLApplication.getInstance().showTextToast("已经申请完成任务！");
                        finish();
                    }

                    if ("userName_notexist".equals(postBackDataBean.getOpcode())) {
                        ZXLApplication.getInstance().showTextToast("申请失败：用户不存在！");
                    }

                    if ("work_notexist".equals(postBackDataBean.getOpcode())) {
                        ZXLApplication.getInstance().showTextToast("申请失败：工作记录不存在！");
                    }

                }else{
                    ZXLApplication.getInstance().showTextToast("任务删除异常，参数不正确");
                    LogZ.d("lyjtest",postBackDataBean.getMsg());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // 打印错误信息
                error.printStackTrace();
                if (pd!=null&&pd.isShowing()) {
                    pd.dismiss();
                }
            }

        });
    }

    protected void dialog(String dialogMsg,final int optype, final String workid) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(dialogMsg);
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            //    ZXLApplication.getInstance().showTextToast("确认任务完成：optype"+optype+"workid:"+workid);
                upUserOpt(optype, workid, null);

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * 每日汇报开关 的change时间
     * @param dialogMsg
     * @param optype
     * @param workid
     * @param dayreport
     */
    protected void sw_dialog(String dialogMsg, final int optype, final String workid,final String dayreport) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(dialogMsg);
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            //    ZXLApplication.getInstance().showTextToast("确认任务完成：optype"+optype+"workid:"+workid);
                    upUserOpt(optype, workid, dayreport);

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ck_everyday_report.setOnCheckedChangeListener(null);
                if(ck_everyday_report.isChecked()){
                    ck_everyday_report.setChecked(false);
                }else{
                    ck_everyday_report.setChecked(true);
                }
                setCk_everyday_report_listener();
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


    /**
     *  当用户是上级时可以对下级任务进行
     *  1.确认完成操作
     *  2.删除操作
     * */
    private void upUserOpt(final int optype, String workid, String dayreport) {
        // 发送请求到服务器
        AsyncHttpClient client = new AsyncHttpClient();
        // 创建请求参数
        RequestParams params = new RequestParams();
        params.put("opcode", "opt_work");
        params.put("Username", PreferenceUtils.getInstance().getUserName());
        params.put("eid", Constant.EID);

        params.put("workid", workid);
        params.put("optype", optype);
        params.put("DayReport", dayreport);

        // 执行post方法
        client.post(MYURL.URL_HEAD, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                pd = ProgressDialog.show(RefreshProgressReportActivity.this, "正在提交数据,请稍后...", "请等待", true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // 设置值
                LogZ.d("getUpUserData-->upUserOpt ", new String(responseBody));
                PostBackDataBean postBackDataBean = new GsonBuilder().create().fromJson(new String(responseBody), PostBackDataBean.class);
                if (pd!=null&&pd.isShowing()) {
                    pd.dismiss();
                }
                if(postBackDataBean.getCode() == 100){
//                    Intent intent=new Intent();
//                    intent.putExtra("optype", optype);
//                    setResult(100, intent);
                    if (1==optype) {
                        ZXLApplication.getInstance().showTextToast("任务完成！");
                        finish();
                    }

                    if (2==optype) {
                        ZXLApplication.getInstance().showTextToast("任务删除！");
                        finish();
                    }

                    if (3==optype) {
                        ZXLApplication.getInstance().showTextToast("拒绝申请！");
                        getWorkChat();
                    }

                    if (4==optype) {//注意设置每日汇报完成界面不应该关闭界面
                        ZXLApplication.getInstance().showTextToast("每日汇报设置完成！");
                        getWorkChat();
                    }

                }else{
                    ZXLApplication.getInstance().showTextToast("任务删除异常，参数不正确");
                    LogZ.d("lyjtest",postBackDataBean.getMsg());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // 打印错误信息
                error.printStackTrace();
                if (pd!=null&&pd.isShowing()) {
                    pd.dismiss();
                }
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        int optype = 0;
        if (data!=null) {
            optype = data.getIntExtra("optype",-1);
        }
        //可以根据多个请求代码来作相应的操作
        if(Constant.ResultCode == resultCode) {
            if(optype == 1) {
                getWorkChat();          //操作之后刷新内容列表
                ZXLApplication.getInstance().showTextToast("操作成功");
            } else if(optype == 2){
                ZXLApplication.getInstance().showTextToast("删除成功");
                setResult(Constant.ResultCode);
                finish();
            }
        }
        if (Constant.progressReport_upload == resultCode) {

            // ZXLApplication.getInstance().showTextToast("开始准备发送图片"+(Constant.progressReport_upload == resultCode));
            progressPhotoReport();
          /*  isRegister = true;
            setHeadImage();
            LogZ.d("lyjtakephoto", "获得照片,是否应该发送请求？");

            upLoadImage();*/
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getWorkChat( ) {
        // 发送请求到服务器
        AsyncHttpClient client = new AsyncHttpClient();
        // 创建请求参数
        final RequestParams params = new RequestParams();
        params.put("opcode", "work_detail_list");
        params.put("Username", PreferenceUtils.getInstance().getUserName());
        params.put("eid", Constant.EID);
        params.put("workid", workID);
        params.put("typeid", UpOrDown);

        // 执行post方法
        client.post(MYURL.URL_HEAD, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                if (pd!=null&&!pd.isShowing()) {
                    pd = ProgressDialog.show(RefreshProgressReportActivity.this, "正在加载数据,请稍后...", "请等待", true);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                LogZ.d("ProgressReportActivity--> ",new String(responseBody));
                if (pd!=null&&pd.isShowing()) {
                    pd.dismiss();
                }
                workDetailLists.clear();
                // 设置值
                reportDetailBean = new GsonBuilder().create().fromJson(new String(responseBody), ReportDetailBean.class);
                if(reportDetailBean == null || reportDetailBean.getCode() == -1) return;
                for (int i=0; i< reportDetailBean.getWorkDetailLists().length; i++){
                    workDetailLists.add(reportDetailBean.getWorkDetailLists()[i]);
                }
                mAdapter.setCurrenttime(reportDetailBean.getCurrenttime());

                // 更新UI
                showUI();
                insertDownUserAutoData();
                if(Constant.ZXL_DOWN_USER == UpOrDown){
                    mAdapter = new ChatMsgViewAdapter(RefreshProgressReportActivity.this, workDetailLists,UpOrDown,headimage);
                    mListView.setAdapter(mAdapter);
                    mAdapter.setCurrenttime(reportDetailBean.getCurrenttime());
                    mAdapter.notifyDataSetChanged();
                    setListViewTopOrButtom(mListView);

//                    if (!mListView.isStackFromBottom()) {
//                        mListView.setStackFromBottom(true);
//                    }
//                    mListView.setStackFromBottom(false);
//                    setListViewTopOrButtom(mListView);
                } else {
                    //数据请求成功自动生成一条语句,插入到数据库表中
                    insertDBAutoData();
    //              setAutoDetailLists();
    //              progressReportDB.addWorkDetailList(reportDetailBean.getWorkid(),workDetailLists);
                    new ProgressBarAsyncTask().execute();
                }
                if (pd!=null&&pd.isShowing()) {
                    pd.dismiss();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                // 打印错误信息
                error.printStackTrace();
                if (pd!=null&&pd.isShowing()) {
                    pd.dismiss();
                }
            }

        });
    }

    private void setListViewTopOrButtom(final ListView mListView) {
        mListView.post(new Runnable() {
            public void run() {
              //  LogZ.d("lyjtest","mListView.getLastVisiblePosition()"+mListView.getLastVisiblePosition());
              //  LogZ.d("lyjtest","mListView.getChildCount()"+mListView.getChildCount());
              //  LogZ.d("lyjtest", "mListView.getAdapter().getCount()" + mListView.getAdapter().getCount());
               /* if(mListView.getAdapter().getCount()>mListView.getChildCount()){
                    mListView.setStackFromBottom(true);
                }else{
                    mListView.setStackFromBottom(false);
                }*/

                if (mListView.getAdapter().getCount()>=1) {
                    mListView.setSelection(mListView.getAdapter().getCount() - 1);
                }
            }
        });

    }

    private void insertDBAutoData() {
        boolean workover = false;
        WorkDetailList workDetailList = new WorkDetailList();
        workDetailList.setReporttypeid("1");
        workDetailList.setReporttime(reportDetailBean.getCurrenttime());

        if(!("".equals(reportDetailBean.getCompletetime()))){
            workDetailList.setReportmessage("圆满完成任务，你辛苦了！");
            workover = true;
        } else {
            String lastReporttime = workDetailLists.getLast().getReporttime();
            //  今天18点的毫秒数 - 上次汇报的时间毫秒数 > 18*60*60*1000
            if(!("".equals(lastReporttime)) && null != lastReporttime
                    && TimeUtil.getDataDiff18(lastReporttime.replace("/","-"),reportDetailBean.getCurrenttime())) {
                workDetailList.setReportmessage(reportDetailBean.getWorkusername()+"你今天没有向我汇报工作进度，请立即向我汇报工作进展！");
            } else {
                long[] timearr = TimeUtil.getDaysHoursBetween(reportDetailBean.getCurrenttime().replace("/", "-"), reportDetailBean.getEndtime().replace("/", "-"));
                if(timearr[0]>0){
                    workDetailList.setReportmessage("你还剩余" + timearr[0] + "天" + timearr[1] + "小时，加油哦！");
                }else if(timearr[0]==0){
                    if (timearr[1]>0) {
                        workDetailList.setReportmessage("你还剩余"+ timearr[1] + "小时，加油哦！");
                    }else{
                        workDetailList.setReportmessage("你已经超时"+ Math.abs(timearr[1]) + "小时！");
                    }
                }else {
                    workDetailList.setReportmessage("你已经超时" +Math.abs( timearr[0] )+ "天" + Math.abs(timearr[1]) + "小时！");
                }
               /* if(null != timearr[0] && !("".equals(timearr[0])) &&Integer.valueOf(timearr[0]) == 0){
                    workDetailList.setReportmessage("你还剩余"+ timearr[1] + "小时，加油哦！");
                } else {
                    workDetailList.setReportmessage("你还剩余" + timearr[0] + "天" + timearr[1] + "小时，加油哦！");
                }*/
            }
        }
        progressReportDB.addWorkDetailList(reportDetailBean.getWorkid(),workDetailList,workover);

    }


    private void insertWorkDetailWithDBRecords() {
        //从后台取出的数据 workDetailLists
        // 数据库中自动插入数据 workDetailListsDB

        for (Iterator<WorkDetailList> iterator = workDetailListsDB.iterator(); iterator.hasNext(); ) {
            WorkDetailList dbdetail = iterator.next();
            insertWorkDetailLists(dbdetail,workDetailLists);
        }

    }

    private void insertWorkDetailLists(WorkDetailList dbdetail, LinkedList<WorkDetailList> workDetailLists) {
        int i=0;
        int result=0;
        for (Iterator<WorkDetailList> iterator = workDetailLists.iterator(); iterator.hasNext(); ) {
            WorkDetailList detail = iterator.next();
            if(TimeUtil.compareTime(dbdetail.getReporttime(),detail.getReporttime())){
                result=i;
            }else{
                break;
            }
            i++;
        }
        workDetailLists.add(result + 1, dbdetail);
    }

    class ProgressBarAsyncTask extends AsyncTask<Integer, Integer, String> {

        public ProgressBarAsyncTask() {
            super();
        }

        @Override
        protected String doInBackground(Integer... params) {
            workDetailListsDB = progressReportDB.getWorkDetail(reportDetailBean.getWorkid());

            return "";
        }

        /**
         * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）
         * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置
         */
        @Override
        protected void onPostExecute(String result) {
            // 合并数据
            insertWorkDetailWithDBRecords();

            mAdapter = new ChatMsgViewAdapter(RefreshProgressReportActivity.this, workDetailLists,UpOrDown,headimage);
            mListView.setAdapter(mAdapter);
            mAdapter.setCurrenttime(reportDetailBean.getCurrenttime());
            mAdapter.notifyDataSetChanged();
            setListViewTopOrButtom(mListView);
            startGetNewReportMsg();
        }
    }

    private void insertDownUserAutoData() {
        //workDetailLists 下级时进行插入2条数据
        if(Constant.ZXL_UP_USER == UpOrDown){
            //如果需要每日汇报才出现这句话
            if ("1".equals(reportDetailBean.getDayreport())) {
                WorkDetailList workDetail3 = new WorkDetailList();
                workDetail3.setReportmessage("请你每天晚上12点之前向我汇报工作！");
                workDetail3.setReporttime(reportDetailBean.getStarttime());
                workDetail3.setReporttypeid("1");
                workDetailLists.addFirst(workDetail3);
            }
        }

        WorkDetailList workDetail2 = new WorkDetailList();
        workDetail2.setReportmessage(reportDetailBean.getEnddays().substring(0,reportDetailBean.getEnddays().length()-3)+"天内完成");
        workDetail2.setReporttime(reportDetailBean.getStarttime());
        workDetail2.setReporttypeid("0");
        workDetailLists.addFirst(workDetail2);

        WorkDetailList workDetail1 = new WorkDetailList();
        workDetail1.setReportmessage(reportDetailBean.getWorktitle());
        workDetail1.setReporttime(reportDetailBean.getStarttime());
        workDetail1.setReporttypeid("1");
        workDetailLists.addFirst(workDetail1);
    }

    private void showUI() {
        // UI
        if(!("".equals(reportDetailBean.getCompletetime())) && Constant.ZXL_UP_USER == UpOrDown){
            ll_upuser_report.setVisibility(View.GONE);
            ll_downuser_operate.setVisibility(View.GONE);
        } else {
            if(Constant.ZXL_UP_USER == UpOrDown){
                tv_which_task.setText(reportname + "交给我的任务");
                et_progress_report.setHint("输入我的汇报内容");
                ll_upuser_report.setVisibility(View.VISIBLE);
            } else if(Constant.ZXL_DOWN_USER == UpOrDown){
                tv_which_task.setText(reportname + "的任务");
                ll_downuser_operate.setVisibility(View.VISIBLE);
            }
        }
        //完成 或者超时完成的情况 申请完成按钮要消失
        if("2".equals(reportDetailBean.getStatus())||"3".equals(reportDetailBean.getStatus())){
            Button button = (Button) findViewById(R.id.ibtn_submit_edit);
            button.setVisibility(View.GONE);
        }
        //如果已经申请了完成任务 并且任务正在进行中
        if("1".equals(reportDetailBean.getApplycomplete()) && "0".equals(reportDetailBean.getStatus())){
            Button button = (Button) findViewById(R.id.ibtn_submit_edit);
            button.setText("待审核...");
            button.setEnabled(false);

            //设置上级的操作按钮
            findViewById(R.id.rl_confirm_delete).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.rl_confirm_delete).setVisibility(View.GONE);
        }

        //初始化每日汇报开关
        if("0".equals(reportDetailBean.getDayreport())){
            ck_everyday_report.setChecked(false);
        }
        //这里设置onchange 时间可以避免上面的方法修改checked时触发onchange事件
        setCk_everyday_report_listener();
    }

    private void setCk_everyday_report_listener() {
        ck_everyday_report.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    sw_dialog("是否开启每日汇报?", 4, workID, "1");
                } else {
                    sw_dialog("是否关闭每日汇报?", 4, workID, "0");
                }
            }
        });
    }

    private void progressReport(String message) {
        // 发送请求到服务器
        AsyncHttpClient client = new AsyncHttpClient();
        // 创建请求参数
        RequestParams params = new RequestParams();
        params.put("opcode", "reportwork");
        params.put("Username", PreferenceUtils.getInstance().getUserName());
        params.put("eid", Constant.EID);
        params.put("Message", message);
        params.put("workid", workID);

        // 执行post方法
        client.post(MYURL.URL_HEAD, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                workDetailLists.clear();
                // 设置值
                PostBackDataBean postBackDataBean = new GsonBuilder().create().fromJson(new String(responseBody), PostBackDataBean.class);
                if (postBackDataBean.getCode() == 100) {
                    ZXLApplication.getInstance().showTextToast("发送成功");
                    et_progress_report.setText("");
                    getWorkChat();
                } else if (-1 == postBackDataBean.getCode()) {
                    ZXLApplication.getInstance().showTextToast("服务器错误！");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // 打印错误信息
                error.printStackTrace();

            }

        });
    }
    private String path =ImageUtils.path;
    private void progressPhotoReport() {
        File file = new File(path + Constant.testupload+".jpg");
        // 发送请求到服务器
        AsyncHttpClient client = new AsyncHttpClient();
        // 创建请求参数
        RequestParams params = new RequestParams();
        params.put("opcode", "reportwork");
        params.put("Username", PreferenceUtils.getInstance().getUserName());
        params.put("eid", Constant.EID);
        params.put("workid", workID);
        try {
            params.put("profile_picture", file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            ZXLApplication.getInstance().showTextToast("未能找到文件，上传图片失败");
            return ;
        }

        // 执行post方法
        client.post(MYURL.URL_HEAD, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                pd=ProgressDialog.show(RefreshProgressReportActivity.this, "正在提交图片,请稍后...", "请等待", true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                workDetailLists.clear();
                // 设置值
                PostBackDataBean postBackDataBean = new GsonBuilder().create().fromJson(new String(responseBody), PostBackDataBean.class);
                if(postBackDataBean.getCode() == 100){
                    ZXLApplication.getInstance().showTextToast("发送成功");
                    et_progress_report.setText("");
                    getWorkChat();
                } else if(-1 == postBackDataBean.getCode()){
                    ZXLApplication.getInstance().showTextToast("服务器错误！");
                }
                if (pd!=null&&pd.isShowing()) {
                    pd.dismiss();
                }
                getWorkChat();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // 打印错误信息
                error.printStackTrace();
                ZXLApplication.getInstance().showTextToast("上传图片失败，请稍后重试");
                if (pd!=null&&pd.isShowing()) {
                    pd.dismiss();
                }
            }

        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            setResult(100);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     *  表情View部分
     * */
    private void AddExpression(){
        gridview = (GridView) findViewById(R.id.gridview);
        //生成动态数组，并且转入数据
        ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();
        int expression = 0;
        String exprcode= "";
        for(int i=0;i<14;i++){
            HashMap<String, Object> map = new HashMap<String, Object>();
            switch (i){
                case 0:
                    expression = R.drawable.expression_01;
                    exprcode   = "[笑脸]";
                    break;
                case 1:
                    expression = R.drawable.expression_02;
                    exprcode   = "[龇牙]";
                    break;
                case 2:
                    expression = R.drawable.expression_03;
                    exprcode   = "[阴险]";
                    break;
                case 3:
                    expression = R.drawable.expression_04;
                    exprcode   = "[胜利]";
                    break;
                case 4:
                    expression = R.drawable.expression_05;
                    exprcode   = "[好的]";
                    break;
                case 5:
                    expression = R.drawable.expression_06;
                    exprcode   = "[强]";
                    break;
                case 6:
                    expression = R.drawable.expression_07;
                    exprcode   = "[握手]";
                    break;
                case 7:
                    expression = R.drawable.expression_08;
                    exprcode   = "[折磨]";
                    break;
                case 8:
                    expression = R.drawable.expression_09;
                    exprcode   = "[流汗]";
                    break;
                case 9:
                    expression = R.drawable.expression_10;
                    exprcode   = "[发怒]";
                    break;
                case 10:
                    expression = R.drawable.expression_11;
                    exprcode   = "[咒骂]";
                    break;
                case 11:
                    expression = R.drawable.expression_12;
                    exprcode   = "[菜刀]";
                    break;
                case 12:
                    expression = R.drawable.expression_14;
                    exprcode   = "[弱]";
                    break;
                case 13:
                    expression = R.drawable.expression_13;
                    exprcode   = "[敲打]";
                    break;
            }
            map.put("ItemImage", expression);//添加图像资源的ID
            map.put("ItemText",  exprcode);//按序号做ItemText
            lstImageItem.add(map);
        }
        //生成适配器的ImageItem <====> 动态数组的元素，两者一一对应
        SimpleAdapter saImageItems = new SimpleAdapter(this, //没什么解释
                lstImageItem,//数据来源
                R.layout.expression_items,//night_item的XML实现
                //动态数组与ImageItem对应的子项
                new String[] {"ItemImage"},

                //ImageItem的XML文件里面的一个ImageView,两个TextView ID
                new int[] {R.id.ItemImage});
        //添加并且显示
        gridview.setAdapter(saImageItems);
        //添加消息处理
        gridview.setOnItemClickListener(new ItemClickListener());
    }

    //当AdapterView被单击(触摸屏或者键盘)，则返回的Item单击事件
    class  ItemClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> arg0,//The AdapterView where the click happened
                                View arg1,//The view within the AdapterView that was clicked
                                int arg2,//The position of the view in the adapter
                                long arg3//The row id of the item that was clicked
        ) {
            //在本例中arg2=arg3
            HashMap<String, Object> item=(HashMap<String, Object>) arg0.getItemAtPosition(arg2);
            //显示所选Item的ItemText
            progressReport((String)item.get("ItemText"));
            gridview.setVisibility(View.GONE);
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        new  ProgressReportDB(getApplicationContext()).close();
        if (timer!=null) {
            timer.cancel();
        }

    }
}
