package com.jhlc.material;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.jhlc.material.bean.PostBackDataBean;
import com.jhlc.material.utils.*;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;
import org.apache.http.Header;

public class ContactUsActivity extends Activity {

    //友盟统计
    private Context mContext;
    private final  String mPageName = "ContactUsActivity";
    private EditText    et_content_us,et_phone_num;
    private Button ibtn_submit_edit;
    private String      send_content , phone_num;
    private ZXLApplication zxlApplication = ZXLApplication.getInstance();

    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_tab_contactus);
        initView();
        mContext=this;
    }

    private void initView() {
        et_content_us    = (EditText) findViewById(R.id.et_content_us);
        et_phone_num     = (EditText) findViewById(R.id.et_phone_num);

        ibtn_submit_edit = (Button) findViewById(R.id.ibtn_submit_edit);
        ibtn_submit_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_content = et_content_us.getText().toString().trim();
                phone_num    = et_phone_num.getText().toString().trim();
                if(checkContent()) {
                    pd= ProgressDialog.show(ContactUsActivity.this, "正在提交建议...", "请等待", true);
                    contentUsMsg();
                }
            }
        });

    }

    private boolean checkContent(){
        if(null == send_content || "".equals(send_content)){
            zxlApplication.showTextToast("需求内容不能为空！");
            return false;
        }
        if(null == phone_num || "".equals(phone_num)){
            zxlApplication.showTextToast("手机号码不能为空！");
            return false;
        }
        if(!StringUtils.isMobile(phone_num)){
            zxlApplication.showTextToast("请输入正确手机号！");
            return false;
        }
        return true;
    }

    private void contentUsMsg( ) {
        // 发送请求到服务器
        AsyncHttpClient client = new AsyncHttpClient();
        // 创建请求参数
        RequestParams params = new RequestParams();
        params.put("opcode", "contactus");
        params.put("Username", PreferenceUtils.getInstance().getUserName());
        params.put("eid", Constant.EID);
        params.put("msg", send_content);
        params.put("phonenumber", phone_num);


        // 执行post方法
        client.post(MYURL.URL_HEAD, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                ibtn_submit_edit.setEnabled(false);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                ibtn_submit_edit.setEnabled(true);
                pd.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                // 设置值
                LogZ.d("ContactUsActivity--> ", new String(responseBody));
                try {
                    PostBackDataBean postBackDataBean = new GsonBuilder().create().fromJson(new String(responseBody), PostBackDataBean.class);
                    if(postBackDataBean.getCode() == 100){
                        ZXLApplication.getInstance().showTextToast(postBackDataBean.getMsg()+"！");
                        et_content_us.setText("");
                        et_phone_num.setText("");
                        InputMethodManager imm = (InputMethodManager) ContactUsActivity.this
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                        // imm.hideSoftInputFromWindow(myEditText.getWindowToken(), 0);
                        if (imm.isActive())  //一直是true
                            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                    } else if(-1 == postBackDataBean.getCode()){
                        ZXLApplication.getInstance().showTextToast("服务器错误！");
                    }
                } catch (JsonSyntaxException exception){
                    exception.printStackTrace();
                    ZXLApplication.getInstance().showTextToast("数据解析错误");
                }

                ibtn_submit_edit.setEnabled(true);
                pd.dismiss();


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // 打印错误信息
                error.printStackTrace();
                ibtn_submit_edit.setEnabled(true);
                pd.dismiss();
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        MobclickAgent.onPageStart(mPageName);
        MobclickAgent.onResume(mContext);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd( mPageName );
        MobclickAgent.onPause(mContext);
    }
}
