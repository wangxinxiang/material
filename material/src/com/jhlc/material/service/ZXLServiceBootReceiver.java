package com.jhlc.material.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jhlc.material.utils.LogZ;

public class ZXLServiceBootReceiver extends BroadcastReceiver {


	@Override
    public void onReceive(Context context, Intent intent) {
        LogZ.d("lyjtest", "接收到开机启动消息!");
        Intent myIntent = new Intent();
        myIntent.setAction("com.jhlc.zxl.service.NewMessageService");
        context.startService(myIntent);
    }

}
