package com.jhlc.material;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.gson.GsonBuilder;
import com.jhlc.material.bean.PostBackDataBean;
import com.jhlc.material.utils.*;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;
import java.io.File;

/**
 */
public class RegisterActivity extends Activity {
    private TextView    tv_title_name,tv_title_centre;
    private ImageButton ibtn_close_edit,ibtn_submit_edit;
    private ImageView   img_head,img_photo_mark;
    private EditText    et_user_name;
    private String  path = ImageUtils.path;//sd路径;
    private String  username;
    private boolean isRegister = false;

    private ZXLApplication zxlApplication = ZXLApplication.getInstance();
    private PreferenceUtils preferenceUtils = PreferenceUtils.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        initView();
        setListener();

    }
    private void initView() {
        tv_title_name    = (TextView) findViewById(R.id.tv_title_name);
        tv_title_centre  = (TextView) findViewById(R.id.tv_title_centre);
        ibtn_close_edit  = (ImageButton) findViewById(R.id.ibtn_close_edit);
        ibtn_submit_edit = (ImageButton) findViewById(R.id.ibtn_submit_edit);
        img_head         = (ImageView) findViewById(R.id.img_head);
        img_photo_mark   = (ImageView) findViewById(R.id.img_photo_mark);
        et_user_name     = (EditText)  findViewById(R.id.et_user_name);

        tv_title_name.setVisibility(View.GONE);
        tv_title_centre.setVisibility(View.VISIBLE);
        tv_title_centre.setText("注册");

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
                ibtn_submit_edit.setEnabled(false);
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
                Intent intent = new Intent(RegisterActivity.this, AddHeadImageActivity.class);
                startActivityForResult(intent, Constant.RequestCode);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.e("file--> ","1  "+ isRegister);

        //可以根据多个请求代码来作相应的操作
        if(Constant.ResultCode == resultCode) {
            Log.e("file--> ","2  "+ isRegister);

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
                LogZ.d("upLoadImage--file> ", "exists");

            } catch (Exception e) {
                e.printStackTrace();
            }

            client.post(MYURL.URL_HEAD, params, new AsyncHttpResponseHandler(){

                @Override
                public void onStart() {
                    super.onStart();
                    findViewById(R.id.loading_bar_rl).setVisibility(View.VISIBLE);

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
                    finish();
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
                finish();
            }
        }
    }


}
