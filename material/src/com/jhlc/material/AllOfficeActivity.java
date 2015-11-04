package com.jhlc.material;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.google.gson.GsonBuilder;
import com.jhlc.material.bean.OfficeBean;
import com.jhlc.material.bean.OfficeRetutnBean;
import com.jhlc.material.bean.OfficeUserBean;
import com.jhlc.material.db.OfficeDB;
import com.jhlc.material.service.LoaderBusiness;
import com.jhlc.material.utils.Constant;
import com.jhlc.material.utils.LogZ;
import com.jhlc.material.utils.MYURL;
import com.jhlc.material.utils.PreferenceUtils;
import com.jhlc.material.view.RoundImageView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;
import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import android.os.Message;

/**
 * Created by 104468 on 2015/3/12.
 */
public class AllOfficeActivity extends Activity {


    private final String mPageName = "AllOfficeActivity";

    private List<OfficeBean> list = new ArrayList<OfficeBean>();
    private ExpandableListView listView;
    private ExpandableListAdapter adapter;

    private int sign=-1; //控制grop的展开


    private OfficeDB officeDB = new OfficeDB(this);


    private EditText office_search_text;


    private int activityStatus = ActivitStatus.PLAIN_STATUS;


    private interface ActivitStatus{
        public int PLAIN_STATUS = 0;
        public int SEARCH_STATUS = 1;
    }


    private RelativeLayout loading_bar_rl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.tab_all_office);
        LogZ.d("AllOfficeActivity", "启动");


        ZXLApplication.getInstance().addActivity(this);
        initView();


    }

    private void initView() {


        office_search_text = (EditText) findViewById(R.id.office_search_text);
        office_search_text.addTextChangedListener(new EditChangedListener());

        loading_bar_rl = (RelativeLayout) findViewById(R.id.loading_bar_rl);


        ImageButton office_search = (ImageButton) findViewById(R.id.office_search);
        office_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getWindow().peekDecorView();
                if (view != null) {
                    InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });


        listView = (ExpandableListView) findViewById(R.id.all_office_list);
        adapter = new ExpandableListAdapter(this);
        listView.setAdapter(adapter);

        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                LogZ.d("onChildClick", "点击了日志");


                Intent intent = new Intent(AllOfficeActivity.this, LogSystemActivity.class);
                OfficeBean officeBean = list.get(groupPosition);

                OfficeUserBean userBean = officeBean.getList().get(childPosition);
                intent.putExtra("username", userBean.getUsername());
                intent.putExtra("headimage", userBean.getHeadimage());

                startActivity(intent);
                return true;
            }
        });

//        listView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
//            @Override
//            public void onGroupExpand(int groupPosition) {
//                LogZ.d("onChildClick", "点击了group");
//                for (int i = 0; i < list.size(); i++) {
//                    if (groupPosition != i) {
//                        listView.collapseGroup(i);
//                    }
//                }
//
//            }
//        });

