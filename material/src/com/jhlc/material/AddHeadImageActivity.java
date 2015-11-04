package com.jhlc.material;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.*;
import android.widget.*;
import com.google.gson.GsonBuilder;
import com.jhlc.material.bean.PostBackDataBean;
import com.jhlc.material.utils.*;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;

import java.io.*;

//ffd6147d-8028-48d1-a85c-bf042df9f6ce
public class AddHeadImageActivity extends Activity{
    private TextView tv_take_pictures,tv_photo_album;
    private TextView tv_edit_cancle;
    private PopupWindow popWindow;
    private ImageView img_my_photo;

    private String path = ImageUtils.path;//sd路径;
    private Intent intent;
    private Bitmap head;    //头像Bitmap
    private String username;
    private boolean changeheadimage = false;
    private ZXLApplication zxlApplication = ZXLApplication.getInstance();
    private PreferenceUtils preferenceUtils = PreferenceUtils.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_headimage_dialog);

        initView();
        setListener();

    }

    private void setListener() {
        tv_edit_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_take_pictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//              intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "head.jpg")));
                startActivityForResult(intent, 2);//采用ForResult打开
            }
        });

        tv_photo_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(intent, 1);
            }
        });

    }

    private void initView() {
        if(Constant.ChangeHeadImage.equals(getIntent().getStringExtra(Constant.ChangeHeadImage))){
            changeheadimage = true;
        }

        username         = getIntent().getStringExtra("username");
        tv_take_pictures = (TextView)findViewById(R.id.tv_take_pictures);
        tv_photo_album   = (TextView)findViewById(R.id.tv_photo_album);
        tv_edit_cancle   = (TextView)findViewById(R.id.tv_edit_cancle);
//      img_my_photo = (ImageView) findViewById(R.id.img_my_photo);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if(data != null && data.getData() != null) {
                    Intent intent1 = new Intent(AddHeadImageActivity.this,ClipPictureActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("url", data.getData());
                    intent1.putExtras(bundle);
                    startActivityForResult(intent1, 3);
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    if(data !=null){ //可能尚未指定intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                        //返回有缩略图
                        if(data.hasExtra("data")){
                            Bitmap thumbnail = data.getParcelableExtra("data");
                            Intent intent1 = new Intent(AddHeadImageActivity.this,ClipPictureActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("bitmap", thumbnail);
                            intent1.putExtras(bundle);
                            startActivityForResult(intent1, 3);
                        }
                    }else{
                        //由于指定了目标uri，存储在目标uri，intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                        // 通过目标uri，找到图片
                        // 对图片的缩放处理
                        // 操作
                    }
                }

                break;
            case 3:

                Bitmap bitmap = null;
                if (data != null)
                {
                    byte[] bis = data.getByteArrayExtra("bitmap");
                    bitmap = BitmapFactory.decodeByteArray(bis, 0, bis.length);
                }
                //得到bitmap后的操作
                try{
                    //setPicToView(bitmap);//保存在SD卡中
                    ImageUtils.setPicToView(bitmap,"head");
                } catch (Exception e){
                    // 保存不成功时捕获异常
                    e.printStackTrace();
                }
                /**
                 * 上传服务器代码
                 */
                if(changeheadimage) {
                    upLoadImage();
                } else {
                    setResult(Constant.ResultCode);
                    finish();
                }
                break;
            default:
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    };

    /*private void setPicToView(Bitmap mBitmap) {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            return;
        }
        FileOutputStream b = null;
        File file = new File(path);
        file.mkdirs();// 创建文件夹
        String fileName = path + "head.jpg";//图片名字
        try {   
            b = new FileOutputStream(fileName);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 60, b);// 把数据写入文件

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                //关闭流
                b.flush();
                b.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }*/

    // 上传图片
    private void upLoadImage(){

        AsyncHttpClient client = new AsyncHttpClient();

        File file = new File(path + "head.jpg");

        if(file.exists() && file.length() > 0){
            RequestParams params = new RequestParams();
            try {
                params.put("opcode","reg_user_aid");
                params.put("Username",preferenceUtils.getUserName());
                params.put("eid", Constant.EID);
                params.put("profile_picture", file);
                LogZ.d("upLoadImage--file> ", "exists");

            } catch (Exception e) {
                e.printStackTrace();
            }

            client.post(MYURL.URL_HEAD, params, new AsyncHttpResponseHandler(){

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
                    zxlApplication.showTextToast("服务器出错，修改失败！");

                }
            });
        }else {
            zxlApplication.showTextToast("文件不存在");
        }
    }

    private void getShowMsg(PostBackDataBean postBackDataBean){
        if(null == postBackDataBean){
            zxlApplication.showTextToast("修改请求失败！");
        }

        if(-1 == postBackDataBean.getCode()){
            zxlApplication.showTextToast(postBackDataBean.getMsg());
        } else if(100 == postBackDataBean.getCode()){
            if("eid_exist".equals(postBackDataBean.getOpcode())){
                zxlApplication.showTextToast("该企业号已存在");
            } else if("reguser_eid_success".equals(postBackDataBean.getOpcode())){
                zxlApplication.showTextToast("修改成功！");
                preferenceUtils.setHeadImage(postBackDataBean.getHeadimg());
                finish();
            }
        }
    }

    //----------------------------------------------| 暂不用 |-----------------------------------------------------
    private void showPopWindow(View parent) {
        LayoutInflater inflater = (LayoutInflater)
                this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View vPopWindow=inflater.inflate(R.layout.select_image_popwindow, null, false);
        //宽300 高300
//        popWindow = new PopupWindow(vPopWindow,this.getResources().getDimensionPixelSize(R.dimen.popmenu_width), ViewGroup.LayoutParams.WRAP_CONTENT,true);
        Button btn_take_pic = (Button)vPopWindow.findViewById(R.id.btn_take_pic);
        btn_take_pic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "head.jpg")));
                startActivityForResult(intent, 2);//采用ForResult打开
            }
        });

        Button btn_selete_head_img = (Button)vPopWindow.findViewById(R.id.btn_selete_head_img);
        btn_selete_head_img.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 1);
            }
        });

        popWindow.setBackgroundDrawable(new BitmapDrawable());

//        showAsDropDown(rl_select_head_image);

    }

    // 下拉式 弹出 pop菜单 parent 右下角
    public void showAsDropDown(View parent) {

        popWindow.showAsDropDown(parent,(parent.getWidth()-popWindow.getWidth())/2, 0-10*(parent.getHeight()/4));
        // 使其聚集
        popWindow.setFocusable(true);
        // 设置允许在外点击消失
        popWindow.setOutsideTouchable(true);
        // 刷新状态
        popWindow.update();
    }

}
