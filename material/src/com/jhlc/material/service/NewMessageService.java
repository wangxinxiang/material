package com.jhlc.material.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.google.gson.GsonBuilder;
import com.jhlc.material.ZXLApplication;
import com.jhlc.material.bean.DownUserNewList;
import com.jhlc.material.bean.NewMessageBean;
import com.jhlc.material.bean.UserNewMsgList;
import com.jhlc.material.bean.WorkLowerBean;
import com.jhlc.material.bean.WorkLowerPersonInfor;
import com.jhlc.material.db.SetNewMsgDB;
import com.jhlc.material.utils.*;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class NewMessageService extends Service {

    private static final String TAG = "NewMessageService";
    private Timer timer = new Timer();
    private SetNewMsgDB newMsgDB = new SetNewMsgDB(this);

    public void MyMethod() {
        Log.i(TAG, "BindService-->MyMethod()");
    }

    @Override
    public IBinder onBind(Intent intent) {

        return myBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogZ.d("lyjtest", "在service onCreate!");
        getNewMsgCount();
    }

    public class MyBinder extends Binder {

        public NewMessageService getService() {
            return NewMessageService.this;
        }
    }

    private MyBinder myBinder = new MyBinder();

    private void getNewMsgCount() {
        TimerTask task = new TimerTask() {
            public void run() {
                if (ZXLApplication.getInstance().isconnected()) {
                    //当前登录用户名不为空才去获得请求
                    if (!PreferenceUtils.getUserName().equals("")) {
                        getNewMsgPost();
                    }

                }
            }
        };
        timer.schedule(task, 0, 5000);

    }


    private void getNewMsgPost() {

        // 发送请求到服务器
        SyncHttpClient client = new SyncHttpClient();
        // 创建请求参数
        RequestParams params = new RequestParams();
        params.put("opcode", "select_newinfo");
        final String userName = PreferenceUtils.getUserName();
        params.put("Username", userName);
        params.put("eid", Constant.EID);

        // 执行post方法
        client.post(MYURL.URL_HEAD, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //标示 上下级
                int UpOrDown = -1;

//                LogZ.d(TAG, "userName:" + userName + ",msg|" + new String(responseBody));
                NewMessageBean newMessageBean = new GsonBuilder().create().fromJson(new String(responseBody), NewMessageBean.class);

                /**
                 *  任务模块
                 * */
                UserNewMsgList upUserNewMsgList[] = newMessageBean.getUpUserNewMsgList();
                // 把 有新消息的 姓名 条数 上级或者下级 信息保存到数据库中
                if (null != upUserNewMsgList && 0 != upUserNewMsgList.length) {
                    manageWorkNewMsg(upUserNewMsgList, "10", Constant.WorkItemsNewMsgAction, null);
                    UpOrDown = 1;
                }

                DownUserNewList downUserNewMsgList[] = newMessageBean.getDownUserNewMsgList();
                if (null != downUserNewMsgList && 0 != downUserNewMsgList.length) {
                    manageDownWorkNewMsg(downUserNewMsgList, "0", Constant.WorkItemsNewMsgAction);
                    UpOrDown = 0;
                }


                if (StringUtils.isNotBlank(newMessageBean.getWorkSuperiors())) {
                    manageInvitationNewMsg(newMessageBean.getWorkSuperiors(), "10", Constant.RenWu, Constant.WorkItemsNewMsgAction, null);
                    //新消息有内容就会更新界面
                    UpOrDown = 1;
                }

                WorkLowerBean[] workLowerBeans = newMessageBean.getWorkLower();
                if (null != workLowerBeans && 0 != workLowerBeans.length) {
                    manageDownInvitationNewMsg(workLowerBeans, "0", Constant.RenWu, Constant.WorkItemsNewMsgAction);
                    //新消息有内容就会更新界面
                    UpOrDown = 0;
                }

//                LogZ.d("lyjservice", "在service中发送broadcast下级msg 显示");
                Intent intent = new Intent(Constant.CancleTabFlagAction);
                ArrayList<Integer> ShowExtra = new ArrayList<Integer>();
                // 显示Tab上的 新消息圆点    // 说明任务 shang ji 列表有新消息
                if (UpOrDown == 1) {
                    ShowExtra.add(10);
                }
                if (UpOrDown == 0) {
                    ShowExtra.add(0);
                }

                if (ShowExtra.size() > 0) {
                    intent.putExtra("WhichTabShow", StringUtils.changeIntegerArray(ShowExtra));
                    sendBroadcast(intent);
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // 打印错误信息
                LogZ.d("lyjservice", "NewMessageService 报错");
                error.printStackTrace();
            }

        });
    }

    /**
     * 接收到上级新消息，并保存到数据库，同时发出通知 ProgressReportActivity WorkItemsDetailActivity 接收该通知
     *
     * @param s                 新消息类型 10 任务上级 0 任务下级
     * @param broadcastConstant 广播的消息标志
     */
    private void manageWorkNewMsg(UserNewMsgList[] userNewMsgLists, String s, String broadcastConstant, String departmentName) {
        int length = userNewMsgLists.length;
        for (int i = 0; i < length; i++) {
            String idarray[] = userNewMsgLists[i].getId().split(",");
            for (int j = 0; j < idarray.length; j++) {
                newMsgDB.addNewMsg(userNewMsgLists[i].getUsername(), userNewMsgLists[i].getNoreadcount(), s, idarray[j], departmentName);
            }
            //收到id，向workitem界面发送广播消息
            Intent intent = new Intent(broadcastConstant);
            intent.putExtra("workname", userNewMsgLists[i].getUsername());
            intent.putExtra("workId", userNewMsgLists[i].getId());
            intent.putExtra("upordown", s.equals(Constant.ZXL_UP_USER_NEW_MSG + "") ? Constant.ZXL_UP_USER : Constant.ZXL_DOWN_USER);
            sendBroadcast(intent);
        }
    }

    /**
     * 下级新消息，并保存到数据库，同时发出通知 ProgressReportActivity WorkItemsDetailActivity 接收该通知
     */
    private void manageDownWorkNewMsg(DownUserNewList[] userNewMsgLists, String s, String broadcastConstant) {
        for (DownUserNewList userNewMsgList : userNewMsgLists) {
            String departmentName = userNewMsgList.getDepartmentname();

            Log.d("NewMessage:----->", departmentName);
            UserNewMsgList[] userNewMsgLists1 = userNewMsgList.getPersoninfor();
            manageWorkNewMsg(userNewMsgLists1, s, broadcastConstant, departmentName);
        }
    }

    /**
     * 处理上级邀请的新消息，并保存到数据库，同时发出通知 ProgressReportActivity WorkItemsDetailActivity 接收该通知
     *
     * @param userids
     * @param uporDown          新消息类型 1 上级 0 下级
     * @param msgType           新消息类型 renwu,baoxiao
     * @param broadcastConstant 广播的消息标志
     * @return
     */
    private void manageInvitationNewMsg(String userids, String uporDown, String msgType, String broadcastConstant, String departmentName) {
        String idarray[] = userids.split(",");
        for (int j = 0; j < idarray.length; j++) {
            if (StringUtils.isNotBlank(idarray[j])) {// "" 空字符串不能保存到数据库中去

                Log.d("NewMessage:----->", idarray[j]);
                newMsgDB.addNewMsgByUserId(idarray[j], "1", uporDown, msgType, departmentName);
            }
        }
        //收到id，向workitem界面发送广播消息
        if (Constant.RenWu.equals(msgType)) {
            Intent intent = new Intent(broadcastConstant);
//            intent.putExtra("workname", userNewMsgLists[i].getUsername());
//            intent.putExtra("workId", userNewMsgLists[i].getId());
            intent.putExtra("upordown", uporDown.equals(Constant.ZXL_UP_USER_NEW_MSG + "") ? Constant.ZXL_UP_USER : Constant.ZXL_DOWN_USER);
            sendBroadcast(intent);
        }
    }
    /**
     * 处理下级邀请的新消息，并保存到数据库，同时发出通知 ProgressReportActivity WorkItemsDetailActivity 接收该通知
     *
     * @param uporDown          新消息类型 1 上级 0 下级
     * @param msgType           新消息类型 renwu,baoxiao
     * @param broadcastConstant 广播的消息标志
     * @return
     */
    private void manageDownInvitationNewMsg(WorkLowerBean[] workLowerBeans, String uporDown, String msgType, String broadcastConstant) {
        for (WorkLowerBean workLowerBean : workLowerBeans) {
            String departmentName = workLowerBean.getOffice_name();
            WorkLowerPersonInfor[] workLowerPersonInfor = workLowerBean.getUserid();
            for (WorkLowerPersonInfor workLowerPersonInfor1 : workLowerPersonInfor) {
                manageInvitationNewMsg(workLowerPersonInfor1.getUserid(), uporDown, msgType, broadcastConstant, departmentName);
            }
        }
    }


    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }
}