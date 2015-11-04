package com.jhlc.material.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

/** 闹钟管理类 **/
public class MyAlarmManager {

	public static void addAlarm(Context context, long timeMillis,int alarmId,String alarm_msg,int UpOrDown) {
		AlarmManager aManager = (AlarmManager) context.getSystemService(Service.ALARM_SERVICE);
		Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("UpOrDown",UpOrDown);
        intent.putExtra("alarm_msg",alarm_msg);
        intent.putExtra("alarmId",alarmId);
		PendingIntent sender = PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		//或者以下面方式简化
		aManager.set(AlarmManager.RTC_WAKEUP, timeMillis, sender);// 一次性闹钟

	}

	public static void cancleAlarm(Context context,int alarmId) {
		Intent intent = new Intent(context,AlarmReceiver.class);  
        PendingIntent pi = PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager aManager = (AlarmManager) context.getSystemService(Service.ALARM_SERVICE);  
        aManager.cancel(pi);

	}

}
