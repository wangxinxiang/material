package com.jhlc.material;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import com.android.volley.VolleyError;
import com.jhlc.material.adapter.TaskDepListExpAdapter;
import com.jhlc.material.bean.*;
import com.jhlc.material.utils.MYURL;
import com.jhlc.material.utils.PreferenceUtils;
import com.jhlc.material.volley.VolleyInterface;
import com.jhlc.material.volley.VolleyRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/9/14 0014.
 */
public class TaskDepListActivity extends TitleActivity {
    private static String TAG = "TaskDepListActivity";
    private ExpandableListView expandList;
    private TaskDepListExpAdapter adapter;
    private ArrayList<DepTaskListByClass> list = new ArrayList<DepTaskListByClass>();
    private boolean isEdit = false;
    private boolean httpById;
    private String title, office_id,userName;

    @Override
    protected int setContentViewId() {
        return R.layout.task_dep_list;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isEdit = getIntent().getBooleanExtra("isEdit", false);
        title = getIntent().getStringExtra("title");
        if (getIntent().getStringExtra("office_id")!=null){
            office_id = getIntent().getStringExtra("office_id");
            httpById=true; //从执行界面进入 根据office id 联网
        }else {
            userName=PreferenceUtils.getUserName();
            httpById=false;//从处室任务按钮进入 根据用户名联网
        }

        setTitleName(title);
        expandList = (ExpandableListView) findViewById(R.id.exp_lv_task_dep);
        adapter = new TaskDepListExpAdapter(TaskDepListActivity.this, list, isEdit);
        expandList.setAdapter(adapter);

        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (httpById){
            getHttpById(office_id);//开始联网获取数据
        }else {
            gethttpByName(userName);
        }

    }

    private void initListener() {
        //父级视图上的 添加按钮 监听
        adapter.setOnChildAddListener(new TaskDepListExpAdapter.OnAddListener() {
            @Override
            public void addClick(int position, String wrokTag) {
                Intent intent = new Intent(TaskDepListActivity.this, TaskDep_Add_Activity.class);
                intent.putExtra("title","新建"+wrokTag);
                intent.putExtra("workTag", getTagByPosition(position));

                startActivity(intent);
            }
        });

        //子项点击事件 实现跳转功能
        expandList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                if (!list.get(groupPosition).getWorkmessage().get(childPosition).getMessage().equals("")) {
                    Intent intent = new Intent(TaskDepListActivity.this, TaskDepDetailActivity.class);
                    intent.putExtra("isEdit", isEdit);
                    intent.putExtra("workId", list.get(groupPosition).getWorkmessage().get(childPosition).getId());
                    intent.putExtra("workTag", getTagByPosition(groupPosition));
                    intent.putExtra("workState",list.get(groupPosition).getWorkmessage().get(childPosition).getIsComplate());
                    startActivity(intent);
                } else {
                    //子项数据为空 不能点击
                }
                return true;
            }
        });

////        父项点击 主要实现收缩展开功能
//        expandList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
//            @Override
//            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
//                //isGroupExpanded 返回当前group是否展开 展开为true
//                Log.d(TAG,"groupPosition="+groupPosition);
////                if (expandList.isGroupExpanded(groupPosition)) {
////                    expandList.collapseGroup(groupPosition);
////                } else {
////                    expandList.expandGroup(groupPosition);
////                }
//                return true;
//            }
//        });
    }

    private String getTagByPosition(int groupPosition) {
       // （1：年度任务 2：季度任务 3月度任务 4专项任务） position从0开始计数 所以+1 得到所需类型
        //根据position 0位开始位 加一 显示对tag 的转化
        return groupPosition+1+"";
    }


    public void gethttpByName(String name){
//        ZXLApplication.getInstance().showTextToast("根据用户名联网");
        Map<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("opcode","GetAllOfficeworkByDepartment");
        hashMap.put("username",name);

        VolleyRequest.RequestPost(this, MYURL.URL_HEAD, name, hashMap, new VolleyInterface<DepByName>(DepByName.class,TAG) {
            @Override
            public void onMySuccess(DepByName result) {
                list.clear();
                int length = result.getWorkAllmessage().size();
                for (int i = 0; i < length; i++) {
                    list.add(result.getWorkAllmessage().get(i));
                }
                setNullByDefault(length, "暂无任务");//当某项任务为空时 设置一个默认提示项目填充子项
                adapter.notifyDataSetChanged();
                setExpandGroup();
            }

            @Override
            public void onMyError(VolleyError error) {

            }
        });

    }

    public void getHttpById(String office_id) {
        Map<String, String> hashMap = new HashMap<String, String>();

        hashMap.put("opcode", "GetAllOfficework");
        hashMap.put("officeid", office_id);
        hashMap.put("username", PreferenceUtils.getUserName());

        VolleyRequest.RequestPost(this, MYURL.URL_HEAD, office_id, hashMap, new VolleyInterface<DepById>(DepById.class, TAG) {
            @Override
            public void onMySuccess(DepById result) {
                list.clear();
                int length = result.getGetAllOfficeworkByDepartment().size();
                for (int i = 0; i < length; i++) {
                    list.add(result.getGetAllOfficeworkByDepartment().get(i));
                }
                setNullByDefault(length, "暂无任务");//当某项任务为空时 设置一个默认提示项目填充子项
                adapter.notifyDataSetChanged();
                setExpandGroup();
            }

            @Override
            public void onMyError(VolleyError error) {

            }
        });
    }


    private void setExpandGroup() {
        //展开explistview 的方法
        int length = adapter.getGroupCount();
        for (int i = 0; i < length; i++) {
            expandList.expandGroup(i);
        }
//        Log.d(TAG,"setExpandGroup "+length);
    }

    private void setNullByDefault(int length, String Default) {
        //参数说明 Default为设置item项右边的 提示文字
        for (int i = 0; i < length; i++) {
            if (list.get(i).getWorkmessage().size() == 0) {
                List<DepTaskItem> items = new ArrayList<>(1);
                DepTaskItem item = new DepTaskItem("", Default, "", "");
                items.add(item);
                list.get(i).setWorkmessage(items);
            }
        }
    }
}
