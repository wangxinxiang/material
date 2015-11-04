package com.jhlc.material;

import android.app.Dialog;
import android.content.Context;
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
public class MakeSureLogoutDialog extends Dialog {
    private TextView tv_notic_content;
    private Button   btn_confirm,btn_cancle;

    private String  notic_content="";
    private Context context;
    private OnPaymentListener onPaymentListener;
    private int ptype = 0;
    private String expenseid = "";

    public MakeSureLogoutDialog(Context context, int theme){
        super(context, theme);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.makesure_logout_dialog);

        initView();
        setListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(ptype == 2) {
            tv_notic_content.setText("我同意本次报销！");
        } else if(ptype == 4){
            tv_notic_content.setText("我不同意本次报销！");
        }
    }

    private void setListener() {
        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ZXLApplication.getInstance().showTextToast("cancel");
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ZXLApplication.getInstance().showTextToast("confirm");

            }
        });

    }

    private void initView() {
        tv_notic_content = (TextView) findViewById(R.id.tv_notic_content);
        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        btn_cancle  = (Button) findViewById(R.id.btn_cancle);

    }



    /**
     *  当用户是上级时可以对下级任务进行
     *  1.确认完成操作
     *  2.删除操作
     * */
    private void paymentOpt(final int optype, String expenseid) {
        // 发送请求到服务器
        AsyncHttpClient client = new AsyncHttpClient();
        // 创建请求参数
        RequestParams params = new RequestParams();
        params.put("opcode", "pass_expense");
        params.put("Username", PreferenceUtils.getInstance().getUserName());
        params.put("eid", Constant.EID);
        params.put("expenseid", expenseid);
        params.put("ptype", optype);
        LogZ.d("getUpUserData--> ", Constant.EID + PreferenceUtils.getInstance().getUserName() + expenseid + optype);
        //http://zhixingli.shilehui.com/?opcode= pass_expense& expenseid=1&Username=董事长&eid=X&ptype=2

        // 执行post方法
        client.post(MYURL.URL_HEAD, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // 设置值
                LogZ.d("getUpUserData--> ", new String(responseBody));
                PostBackDataBean postBackDataBean = new GsonBuilder().create().fromJson(new String(responseBody), PostBackDataBean.class);
                if(postBackDataBean.getCode() == 100){
                    ZXLApplication.getInstance().showTextToast("操作成功！");
                    onPaymentListener.opType(ptype);
                    MakeSureLogoutDialog.this.dismiss();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // 打印错误信息
                error.printStackTrace();

            }

        });
    }

    public void setPtype(int ptype) {
        this.ptype = ptype;
    }

    public void setExpenseid(String expenseid) {
        this.expenseid = expenseid;
    }

    public void setPaymentListener(OnPaymentListener onPaymentListener) {
        this.onPaymentListener = onPaymentListener;
    }


    public interface OnPaymentListener {
        void  opType(int type);
    }

}
