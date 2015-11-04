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
public class TaskDep_Edit_Activity extends AddOrEditTaskActivity {
    private static final String TAG = "TaskDep_Edit_Activity";
    private String time;
    private String workId,workTag;

    @Override
    protected void setKeyOrBackPressed() {
        super.setKeyOrBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        time=getIntent().getStringExtra("time");
        workId=getIntent().getStringExtra("workid");
        workTag=getIntent().getStringExtra("workTag");
        setTimeIsVisibility(true, time, false);
    }

    @Override
    protected void httpSubmit() {
        super.httpSubmit();
        Map<String,String> hashMap=new HashMap<>();
        hashMap.put("opcode","updateofficework");
        hashMap.put("Username", PreferenceUtils.getUserName());
        hashMap.put("eid", Constant.EID);
        hashMap.put("message",getInputContext());
        hashMap.put("worktag",workTag);
        hashMap.put("workid",workId);

        VolleyRequest.RequestPost(this, MYURL.URL_HEAD, VolleyTag, hashMap, new VolleyInterface<String>(String.class,TAG) {
            @Override
            public void onMySuccess(String result) {
                ZXLApplication.getInstance().showTextToastLong("编辑任务成功");
                finish();
            }

            @Override
            public void onMyError(VolleyError error) {

            }
        });
    }
}
