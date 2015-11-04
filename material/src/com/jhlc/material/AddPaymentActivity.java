package com.jhlc.material;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.*;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.google.gson.GsonBuilder;
import com.jhlc.material.bean.PostBackDataBean;
import com.jhlc.material.utils.*;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 */
public class AddPaymentActivity extends Activity implements OnGetGeoCoderResultListener {
    private TextView    tv_title_name;
    private ImageButton ibtn_close_edit;

    private Button ibtn_submit_edit;

    private ImageView ibtn_take_invoice;
    private EditText    et_expense_address,et_expense_thing,et_expense_money;
    private String      expense_address,expense_thing,expense_money;

    private Bitmap head;//头像Bitmap
    private String path = ImageUtils.path;//sd路径
    private ZXLApplication zxlApplication = ZXLApplication.getInstance();
    private String   workusername;

    //位置相关
    GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
    BaiduMap mBaiduMap = null;
    MapView mMapView = null;
    boolean isFirstLoc = true;// 是否首次定位
    LocationClient mLocClient;
    private double mLatitude=0;
    private double mLongitude=0;
    private String address;//地址
    private String city=null;//城市
    public MyLocationListenner myListener = new MyLocationListenner();

    private ProgressDialog pd;
    private boolean photoflag=false;//记录有没有拍过发票的标志位

    private String paymentFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(this);

        setContentView(R.layout.add_payment);
        //进入这个界面，默认都是设置为flase
        PreferenceUtils.getInstance().setPaymentImgHasSetted(false);
        initView();
        bindData();
        setListener();
        getBaiDuMap();

        //通过android 自带类去 获取经纬度
    }

    private void getBaiDuMap() {
// 地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();

        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);

        //---------------------------------------------------------


        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);//设置地图的缩放比例

        mBaiduMap.setMapStatus(msu);//将前面的参数交给BaiduMap类

        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(getApplicationContext());
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
//        mLocClient.setAK("w9CjchYknC4pIjlFMFIpBbuy");
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setPriority(LocationClientOption.NetWorkFirst);//设置网络优先(不设置，默认是gps优先)
        option.setAddrType("all");// 返回的定位结果包含地址信息
        option.setScanSpan(15000);// 设置发起定位请求的间隔时间为10s(小于1秒则一次定位)
        mLocClient.setLocOption(option);
        mLocClient.start();
        mLocClient.requestLocation();
        mBaiduMap.isBuildingsEnabled();//获取是否允许楼块效果

    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null)
                return;
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
//            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(u);
            }
            address=location.getAddrStr();
            mLatitude=location.getLatitude();
            mLongitude=location.getLongitude();
            city=location.getCity();
            Log.v("address", "经度= " + mLatitude + " 纬度=" + mLongitude + " 地址：" + address); // TODO 需要的参数
