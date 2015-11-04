package com.jhlc.material.volley;


import android.content.Context;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.jhlc.material.ZXLApplication;


import java.util.Map;

public class VolleyRequest
{
    public static StringRequest stringRequest;
    public static Context context;

    public static void RequestGet(Context context,String url, String tag, VolleyInterface vif)
    {

        ZXLApplication.getHttpQueues().cancelAll(tag);
        stringRequest = new StringRequest(Request.Method.GET,url,vif.loadingListener(),vif.errorListener());
        stringRequest.setTag(tag);
        ZXLApplication.getHttpQueues().add(stringRequest);
        // 不写也能执行
//        MyApplication.getHttpQueues().start();
    }

    public static void RequestPost(Context context,String url, String tag,final Map<String, String> params, VolleyInterface vif)
    {
//        ProgressDialog.createDialog(context); //网络请求开始 创建dialog
//        ProgressDialog.setMessage("正在与服务器连接....");
        ZXLApplication.getHttpQueues().cancelAll(tag);
        stringRequest = new StringRequest(Request.Method.POST,url,vif.loadingListener(),vif.errorListener())
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        stringRequest.setTag(tag);
        ZXLApplication.getHttpQueues().add(stringRequest);
        // 不写也能执行
//        MyApplication.getHttpQueues().start();
    }
}
