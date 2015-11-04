package com.jhlc.material;

import android.content.*;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.google.gson.GsonBuilder;
import com.jhlc.material.bean.PostBackDataBean;
import com.jhlc.material.db.NoteBookDB;
import com.jhlc.material.db.ProgressReportDB;
import com.jhlc.material.db.SetNewMsgDB;
import com.jhlc.material.db.UserListDB;
import com.jhlc.material.service.NewMessageService;
import com.jhlc.material.utils.Constant;
import com.jhlc.material.utils.LogZ;
import com.jhlc.material.utils.MYURL;
import com.jhlc.material.utils.PreferenceUtils;
import com.jhlc.material.view.MyRadioGroup;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import org.apache.http.Header;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2015/9/12 0012.
 */
public class MainFragmentActivity extends FragmentActivity {
    private static String TAG = "MainFragmentActivity";

    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private MyRadioGroup radioGroup;
    private RelativeLayout rl_task_tips, rl_down;

    private Fragment_down downFragment;
    private Fragment_up upFragment;
    private Fragment_log logFragment;
    private Fragment_notice supFragment;

    private SetNewMsgDB newMsgDB ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_tab_fragactivity);
        ZXLApplication.getInstance().addActivity(this);

        //友盟提示更新
        UmengUpdateAgent.update(this);

        initView();
        initListener();
        setSelect(0);//手动设置 默认选中项
        login();//联网登录
        IntentFilter filter = new IntentFilter(Constant.CancleTabFlagAction);

        newMsgDB = new SetNewMsgDB(MainFragmentActivity.this);
        registerReceiver(broadcastReceiver, filter);

        startService(new Intent(Constant.NewMessageServiceAction));
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            showRedPoint();
        }
    };


    private void initView() {
        radioGroup = (MyRadioGroup) findViewById(R.id.bottom_group);
        rl_task_tips = (RelativeLayout) findViewById(R.id.rl_task_tips);
        rl_down = (RelativeLayout) findViewById(R.id.rl_down_tips);
        ((RadioButton) radioGroup.findViewById(R.id.tab_rbtn_task)).setChecked(true);//设置默认项
    }

    private void initListener() {

        radioGroup.setOnCheckedChangeListener(new MyRadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(MyRadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.tab_rbtn_task:
                        setSelect(0);
                        break;
                    case R.id.tab_rbtn_exe:
                        setSelect(1);
                        break;
                    case R.id.tab_rbtn_log:
                        setSelect(2);
                        break;
                    case R.id.tab_rbtn_sup:
                        setSelect(3);
                        break;
                }
            }
        });
    }


    //选中
    private void setSelect(int position) {
        fragmentManager = getSupportFragmentManager();//
        //
        transaction = fragmentManager.beginTransaction();
        hideFragment(transaction);//
        switch (position) {
            //
            case 0:
                if (upFragment == null) {
                    upFragment = new Fragment_up();
                    transaction.add(R.id.content, upFragment);
                } else {
                    transaction.show(upFragment);
                }
                break;

            case 1:
                if (downFragment == null) {
                    downFragment = new Fragment_down();
                    transaction.add(R.id.content, downFragment);
                } else {
                    transaction.show(downFragment);
                }
                break;

            case 2:
                if (logFragment == null) {
                    logFragment = new Fragment_log();
                    transaction.add(R.id.content, logFragment);
                } else {
                    transaction.show(logFragment);
                }
                break;

            case 3:
                if (supFragment == null) {
                    supFragment = new Fragment_notice();
                    transaction.add(R.id.content, supFragment);
                } else {
                    transaction.show(supFragment);

                }
                break;

            default:
                break;
        }
        transaction.commit();//
    }

    private void hideFragment(FragmentTransaction transaction) {
        if (downFragment != null) {
            transaction.hide(downFragment);
        }
        if (upFragment != null) {
            transaction.hide(upFragment);
        }
        if (logFragment != null) {
            transaction.hide(logFragment);
        }
        if (supFragment != null) {
            transaction.hide(supFragment);
        }
    }

    //实现自动登录
    private void login() {
        if (PreferenceUtils.getIsLogin()) {
            AsyncHttpClient client = new AsyncHttpClient();

            String m_szAndroidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

            RequestParams params = new RequestParams();
            try {
                params.put("opcode", "ls_loginByNameAndPwd");
                params.put("userName", PreferenceUtils.getUserName());
                params.put("userPwd", PreferenceUtils.getPassWord());
                params.put("eid", Constant.EID);
                params.put("at", m_szAndroidID);

            } catch (Exception e) {
                e.printStackTrace();
            }

            client.post(MYURL.URL_HEAD, params, new AsyncHttpResponseHandler() {

                @Override
                public void onStart() {
                    super.onStart();
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    LogZ.d(TAG, new String(responseBody));
                    //{"code":100,"msg":"success","opcode":"reguser_eid_success","headimg":"/UserImage/201411080338542207.JPG"}

                    PostBackDataBean postBackDataBean = new GsonBuilder().create().fromJson(new String(responseBody), PostBackDataBean.class);
                    if (null == postBackDataBean) {
                        ZXLApplication.getInstance().showTextToast("请求失败！");
                    }
                    if (-1 == postBackDataBean.getCode()) {
                        ZXLApplication.getInstance().showTextToast(postBackDataBean.getMsg());
                    } else if (100 == postBackDataBean.getCode()) {
                        if ("reguser_eid_success".equals(postBackDataBean.getOpcode())) {
                            //成功登录 写入数据 释放radiogroup 的点击
                            PreferenceUtils.getInstance().setHeadImage(postBackDataBean.getHeadimg());

                        } else{
                            loginFailureToast(postBackDataBean.getMsg());
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    loginFailureToast("登录失败，请检查网络");
                }
            });
        }
    }


    //登录失败 需要跳转到登录界面
    private void loginFailureToast(String toast) {
        PreferenceUtils.getInstance().clearSP();
        PreferenceUtils.getInstance().setIsLogin(false);
        new SetNewMsgDB(this).delete();
        new UserListDB(this).delete();
        new ProgressReportDB(this).delete();
        new NoteBookDB(this).delete();
        ZXLApplication.getInstance().showTextToast(toast);
        Intent intent = new Intent(MainFragmentActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean isQuit = false;

    private final Timer timer = new Timer();

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // 双击推出程序
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (isQuit == false) {
                isQuit = true;
                Toast.makeText(getBaseContext(), R.string.MainFragment_back, Toast.LENGTH_LONG).show();
                TimerTask task = null;
                task = new TimerTask() {
                    @Override
                    public void run() {
                        isQuit = false;
                    }
                };
                timer.schedule(task, 2000);
            } else {
//                moveTaskToBack(true);
                finish();
            }
        } else {
            return super.dispatchKeyEvent(event);
        }
        return true;
    }

    private void showRedPoint() {
        if (newMsgDB.getByUpOrDown(Constant.ZXL_DOWN_USER_NEW_MSG + "") ) {
            rl_down.setVisibility(View.VISIBLE);
        } else {
            rl_down.setVisibility(View.GONE);
        }

        if (newMsgDB.getByUpOrDown(Constant.ZXL_UP_USER_NEW_MSG + "") ) {
            rl_task_tips.setVisibility(View.VISIBLE);
        } else {
            rl_task_tips.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        stopService(new Intent(Constant.NewMessageServiceAction));
    }

    @Override
    protected void onResume() {
        super.onResume();
        showRedPoint();
    }


}