//            Toast.makeText(AddPaymentActivity.this,"经度= " + mLatitude + " 纬度=" + mLongitude + " 地址：" + address,Toast.LENGTH_SHORT).show();
        }

        public void onReceivePoi(BDLocation poiLocation) {
            Log.v("address", "onReceivePoi= " + mLatitude + " onReceivePoi=" + mLongitude + " onReceivePoi：" + address); // TODO 需要的参数

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


                if(check()) {
                    upLoadImage();
                    LogZ.d("mLatitude--> ",mLatitude+" + "+mLongitude);
                }
            }
        });

        ibtn_take_invoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PreferenceUtils.getPaymentImgHasSetted()){//如果用户设置过发票照片，就进入预览界面
                    Intent intent=new Intent(AddPaymentActivity.this,DisplayImageActivity.class);
                    intent.putExtra("imgkey",PreferenceUtils.getPaymentImgFileName());
                    intent.putExtra("showresetBtn",true);
                    //ZXLApplication.getInstance().showTextToast("传递过去的参数："+workDetailList.getBigimage());
                    startActivityForResult(intent, 4);
                    return ;
                }
                //TODO: 这里是不是应该有一个已经拍好的照片预览的功能？
                Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                paymentFileName= TimeUtil.getFileKeyTimeStr();
                Uri imageUri = Uri.fromFile(new File(path,paymentFileName+".jpg"));
                //指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(openCameraIntent, 3);
            }
        });

    }

    private boolean check() {
        if(!checkAddress()){
            return false;
        }
        if(!PreferenceUtils.getPaymentImgHasSetted()){
            AlertDialog.Builder builder = new AlertDialog.Builder(AddPaymentActivity.this);
            builder.setMessage("确认不提交发票照片？");
            builder.setTitle("提示");
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                   upLoadImage();

                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
            return false;
        }
        return true;
    }

    private boolean checkAddress(){
        expense_address = et_expense_address.getText().toString().trim();
        expense_thing   = et_expense_thing.getText().toString().trim();
        expense_money   = et_expense_money.getText().toString().trim();
        if(StringUtils.isBlank(expense_address)){
            ZXLApplication.getInstance().showTextToast("请填写开销地点");
            return false;
        }
        if(StringUtils.isBlank(expense_thing)){
            ZXLApplication.getInstance().showTextToast("请填写开销事项");
            return false;
        }
        if(StringUtils.isBlank(expense_thing)){
            ZXLApplication.getInstance().showTextToast("请填写开销金额");
            return false;
        }
        return true;
    }

    private void bindData() {
        Intent intent = this.getIntent();
        workusername = intent.getStringExtra("workusername");
        tv_title_name.setText("报销");

    }

    private void initView() {
        tv_title_name    = (TextView) findViewById(R.id.tv_title_name);
        ibtn_close_edit  = (ImageButton) findViewById(R.id.ibtn_close_edit);
        ibtn_submit_edit = (Button) findViewById(R.id.ibtn_submit_edit);

        ibtn_take_invoice  = (ImageView) findViewById(R.id.ibtn_take_invoice);
        et_expense_address = (EditText) findViewById(R.id.et_expense_address);
        et_expense_thing   = (EditText) findViewById(R.id.et_expense_thing);
        et_expense_money   = (EditText) findViewById(R.id.et_expense_money);

    }

    // 上传图片
    private void upLoadImage(){

        AsyncHttpClient client = new AsyncHttpClient();

        File file = new File(path + PreferenceUtils.getPaymentImgFileName()+".jpg");


            RequestParams params = new RequestParams();
            try {
                params.put("opcode","add_expense");
                params.put("Username",PreferenceUtils.getUserName());
                params.put("receptusername",workusername);
                params.put("message",expense_thing);
                params.put("Longitude",mLongitude);
                params.put("Latitude",mLatitude);
                params.put("position",expense_address);
                params.put("emoney",expense_money);
                params.put("sysposition",address);
                params.put("eid", Constant.EID);
                if(PreferenceUtils.getPaymentImgHasSetted()&&file.exists() && file.length() > 0) {
                    params.put("profile_picture", file);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            client.post(MYURL.URL_HEAD, params, new AsyncHttpResponseHandler(){

                @Override
                public void onStart() {
                    super.onStart();
                    pd = ProgressDialog.show(AddPaymentActivity.this, "正在提交数据,请稍后...", "请等待", true);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    LogZ.d("userlist--$>",new String(responseBody));
                    pd.dismiss();
                    PostBackDataBean postBackDataBean = new GsonBuilder().create().fromJson(new String(responseBody), PostBackDataBean.class);
                    if(!("-1".equals(postBackDataBean.getCode()))){
                            ZXLApplication.getInstance().showTextToast("添加成功");
                            setResult(Constant.ResultCode);
                            finish();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    zxlApplication.showTextToast("网络异常，提交失败！");
                    pd.dismiss();
                }
            });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case 4:
                boolean needPhotoReset=data.getBooleanExtra("pohotreset",false);
                if (needPhotoReset) {
                   // ZXLApplication.getInstance().showTextToast("从显示界面跳转过来 needPhotoReset");
                    Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    paymentFileName = TimeUtil.getFileKeyTimeStr();
                    Uri imageUri = Uri.fromFile(new File(path, paymentFileName + ".jpg"));
                    //指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
                    openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(openCameraIntent, 3);
                }else{
                   // ZXLApplication.getInstance().showTextToast("从显示界面跳转过来");
                }

                break;
            //这里要注意一下，就算是拍照界面取消的话 resultCode 也是3
            case 3:
//                if (data != null) {
                   /* Bundle extras = data.getExtras();
                    head = extras.getParcelable("data");
                    if(head!=null){
                        try{
                            setPicToView(head);//保存在SD卡中
                            ibtn_take_invoice.setImageBitmap(head);
                        } catch (Exception e){
                            // 保存不成功时捕获异常
                            e.printStackTrace();
                        }
                        *//**
                         * 上传服务器代码
                         *//*
                        //setHeadImage();

                    }*/

                    try{
                        String paymentFile = path + paymentFileName + ".jpg";
                        File file = new File(paymentFile);
                        if (!file.exists()) {//如果文件不存在表示界面
                            return ;
                        }
                        //如果用户拍过照片，就讲照片名字保存起来，方便预览。
                        PreferenceUtils.getInstance().setPaymentImgFileName(paymentFileName);
                        //拍过报销的标志位
                        PreferenceUtils.getInstance().setPaymentImgHasSetted(true);
                        //将保存在本地的图片取出并缩小后显示在界面上
                        Bitmap bitmap = BitmapFactory.decodeFile(paymentFile);


                        Bitmap newBitmap = ImageTools.zoomBitmapAutoByWidth(bitmap);
                        //由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
                        ImageUtils.setPicToView(newBitmap, paymentFileName);
                        bitmap.recycle();
                        //将处理过的图片显示在界面上，并保存到本地
                        ibtn_take_invoice.setImageBitmap(newBitmap);

                    } catch (Exception e){
                        // 保存不成功时捕获异常
                        e.printStackTrace();
                    }

//                }
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
        String fileName = path + "payment.jpg";//图片名字
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

//    /**
//     * 调用系统的裁剪
//     * @param uri
//     */
//    public void cropPhoto(Uri uri) {
//        Intent intent = new Intent("com.android.camera.action.CROP");
//        intent.setDataAndType(uri, "image/*");
//        intent.putExtra("crop", "true");
//        // aspectX aspectY 是宽高的比例
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);
//        // outputX outputY 是裁剪图片宽高
//        intent.putExtra("outputX", 150);
//        intent.putExtra("outputY", 150);
//        intent.putExtra("return-data", true);
//        startActivityForResult(intent, 3);
//    }


    @Override
    protected void onPause() {
        mMapView.onPause();

        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        mSearch.destroy();
        if (mLocClient!=null&&myListener!=null) {
            mLocClient.unRegisterLocationListener(myListener);
        }
        super.onDestroy();
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(AddPaymentActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        mBaiduMap.clear();
        mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.red_dot)));
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
                .getLocation()));
        String strInfo = String.format("纬度：%f 经度：%f",
                result.getLocation().latitude, result.getLocation().longitude);
        Toast.makeText(AddPaymentActivity.this, strInfo, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(AddPaymentActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        mBaiduMap.clear();
        mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.red_dot)));
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
                .getLocation()));
        Toast.makeText(AddPaymentActivity.this, result.getAddress(),
                Toast.LENGTH_LONG).show();

    }


}
