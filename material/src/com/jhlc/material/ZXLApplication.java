package com.jhlc.material;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.RemoteViews;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.jhlc.material.utils.ImageUtils;
import com.jhlc.material.utils.LogZ;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UpdateConfig;

import java.io.File;
import java.util.LinkedList;
import java.util.List;


public class ZXLApplication extends Application {
    private List<Activity> activityList = new LinkedList<Activity>();
    private static ZXLApplication mApplication;
    private PushAgent mPushAgent;

    private static RequestQueue queue;//volley 请求队列

    public synchronized static ZXLApplication getInstance() {
        return mApplication;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        queue= Volley.newRequestQueue(getApplicationContext());//初始化volley网络请求队列

        UpdateConfig.setDebug(true);
        UmengUpdateAgent.setUpdateOnlyWifi(false);

        //校验下app对应的下载目录是否存在，如果不存在要创建目录
        String path= ImageUtils.path;
        File file=new File(path);
        if(!file.exists()){
            file.mkdir();
        }
//        SDKInitializer.initialize(getApplicationContext());

        mApplication = this;
        mPushAgent = PushAgent.getInstance(this);
        mPushAgent.setDebugMode(true);
        InitUmeng();//友盟 有关的初始化



    }

    public static RequestQueue getHttpQueues(){
        return queue;
    }

    private void InitUmeng() {
        /**
         * 该Handler是在IntentService中被调用，故
         * 1. 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
         * 2. IntentService里的onHandleIntent方法是并不处于主线程中，因此，如果需调用到主线程，需如下所示;
         * 	      或者可以直接启动Service
         * */
        UmengMessageHandler messageHandler = new UmengMessageHandler(){
            @Override
            public void dealWithCustomMessage(final Context context, final UMessage msg) {
                new Handler(getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        UTrack.getInstance(getApplicationContext()).trackMsgClick(msg);
                        Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public Notification getNotification(Context context,
                                                UMessage msg) {
                switch (msg.builder_id) {
                    case 1:
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                        RemoteViews myNotificationView = new RemoteViews(context.getPackageName(), R.layout.notification_view);
                        myNotificationView.setTextViewText(R.id.notification_title, msg.title);
                        myNotificationView.setTextViewText(R.id.notification_text, msg.text);
                        myNotificationView.setImageViewBitmap(R.id.notification_large_icon, getLargeIcon(context, msg));
                        myNotificationView.setImageViewResource(R.id.notification_small_icon, getSmallIconId(context, msg));
                        builder.setContent(myNotificationView);
                        Notification mNotification = builder.build();
                        //由于Android v4包的bug，在2.3及以下系统，Builder创建出来的Notification，并没有设置RemoteView，故需要添加此代码
                        mNotification.contentView = myNotificationView;
                        return mNotification;
                    default:
                        //默认为0，若填写的builder_id并不存在，也使用默认。
                        return super.getNotification(context, msg);
                }
            }
        };
        mPushAgent.setMessageHandler(messageHandler);

        /**
         * 该Handler是在BroadcastReceiver中被调用，故
         * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
         * */
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler(){
            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
            }
        };
        mPushAgent.setNotificationClickHandler(notificationClickHandler);
    }

    //显示简单的对话框
    public void showAlertDialog(Context context, String message) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .create();
        alertDialog.setMessage(message);
        alertDialog.setButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                alertDialog.dismiss();

            }
        });
        alertDialog.show();

    }

    //得到屏幕高度
    public int getScreenHeight(Context context) {

        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display=wm.getDefaultDisplay();
        Point size=new Point();
        display.getSize(size);
        return size.y;
    }
    //得到屏幕宽度
    public int getScreenWidth(Context context) {

        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display=wm.getDefaultDisplay();
        Point size=new Point();
        display.getSize(size);
        return size.x;
    }





    /**
     * 显示一个text的toast,且最多只显示一个toast的时间
     *
     */
    public void showTextToast(String msg) {

        Toast toast = null;

        if (toast == null) {
            toast = Toast.makeText(getApplicationContext(), msg,
                    Toast.LENGTH_SHORT);

        } else {

            toast.setText(msg);
        }

        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * 显示一个text的toast,且最多只显示一个toast的时间
     *
     */
    public void showTextToastLong(String msg) {

        Toast toast = null;

        if (toast == null) {
            toast = Toast.makeText(getApplicationContext(), msg,
                    Toast.LENGTH_LONG);

        } else {

            toast.setText(msg);
        }

        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * 显示一个text的toast,且最多只显示一个toast的时间
     *
     */
    public void showNetWorkingErrorTextToast() {
        showTextToast("您的网络不顺畅，请检查网络设置！");
    }

    //检测网络
    public boolean isconnected( ) {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo[] infos = cm.getAllNetworkInfo();
            if (infos != null) {
                for (NetworkInfo ni : infos) {
                    if (ni.isConnected()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    //添加Activity到容器中
    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    //遍历所有Activity并finish
    public void exit() {
        for (Activity activity : activityList) {
            activity.finish();
        }
        System.exit(0);
    }

    public void exit(Activity activity) {
        for (Activity activity1 : activityList) {
            if (activity != activity1) {
                LogZ.d("exit --->", activity1.getLocalClassName());
                activity1.finish();
            }
        }
    }
}
