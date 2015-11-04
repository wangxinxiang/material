package com.jhlc.material;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.google.gson.GsonBuilder;
import com.jhlc.material.bean.PostBackDataBean;
import com.jhlc.material.db.NoteBookDB;
import com.jhlc.material.service.LoaderBusiness;
import com.jhlc.material.utils.*;
import com.jhlc.material.view.RoundImageView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 放在外面的登录界面 ，loading结束后出现
 */
public class LoginActivity extends Activity {
    private static final String TAG = "LoginActivity";
    private RelativeLayout rl_layout_info;
    private ImageButton ibtn_close_edit;
    private Button ibtn_submit_edit; //登录按钮
    private ImageView img_photo_mark, img_myinfo_logo, img_myinfo_logo_right;
    private RoundImageView img_head;
    private EditText et_user_name;
    private EditText et_user_pwd;
    private String path = ImageUtils.path;//sd路径;
    private String username;
    private String userpwd;
    private boolean headImgHasSetted = false;
    private TextView tv_user_name;

    private ZXLApplication zxlApplication = ZXLApplication.getInstance();
    private PreferenceUtils preferenceUtils = PreferenceUtils.getInstance();


    private RelativeLayout user_is_login;

    private ImageView user_is_login_image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.tab_myinfo);
        initView();
        initListener();
        ZXLApplication.getInstance().addActivity(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (PreferenceUtils.getIsLogin()) {
            setHeadImage();
            updateView();
        }


    }

    private void initView() {
        rl_layout_info = (RelativeLayout) findViewById(R.id.rl_layout_info);
        ibtn_close_edit = (ImageButton) findViewById(R.id.ibtn_close_edit);
        ibtn_submit_edit = (Button) findViewById(R.id.ibtn_submit_edit);
        img_head = (RoundImageView) findViewById(R.id.img_head);
        img_photo_mark = (ImageView) findViewById(R.id.img_photo_mark);
        et_user_name = (EditText) findViewById(R.id.et_user_name);
        et_user_pwd = (EditText) findViewById(R.id.et_user_pwd);
        tv_user_name = (TextView) findViewById(R.id.tv_user_name);
        img_myinfo_logo = (ImageView) findViewById(R.id.img_myinfo_logo);
        img_myinfo_logo_right = (ImageView) findViewById(R.id.img_myinfo_logo_right);
        //ZXLApplication.getInstance().showTextToast("开始获得焦点");
        et_user_name.requestFocus();
        et_user_name.setSelection(0);
        //不用更新界面，这个界面不会随着用户状态的变化而变化
        // updateView();

      /*  if(getIntent().getBooleanExtra("login",false)){
            ibtn_close_edit.setVisibility(View.VISIBLE);
            img_myinfo_logo_right.setVisibility(View.VISIBLE);
            img_myinfo_logo.setVisibility(View.GONE);
        }*/

        user_is_login = (RelativeLayout) findViewById(R.id.user_is_login);

        user_is_login_image = (ImageView) findViewById(R.id.user_is_login_image);
        user_is_login_image.setImageDrawable(this.getResources().getDrawable(R.drawable.submit_pressed));



    }

    private void updateView() {
        if (preferenceUtils.getIsLogin()) {
            rl_layout_info.setVisibility(View.GONE);
            //  tv_user_name.setText(PreferenceUtils.getInstance().getUserName());
        } else {
            rl_layout_info.setVisibility(View.VISIBLE);
            // tv_user_name.setText("上传头像");
        }
    }

    private void initListener() {
        ibtn_close_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ibtn_submit_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = et_user_name.getText().toString().trim();
                userpwd = et_user_pwd.getText().toString().trim();

                if (!("".equals(username))) {
                    if (!("".equals(userpwd))) {
                        //登录接口
                        upLoadImage();
                    } else {
                        zxlApplication.showTextToast("请填写你的密码");
                    }
//                    if(isRegister) { 不上传头像也能登录

//                    } else {
//                        zxlApplication.showTextToast("请选择你的头像");
//                    }
                } else {
                    zxlApplication.showTextToast("请填写你的名字");
                }
            }
        });

        img_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PreferenceUtils.getIsLogin()) {
                    LogoutDialog logoutDialog = new LogoutDialog(LoginActivity.this, R.style.MyDialog);
                    logoutDialog.show();
                    WindowManager windowManager = getWindowManager();
                    Display display = windowManager.getDefaultDisplay();
                    WindowManager.LayoutParams lp = logoutDialog.getWindow().getAttributes();
                    lp.width = (int) (display.getWidth()); //设置宽度
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
                    Intent intent = new Intent(LoginActivity.this, AddHeadImageActivity.class);
                    startActivityForResult(intent, Constant.RequestCode);
                }
            }
        });
        user_is_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user_is_login_image.getDrawable() == null) {
                    user_is_login_image.setImageDrawable(getResources().getDrawable(R.drawable.submit_pressed));
                } else {
                    user_is_login_image.setImageDrawable(null);
                }
            }
        });

    }

    private void setUserInfo() {
        try {
            LogZ.d("getIsLogin--> ", "" + PreferenceUtils.getIsLogin());
            LoaderBusiness.loadImage(MYURL.URL_HEAD + PreferenceUtils.getHeadImage(), img_head);

            if (PreferenceUtils.getIsLogin()) {
                tv_user_name.setText(PreferenceUtils.getUserName());
            } else {
                tv_user_name.setText("上传头像");
            }
            rl_layout_info.setVisibility(View.VISIBLE);
            img_photo_mark.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //可以根据多个请求代码来作相应的操作
        if (Constant.ResultCode == resultCode) {
            headImgHasSetted = true;
            PreferenceUtils.getInstance().setHeadImgHasSetted(true);
            setHeadImage();
        } else {
            headImgHasSetted = false;
//            ZXLApplication.getInstance().showTextToast("没设置头像");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setHeadImage() {


        File file = new File(path + "head.jpg");
        if (!file.exists()) {
            return;
        }

        Bitmap bt = BitmapFactory.decodeFile(path + "head.jpg");//从Sd中找头像，转换成Bitmap

        if (bt != null) {
            @SuppressWarnings("deprecation")
            Drawable drawable = new BitmapDrawable(bt);//转换成drawable
            img_head.setImageDrawable(drawable);
            img_photo_mark.setVisibility(View.GONE);
        } else {
            /**
             *	如果SD里面没有则需要从服务器取头像，取回来的头像再保存在SD中
             */
        }
    }

    // 上传图片 和登录接口
    private void  upLoadImage() {

        AsyncHttpClient client = new AsyncHttpClient();

        File file = new File(path + "head.jpg");
        String m_szAndroidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        RequestParams params = new RequestParams();
        try {
            //pwd 有2 是验证码登录 没有2是123456密码登录
            params.put("opcode", "ls_loginByNameAndPwd");
            params.put("userName", username);
            params.put("userPwd", userpwd);
            params.put("eid", Constant.EID);
            if (headImgHasSetted && file.exists() && file.length() > 0) {
                params.put("profile_picture", file);
            }
            params.put("at", m_szAndroidID);
//                LogZ.d("upLoadImage--file> ","exists");

        } catch (Exception e) {
            e.printStackTrace();
        }

        client.post(MYURL.URL_HEAD, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                findViewById(R.id.loading_bar_rl).setVisibility(View.VISIBLE);
                ibtn_submit_edit.setEnabled(false);
                img_head.setEnabled(false);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                findViewById(R.id.loading_bar_rl).setVisibility(View.GONE);

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                LogZ.d(TAG, new String(responseBody));
                //{"code":100,"msg":"success","opcode":"reguser_eid_success","headimg":"/UserImage/201411080338542207.JPG"}

                PostBackDataBean postBackDataBean = new GsonBuilder().create().fromJson(new String(responseBody), PostBackDataBean.class);
                if (null != postBackDataBean) {
                    boolean rightLogin = getShowMsg(postBackDataBean);
                    //登陆失败
                    if (!rightLogin) {
                        LogZ.d("rightLogin--> ", rightLogin + "rightLogin1");
//                            findViewById(R.id.loading_bar_rl).setVisibility(View.GONE);
//                            //tv_user_name.setText(username);
//                            rl_layout_info.setVisibility(View.VISIBLE);
//                            ibtn_submit_edit.setVisibility(View.VISIBLE);
                        ibtn_submit_edit.setEnabled(true);
                        img_head.setEnabled(true);
                    }
                    //登陆成功
                    else {
                        LogZ.d("rightLogin--> ", rightLogin + "rightLogin2");
                        tv_user_name.setText(username);
//                            rl_layout_info.setVisibility(View.GONE);
                        PreferenceUtils.getInstance().setHeadImage(postBackDataBean.getHeadimg());
                    }
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                zxlApplication.showTextToast("注册失败，请检查网络");
                findViewById(R.id.loading_bar_rl).setVisibility(View.GONE);
                ibtn_submit_edit.setEnabled(true);
                img_head.setEnabled(true);
            }
        });
    }

    private boolean getShowMsg(PostBackDataBean postBackDataBean) {
        if (null == postBackDataBean) {
            zxlApplication.showTextToast("注册请求失败！");
        }
        LogZ.d("Login---->msg", postBackDataBean.getOpcode());
        if (-1 == postBackDataBean.getCode()) {
            zxlApplication.showTextToast(postBackDataBean.getMsg());
            return false;
        } else if (100 == postBackDataBean.getCode()) {
            Log.d("postBackDataBean", postBackDataBean.getOpcode());
            if ("reguser_eid_success".equals(postBackDataBean.getOpcode())) {
                //注册新的用户 或者更换用户时 清除之前数据库的数据
                zxlApplication.showTextToast("登录成功！");
                preferenceUtils.setUserName(username);
                preferenceUtils.setPassWord(userpwd);
                preferenceUtils.setHeadImage(postBackDataBean.getHeadimg());
                if (user_is_login_image.getDrawable() == null) {
                    preferenceUtils.setIsLogin(false);
                } else {
                    preferenceUtils.setIsLogin(true);
                }

                Intent intent = new Intent(LoginActivity.this, MainFragmentActivity.class);
                //登录成功 界面跳转
                startActivity(intent);
                finish();
                return true;
            } else {
                zxlApplication.showTextToastLong(postBackDataBean.getMsg());
                return false;
            }
//            else if("userName_exist".equals(postBackDataBean.getOpcode())){
//                zxlApplication.showTextToast("没有您输入的名字，请仔细核对");
//                return true;
//            }

        }
        return true;
    }


    //发送验证码 的网络请求
    private void getIdentifyingCode() {
        AsyncHttpClient client = new AsyncHttpClient();

        String m_szAndroidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        RequestParams params = new RequestParams();

        params.put("opcode", "mobilevalidate");
        params.put("userName", username);
        params.put("eid", Constant.EID);
        params.put("at", m_szAndroidID);

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

                LogZ.d("upLoadImage--> ", new String(responseBody));
                //{"code":100,"msg":"success","opcode":"reguser_eid_success","headimg":"/UserImage/201411080338542207.JPG"}

                PostBackDataBean postBackDataBean = new GsonBuilder().create().fromJson(new String(responseBody), PostBackDataBean.class);
                if (null != postBackDataBean) {
                    if (100 == postBackDataBean.getCode() && "send_validatemessage_success".equals(postBackDataBean.getOpcode())) {
                        zxlApplication.showTextToastLong("请求成功，请等待短信验证码");
                    } else {
                        zxlApplication.showTextToastLong(postBackDataBean.getMsg());
                    }


                } else {
                    zxlApplication.showTextToastLong("服务端返回未空");
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                zxlApplication.showTextToast("发送失败，请检查网络");
            }
        });
    }


    private boolean isQuit = false;

    private final Timer timer = new Timer();

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 双击推出程序
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isQuit == false) {
                isQuit = true;
                Toast.makeText(getBaseContext(), "再按一次退出", Toast.LENGTH_LONG).show();
                TimerTask task = null;
                task = new TimerTask() {
                    @Override
                    public void run() {
                        isQuit = false;
                    }
                };
                timer.schedule(task, 2000);
            } else {
                //HygApplication.getInstance().exit();
                ZXLApplication.getInstance().exit();
            }
        }
        return true;
    }

//    class MyCountDownTimer extends CountDownTimer {
//        /**
//         * @param millisInFuture    表示以毫秒为单位 倒计时的总数
//         *                          <p>
//         *                          例如 millisInFuture=1000 表示1秒
//         * @param countDownInterval 表示 间隔 多少微秒 调用一次 onTick 方法
//         *                          <p>
//         *                          例如: countDownInterval =1000 ; 表示每1000毫秒调用一次onTick()
//         */
//        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
//            super(millisInFuture, countDownInterval);
//        }
//
//
//        @Override
//        public void onFinish() {
//            get_identifying_code.setText("获取验证码");
//            get_identifying_code.setEnabled(true);
//        }
//
//        @Override
//        public void onTick(long millisUntilFinished) {
//            get_identifying_code.setText(millisUntilFinished / 1000 + "秒后重新获取");
//        }
//    }
}
