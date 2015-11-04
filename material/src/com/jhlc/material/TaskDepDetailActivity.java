package com.jhlc.material;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.*;
import com.android.volley.VolleyError;
import com.jhlc.material.adapter.TaskDepDetailAdapter;
import com.jhlc.material.bean.DepTaskDetail;
import com.jhlc.material.bean.DepTaskDetailProgress;
import com.jhlc.material.utils.Constant;
import com.jhlc.material.utils.MYURL;
import com.jhlc.material.utils.PreferenceUtils;
import com.jhlc.material.view.OkAndCancelDialog;
import com.jhlc.material.volley.VolleyInterface;
import com.jhlc.material.volley.VolleyRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/9/15 0015.
 */
public class TaskDepDetailActivity extends TitleActivity implements OkAndCancelDialog.OnDialogClickListener {
    private static String TAG = "TaskDepDetailActivity";

    public final static String TAG_DIALOG_SUBMIT = "TAG_DIALOG_SUBMIT";
    public final static String TAG_DIALOG_DELETE = "TAG_DIALOG_DELETE";
    public final static String TAG_DIALOG_DELETE_DETAIL = "TAG_DIALOG_DELETE_DETAIL";
    public final static int TYPE_SUBMIT = 0, TYPE_DELETE = 1,TYPE_DELETE_DETAIL=2;
    private OkAndCancelDialog dialog; //提示框

    private Button btn_edit;
    private TextView tv_time, tv_context,tv_title;
    private ImageButton ibtn_add;
    private ListView listView;
    private TaskDepDetailAdapter adapter;
    private ArrayList<DepTaskDetailProgress> mList = new ArrayList<>();

    private boolean isEdit = false;//是否显示 操作功能的布尔值
    private String workId,workTag;
    private String context,time;
    private String workDetailid;//某个被选中删除的任务进展id
    private String workState;//任务状态 0-未完成 1-已完成

    //必须重写的 返回该activity的布局文件ID
    @Override
    protected int setContentViewId() {
        return R.layout.task_dep_detail;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();//初始化title
        setData();//设置得到的 参数
        adapter = new TaskDepDetailAdapter(TaskDepDetailActivity.this, mList, isEdit);
        listView.setAdapter(adapter);

        initListener();//初始化 点击事件
    }

    @Override
    protected void onResume() {
        super.onResume();
        getHttp();//开始联网获取数据
    }