//        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
//            @Override
//            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long l) {
//                LogZ.d("onChildClick", "点击了group");
//                if (sign== -1) {
//                    // 展开被选的group
//                    listView.expandGroup(groupPosition);
//                    // 设置被选中的group置于顶端
//                    listView.setSelectedGroup(groupPosition);
//                    sign= groupPosition;
//                } else if (sign== groupPosition) {
//                    listView.collapseGroup(sign);
//                    sign= -1;
//                } else {
//                    listView.collapseGroup(sign);
//                    // 展开被选的group
//                    listView.expandGroup(groupPosition);
//                    // 设置被选中的group置于顶端
//                    listView.setSelectedGroup(groupPosition);
//                    sign= groupPosition;
//                }
//                return true;
//            }
//        });


    }


    private void getContent() {
        loading_bar_rl.setVisibility(View.VISIBLE);
        AsyncHttpClient client = new AsyncHttpClient();


        RequestParams params = new RequestParams();
        params.put("opcode", "ls_getalluser");
        params.put("eid", Constant.EID);
        client.post(MYURL.URL_HEAD, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                LogZ.d("ls_getallusers ", "链接网络");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                LogZ.d("ls_getallusers", new String(responseBody));
                //{"code":100,"msg":"success","opcode":"reguser_eid_success","headimg":"/UserImage/201411080338542207.JPG"}
                OfficeRetutnBean officeRetutnBean = new GsonBuilder().create().fromJson(new String(responseBody), OfficeRetutnBean.class);
                if (officeRetutnBean != null && "100".equals(officeRetutnBean.getCode()) && "ls_getallusers".equals(officeRetutnBean.getOpcode()) && officeRetutnBean.getList() != null) {
                    LogZ.d("ls_getallusers", "数据转换成功");
                    long time = System.currentTimeMillis();
                    LogZ.d("officeDB --->time", "当前的时间戳"+time);
                    LogZ.d("officeDB --->gettime", "存储的时间戳"+PreferenceUtils.getCurrentTimeMillis());
                    if(PreferenceUtils.getCurrentTimeMillis() == 0 || time - PreferenceUtils.getCurrentTimeMillis() > 1000000){
                        LogZ.d("officeDB", "连接数据库");
                        final List<OfficeBean> oblist = officeRetutnBean.getList();
                        PreferenceUtils.getInstance().setCurrentTimeMillis(time);
                        Thread thread = new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                officeDB.updateOffice(oblist);
                            }
                        };
                        thread.start();
                    }
                    list = new ArrayList<OfficeBean>();
                    adapter.setList(officeRetutnBean.getList());
                    adapter.notifyDataSetChanged();
                } else {
                    list = new ArrayList<OfficeBean>();
                    adapter.setList(officeDB.getData());
                    adapter.notifyDataSetChanged();
                }
                loading_bar_rl.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                ZXLApplication.getInstance().showNetWorkingErrorTextToast();
                list = new ArrayList<OfficeBean>();
                adapter.setList(officeDB.getData());
                adapter.notifyDataSetChanged();
                loading_bar_rl.setVisibility(View.GONE);
            }
        });
    }

    private class EditChangedListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            /** 输入文本之前的状态*/
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            /** 输入文字中的状态*/


        }

        @Override
        public void afterTextChanged(Editable s) {
            /** 输入文字后*/
            String search = office_search_text.getText().toString();
            if(!"".equals(search) && search.length() != 0){
                activityStatus = ActivitStatus.SEARCH_STATUS;
                LogZ.d("officeSearch","搜索关键字"+search);
                List<OfficeBean> oblist = officeDB.getDataBySearch(search);
                if(oblist.size() != 0){
                    LogZ.d("officeSearch","有搜索到数据");
                    list = new ArrayList<OfficeBean>();

                    adapter.setList(oblist);
                    adapter.notifyDataSetChanged();

                }else{
                    ZXLApplication.getInstance().showTextToast("没有符合要求的人");
                }
            }else{
                activityStatus = ActivitStatus.PLAIN_STATUS;
                list = new ArrayList<OfficeBean>();
                adapter.setList(officeDB.getData());
                adapter.notifyDataSetChanged();
            }
        }

    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            adapter.notifyDataSetChanged();
            super.handleMessage(msg);
        }
    };

private class ExpandableListAdapter extends BaseExpandableListAdapter {
        private Context context;


        public ExpandableListAdapter(Context context) {
            this.context = context;
        }

        public void setList(List<OfficeBean> list) {
            LogZ.d("listadd", "添加数据");

            OfficeUserBean userBean = new OfficeUserBean();
            userBean.setUsername("我的日志");
            userBean.setHeadimage("");
            userBean.setJob("");

            List<OfficeUserBean> userBeansList = new ArrayList<OfficeUserBean>();
            userBeansList.add(userBean);
            OfficeBean bean = new OfficeBean();
            bean.setOfficename("");
            bean.setList(userBeansList);
            AllOfficeActivity.this.list.add(bean);

            for (int i = 0; i < list.size(); i++) {
                AllOfficeActivity.this.list.add(list.get(i));
            }
        }

