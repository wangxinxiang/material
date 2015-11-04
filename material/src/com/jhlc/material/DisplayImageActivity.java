package com.jhlc.material;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.jhlc.material.utils.*;
import com.jhlc.material.view.ClipImageLayout;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.*;


/**
 * @author Administrator
 * 显示大图片
 */
public class DisplayImageActivity extends Activity {
    private ClipImageLayout mClipImageLayout;
    private static  int SCALE = 5;//照片缩小比例
  private  String path = ImageUtils.path;

    private ProgressDialog pd;
    private String paymentFileName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_picture);

        findViewById(R.id.ibtn_submit_edit).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                setResult(4, intent);
                finish();
            }
        });

        findViewById(R.id.ibtn_resetphoto_edit).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.putExtra("pohotreset",true);
                setResult(4,intent);
                finish();
            }
        });

        String url=getIntent().getStringExtra("bigurl");
        String key=getIntent().getStringExtra("imgkey");
        boolean showresetBtn=getIntent().getBooleanExtra("showresetBtn",false);
        if(showresetBtn){
            findViewById(R.id.ibtn_resetphoto_edit).setVisibility(View.VISIBLE);
        }
        File file=new File(path+key+".jpg");
        if (!file.exists()) {
            LogZ.e("lyjtest","文件不存在："+file.getAbsolutePath());
            DownLoadImg downloadTask=new DownLoadImg();
            downloadTask.execute(MYURL.img_HEAD+url,key);
        }else{
            LogZ.e("lyjtest","文件已存在："+file.getAbsolutePath());
            SubsamplingScaleImageView imageView = (SubsamplingScaleImageView) findViewById(R.id.imageView);
            imageView.setImageFile(path+ key+".jpg");
        }


    }


    class DownLoadImg extends AsyncTask<String, Integer, String> {

        public DownLoadImg() {
            super();
        }

        @Override
        protected  String doInBackground(String... url) {
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(url[0]);
            HttpResponse response;
            try {
                response = client.execute(get);
                HttpEntity entity = response.getEntity();
                long length = entity.getContentLength();
              int  fileSize = (int) length;
                int downLoadFileSize=0;
                if (fileSize <= 0) throw new RuntimeException("无法获知文件大小 ");

                InputStream is = entity.getContent();
                FileOutputStream fileOutputStream = null;
                if (is != null) {


                    File file = new File(
                            path, url[1]+".jpg");
                    fileOutputStream = new FileOutputStream(file);

                    byte[] buf = new byte[1024];
                    int ch = -1;
                    int count = 0;

                    while ((ch = is.read(buf)) != -1) {
                        fileOutputStream.write(buf, 0, ch);
                        count += ch;
                        downLoadFileSize += ch;
                        publishProgress(fileSize,downLoadFileSize);
                        if (length > 0) {

                        }
                    }
                }
                fileOutputStream.flush();
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return url[1];
        }

        /**
         * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）
         * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置
         */
        @Override
        protected void onPostExecute(String result) {
            pd.dismiss();
            SubsamplingScaleImageView imageView = (SubsamplingScaleImageView) findViewById(R.id.imageView);
            imageView.setImageFile(path+ result+".jpg");
        }


        //该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(DisplayImageActivity.this);
            pd.setTitle("正在下载图片...");
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            pd.setMax(values[0]);
            pd.setProgress(values[1]);
        }

    }

}