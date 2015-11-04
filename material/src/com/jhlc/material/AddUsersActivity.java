package com.jhlc.material;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.google.gson.GsonBuilder;
import com.jhlc.material.bean.PostBackDataBean;
import com.jhlc.material.utils.Constant;
import com.jhlc.material.utils.LogZ;
import com.jhlc.material.utils.MYURL;
import com.jhlc.material.utils.PreferenceUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;

public class AddUsersActivity extends Activity{
    private EditText et_updown_name;
    private TextView tv_title_name;
    private int UpOrDown = -1;
    private int Invite=1;
    private String invitename;
    private ZXLApplication application = ZXLApplication.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_updown_user);
        initView();
        setListener();
    }

    private void initView() {
        tv_title_name  = (TextView) findViewById(R.id.tv_title_name);
        et_updown_name = (EditText) findViewById(R.id.et_updown_name);
        et_updown_name.requestFocus();
        et_updown_name.setSelection(0);

        UpOrDown = getIntent().getIntExtra("UpOrDown",-1);
        Invite = getIntent().getIntExtra("invite",1);
        if(Constant.ZXL_UP_USER == UpOrDown){
            if(Invite==2){//报销界面
                et_updown_name.setHint("请输入报销审批上级真实姓名");
                tv_title_name.setText("添加报销审批上级");
            }else{//正常上下级添加界面
                et_updown_name.setHint("请输入上级真实姓名");
                tv_title_name.setText("添加上级");
            }

        } else if(Constant.ZXL_DOWN_USER == UpOrDown){
            if (Invite==2) {
                et_updown_name.setHint("请输入下属真实姓名");
                tv_title_name.setText("添加下属");
            }else{
                et_updown_name.setHint("请输入下属真实姓名");
                tv_title_name.setText("添加下属");
            }
        }
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
                invitename = et_updown_name.getText().toString().trim();
                if(checkName()) {
                    inviteUsers();
                }
            }
        });
    }

    private boolean checkName(){
        if(null == invitename || "".equals(invitename)){
            application.showTextToast("请输入你的姓名");
            return false;
        } else if(invitename.length() > 12){
            application.showTextToast("长度不能大于12");
            return false;
        }
        return true;
    }

    private void inviteUsers( ) {
        // 发送请求到服务器
        AsyncHttpClient client = new AsyncHttpClient();
        // 创建请求参数
        RequestParams params = new RequestParams();
        params.put("opcode", "invitation_user");
        params.put("Username", PreferenceUtils.getUserName());
        params.put("invitation_name", invitename);
        params.put("eid", Constant.EID);
        params.put("typeid", UpOrDown);
        params.put("invite", Invite);

        // 执行post方法
        client.post(MYURL.URL_HEAD, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // 设置值
                LogZ.d("getUpUserData--> ", new String(responseBody));
                PostBackDataBean postBackDataBean = new GsonBuilder().create().fromJson(new String(responseBody), PostBackDataBean.class);
                if(100 == postBackDataBean.getCode()){
                    String toast_msg = getToastMsg(postBackDataBean);
                    application.showTextToast(toast_msg);
                    setResult(100);
                    finish();
                } else if(-1 == postBackDataBean.getCode()){
                    application.showTextToast("服务器错误！");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // 打印错误信息
                error.printStackTrace();

            }

        });
    }

    private String getToastMsg(PostBackDataBean postBackDataBean) {
        String toast_msg = "";
        String opcode    = postBackDataBean.getOpcode();
        if("user_notexist".equals(opcode)){
            toast_msg = "用户不存在";
        } else if("invitationuser_exist".equals(opcode)){
            toast_msg = "该用户已经是您的下属或者上级";
        } else if("invitationuser_success".equals(opcode)){
            toast_msg = "添加成功";
        } else if("unknow_error".equals(opcode)){
            toast_msg = "未知错误";
        } else {
            toast_msg = "404";
        }
        return toast_msg;
    }

}