        @Override
        public int getGroupCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            // TODO Auto-generated method stub
            return groupPosition;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            // TODO Auto-generated method stub

            OfficeBean officeBean = list.get(groupPosition);
            return officeBean.getList().size();
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            OfficeBean officeBean = list.get(groupPosition);
            OfficeUserBean userBean = officeBean.getList().get(childPosition);
            return userBean;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            // TODO Auto-generated method stub
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            GroupHolder groupHolder = null;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(R.layout.office_group_item, parent, false);

                groupHolder = new GroupHolder();
                groupHolder.textView = (TextView) convertView.findViewById(R.id.office_name);
//                groupHolder.imageView= (ImageView) convertView.findViewById(R.id.img_expand);
//                groupHolder.line= (TextView) findViewById(R.id.tv_line);
                convertView.setTag(groupHolder);
            } else {
                groupHolder = (GroupHolder) convertView.getTag();
            }
            convertView.setClickable(true);
            OfficeBean bean = list.get(groupPosition);
            if ("".equals(bean.getOfficename())) {
                groupHolder.textView.setVisibility(View.GONE);
            } else {
                if (groupHolder.textView.getVisibility() == View.GONE) {
                    groupHolder.textView.setVisibility(View.VISIBLE);
                }
                groupHolder.textView.setText(bean.getOfficename());
            }
            listView.expandGroup(groupPosition);
            //判断图标剪头的方向
//            if(isExpanded){
//                groupHolder.imageView.setImageResource(R.drawable.expand_down);
//            }else{
//                groupHolder.imageView.setImageResource(R.drawable.expand_up);
//            }
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ChildHolder childHolder = null;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(R.layout.office_child_item, parent, false);
                childHolder = new ChildHolder();
                childHolder.imageView = (RoundImageView) convertView.findViewById(R.id.round_head_image);
                childHolder.username = (TextView) convertView.findViewById(R.id.tv_user_name);
                childHolder.userjob = (TextView) convertView.findViewById(R.id.tv_user_job);
                convertView.setTag(childHolder);
            } else {
                childHolder = (ChildHolder) convertView.getTag();
            }
            OfficeUserBean bean = list.get(groupPosition).getList().get(childPosition);

            //我的日志时获取本地头像
            if("我的日志".equals(bean.getUsername())){
                if (!"".equals(PreferenceUtils.getHeadImage())) {
                    LoaderBusiness.loadImage(MYURL.img_HEAD + PreferenceUtils.getHeadImage(), childHolder.imageView);
                } else {
                    childHolder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.defult_user_headimg));
                }
            }else{
                if (bean != null && !"".equals(bean.getHeadimage())) {
                    LoaderBusiness.loadImage(MYURL.img_HEAD + bean.getHeadimage(), childHolder.imageView);
                } else {
                    childHolder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.defult_user_headimg));
                }
            }
            ///// 判断本地用户头像和服务器是否相同不相同刷新
            if(bean.getUsername().equals(PreferenceUtils.getUserName())){
                if(!bean.getHeadimage().equals(PreferenceUtils.getHeadImage())){
                    PreferenceUtils.getInstance().setHeadImage(bean.getHeadimage());
                    Thread thread = new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            try {
                                Thread.sleep(700);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Message msg = new Message();
                            handler.sendMessage(msg);
                        }
                    };
                    thread.start();
                }
            }
            childHolder.username.setText(bean.getUsername());

            childHolder.userjob.setText(bean.getJob());
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition,
                                         int childPosition) {
            // TODO Auto-generated method stub
            return true;
        }

    }

    private class GroupHolder {
        public TextView textView;
        public ImageView imageView;
        public TextView line;
    }

    private class ChildHolder {
        public RoundImageView imageView;
        public TextView username;
        public TextView userjob;
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(mPageName);
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(activityStatus == ActivitStatus.PLAIN_STATUS){
            getContent();
        }
        MobclickAgent.onPageStart(mPageName);
        MobclickAgent.onResume(this);
    }
}
