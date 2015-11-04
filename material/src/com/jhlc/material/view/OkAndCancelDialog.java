package com.jhlc.material.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jhlc.material.R;


/**
 * Created by Administrator on 2015/9/15 0015.
 */
public class OkAndCancelDialog extends DialogFragment implements View.OnClickListener {
    private static String TAG="OkAndCancelDialog";
    private OnDialogClickListener listener;
    private Button btn_ok,btn_cancel;
    private TextView tv_message;
    //类方法 传入标题和提示信息  返回dialogfragment 实例
    public static OkAndCancelDialog newInstance(String message,int type){
        OkAndCancelDialog okAndCancelDialog=new OkAndCancelDialog();
        Bundle bundle=new Bundle();
        bundle.putString("message",message);
        bundle.putInt("type",type);
        okAndCancelDialog.setArguments(bundle);
        return okAndCancelDialog;
    }


    @Override
    public void onAttach(Activity activity) {
        //在fragment生命周期绑定activity阶段 检查 该activity是否实现了接口
        try {
            listener= (OnDialogClickListener) activity;
        }catch (ClassCastException e){
            Log.d(TAG,"OkAndCancelDialog can't get interface ");
        }
        super.onAttach(activity);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.ok_cancel_dialog,container,false);
        tv_message= (TextView) view.findViewById(R.id.tv_dialog_message);
        tv_message.setText(getMessage());

        btn_ok= (Button) view.findViewById(R.id.btn_dialog_ok);
        btn_cancel= (Button) view.findViewById(R.id.btn_dialog_cancel);
        btn_ok.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //如果setCancelable()中参数为true，若点击dialog覆盖不到的activity的空白或者按返回键，
        //则进行cancel，状态检测依次onCancel()和onDismiss()。如参数为false，则按空白处或返回键无反应。缺省为true
        super.onCreate(savedInstanceState);
        setCancelable(true);


        int style=DialogFragment.STYLE_NO_TITLE;//DialogFragment的风格类
        /*STYLE_NORMAL = 0;
         STYLE_NO_TITLE = 1; 无标题
         STYLE_NO_FRAME = 2; 窗口 陷入 父级界面中
         STYLE_NO_INPUT = 3; 窗口不能 接受点击 但是父级界面能接受
         */
        int theme = android.R.style.Theme_Holo_Light_Dialog; //亮色主题
        setStyle(style,theme);
    }


    private String getMessage() {
        //返回消息内容 默认提示内容
        return getArguments().getString("message","确定退出");
    }

    private int getType(){
        //返回类型 默认为0
        return getArguments().getInt("type",0);
    }

    @Override
    public void onClick(View v) {
        listener= (OnDialogClickListener) getActivity();
        if (v.getId()==R.id.btn_dialog_ok){
            listener.onDialogClick(getTag(),true,getType());
        }else {
            listener.onDialogClick(getTag(),false, getType());
        }
    }


    //重写dialog的点击方法


    public interface OnDialogClickListener{
        public void onDialogClick(String Tag,boolean isOk,int Type);
    }
}
