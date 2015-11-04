package com.jhlc.material;

import android.os.Bundle;

import com.android.volley.VolleyError;
import com.jhlc.material.utils.Constant;
import com.jhlc.material.utils.MYURL;
import com.jhlc.material.utils.PreferenceUtils;
import com.jhlc.material.volley.VolleyInterface;
import com.jhlc.material.volley.VolleyRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/9/18 0018.
 */
public class TaskDepDetail_Edit_Activity extends AddOrEditTaskActivity {
    private static final String TAG = "TaskDepDetail_Edit_Activity";

    private String time,workdetailid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitleName("编辑任务进展");
        time=getIntent().getStringExtra("time");
        workdetailid=getIntent().getStringExtra("workdetailid");
        if (time!=null){
            setTimeIsVisibility(true, time, false);
        }

    }


    @Override
    protected void httpSubmit() {

        super.httpSubmit();
//        ZXLApplication.getInstance().showTextToast("编辑任务进展提交了任务");
        Map<String,String> hashMap=new HashMap<>();
        hashMap.put("opcode","updateofficedetailwork");
        hashMap.put("Username", PreferenceUtils.getUserName());
        hashMap.put("eid", Constant.EID);
        hashMap.put("message",getInputContext());
        hashMap.put("workdetailid",workdetailid);

        VolleyRequest.RequestPost(this, MYURL.URL_HEAD, VolleyTag, hashMap, new VolleyInterface<String>(String.class,TAG) {
            @Override
            public void onMySuccess(String result) {
                ZXLApplication.getInstance().showTextToastLong("编辑处室任务进展成功");
                finish();
            }

            @Override
            public void onMyError(VolleyError error) {

            }
        });
    }
}
