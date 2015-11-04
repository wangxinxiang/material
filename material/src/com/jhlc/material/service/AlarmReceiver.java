package com.jhlc.material.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class AlarmReceiver extends BroadcastReceiver {
    private String newmsg_name = "";
    private String alarm_msg   = "";
    private int    UpOrDown    = -1;

	@Override
    public void onReceive(Context context, Intent intent) {
        UpOrDown    = intent.getIntExtra("UpOrDown",-1);
        newmsg_name = intent.getStringExtra("newmsg_name");
        alarm_msg   = intent.getStringExtra("alarm_msg");
        //不再自己产生notification
        //sendNotification(context,newmsg_name,alarm_msg,UpOrDown);
        cancleAlarm(context,intent);
    }

    private void cancleAlarm(Context context,Intent intent) {
        MyAlarmManager.cancleAlarm(context,intent.getIntExtra("alarmId",0));

    }

//    public static void sendNotification(Context context,String newmsg_name,String alarm_msg,int UpOrDown  ) {
//        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
//        String noticmsg = "";
//        if(!("".equals(newmsg_name)) && null != newmsg_name){
//            String msg_name[] = newmsg_name.split("-");
//            if(msg_name.length==1){
//                noticmsg = "您有来自"+msg_name[0]+"的新消息啦！";
//            } else {
//                noticmsg = "您有新消息来啦！";
//            }
//        }
//        if(!("".equals(alarm_msg)) && null != alarm_msg){
//            noticmsg = alarm_msg+"给你布置的任务时间到啦！";
//        }
//        Notification notification = new Notification(R.drawable.zxl_logo, noticmsg, System.currentTimeMillis());
//        notification.flags = Notification.FLAG_AUTO_CANCEL;
//        Intent i = new Intent(context, NoticeLoadingActivity.class);
//        i.setAction(Intent.ACTION_MAIN);
//        i.addCategory(Intent.CATEGORY_LAUNCHER);
//        LogZ.d("AlarmReceiver-UpOrDown-->", "" + UpOrDown);
//        i.putExtra("AlarmUpOrDown", UpOrDown);
//        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        // 通知时发出的默认声音
//        notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
//        //添加震动
//        notification.defaults |= Notification.DEFAULT_VIBRATE;
//        long[] vibrate = {0,100,200,300};
//        notification.vibrate = vibrate ;
//
//        PendingIntent contentIntent = PendingIntent.getActivity(context,0,i, PendingIntent.FLAG_UPDATE_CURRENT);
//        LogZ.d("lyjtest","准备添加setLatestEventInfo|"+newmsg_name+"|"+"".equals(newmsg_name)+(!("".equals(newmsg_name)) && null != newmsg_name));
//        if(!("".equals(newmsg_name)) && null != newmsg_name){
//            String msg_name[] = newmsg_name.split("-");
//            if(msg_name.length==1){
//                notification.setLatestEventInfo(context,"94执行力","您有来自"+msg_name[0]+"的新消息啦！",contentIntent);
//                LogZ.d("lyjtest","添加了setLatestEventInfo1");
//            } else {
//                notification.setLatestEventInfo(context,"94执行力","您有新消息来啦！",contentIntent);
//                LogZ.d("lyjtest","添加了setLatestEventInfo2");
//            }
//        }
//        if(!("".equals(alarm_msg)) && null != alarm_msg){
//            notification.setLatestEventInfo(context,"94执行力",alarm_msg+"给你布置的任务时间到啦！",contentIntent);
//            LogZ.d("lyjtest","添加了setLatestEventInfo3");
//        }
//        notificationManager.notify(0, notification);
//
//    }

}