    public void getHttp() {
        Map<String, String> hasnMap = new HashMap<>();
        hasnMap.put("opcode", "GetAllOfficeDetailworkByWorkid");
        hasnMap.put("workid", workId);
        VolleyRequest.RequestPost(TaskDepDetailActivity.this, MYURL.URL_HEAD, workId, hasnMap, new VolleyInterface<DepTaskDetail>(DepTaskDetail.class, TAG) {
            @Override
            public void onMySuccess(DepTaskDetail result) {
                context=result.getWorkAllDetailmessage().get(0).getMessage();
                time=result.getWorkAllDetailmessage().get(0).getAddtime();
                tv_context.setText(context);
                tv_time.setText("发布时间：" + time);
                mList.clear();
                int length = result.getWorkAllDetailmessage().get(0).getOfficeWorkprogress().size();
                for (int i = 0; i < length; i++) {
                    mList.add(result.getWorkAllDetailmessage().get(0).getOfficeWorkprogress().get(i));
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onMyError(VolleyError error) {

            }
        });
    }

    private void showOkAndCancelDialog(String Tag, String message, int type) {
        dialog = OkAndCancelDialog.newInstance(message, type);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        dialog.show(transaction, Tag);
    }


    private void setData() {

        setTitleName("任务详情");
        tv_title.setText("进展情况");
        isEdit = getIntent().getBooleanExtra("isEdit", false);//取出能否操作标识位 默认false
        workId = getIntent().getStringExtra("workId");
        workTag = getIntent().getStringExtra("workTag");
        workState = getIntent().getStringExtra("workState");//根据状态值返回 完成或者未完成
        workState=getButtonTextByWorkState(workState);
    }

    private String getButtonTextByWorkState(String workState) {
        if (workState.equals("0")){
            return "完成";
        }else {
            return "未完成";
        }

    }


    private void initListener() {
        //如果为true 则能编辑 设置功能
        if (isEdit) {

            setOnButtonNameAndListener(workState, new OnSubmitListener() {
                @Override
                public void submitClick() {
//                    Toast.makeText(TaskDepDetailActivity.this,"点击 完成",Toast.LENGTH_SHORT).show();
                    showOkAndCancelDialog(TAG_DIALOG_SUBMIT, "确定该任务"+workState, TYPE_SUBMIT);
                }
            });

            setOnDeleteListener(new OnDeleteListener() {
                @Override
                public void deleteClick() {
//                    Toast.makeText(TaskDepDetailActivity.this, "点击 删除", Toast.LENGTH_SHORT).show();
                    showOkAndCancelDialog(TAG_DIALOG_DELETE, "确定删除该任务", TYPE_DELETE);
                }
            });

            ibtn_add.setVisibility(View.VISIBLE);
            ibtn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(TaskDepDetailActivity.this, "点击 add", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(TaskDepDetailActivity.this, TaskDepDetail_Add_Activity.class);
                    intent.putExtra("workid", workId);
                    intent.putExtra("workTag",workTag);
                    startActivity(intent);

                }
            });

            btn_edit.setVisibility(View.VISIBLE);
            btn_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //标题栏的编辑 点击事件
                    if (!context.equals("") && !time.equals("")) {
                        Intent intent = new Intent(TaskDepDetailActivity.this, TaskDep_Edit_Activity.class);
                        intent.putExtra("context", context);
                        intent.putExtra("time", time);
                        intent.putExtra("workid", workId);
                        intent.putExtra("workTag",workTag);
                        intent.putExtra("title", "编辑任务内容");
                        startActivity(intent);
                    } else {
                        ZXLApplication.getInstance().showTextToast("正在联网获取数据，请再次点击编辑");
                    }

                }
            });


           adapter.setOnItemEditClickListener(new TaskDepDetailAdapter.OnEditListener() {
               @Override
               public void editClick(int position, String id, String context, String time) {
                   Intent intent = new Intent(TaskDepDetailActivity.this, TaskDepDetail_Edit_Activity.class);
                   intent.putExtra("context", context);
                   intent.putExtra("workDetailid", id);
                   intent.putExtra("time", time);
                   startActivity(intent);
               }
           });

            adapter.setOnItemDeleteClickListener(new TaskDepDetailAdapter.OnDeleteListener() {
                @Override
                public void deleteClick(int position, String id, String context, String time) {
                    workDetailid=id;
                    showOkAndCancelDialog(TAG_DIALOG_DELETE_DETAIL, "确定删除该任务进展详情", TYPE_DELETE_DETAIL);
                }
            });

        }


    }



    private void initView() {
        tv_time = (TextView) findViewById(R.id.tv_task_dep_time);
        tv_context = (TextView) findViewById(R.id.tv_task_dep_context);
        tv_title= (TextView) findViewById(R.id.tv_group_title);
        btn_edit = (Button) findViewById(R.id.btn_task_dep_edit);
        ibtn_add = (ImageButton) findViewById(R.id.ibtn_group_add);
        listView = (ListView) findViewById(R.id.lv_task_dep_detail);
    }


    @Override
    public void onDialogClick(String Tag, boolean isOk, int Type) {
        dialog.dismiss();
        if (isOk) {
            switch (Type) {
                case TYPE_SUBMIT:
                    httpSubmit(workId);//开始联网 完成操作
                    break;
                case TYPE_DELETE:
                    httpDelete(workId);//开始联网 删除操作
                    break;
                case TYPE_DELETE_DETAIL:
                    httpDeleteDtail(workDetailid);
                    break;
            }
        }

    }

    private void httpDeleteDtail(String workDetailid) {
        Map<String,String> hashMap=new HashMap<>();
        hashMap.put("opcode","DeleteOfficeWorkDetail");
        hashMap.put("workDetailid",workDetailid);

        VolleyRequest.RequestPost(this, MYURL.URL_HEAD, this.toString(), hashMap, new VolleyInterface<String>(String.class,TAG) {
            @Override
            public void onMySuccess(String result) {
                ZXLApplication.getInstance().showTextToastLong("删除任务进展成功");
                getHttp();//删除 任务后 刷新数据
            }

            @Override
            public void onMyError(VolleyError error) {

            }
        });
    }

    private void httpDelete(String workId) {
        Map<String,String> hashMap=new HashMap<>();
        hashMap.put("opcode","DeleteOfficeWork");
        hashMap.put("workid",workId);

        VolleyRequest.RequestPost(this, MYURL.URL_HEAD, this.toString(), hashMap, new VolleyInterface<String>(String.class,TAG) {
            @Override
            public void onMySuccess(String result) {
                ZXLApplication.getInstance().showTextToastLong("删除任务成功");
                finish();
            }

            @Override
            public void onMyError(VolleyError error) {

            }
        });
    }

    private void httpSubmit(String workId) {
        Map<String,String> hashMap=new HashMap<>();
        hashMap.put("opcode","CompleteTask");
        hashMap.put("username", PreferenceUtils.getUserName());
        hashMap.put("eid", Constant.EID);
        hashMap.put("workid",workId);

        VolleyRequest.RequestPost(this, MYURL.URL_HEAD, this.toString(), hashMap, new VolleyInterface<String>(String.class,TAG) {
            @Override
            public void onMySuccess(String result) {
                ZXLApplication.getInstance().showTextToastLong("操作成功");
                finish();
            }

            @Override
            public void onMyError(VolleyError error) {

            }
        });
    }



}
