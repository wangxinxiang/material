package com.jhlc.material;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.google.gson.GsonBuilder;
import com.jhlc.material.bean.PostBackDataBean;
import com.jhlc.material.service.LoaderBusiness;
import com.jhlc.material.utils.*;
import com.jhlc.material.view.RoundImageView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;

import java.io.File;

/**
 */
public class MyInfoActivity extends Activity {
    private RelativeLayout rl_layout_info;
    private ImageButton ibtn_close_edit;
    private Button      ibtn_submit_edit;
    private ImageView   img_photo_mark,img_myinfo_logo,img_myinfo_logo_right;
    private RoundImageView img_head;
    private EditText    et_user_name;
    private String      path = ImageUtils.path;//sd路径;
    private String      username;
    private boolean     isRegister = false;
    private TextView    tv_user_name;

    private ZXLApplication  zxlApplication  = ZXLApplication.getInstance();
    private PreferenceUtils preferenceUtils = PreferenceUtils.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_myinfo);
        initView();
        setListener();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(preferenceUtils.getIsLogin()){
            setHeadImage();
            updateView();
        }
    }

    private void initView() {
        rl_layout_info   = (RelativeLayout) findViewById(R.id.rl_layout_info);
        ibtn_close_edit  = (ImageButton)    findViewById(R.id.ibtn_close_edit);
        ibtn_submit_edit = (Button)         findViewById(R.id.ibtn_submit_edit);
        img_head         = (RoundImageView) findViewById(R.id.img_head);
        img_photo_mark   = (ImageView) findViewById(R.id.img_photo_mark);
        et_user_name     = (EditText)  findViewById(R.id.et_user_name);
        tv_user_name     = (TextView)  findViewById(R.id.tv_user_name);
        img_myinfo_logo  = (ImageView) findViewById(R.id.img_myinfo_logo);
        img_myinfo_logo_right = (ImageView) findViewById(R.id.img_myinfo_logo_right);

        updateView();

        if(getIntent().getBooleanExtra("login",false)){
            ibtn_close_edit.setVisibility(View.VISIBLE);
            img_myinfo_logo_right.setVisibility(View.VISIBLE);
            img_myinfo_logo.setVisibility(View.GONE);
        }

    }

    private void updateView() {
        if(preferenceUtils.getIsLogin()) {
            rl_layout_info.setVisibility(View.GONE);
            tv_user_name.setText(PreferenceUtils.getInstance().getUserName());
        } else {
            rl_layout_info.setVisibility(View.VISIBLE);
            tv_user_name.setText("上传头像");
        }
    }

    private void setListener() {
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
        });

        img_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PreferenceUtils.getInstance().getIsLogin()){
                    LogoutDialog logoutDialog = new LogoutDialog(MyInfoActivity.this,R.style.MyDialog);
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
                    Intent intent = new Intent(MyInfoActivity.this, AddHeadImageActivity.class);
                    startActivityForResult(intent, Constant.RequestCode);
                }
            }
        });
    }

    private void setUserInfo() {
        try {
            LogZ.d("getIsLogin--> ", "" + PreferenceUtils.getInstance().getIsLogin());
            LoaderBusiness.loadImage(MYURL.URL_HEAD + PreferenceUtils.getInstance().getHeadImage(), img_head);

            if(PreferenceUtils.getInstance().getIsLogin()) {
                tv_user_name.setText(PreferenceUtils.getInstance().getUserName());
            } else {
                tv_user_name.setText("上传头像");
            }
            rl_layout_info.setVisibility(View.VISIBLE);
            img_photo_mark.setVisibility(View.VISIBLE);
            isRegister = false;
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        //可以根据多个请求代码来作相应的操作
        if(Constant.ResultCode == resultCode) {
            isRegister = true;
            setHeadImage();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setHeadImage() {

        Bitmap bt = BitmapFactory.decodeFile(path + "head.jpg");//从Sd中找头像，转换成Bitmap

        File file = new File(path + "head.jpg");
        if(file.exists()){
            Log.e("file--> ",path + "head.jpg"+" -- "+file.length());
        }

        if(bt!=null){
            @SuppressWarnings("deprecation")
            Drawable drawable = new BitmapDrawable(bt);//转换成drawable
            img_head.setImageDrawable(drawable);
            img_photo_mark.setVisibility(View.GONE);
        }else{
            /**
             *	如果SD里面没有则需要从服务器取头像，取回来的头像再保存在SD中
             */
        }
    }

    // 上传图片
    private void upLoadImage(){

        AsyncHttpClient client = new AsyncHttpClient();

        File file = new File(path + "head.jpg");

        if(file.exists() && file.length() > 0){
            RequestParams params = new RequestParams();
            try {
                params.put("opcode","reg_user_aid");
                params.put("Username",username);
                params.put("eid", Constant.EID);
                params.put("profile_picture", file);
                LogZ.d("upLoadImage--file> ","exists");

            } catch (Exception e) {
                e.printStackTrace();
            }

            client.post(MYURL.URL_HEAD, params, new AsyncHttpResponseHandler(){

                @Override
                public void onStart() {
                    super.onStart();
                    findViewById(R.id.loading_bar_rl).setVisibility(View.VISIBLE);
                    ibtn_submit_edit.setEnabled(false);
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    findViewById(R.id.loading_bar_rl).setVisibility(View.GONE);

                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    LogZ.d("upLoadImage--> ",new String(responseBody));
                    //{"code":100,"msg":"success","opcode":"reguser_eid_success","headimg":"/UserImage/201411080338542207.JPG"}

                    PostBackDataBean postBackDataBean = new GsonBuilder().create().fromJson(new String(responseBody), PostBackDataBean.class);
                    if(null != postBackDataBean) {
                        getShowMsg(postBackDataBean);
                    }
                    tv_user_name.setText(username);
                    rl_layout_info.setVisibility(View.GONE);

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    zxlApplication.showTextToast("服务器出错，注册失败！");
                    findViewById(R.id.loading_bar_rl).setVisibility(View.GONE);

                }
            });
        }else {
            zxlApplication.showTextToast("文件不存在");
        }
    }

    private void getShowMsg(PostBackDataBean postBackDataBean){
        if(null == postBackDataBean){
            zxlApplication.showTextToast("注册请求失败！");
        }

        if(-1 == postBackDataBean.getCode()){
            zxlApplication.showTextToast(postBackDataBean.getMsg());
        } else if(100 == postBackDataBean.getCode()){
            if("eid_exist".equals(postBackDataBean.getOpcode())){
                zxlApplication.showTextToast("该企业号已存在");
            } else if("reguser_eid_success".equals(postBackDataBean.getOpcode())){
                //注册新的用户 或者更换用户时 清除之前数据库的数据
                zxlApplication.showTextToast("注册成功！");
                preferenceUtils.setUserName(username);
                preferenceUtils.setHeadImage(postBackDataBean.getHeadimg());
                preferenceUtils.setIsLogin(true);
            }
        }
    }


}
