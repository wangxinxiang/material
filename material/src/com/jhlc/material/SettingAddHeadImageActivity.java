package com.jhlc.material;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jhlc.material.utils.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 从设置中添加头像，主要的修改是选择好头像后自己消失
 */
public class SettingAddHeadImageActivity extends Activity{
    private TextView tv_take_pictures,tv_photo_album;
    private TextView tv_edit_cancle;
    private PopupWindow popWindow;
    private ImageView img_my_photo;

    private String path = ImageUtils.path;//sd路径;
    private Intent intent;
    private Bitmap head;    //头像Bitmap
    private String username;
    private boolean changeheadimage = false;
    //用来标示是否是上传图片的请求
    private boolean uploadImg = false;
    private ZXLApplication zxlApplication = ZXLApplication.getInstance();
    private PreferenceUtils preferenceUtils = PreferenceUtils.getInstance();

    private ProgressDialog pd;

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
                if (changeheadimage) {
                    startActivityForResult(intent, 2);//采用ForResult打开
                }
                if (uploadImg) {//如果是上传图片，用20来处理
                    startActivityForResult(intent, 20);//采用ForResult打开
                }
            }
        });

        tv_photo_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (changeheadimage) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_PICK);
                    startActivityForResult(intent, 1);
                }

                if(uploadImg){//如果是上传图片，用10来处理
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_PICK);
                    startActivityForResult(intent, 10);
                }
            }
        });

    }

    private void initView() {
        if(Constant.ChangeHeadImage.equals(getIntent().getStringExtra(Constant.ChangeHeadImage))){
            changeheadimage = true;
        }
        if(Constant.uploadPic.equals(getIntent().getStringExtra(Constant.uploadPic))){
            uploadImg = true;
            ((TextView)findViewById(R.id.tv_take_pictures)).setText("拍摄照片");
            ((TextView)findViewById(R.id.tv_photo_album)).setText("从相册选择图片");
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
                Log.i("onActivityResultImage","返回选择页面");
                if(data != null && data.getData() != null) {
                    Log.i("onActivityResultImage","得到图片");
                    Intent intent1 = new Intent(SettingAddHeadImageActivity.this,ClipPictureActivity.class);
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
                            Intent intent1 = new Intent(SettingAddHeadImageActivity.this,ClipPictureActivity.class);
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
                    setPicToView(bitmap);//保存在SD卡中
                } catch (Exception e){
                    // 保存不成功时捕获异常
                    e.printStackTrace();
                }
                //todo: 不在这里上传头像
                setResult(Constant.settting_changeheadimg);
                finish();
//                /**
//                 * 上传服务器代码
//                 */
//                if(changeheadimage) {
//                    LogZ.d("lyjtakephoto", "SettingAddHeadImageActivity result3 上传服务器代码");
//                    upLoadImage();
//                } else {
//                    setResult(Constant.ResultCode);
//                    finish();
//                }
                break;
            case 10://处理对话框上传图片 从相册上传
                if(data != null && data.getData() != null) {
                    ContentResolver resolver = getContentResolver();
                    try {
                        Bitmap photo = MediaStore.Images.Media.getBitmap(resolver,  data.getData());
                        try{
                            setPicToLocalView(photo, Constant.testupload);//保存在SD卡中
                        } catch (Exception e){
                            // 保存不成功时捕获异常
                            e.printStackTrace();
                        }

                        setResult(Constant.progressReport_upload);
                        finish();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                break;
            case 20://处理对话框上传图片 来自相册
                if (resultCode == RESULT_OK) {
                    if(data !=null){ //可能尚未指定intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                        //返回有缩略图
                        if(data.hasExtra("data")){
                            Bitmap thumbnail = data.getParcelableExtra("data");
                            pd = ProgressDialog.show(SettingAddHeadImageActivity.this, "请稍后...", "请等待", true);
                            try{
                                setPicToLocalView(thumbnail,Constant.testupload);//保存在SD卡中
                            } catch (Exception e){
                                // 保存不成功时捕获异常
                                e.printStackTrace();
                            }

                            setResult(Constant.progressReport_upload);
                            finish();
                        }
                    }else{
                        //由于指定了目标uri，存储在目标uri，intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                        // 通过目标uri，找到图片
                        // 对图片的缩放处理
                        // 操作
                    }
                }

                break;
            default:
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    };

    private void setPicToView(Bitmap mBitmap) {
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
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件

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
    }

    private void setPicToLocalView(Bitmap mBitmap,String key) {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            return;
        }
        FileOutputStream b = null;
        File file = new File(path);
        file.mkdirs();// 创建文件夹
        String fileName = path + key+".jpg";//图片名字
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
