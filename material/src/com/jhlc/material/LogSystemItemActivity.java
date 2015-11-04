package com.jhlc.material;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jhlc.material.service.LoaderBusiness;
import com.jhlc.material.utils.LogZ;
import com.jhlc.material.utils.MYURL;
import com.jhlc.material.utils.PreferenceUtils;
import com.jhlc.material.utils.StringUtils;
import com.jhlc.material.view.RoundImageView;

/**
 * Created by Administrator on 2015/5/3.
 */
public class LogSystemItemActivity extends Activity {
    private TextView tv_user_name;
    private RoundImageView img_head_photo;
    private ImageView ibtn_close_actvity;
    private TextView txt_context;
    private TextView txt_time;

    private String username;
    private String headimage;
    private String context;
    private String time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notebook_item_show);
        intiview();
        username = getIntent().getStringExtra("username");
        headimage = getIntent().getStringExtra("headimage");
        context = getIntent().getStringExtra("context");
        time = getIntent().getStringExtra("time");
//        Toast.makeText(getApplicationContext(),""+time.indexOf(" "), Toast.LENGTH_SHORT).show();
        time= time.substring(0,time.indexOf(" "));

    }

    private void intiview() {
        tv_user_name = (TextView) findViewById(R.id.tv_user_name);
        txt_context = (TextView) findViewById(R.id.bookItem_context);
        txt_time = (TextView) findViewById(R.id.bookItem_time);

        img_head_photo = (RoundImageView) findViewById(R.id.img_head_photo);
        img_head_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //startActivity(new Intent(LogSystemActivity.this, SettingActivity.class));
            }

        });

        ibtn_close_actvity = (ImageView) findViewById(R.id.ibtn_close_actvity);
        ibtn_close_actvity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        setUserInfo();
    }

    private void setUserInfo() {
        try {
            LogZ.d("getIsLogin--> ", "" + PreferenceUtils.getIsLogin());
            // LoaderBusiness.loadImage(MYURL.URL_HEAD + PreferenceUtils.getInstance().getHeadImage(), img_head_photo);
            setHeadImage();
            tv_user_name.setText(username);
            txt_context.setText(context);
            txt_time.setText(time);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setHeadImage() {
        if(StringUtils.isNotBlank(headimage)){
            LoaderBusiness.loadImage(MYURL.img_HEAD + headimage, img_head_photo);
        }else{
            img_head_photo.setImageDrawable(this.getResources().getDrawable(R.drawable.defult_user_headimg));
        }
    }
}
