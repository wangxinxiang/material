package com.jhlc.material;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.*;
import android.widget.*;

import com.android.volley.VolleyError;
import com.google.gson.GsonBuilder;
import com.jhlc.material.bean.DownUserList;
import com.jhlc.material.bean.PostBackDataBean;
import com.jhlc.material.bean.UserBean;
import com.jhlc.material.db.*;
import com.jhlc.material.service.LoaderBusiness;
import com.jhlc.material.utils.*;
import com.jhlc.material.view.OkAndCancelDialog;
import com.jhlc.material.view.RoundImageView;
import com.jhlc.material.volley.VolleyInterface;
import com.jhlc.material.volley.VolleyRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 设置界面
 */
public class SettingActivity extends FragmentActivity implements OkAndCancelDialog.OnDialogClickListener{
    private final static String TAG = "SettingActivity";

    private Button ibtn_changeHeadImg, ibtn_logout, ibtn_changePassWord;
    private RoundImageView img_head;
    private String path = ImageUtils.path;//sd路径;

    private boolean isRegister = false;
    private TextView tv_user_name;

    private ZXLApplication zxlApplication = ZXLApplication.getInstance();
    private PreferenceUtils preferenceUtils = PreferenceUtils.getInstance();
    //用户提交给服务器的用户名
    private String username;
    private String inputName;

    private ProgressDialog pd;


    public final static String TAG_DIALOG_SUBMIT = "TAG_DIALOG_SUBMIT";
    public final static int TYPE_SUBMIT = 0;//dialog 对话框需要的常量
    private OkAndCancelDialog dialog; //提示框
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        initView();
        setListener();
        ZXLApplication.getInstance().addActivity(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setHeadImage();
        tv_user_name.setText(preferenceUtils.getUserName());
    }

    private void initView() {
        ibtn_changeHeadImg = (Button) findViewById(R.id.ibtn_changeHeadImg);
        ibtn_logout = (Button) findViewById(R.id.ibtn_logout);
        img_head = (RoundImageView) findViewById(R.id.img_head);
        tv_user_name = (TextView) findViewById(R.id.tv_user_name);
        ibtn_changePassWord = (Button) findViewById(R.id.ibtn_changePassWord);


        LoaderBusiness.loadImage(MYURL.img_HEAD + PreferenceUtils.getInstance().getHeadImage(), img_head);

        TextView versiontv = (TextView) findViewById(R.id.version_tv);
        versiontv.setText(getVersion() + "版本");


    }


