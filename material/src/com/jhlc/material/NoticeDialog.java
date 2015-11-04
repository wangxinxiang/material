package com.jhlc.material;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

/**
 */
public class NoticeDialog extends Activity{
    private TextView tv_notic_content;
    private Button   btn_confirm,btn_cancle;

    private int optype;
    private String workid, notic_content="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice_dialog);
        initView();
        getIntentData();
        setListener();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        optype = intent.getIntExtra("optype",-1);
        workid = intent.getStringExtra("workID");
        notic_content = intent.getStringExtra("notic_content");
    }

    private void setListener() {
        tv_notic_content.setText(notic_content);

        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.putExtra("optype", -1);
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upUserOpt(optype,workid);
            }
        });

    }

    private void initView() {
        tv_notic_content = (TextView) findViewById(R.id.tv_notic_content);
        btn_confirm = (Button) NoticeDialog.this.findViewById(R.id.btn_confirm);
        btn_cancle  = (Button) NoticeDialog.this.findViewById(R.id.btn_cancle);

    }

    /**
     *  当用户是上级时可以对下级任务进行
     *  1.确认完成操作
     *  2.删除操作
     * */
    private void upUserOpt(final int optype, String workid) {
        // 发送请求到服务器
        AsyncHttpClient client = new AsyncHttpClient();
        // 创建请求参数
        RequestParams params = new RequestParams();
        params.put("opcode", "opt_work");
        params.put("Username", PreferenceUtils.getInstance().getUserName());
        params.put("eid", Constant.EID);
        params.put("workid", workid);
        params.put("optype", optype);

        // 执行post方法
        client.post(MYURL.URL_HEAD, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // 设置值
                LogZ.d("getUpUserData-->NoticeDialog ", new String(responseBody));
                PostBackDataBean postBackDataBean = new GsonBuilder().create().fromJson(new String(responseBody), PostBackDataBean.class);
                if(postBackDataBean.getCode() == 100){
                    Intent intent=new Intent();
                    intent.putExtra("optype", optype);
                    setResult(100, intent);
                    finish();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // 打印错误信息
                error.printStackTrace();

            }

        });
    }


}
