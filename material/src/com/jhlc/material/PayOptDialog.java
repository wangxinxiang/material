package com.jhlc.material;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


/**
 */
public class PayOptDialog extends Dialog {
    private TextView tv_notic_content;
    private Button   btn_confirm,btn_cancle;

    private String  notic_content="";
    private Context context;
    private OnPaymentListener onPaymentListener;
    private int ptype = 0;
    private String expenseid = "";

    private PositiveListener positiveListener;
    private NegativeListener negativeListener;

    public PayOptDialog(Context context, int theme){
        super(context, theme);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.notice_dialog);

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
                PayOptDialog.this.dismiss();
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                paymentOpt(ptype,expenseid);
                if(positiveListener!=null){
                    positiveListener.positiveClick();
                }

            }
        });

    }

    private void initView() {
        tv_notic_content = (TextView) findViewById(R.id.tv_notic_content);
        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        btn_cancle  = (Button) findViewById(R.id.btn_cancle);

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

    public void setPositiveListener(PositiveListener positiveListener) {
        this.positiveListener = positiveListener;
    }

    public void setNegativeListener(NegativeListener negativeListener) {
        this.negativeListener = negativeListener;
    }

    public interface OnPaymentListener {
        void  opType(int type);
    }

    public interface PositiveListener{
        void positiveClick();
    }

    public interface NegativeListener{
        void negativeClick();
    }

}