    private void setListener() {

        /*ibtn_submit_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = et_user_name.getText().toString().trim();
                if(!("".equals(username))) {
                    if(isRegister) {
                        upLoadImage();
                    } else {
                        zxlApplication.showTextToast("请选择你的头像");
                    }
                } else {
                    zxlApplication.showTextToast("请填写你的名字");
                }
            }
        });*/


        ibtn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showOkAndCancelDialog(TAG_DIALOG_SUBMIT, "是否确定退出？", TYPE_SUBMIT);
            }
        });


        findViewById(R.id.ibtn_baoxiao).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUpdateDialog();
            }
        });


        ibtn_changeHeadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SettingActivity.this, SettingAddHeadImageActivity.class);
                intent.putExtra(Constant.ChangeHeadImage, Constant.ChangeHeadImage);
                startActivityForResult(intent, Constant.settting_changeheadimg);
                /*if(PreferenceUtils.getInstance().getIsLogin()){
                    LogoutDialog logoutDialog = new LogoutDialog(SettingActivity.this,R.style.MyDialog);
                    logoutDialog.show();
                    WindowManager windowManager = getWindowManager();
                    Display display = windowManager.getDefaultDisplay();
                    WindowManager.LayoutParams lp = logoutDialog.getWindow().getAttributes();
                    lp.width = (int)(display.getWidth()); //设置宽度
                    Window window = logoutDialog.getWindow();
                    window.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的地位
                    window.setAttributes(lp);

                    logoutDialog.setRefreshViewListener(new LogoutDialog.RefreshViewListener() {
                        @Override
                        public void refresh() {
                            setUserInfo();
                        }
                    });
                } else {
                    Intent intent = new Intent(SettingActivity.this, AddHeadImageActivity.class);
                    startActivityForResult(intent, Constant.RequestCode);
                }*/
            }
        });

        ibtn_changePassWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePasswordDialog dialog = new ChangePasswordDialog();
                dialog.show(getSupportFragmentManager(), "");
            }
        });
    }


    //创建出dialog
    //参数说明 Tag 为设置管理栈中的tag message为提示内容 type为对话框类型
    private void showOkAndCancelDialog(String Tag, String message, int type) {
        dialog = OkAndCancelDialog.newInstance(message, type);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        dialog.show(transaction, Tag);
    }

    @Override
    public void onDialogClick(String Tag, boolean isOk, int Type) {
        //自定dialog点击事件监听
        dialog.dismiss();
       if (isOk){
           userLogout();
       }
    }

    private void setUserInfo() {
        try {
            LogZ.d("getIsLogin--> ", "" + PreferenceUtils.getInstance().getIsLogin());
            LoaderBusiness.loadImage(MYURL.img_HEAD + PreferenceUtils.getInstance().getHeadImage(), img_head);

            if (PreferenceUtils.getInstance().getIsLogin()) {
                tv_user_name.setText(PreferenceUtils.getInstance().getUserName());
            } else {
                tv_user_name.setText("上传头像");
            }
//            rl_layout_info.setVisibility(View.VISIBLE);
//            img_photo_mark.setVisibility(View.VISIBLE);
            isRegister = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // ZXLApplication.getInstance().showTextToast("开始准备修改头像"+(Constant.settting_changeheadimg == resultCode));
        //请求更换头像
        if (Constant.settting_changeheadimg == resultCode) {
            isRegister = true;
            PreferenceUtils.getInstance().setHeadImgHasSetted(true);
            setHeadImage();
            LogZ.d("lyjtakephoto", "获得照片,是否应该发送请求？");

            upLoadImage();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setHeadImage() {

        if (PreferenceUtils.getHeadImgHasSetted()) {


            File file = new File(path + "head.jpg");
            if (!file.exists()) {
                return;
            }
            Bitmap bt = BitmapFactory.decodeFile(path + "head.jpg");//从Sd中找头像，转换成Bitmap
            if (bt != null) {
                @SuppressWarnings("deprecation")
                Drawable drawable = new BitmapDrawable(bt);//转换成drawable
                img_head.setImageDrawable(drawable);
//            img_photo_mark.setVisibility(View.GONE);
            } else {
                /**
                 *	如果SD里面没有则需要从服务器取头像，取回来的头像再保存在SD中
                 */
            }
        } else {
            if (StringUtils.isNotBlank(PreferenceUtils.getHeadImage())) {
                LoaderBusiness.loadImage(MYURL.img_HEAD + PreferenceUtils.getHeadImage(), img_head);
            }
        }
    }

    // 上传图片
    private void upLoadImage() {

        AsyncHttpClient client = new AsyncHttpClient();

        File file = new File(path + "head.jpg");
        String m_szAndroidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        if (file.exists() && file.length() > 0) {
            RequestParams params = new RequestParams();
            try {
                params.put("opcode", "reg_user_aid");
                params.put("Username", preferenceUtils.getUserName());
                params.put("eid", Constant.EID);
                params.put("profile_picture", file);
                params.put("at", m_szAndroidID);
                LogZ.d("upLoadImage--file> ", "exists");

            } catch (Exception e) {
                e.printStackTrace();
            }

            client.post(MYURL.URL_HEAD, params, new AsyncHttpResponseHandler() {

                @Override
                public void onStart() {
                    super.onStart();
                    TextView loading_tv = (TextView) findViewById(R.id.catalogue_loading_bar_msg);
                    loading_tv.setText("正在上传头像...");
                    findViewById(R.id.loading_bar_rl).setVisibility(View.VISIBLE);
                    ibtn_changeHeadImg.setEnabled(false);
                    ibtn_changePassWord.setEnabled(false);
                    ibtn_logout.setEnabled(false);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    LogZ.d("upLoadImage--> ", new String(responseBody));
                    //{"code":100,"msg":"success","opcode":"reguser_eid_success","headimg":"/UserImage/201411080338542207.JPG"}

                    PostBackDataBean postBackDataBean = new GsonBuilder().create().fromJson(new String(responseBody), PostBackDataBean.class);
                    if (null != postBackDataBean) {
                        getShowMsg(postBackDataBean);
                    }
                    findViewById(R.id.loading_bar_rl).setVisibility(View.GONE);
                    ibtn_changeHeadImg.setEnabled(true);
                    ibtn_changePassWord.setEnabled(true);
                    ibtn_logout.setEnabled(true);
                    ZXLApplication.getInstance().showTextToast("头像修改成功！");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    zxlApplication.showTextToast("服务器出错，修改失败！");
                    findViewById(R.id.loading_bar_rl).setVisibility(View.GONE);
                    ibtn_changeHeadImg.setEnabled(true);
                    ibtn_changePassWord.setEnabled(true);
                    ibtn_logout.setEnabled(true);

                }
            });
        } else {
            zxlApplication.showTextToast("文件不存在");
        }
    }

    //登出联网操作
    private void userLogout() {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        try {
            String m_szAndroidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            params.put("opcode", "logout");
            params.put("Username", preferenceUtils.getUserName());
            params.put("eid", Constant.EID);
            params.put("token", m_szAndroidID);

        } catch (Exception e) {
            e.printStackTrace();
        }

        client.post(MYURL.URL_HEAD, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                TextView loading_tv = (TextView) findViewById(R.id.catalogue_loading_bar_msg);
                loading_tv.setText("正在退出...");
                findViewById(R.id.loading_bar_rl).setVisibility(View.VISIBLE);
                ibtn_changeHeadImg.setEnabled(false);
                ibtn_changePassWord.setEnabled(false);
                ibtn_logout.setEnabled(false);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                LogZ.d("upLoadImage--> ", new String(responseBody));
                //{"code":100,"msg":"success","opcode":"reguser_eid_success","headimg":"/UserImage/201411080338542207.JPG"}

                PostBackDataBean postBackDataBean = new GsonBuilder().create().fromJson(new String(responseBody), PostBackDataBean.class);
                if (null != postBackDataBean) {
                    boolean flag = getLogoutShowMsg(postBackDataBean);
                    if (flag) {
                        PreferenceUtils.getInstance().clearSP();
                        new SetNewMsgDB(SettingActivity.this).delete();
                        new UserListDB(SettingActivity.this).delete();
                        new ProgressReportDB(SettingActivity.this).delete();
                        new NoteBookDB(SettingActivity.this).delete();
//                        new OfficeDB(SettingActivity.this).delete();
                        PreferenceUtils.getInstance().setIsLogin(false);
                        PreferenceUtils.getInstance().setCurrentTimeMillis(0);
                        ZXLApplication.getInstance().showTextToast("退出成功");
                        //测试提交，随意输入字符
                        Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                        startActivity(intent);
                        ZXLApplication.getInstance().exit(SettingActivity.this);
                        finish();
                    } else {
                        findViewById(R.id.loading_bar_rl).setVisibility(View.GONE);
                        ibtn_changeHeadImg.setEnabled(true);
                        ibtn_changePassWord.setEnabled(true);
                        ibtn_logout.setEnabled(true);
                    }
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                zxlApplication.showTextToast("服务器出错，修改失败！");
                findViewById(R.id.loading_bar_rl).setVisibility(View.GONE);
                ibtn_changeHeadImg.setEnabled(true);
                ibtn_changePassWord.setEnabled(true);
                ibtn_logout.setEnabled(true);

            }
        });
    }


    private void changeName(final String inputName) {

        AsyncHttpClient client = new AsyncHttpClient();


        RequestParams params = new RequestParams();

        params.put("opcode", "update_username");
        params.put("Username", preferenceUtils.getUserName());
        params.put("eid", Constant.EID);
        params.put("newusername", inputName);
        this.inputName = inputName;


        client.post(MYURL.URL_HEAD, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                TextView loading_tv = (TextView) findViewById(R.id.catalogue_loading_bar_msg);
                loading_tv.setText("正在修改用户名...");
                findViewById(R.id.loading_bar_rl).setVisibility(View.VISIBLE);
                ibtn_changeHeadImg.setEnabled(false);
                ibtn_changePassWord.setEnabled(false);
                ibtn_logout.setEnabled(false);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                LogZ.d("upLoadImage--> ", new String(responseBody));
                //{"code":100,"msg":"success","opcode":"reguser_eid_success","headimg":"/UserImage/201411080338542207.JPG"}

                PostBackDataBean postBackDataBean = new GsonBuilder().create().fromJson(new String(responseBody), PostBackDataBean.class);
                if (null != postBackDataBean) {
                    getShowChangeNameMsg(postBackDataBean);
                }
                findViewById(R.id.loading_bar_rl).setVisibility(View.GONE);
                ibtn_changeHeadImg.setEnabled(true);
                ibtn_changePassWord.setEnabled(true);
                ibtn_logout.setEnabled(true);


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                zxlApplication.showTextToast("服务器出错，修改失败！");
                findViewById(R.id.loading_bar_rl).setVisibility(View.GONE);
                ibtn_changeHeadImg.setEnabled(true);
                ibtn_changePassWord.setEnabled(true);
                ibtn_logout.setEnabled(true);

            }
        });

    }

    private boolean getLogoutShowMsg(PostBackDataBean postBackDataBean) {
        if (null == postBackDataBean) {
            zxlApplication.showTextToast("退出请求失败！");
            return false;
        }

        if (-1 == postBackDataBean.getCode()) {
            zxlApplication.showTextToast(postBackDataBean.getMsg());
            return false;
        } else if (100 == postBackDataBean.getCode()) {
            zxlApplication.showTextToast("退出成功");
            return true;
        } else {
            zxlApplication.showTextToast("退出失败");
            return false;
        }
    }

    private void getShowMsg(PostBackDataBean postBackDataBean) {
        if (null == postBackDataBean) {
            zxlApplication.showTextToast("修改请求失败！");
        }

        if (-1 == postBackDataBean.getCode()) {
            zxlApplication.showTextToast(postBackDataBean.getMsg());
        } else if (100 == postBackDataBean.getCode()) {
            if ("eid_exist".equals(postBackDataBean.getOpcode())) {
                zxlApplication.showTextToast("该企业号已存在");
            } else if ("reguser_eid_success".equals(postBackDataBean.getOpcode())) {
                zxlApplication.showTextToast("修改成功！");
                preferenceUtils.setHeadImage(postBackDataBean.getHeadimg());
                //finish();
            }
        }
    }

    private void getShowChangeNameMsg(PostBackDataBean postBackDataBean) {
        if (null == postBackDataBean) {
            zxlApplication.showTextToast("修改请求失败！");
        }

        if (-1 == postBackDataBean.getCode()) {
            zxlApplication.showTextToast(postBackDataBean.getMsg());
        } else if (100 == postBackDataBean.getCode()) {
            if ("username_notexist".equals(postBackDataBean.getOpcode())) {
                zxlApplication.showTextToast("用户名无效，请重新登录");
            } else if ("newusername_exist".equals(postBackDataBean.getOpcode())) {
                zxlApplication.showTextToast("新的用户名已经存在，请换一个用户名");

            } else if ("update_username_success".equals(postBackDataBean.getOpcode())) {
                zxlApplication.showTextToast("用户名修改成功");
                //更换用户名的时候同时也考虑将本地数据库里记录的用户名更换掉
                new NoteBookDB(getApplicationContext()).changeName(preferenceUtils.getUserName(), inputName);

                tv_user_name.setText(inputName);
                PreferenceUtils.getInstance().clearSP();
                new SetNewMsgDB(SettingActivity.this).delete();
                new UserListDB(SettingActivity.this).delete();
                new ProgressReportDB(SettingActivity.this).delete();
                new NoteBookDB(SettingActivity.this).delete();
                new OfficeDB(SettingActivity.this).delete();
                PreferenceUtils.getInstance().setCurrentTimeMillis(0);
                PreferenceUtils.getInstance().setUserName(inputName);

            }
        }
    }


    private void inputTitleDialog() {

        final EditText inputServer = new EditText(this);
        inputServer.setFocusable(true);
        inputServer.setText(tv_user_name.getText().toString());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请输入您要更换的姓名").setIcon(
                null).setView(inputServer).setNegativeButton("取消", null);
        builder.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        String inputName = inputServer.getText().toString();
                        if ("".equals(inputName)) {
                            ZXLApplication.getInstance().showTextToast("要更换的姓名不能为空!" + inputName);
                        }else if(preferenceUtils.getUserName().equals(inputName)){
//                            ZXLApplication.getInstance().showTextToast("");
                        } else {
                            changeName(inputName);
                        }
                    }
                });
        builder.show();
        inputServer.requestFocus();
