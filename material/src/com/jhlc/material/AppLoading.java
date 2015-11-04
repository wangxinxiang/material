package com.jhlc.material;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.jhlc.material.utils.PreferenceUtils;

/**
 * Created by 104468 on 2014/11/4.
 */
public class AppLoading extends Activity {
    private PreferenceUtils preferenceUtils = PreferenceUtils.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_loading);
        //第一次登陆都是设置需要更新
        PreferenceUtils.getInstance().setNeedUpdate(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (PreferenceUtils.getIsLogin()) {//如果已经登录就直接进入系统
                    Intent intent=new Intent(AppLoading.this,MainFragmentActivity.class);
                    startActivity(intent);
                    AppLoading.this.finish();
                }else{
                    Intent intent = new Intent(AppLoading.this, LoginActivity.class);
                    startActivity(intent);
                    AppLoading.this.finish();
                }
            }
        }, 500);
        //设置等待时机 默认2秒
    }
}
