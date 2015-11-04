package com.jhlc.material;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.view.View;
import android.widget.*;

import com.jhlc.material.view.OkAndCancelDialog;

/**
 * Created by Administrator on 2015/9/15 0015.
 */
public class AddOrEditTaskActivity extends TitleActivity implements OkAndCancelDialog.OnDialogClickListener {
    private final static String TAG = "AddOrEditTaskActivity";
    protected String VolleyTag;
    public final static String TAG_DIALOG_SUBMIT = "TAG_DIALOG_SUBMIT";
    public final static String TAG_DIALOG_CLEAR = "TAG_DIALOG_CLEAR";
    public final static String TAG_DIALOG_FINISH = "TAG_DIALOG_FINISH";
    public final static int TYPE_SUBMIT = 0;
    public final static int TYPE_CLEAR = 1;
    public final static int TYPE_FINISH = 2;

    public final static int REQUEST = 100;
    public final static int RESULT = 1001;

    private OkAndCancelDialog dialog; //提示框
    private String title;
    private String context;
    private EditText edt_input;

    private Button btn_change_time;
    private TextView tv_time;
    private TextView tv_word_count;


    @Override
    protected int setContentViewId() {
        return R.layout.add_or_edit;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        VolleyTag=this.toString();

        context = getIntent().getStringExtra("context");
        if (context != null) {
            edt_input.setText(context);//如果传入的内容不为空 就设置值
            edt_input.setSelection(context.length());
            tv_word_count.setText(edt_input.getText().length()+"/500");
        }
        title = getIntent().getStringExtra("title");
        if (title != null) {
            setTitleName(title);
        }

        setTimeIsVisibility(true, null, true);//默认时间控件都显示 null为默认显示当前时间
        initListener();
    }

    //参数说明 isTv 控制textView显隐 date 为显示的时间 传null 获取当前时间
    //        isBtn 控制 修改时间button 显隐
    protected void setTimeIsVisibility(boolean isTv, String date, boolean isBtn) {

        if (isTv) {
            tv_time.setVisibility(View.VISIBLE);
            if (date != null) {
                tv_time.setText(date);
            } else {
                Time time = new Time();
                time.setToNow();
                tv_time.setText(getFormatTime(time));
            }
        } else {
            tv_time.setVisibility(View.INVISIBLE);
        }

        if (isBtn) {
            btn_change_time.setVisibility(View.VISIBLE);
        } else {
            btn_change_time.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT) {
            String date = data.getStringExtra("date");
            tv_time.setText(date);
        }
    }


    private void initView() {
        edt_input = (EditText) findViewById(R.id.edt_task_input);
        btn_change_time = (Button) findViewById(R.id.btn_change_time);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_word_count= (TextView) findViewById(R.id.tv_work_count);
    }

    //点击修改时间 跳转到时间选择界面
    private void initListener() {
        btn_change_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddOrEditTaskActivity.this, DatePickerActivity.class);
                intent.putExtra("date", tv_time.getText().toString());
                startActivityForResult(intent, REQUEST);
            }
        });


        edt_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tv_word_count.setText(s.length()+"/500");
            }
        });

        //点击提交按钮 弹出对话框
        setOnButtonListener(new OnSubmitListener() {
            @Override
            public void submitClick() {
                if (!getInputContext().equals("")) {
                    showOkAndCancelDialog(TAG_DIALOG_SUBMIT, "是否确定提交", TYPE_SUBMIT);
                } else {
                    ZXLApplication.getInstance().showTextToast("输入内容为空，不能提交");
                }


            }
        });

        //点击 垃圾桶按钮 清空输入框
        setOnDeleteListener(new OnDeleteListener() {
            @Override
            public void deleteClick() {
                if (getInputContext().equals("")) {
                    ZXLApplication.getInstance().showTextToast("输入内容为空，不需要清空");
                } else {
                    showOkAndCancelDialog(TAG_DIALOG_CLEAR, "是否清空输入内容", TYPE_CLEAR);
                }

            }
        });
    }

    //创建出dialog
    //参数说明 Tag 为设置管理栈中的tag message为提示内容 type为对话框类型
    private void showOkAndCancelDialog(String Tag, String message, int type) {
        dialog = OkAndCancelDialog.newInstance(message, type);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        dialog.show(transaction, Tag);
    }

    //重写 title返回和物理返回键的 点击事件 拦截当输入框不为空的时候的退出
    @Override
    protected void setKeyOrBackPressed() {
        String input = getInputContext();
        if (input.equals("")) {
            //如果输入框为空 退出
            super.setKeyOrBackPressed();
        } else {
            //如果不为空
            if (input.equals(context)) {
                //没有编辑 输入内容和传入内容一致 直接退出
                super.setKeyOrBackPressed();
            } else {
                //用户编辑了内容 单击退出弹出提示
                showOkAndCancelDialog(TAG_DIALOG_FINISH, "输入内容不为空，是否放弃编辑", TYPE_FINISH);
            }
        }

    }

    protected String getInputContext() {
        return edt_input.getText().toString();
    }

    protected String getTime(){
        return tv_time.getText().toString();
    }
    @Override
    public void onDialogClick(String Tag, boolean isOk, int Type) {
        dialog.dismiss();
        if (isOk) {
            switch (Type) {
                case TYPE_SUBMIT:
                    httpSubmit();//开始上传
                    break;
                case TYPE_CLEAR:
                    edt_input.setText("");//清空输入框
                    break;
                case TYPE_FINISH:
                    finish();//结束
                    break;
                default:
                    dialog.dismiss();
                    break;
            }
        }
    }

    protected void httpSubmit() {
        //点击确定提交的后的网络 操作 具体功能 由子类实现
    }

    //解析time为 2015/09/16 格式
    private String getFormatTime(Time time) {
        StringBuffer sb = new StringBuffer();
        sb.append(time.year + "/");
        if (time.month + 1 < 10) {
            sb.append("0" + (time.month + 1) + "/");
        } else {
            sb.append((time.month + 1) + "/");
        }
        if (time.monthDay < 10) {
            sb.append("0" + time.monthDay);
        } else {
            sb.append(time.monthDay);
        }
        return sb.toString();
    }
}
