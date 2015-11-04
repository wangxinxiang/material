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
import android.widget.ListView;
import android.widget.TextView;

import com.jhlc.material.adapter.NoteBookAdapter;
import com.jhlc.material.bean.NoteBookBean;
import com.jhlc.material.db.NoteBookDB;
import com.jhlc.material.service.LoaderBusiness;
import com.jhlc.material.utils.ImageUtils;
import com.jhlc.material.utils.LogZ;
import com.jhlc.material.utils.MYURL;
import com.jhlc.material.utils.PreferenceUtils;
import com.jhlc.material.view.RoundImageView;

import java.io.File;
import java.util.ArrayList;

/**
 * 老的备忘录，登录放里面的，这个界面暂时取消
 */
public class NoteBookActivityOld extends Activity {
    private TextView        tv_user_name;
    private RoundImageView  img_head_photo;
    private NoteBookAdapter noteBookAdapter;
    private ListView        ls_notebook;
    private NoteBookDB      noteBookDB = new NoteBookDB(this);
    private static String   path = ImageUtils.path;//sd路径
    private ArrayList<NoteBookBean> noteBookBeans = new ArrayList<NoteBookBean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_tab_notebook);
        bindData();

        //testupdate
        setContentList();

    }

    private void bindData() {
        tv_user_name = (TextView) findViewById(R.id.tv_user_name);

        img_head_photo = (RoundImageView) findViewById(R.id.img_head_photo);
        img_head_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if(PreferenceUtils.getInstance().getIsLogin()){
                  LogoutDialog logoutDialog = new LogoutDialog(NoteBookActivityOld.this,R.style.MyDialog);
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
                  startActivity(new Intent(NoteBookActivityOld.this, RegisterActivity.class));
              }
            }
        });

    }

    private void setHeadImage() {

        Bitmap bt = BitmapFactory.decodeFile(path + "head.jpg");//从Sd中找头像，转换成Bitmap

        File file = new File(path + "head.jpg");
        if(file.exists()){
            Log.e("file--> ", path + "head.jpg" + " -- " + file.length());
        }

        if(bt!=null){
            @SuppressWarnings("deprecation")
            Drawable drawable = new BitmapDrawable(bt);//转换成drawable
//            img_head_photo.setImageDrawable(drawable);

        }else{

        }
    }

    private void setContentList() {
        noteBookBeans = noteBookDB.getNoteBook(0);
        LogZ.d("UpUserAdapter--> ", "" + noteBookBeans.size());

        noteBookAdapter = new NoteBookAdapter(noteBookBeans,this);
        ls_notebook = (ListView)findViewById(R.id.ls_notebook);
        ls_notebook.setAdapter(noteBookAdapter);

    }

    private void setUserInfo() {
        try {
            LogZ.d("getIsLogin--> ",""+PreferenceUtils.getInstance().getIsLogin());
            LoaderBusiness.loadImage(MYURL.URL_HEAD + PreferenceUtils.getInstance().getHeadImage(), img_head_photo);

            if(PreferenceUtils.getInstance().getIsLogin()) {
                tv_user_name.setText(PreferenceUtils.getInstance().getUserName());
                LogZ.d("getUserName--> ",""+PreferenceUtils.getInstance().getUserName());
            } else {
                tv_user_name.setText("我");
                LogZ.d("getIsLogin--> ","img_head_photo.setBackgroundResource(R.drawable.defult_user_headimg)");

            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {

        super.onResume();
        Log.v("NoteBookActivity--> ", "onResume");

        setUserInfo();

    }
}