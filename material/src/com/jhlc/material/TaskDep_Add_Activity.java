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
public class TaskDep_Add_Activity extends AddOrEditTaskActivity {
    private static final String TAG ="TaskDep_Add_Activity" ;
    private String workTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        workTag=getIntent().getStringExtra("workTag");
        setTimeIsVisibility(true, null, false);

    }

    @Override
    protected void httpSubmit() {
        super.httpSubmit();
//        ZXLApplication.getInstance().showTextToast(TaskDep_Add_Activity.this.toString());
        Map<String,String> hashMap=new HashMap<>();
        hashMap.put("opcode","addofficework");
        hashMap.put("Username", PreferenceUtils.getUserName());
        hashMap.put("eid", Constant.EID);
        hashMap.put("message",getInputContext());
        hashMap.put("worktag",workTag);

        VolleyRequest.RequestPost(this, MYURL.URL_HEAD, VolleyTag, hashMap, new VolleyInterface<String>(String.class,TAG) {
            @Override
            public void onMySuccess(String result) {
                ZXLApplication.getInstance().showTextToastLong("添加处室任务成功");
                finish();
            }

            @Override
            public void onMyError(VolleyError error) {

            }
        });

    }


}