//        inputServer.
    }

    private void showUpdateDialog() {
        // final ProgressDialog pd = ProgressDialog.show(this, "正在检查版本,请稍后...", "请等待", true);
        final AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
        AlertDialog dialog = builder
                .setMessage("即将下载新版94执行力（体验报销功能）,是否更新？")
                .setNeutralButton("下次再说", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        PreferenceUtils.getInstance().setNeedUpdate(false);
                    }
                }).setPositiveButton("确定更新", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        pBar = new ProgressDialog(SettingActivity.this);
                        pBar.setTitle("正在下载");
                        pBar.setMessage("下载中...如果网络状况不好,请稍后再试");
                        pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        pBar.setCancelable(false);
                        pBar.setButton(DialogInterface.BUTTON_POSITIVE, "取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                needInterrupt = true;


                            }
                        });
                        downFile("http://www.94.com/images/zxl_2_0.apk");

//                                        downFile("http://bcscdn.baidu.com/netdisk/BaiduYun_6.1.1.apk");

                        // Toast.makeText(getBaseContext(), "成功:开始更新", Toast.LENGTH_SHORT).show();
                    }
                })
                .setCancelable(false)
                .create();
        dialog.show();
    }

    //以下代码用于版本更新=========================================
    private void sendMsg(int flag) {
        Message msg = new Message();
        msg.what = flag;
        downHandler.sendMessage(msg);
    }

    int fileSize;
    int downLoadFileSize;
    private Handler downHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {//定义一个Handler，用于处理下载线程与UI间通讯
            if (!Thread.currentThread().isInterrupted()) {
                switch (msg.what) {
                    case 0:
                        pBar.setMax(fileSize);
                    case 1:
                        pBar.setProgress(downLoadFileSize);
                        int result = downLoadFileSize * 100 / fileSize;
                        break;
                    case 2:
                        Toast.makeText(getApplicationContext(), "文件下载完成", Toast.LENGTH_SHORT).show();
                        break;

                    case -1:
                        String error = msg.getData().getString("error");
                        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                        break;

                    case 99:
                        pBar.dismiss();
                        downLoadFileSize = 0;
                        try {
                            downT.interrupt();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(getApplicationContext());
                        AlertDialog dialog2 = builder2
                                .setMessage("更新已取消")
                                .setNeutralButton("确定", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialoginterface, int i) {
                                        // finish();
                                    }
                                }).setCancelable(false)
                                .create();
                        dialog2.show();
                        break;

                    case 100:
                        pBar.dismiss();
                        downLoadFileSize = 0;
                        try {
                            downT.interrupt();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                        AlertDialog dialog = builder
                                .setMessage("网络超时,请在网络状况较好地时候更新")
                                .setNeutralButton("确定", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialoginterface, int i) {
                                        // finish();
                                    }
                                }).setCancelable(false)
                                .create();
                        dialog.show();
                        break;
                }
            }
            super.handleMessage(msg);
        }
    };
    boolean needInterrupt = false;
    public ProgressDialog pBar;
    private Handler handler = new Handler();
    private int maxidleSec = 10;
    private int nowidleSec = 0;
    private boolean sendIdleFlag = true;
    Thread downT = null;

    void downFile(final String url) {
        pBar.show();
        new Thread() {
            public void run() {
                while (!needInterrupt && nowidleSec < maxidleSec) {
                    try {
                        sleep(1000);
                        nowidleSec++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
                if (sendIdleFlag && !needInterrupt) {
                    sendMsg(100);
                } else if (needInterrupt) {
                    sendMsg(99);
                }
            }
        }.start();
        downT = new Thread() {
            public void run() {
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(url);
                HttpResponse response;
                try {
                    response = client.execute(get);
                    HttpEntity entity = response.getEntity();
                    long length = entity.getContentLength();
                    fileSize = (int) length;
                    if (fileSize <= 0) throw new RuntimeException("无法获知文件大小 ");
                    sendMsg(0);
                    InputStream is = entity.getContent();
                    FileOutputStream fileOutputStream = null;
                    if (is != null) {

                        File file = new File(
                                Environment.getExternalStorageDirectory() + "/ZxlHead/",
                                "updateapksamples_2.apk");
                        fileOutputStream = new FileOutputStream(file);

                        byte[] buf = new byte[1024];
                        int ch = -1;
                        int count = 0;

                        while (!needInterrupt && (ch = is.read(buf)) != -1) {
                            fileOutputStream.write(buf, 0, ch);
                            count += ch;
                            downLoadFileSize += ch;
                            nowidleSec = 0;
                            sendMsg(1);//更新进度条
                            if (length > 0) {
                            }
                        }
                        if (needInterrupt) { //在取消下载的情况下
//                            sendMsg(99);
                            return;
                        }
                        sendMsg(2);//通知下载完成
                        sendIdleFlag = false;
                    }
                    fileOutputStream.flush();
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                    down();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        };
        downT.start();

    }

    void down() {
        handler.post(new Runnable() {
            public void run() {
                pBar.cancel();
                update();
            }
        });
    }

    void update() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//解决升级过程中闪退的问题
        intent.setDataAndType(Uri.fromFile(new File(Environment
                        .getExternalStorageDirectory() + "/ZxlHead/", "updateapksamples_2.apk")),
                "application/vnd.android.package-archive");
        startActivity(intent);
    }

    private String getVersion() {
        PackageInfo info;
        try {
            info = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            // 当前应用的版本名称
            String versionName = info.versionName;
            // 当前版本的版本号
            int versionCode = info.versionCode;
            // 当前版本的包名
            String packageNames = info.packageName;
            return versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "1.0";
    }



}
