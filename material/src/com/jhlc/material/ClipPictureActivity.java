package com.jhlc.material;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.*;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.*;
import android.view.View.OnClickListener;

import com.jhlc.material.utils.ImageTools;
import com.jhlc.material.utils.LogZ;
import com.jhlc.material.view.ClipImageLayout;


import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * @author Administrator
 * 整体思想是：截取屏幕的截图，然后截取矩形框里面的图片
 * 代码未经优化，只是一个demo。
 */
public class ClipPictureActivity extends Activity {
    private ClipImageLayout mClipImageLayout;
    private static  int SCALE = 2;//照片缩小比例


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clip_picture);

        findViewById(R.id.ibtn_submit_edit).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setHeadImage();
            }
        });

        mClipImageLayout = (ClipImageLayout) findViewById(R.id.id_clipImageLayout);

        ContentResolver resolver = getContentResolver();


        // 得到图片URI 对象
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        Uri originalUri = extras.getParcelable("url");
        LogZ.d("ClipPictureActivity -- uri>", originalUri + "");
        if(null == originalUri){
            Bitmap bitmap = extras.getParcelable("bitmap");
            mClipImageLayout.mZoomImageView.setImageBitmap(bitmap);
        } else {
//            Bitmap photo = BitmapFactory.decodeFile(originalUri.getPath());
//            mClipImageLayout.mZoomImageView.setImageBitmap(photo);

            //照片的原始资源地址
            try {
                //使用ContentProvider通过URI获取原始图片
                Bitmap photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                if (photo != null) {
                    Bitmap smallBitmap;
//                    if(photo.getWidth() > ZXLApplication.getInstance().getScreenWidth(ClipPictureActivity.this)){
//                        SCALE = 5;
//                    }else{
//                        SCALE = 1;
//                    }
                    int degree
                            = 0;
                    try {

                        ExifInterface exifInterface = new ExifInterface(originalUri.getPath());
                        int orientation =
                                exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                        ExifInterface.ORIENTATION_NORMAL);
                        switch (orientation) {
                            case
                                    ExifInterface.ORIENTATION_ROTATE_90:
                                degree = 90;
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_180:
                                degree = 180;
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_270:
                                degree = 270;
                                break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
                   // if (photo.getWidth()>200) { //todo 这里是不是要根据图片大小来设置
//                        smallBitmap = ImageTools.zoomBitmap(photo, photo.getWidth() / SCALE, photo.getHeight() / SCALE);
                        smallBitmap = ImageTools.zoomBitmapAutoByWidth(photo);
                   // }


                    // 创建旋转图片
                    Bitmap resizedBitmap = null;
                    if (degree!=0) {
                        Matrix matrix = new Matrix();
                        matrix.postRotate(degree);
                        resizedBitmap = Bitmap.createBitmap(smallBitmap, 0, 0,
                                smallBitmap.getWidth(), smallBitmap.getHeight(),
                                matrix, true);




                        mClipImageLayout.mZoomImageView.setImageBitmap(resizedBitmap);
                    }else{
                        mClipImageLayout.mZoomImageView.setImageBitmap(smallBitmap);
                    }
                    //释放原始图片占用的内存，防止out of memory异常发生
                   // photo.recycle();



                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }




    }

    public void setHeadImage( ) {
        Bitmap bitmap = mClipImageLayout.clip();
        //保存后再做一次压缩，尽量头像要小
//        Bitmap smallBitmap = ImageTools.zoomBitmap(bitmap, bitmap.getWidth() / 5, bitmap.getHeight() / 5);
        Bitmap smallBitmap = ImageTools.zoomBitmapAutoByWidth(bitmap);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        smallBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bitmapByte = baos.toByteArray();

        Intent intent = new Intent();
        intent.putExtra("bitmap", bitmapByte);
        setResult(3,intent);
        finish();
    }

}