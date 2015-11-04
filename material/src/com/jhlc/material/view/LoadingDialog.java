package com.jhlc.material.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jhlc.material.R;


/**
 * Created by Administrator on 2015/9/16 0016.
 */
public class LoadingDialog extends Dialog {

    public LoadingDialog(Context context, int theme) {
        super(context, theme);
    }

    public static Dialog createLoadingDialog(Context context,String msg){
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.loading_gray_circle, null);
        LinearLayout ll_loading= (LinearLayout) view.findViewById(R.id.ll_loading);
        ImageView img_loading= (ImageView) view.findViewById(R.id.img_loading);
        TextView tv_loading_msg= (TextView) view.findViewById(R.id.tv_loading_msg);
        Animation animation= AnimationUtils.loadAnimation(context,R.anim.loading_animation);
        img_loading.startAnimation(animation);
        tv_loading_msg.setText(msg);

        Dialog loadingDialog=new Dialog(context,R.style.loading_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.setContentView(ll_loading,new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        return loadingDialog;
    }
}
