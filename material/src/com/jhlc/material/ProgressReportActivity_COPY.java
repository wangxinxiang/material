package com.jhlc.material;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import com.google.gson.GsonBuilder;
import com.jhlc.material.adapter.ChatMsgEntity;
import com.jhlc.material.adapter.ChatMsgViewAdapter;
import com.jhlc.material.bean.PostBackDataBean;
import com.jhlc.material.bean.ReportDetailBean;
import com.jhlc.material.bean.WorkDetailList;
import com.jhlc.material.utils.*;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ProgressReportActivity_COPY extends Activity{
    private LinearLayout ll_upuser_report,ll_downuser_operate;

    private String    workID,message,reportname,headimage;
    private EditText  et_progress_report;
    private TextView  tv_which_task;
    private ImageButton ibtn_expression,rl_confirm_complete,rl_delete_task;
    private int UpOrDown;

    private ListView mListView;
    private GridView gridview;
    private ChatMsgViewAdapter mAdapter;
    private List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>();
    private LinkedList<WorkDetailList> workDetailLists = new LinkedList<WorkDetailList>();
    private ArrayList<WorkDetailList> autoDetailLists = new ArrayList<WorkDetailList>();

    private ReportDetailBean reportDetailBean;
    private Intent intent;

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
    }

    private void initData( ) {
        for(int i = 0; i < autoDetailLists.size() + workDetailLists.size(); i++) {

            if(i < autoDetailLists.size()) {
                ChatMsgEntity autoentity = new ChatMsgEntity();
                autoentity.setDate(autoDetailLists.get(i).getReporttime());
                autoentity.setText(autoDetailLists.get(i).getReportmessage());
                autoentity.setName("");
                autoentity.setMsgType(true);
                mDataArrays.add(autoentity);
            }
            if(i < workDetailLists.size()) {
                ChatMsgEntity entity = new ChatMsgEntity();
                entity.setDate(workDetailLists.get(i).getReporttime());
                entity.setText(workDetailLists.get(i).getReportmessage());
                entity.setName("");
                entity.setMsgType(false);
                mDataArrays.add(entity);
            }

        }

    }

    private void initView() {
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

//      mAdapter = new ChatMsgViewAdapter(ProgressReportActivity_COPY.this, mDataArrays,UpOrDown,headimage);
        mListView.setAdapter(mAdapter);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        UpOrDown      = intent.getIntExtra("UpOrDown",-1);
        workID        = intent.getStringExtra("workID");
        reportname    = intent.getStringExtra("receptname");
        headimage     = intent.getStringExtra("imageurl");
        LogZ.d("getintent", workID + "  " + reportname + "  " + headimage);
    }

    private void setListener() {
        findViewById(R.id.rl_close_actvity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(100);
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
                intent = new Intent(ProgressReportActivity_COPY.this,NoticeDialog.class);
                intent.putExtra("optype",1);
                intent.putExtra("workID",workID);
                intent.putExtra("notic_content","是否确认完成？");
                startActivityForResult(intent,111);

            }
        });

        rl_delete_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(ProgressReportActivity_COPY.this,NoticeDialog.class);
                intent.putExtra("optype",2);
                intent.putExtra("workID",workID);
                intent.putExtra("notic_content","是否确认删除？");
                startActivityForResult(intent,111);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        int optype = data.getIntExtra("optype",-1);
        //可以根据多个请求代码来作相应的操作
        if(100 == resultCode) {
            if(optype == 1) {
                getWorkChat();          //操作之后刷新内容列表
                ZXLApplication.getInstance().showTextToast("操作成功");
            } else if(optype == 2){
                ZXLApplication.getInstance().showTextToast("删除成功");
                setResult(100);
                finish();
            }
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
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                LogZ.d("ProgressReportActivity--> ",new String(responseBody));
                mDataArrays.clear();
                workDetailLists.clear();
                autoDetailLists.clear();
                // 设置值
                reportDetailBean = new GsonBuilder().create().fromJson(new String(responseBody), ReportDetailBean.class);
                if(reportDetailBean == null || reportDetailBean.getCode() == -1) return;
                for (int i=0; i< reportDetailBean.getWorkDetailLists().length; i++){
                    workDetailLists.add(reportDetailBean.getWorkDetailLists()[i]);
                }
                // 更新UI
                showUI();

                // 设置自动回复的那个列表
                setAutoDetailLists();
                //把数据组装为 List<ChatMsgEntity> mDataArrays
                initData();

                mAdapter.setCurrenttime(reportDetailBean.getCurrenttime());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                // 打印错误信息
                error.printStackTrace();
            }

        });
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
    }

    private void setAutoDetailLists(){

        for(int i=0; i<= workDetailLists.size(); i++) {
            if(i==0){
                String enddays = reportDetailBean.getEnddays();
                WorkDetailList workDetailList = new WorkDetailList();
                workDetailList.setReporttime(reportDetailBean.getStarttime());
                workDetailList.setReportmessage(enddays.substring(0,enddays.length()-3)+"天内完成");
                workDetailLists.addFirst(workDetailList);
            }

            WorkDetailList workDetailList = new WorkDetailList();
            switch (i){
                case 0:
                    workDetailList.setReporttime(reportDetailBean.getStarttime());
                    workDetailList.setReportmessage(reportDetailBean.getWorktitle());
                    break;
                case 1:
//                  workDetailList.setReporttime(reportDetailBean.getCurrenttime());
                    workDetailList.setReportmessage("请你每天晚上12点前向我汇报！");
                    break;
                default:
                    //如果已经完成
                    if(i == workDetailLists.size() && !("".equals(reportDetailBean.getCompletetime()))){
//                        workDetailList.setReporttime(reportDetailBean.getCurrenttime());
                        workDetailList.setReportmessage("圆满完成任务，你辛苦了！");
                    } else {
                        String lastReporttime = reportDetailBean.getWorkDetailLists()[reportDetailBean.getWorkDetailLists().length-1].getReporttime();
                        LogZ.d("lastReporttime-->  ",lastReporttime);
                        //  今天18点的毫秒数 - 上次汇报的时间毫秒数 > 18*60*60*1000
                        if(i == workDetailLists.size() && !("".equals(lastReporttime)) && null != lastReporttime
                                && TimeUtil.getDataDiff18(lastReporttime.replace("/","-"),reportDetailBean.getCurrenttime())) {
//                            workDetailList.setReporttime(reportDetailBean.getCurrenttime());
                            workDetailList.setReportmessage(reportDetailBean.getWorkusername()+"你今天没有向我汇报工作进度，请立即向我汇报工作进展！");
                        } else {
//                            workDetailList.setReporttime(reportDetailBean.getCurrenttime());
                            String[] timearr = TimeUtil.getDataDiff(reportDetailBean.getCurrenttime().replace("/", "-"), reportDetailBean.getEndtime().replace("/", "-")).split("-");
                            if(null != timearr[0] && !("".equals(timearr[0])) &&Integer.valueOf(timearr[0]) == 0){
                                workDetailList.setReportmessage("您还剩余"+ timearr[1] + "小时，加油哦！");
                            } else {
                                workDetailList.setReportmessage("您还剩余" + timearr[0] + "天" + timearr[1] + "小时，加油哦！");
                            }
                        }
                    }
                    break;
            }

            autoDetailLists.add(workDetailList);
        }

        //如果是下级这 不要自动回复列表
        if(Constant.ZXL_DOWN_USER == UpOrDown) {
            autoDetailLists.clear();
            WorkDetailList workDetailList = new WorkDetailList();
            workDetailList.setReporttime(reportDetailBean.getStarttime());
            workDetailList.setReportmessage(reportDetailBean.getWorktitle());
            autoDetailLists.add(workDetailList);
        }
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
                mDataArrays.clear();
                workDetailLists.clear();
                autoDetailLists.clear();
                // 设置值
                PostBackDataBean postBackDataBean = new GsonBuilder().create().fromJson(new String(responseBody), PostBackDataBean.class);
                if(postBackDataBean.getCode() == 100){
                    ZXLApplication.getInstance().showTextToast("发送成功");
                    et_progress_report.setText("");
                    getWorkChat();
                } else if(-1 == postBackDataBean.getCode()){
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
        for(int i=0;i<14;i++){
            HashMap<String, Object> map = new HashMap<String, Object>();
            switch (i){
                case 0:
                    expression = R.drawable.expression_01;
                    break;
                case 1:
                    expression = R.drawable.expression_02;
                    break;
                case 2:
                    expression = R.drawable.expression_03;
                    break;
                case 3:
                    expression = R.drawable.expression_04;
                    break;
                case 4:
                    expression = R.drawable.expression_05;
                    break;
                case 5:
                    expression = R.drawable.expression_06;
                    break;
                case 6:
                    expression = R.drawable.expression_07;
                    break;
                case 7:
                    expression = R.drawable.expression_08;
                    break;
                case 8:
                    expression = R.drawable.expression_09;
                    break;
                case 9:
                    expression = R.drawable.expression_10;
                    break;
                case 10:
                    expression = R.drawable.expression_11;
                    break;
                case 11:
                    expression = R.drawable.expression_12;
                    break;
                case 12:
                    expression = R.drawable.expression_13;
                    break;
                case 13:
                    expression = R.drawable.expression_14;
                    break;
            }
            map.put("ItemImage", expression);//添加图像资源的ID
            map.put("ItemText", "e_"+String.valueOf(i));//按序号做ItemText
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

}
