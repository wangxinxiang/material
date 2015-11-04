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
public class TaskDepDetail_Add_Activity extends AddOrEditTaskActivity {
    private static final String TAG = "TaskDepDetail_Add_Activity";
    private String workId,workTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        workId=getIntent().getStringExtra("workid");
        workTag=getIntent().getStringExtra("workTag");
        setTitleName("新增任务进展");
        setTimeIsVisibility(true,null,false);
    }

    @Override
    protected void httpSubmit() {
        super.httpSubmit();
        Map<String,String> hashMap=new HashMap<>();
        hashMap.put("opcode","AddOfficeDetailWork");
        hashMap.put("Username", PreferenceUtils.getUserName());
        hashMap.put("eid", Constant.EID);
        hashMap.put("message",getInputContext());
        hashMap.put("worktag",workTag);
        hashMap.put("workid",workId);

        VolleyRequest.RequestPost(this, MYURL.URL_HEAD, VolleyTag, hashMap, new VolleyInterface<String>(String.class,TAG) {
            @Override
            public void onMySuccess(String result) {
                ZXLApplication.getInstance().showTextToastLong("添加任务进展成功");
                finish();
            }

            @Override
            public void onMyError(VolleyError error) {

            }
        });
    }
}
