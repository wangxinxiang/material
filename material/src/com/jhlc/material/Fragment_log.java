package com.jhlc.material;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.android.volley.VolleyError;
import com.google.gson.GsonBuilder;
import com.idunnololz.widgets.AnimatedExpandableListView;
import com.jhlc.material.adapter.LogAdapter;
import com.jhlc.material.adapter.UsersDownAdapter;
import com.jhlc.material.bean.LogBean;
import com.jhlc.material.bean.NoteBookBean;
import com.jhlc.material.bean.NoteBookFirstServerReturn;
import com.jhlc.material.bean.NoteBookServerMemo;
import com.jhlc.material.bean.OfficeBean;
import com.jhlc.material.bean.OfficeUserBean;
import com.jhlc.material.service.LoaderBusiness;
import com.jhlc.material.utils.Constant;
import com.jhlc.material.utils.LogZ;
import com.jhlc.material.utils.MYURL;
import com.jhlc.material.utils.PreferenceUtils;
import com.jhlc.material.utils.StringUtils;
import com.jhlc.material.utils.TimeUtil;
import com.jhlc.material.view.PullToRefreshView;
import com.jhlc.material.view.RoundImageView;
import com.jhlc.material.volley.VolleyInterface;
import com.jhlc.material.volley.VolleyRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/9/14 0014.
 */
public class Fragment_log extends Fragment   implements PullToRefreshView.OnHeaderRefreshListener, PullToRefreshView.OnFooterRefreshListener {
    private static String TAG = "Fragment_log";
    private final  String mPageName = "LogSystemActivity";
    private View view;
    private LinearLayout ll_my_info;
    private TextView   tv_user_name;
    private RoundImageView  img_head_photo;
    private AnimatedExpandableListView listView;
    private ArrayList<NoteBookBean> noteBookBeans = new ArrayList<NoteBookBean>();
    private LogAdapter logAdapter;

    private PullToRefreshView mPullToRefreshView;
    private int currentPage=0;
    private int pageSize=0;
    private int recordCount=0;

    private ProgressDialog pd;
    private SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd");


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.newmain_tab_notebook, null);
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ll_my_info= (LinearLayout) view.findViewById(R.id.ll_my_info);
        tv_user_name = (TextView) view.findViewById(R.id.tv_user_name);
        img_head_photo = (RoundImageView) view.findViewById(R.id.img_head_photo);
        listView = (AnimatedExpandableListView) view.findViewById(R.id.notebook_listView);
        mPullToRefreshView = (PullToRefreshView) view.findViewById(R.id.pull_refresh_view);
        mPullToRefreshView.setNeedfootfresh(true);
        mPullToRefreshView.setOnHeaderRefreshListener(this);
        mPullToRefreshView.setOnFooterRefreshListener(this);
        logAdapter = new LogAdapter(getActivity());
        listView.setAdapter(logAdapter);
//        List<LogAdapter.GroupItem> items = new ArrayList<>();
//        logAdapter.setData(items);        //设置数据
//        logAdapter.setData(items);        //设置数据
        initListener();
    }

    private void initListener() {
        ll_my_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
            }
        });


        // In order to show animations, we need to use a custom click handler
        // for our ExpandableListView.
        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                // We call collapseGroupWithAnimation(int) and
                // expandGroupWithAnimation(int) to animate group
                // expansion/collapse.
//                d
                LogZ.d("lyjtest", "its on onGroupClick！");
                if (listView.isGroupExpanded(groupPosition)) {
                     listView.collapseGroupWithAnimation(groupPosition);
                } else {
                    listView.expandGroupWithAnimation(groupPosition);
                }
                return true;
            }

        });
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                LogAdapter.ChildHolder holder = (LogAdapter.ChildHolder) view.getTag();
                LogAdapter.ChildItem childItem = logAdapter.getChild(i, i1);
                if (holder.down_ll.getVisibility() == View.GONE) {
                    childItem.showButtonGroup = true;
                    holder.down_ll.setVisibility(View.VISIBLE);
                } else {
                    childItem.showButtonGroup = false;
                    holder.down_ll.setVisibility(View.GONE);
                }
                LogAdapter.GroupItem gi = logAdapter.getGroup(i);
                for (int j = 0; j < gi.items.size(); j++) {
                    LogAdapter.ChildItem item = gi.items.get(j);
                    if (i1 != j) {
                        item.showButtonGroup = false;

                    }
                }
                logAdapter.notifyDataSetChanged();
                return false;
            }
        });

        listView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int i) {
                LogAdapter.GroupItem groupItem = logAdapter.getGroup(i);
                List<LogAdapter.ChildItem> items = groupItem.items;
                for (int j = 0; j < items.size(); j++) {
                    LogAdapter.ChildItem childItem = items.get(j);
                    childItem.showButtonGroup = false;
                }
            }
        });
    }



    //---------------头部

    private void setUserInfo() {
        try {
            LogZ.d("getIsLogin--> ", "" + PreferenceUtils.getIsLogin());
            // LoaderBusiness.loadImage(MYURL.URL_HEAD + PreferenceUtils.getInstance().getHeadImage(), img_head_photo);
            setHeadImage();
            String username = PreferenceUtils.getUserName();
            if(!username.equals(PreferenceUtils.getUserName())) {
                tv_user_name.setText("我");
            } else {
                tv_user_name.setText(username);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setHeadImage() {
        String headimage = PreferenceUtils.getHeadImage();
        if(StringUtils.isNotBlank(headimage)){
            LoaderBusiness.loadImage(MYURL.img_HEAD + headimage, img_head_photo);
        }else{
            img_head_photo.setImageDrawable(this.getResources().getDrawable(R.drawable.defult_user_headimg));
        }
    }

    /**
     * 从服务器获取日志
     * @param pageindex 页数
     */
    private void getMemosFromServer(final int pageindex) {
        AsyncHttpClient client = new AsyncHttpClient();




        RequestParams params = new RequestParams();
        try {
            //String m_szAndroidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            params.put("opcode", "ls_getpagememo");
            //String userName = PreferenceUtils.getUserName();

            params.put("username", PreferenceUtils.getUserName());
            params.put("pageIndex", pageindex);
            params.put("pageSize", 10);
            params.put("eid", Constant.EID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        client.post(MYURL.URL_HEAD, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                LogZ.d("getMemosFromServer--> ", new String(responseBody));
                //{"code":100,"msg":"success","opcode":"reguser_eid_success","headimg":"/UserImage/201411080338542207.JPG"}

                NoteBookFirstServerReturn noteBookServerReturn = new GsonBuilder().create().fromJson(new String(responseBody), NoteBookFirstServerReturn.class);
                if (noteBookServerReturn!=null&&"ls_getpagememo".equals(noteBookServerReturn.getOpcode())&&noteBookServerReturn.getList()!=null) {
                    //  ZXLApplication.getInstance().showTextToast("获得成功:"+noteBookServerReturn.getList().length);


                    if(pageindex==1){
                        noteBookBeans.clear();
                    }
                    noteBookBeans.addAll(change2NotebookBeans(noteBookServerReturn.getList()));
                    currentPage=noteBookServerReturn.getPageIndex();
                    pageSize= noteBookServerReturn.getPageSize();
                    recordCount= noteBookServerReturn.getRecordCount();
                }
                //noteBookBeans = noteBookDB.getNoteBook(0);
                List<LogAdapter.GroupItem> items=changeNoteBookBeans2GroupItemList(noteBookBeans);
                logAdapter.setData(items);
                logAdapter.notifyDataSetChanged();
                if (items.size() != 0) {
                    for (int i = 0; i < items.size(); i++) {
                        listView.expandGroup(i);
                    }
                }

                inItRefreshView();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                ZXLApplication.getInstance().showNetWorkingErrorTextToast();
            }
        });
    }

    private ArrayList<NoteBookBean> change2NotebookBeans(NoteBookServerMemo[] list) {
        ArrayList<NoteBookBean> temp=new ArrayList<NoteBookBean>();
        if(list!=null){
            for (int i = 0; i < list.length; i++) {
                NoteBookServerMemo noteBookServerMemo = list[i];
                NoteBookBean nb=change2NotebookBean(noteBookServerMemo);
                temp.add(nb);
            }
        }
        return temp;
    }

    private NoteBookBean change2NotebookBean(NoteBookServerMemo noteBookServerMemo) {
        NoteBookBean result=new NoteBookBean();
        result.setDate(noteBookServerMemo.getOverTime());
        result.setIstop(Integer.parseInt(noteBookServerMemo.getTop()));
        result.setMid(noteBookServerMemo.getID());
        result.setTitle(noteBookServerMemo.getMemoContent());
        result.setType(StringUtils.getLocalType(noteBookServerMemo.getMType()));
        return result;
    }

//    /**
//     * 将 notebookbean 的集合转成 GroupItem 集合  有时间分组的实现方法
//     * @param noteBookBeans
//     * @return
//     */
//    private List<LogAdapter.GroupItem> changeNoteBookBeans2GroupItemList(ArrayList<NoteBookBean> noteBookBeans) {
//        String nowDateStr=TimeUtil.sdf.format(new Date());
//
////        int
//        List<LogAdapter.GroupItem> result=new ArrayList<LogAdapter.GroupItem>();
//        Map<String,LogAdapter.GroupItem> groupItemMap=new LinkedHashMap<String, LogAdapter.GroupItem>() ;
//
//        LogAdapter.GroupItem delayGi=new LogAdapter.GroupItem();
//        delayGi.title="延期";
//        delayGi.type= Constant.notebook_delay;
//        delayGi.items=new ArrayList<LogAdapter.ChildItem>();
//        groupItemMap.put(Constant.notebook_delay,delayGi);
//
//
//        LogAdapter.GroupItem todayGI=new LogAdapter.GroupItem();
//        todayGI.title="今天";
//        todayGI.type=Constant.notebook_today;
//        todayGI.items=new ArrayList<LogAdapter.ChildItem>();
//        groupItemMap.put(Constant.notebook_today,todayGI);
//
//        LogAdapter.GroupItem tomorrowGI=new LogAdapter.GroupItem();
//        tomorrowGI.title="明天";
//        tomorrowGI.type=Constant.notebook_tomorrow;
//        tomorrowGI.items=new ArrayList<LogAdapter.ChildItem>();
//        groupItemMap.put(Constant.notebook_tomorrow,tomorrowGI);
//
//        LogAdapter.GroupItem afterGI=new LogAdapter.GroupItem();
//        afterGI.title="近期";
//        afterGI.type=Constant.notebook_recent;
//        afterGI.items=new ArrayList<LogAdapter.ChildItem>();
//        groupItemMap.put(Constant.notebook_recent,afterGI);
//
//        LogAdapter.GroupItem laterGI=new LogAdapter.GroupItem();
//        laterGI.title="以后";
//        laterGI.type=Constant.notebook_later;
//        laterGI.items=new ArrayList<LogAdapter.ChildItem>();
//        groupItemMap.put(Constant.notebook_later,laterGI);
//
//
//        for (Iterator<NoteBookBean> iterator = noteBookBeans.iterator(); iterator.hasNext(); ) {
//            NoteBookBean bookBean = iterator.next();
//            LogAdapter.GroupItem gi=null;
//            long days=  TimeUtil.getDaysBetween(nowDateStr,bookBean.getDate());
//            if("4".equals(bookBean.getType())){
//                gi=laterGI;
//            }else {
//                if (days < 0) {
//                    gi = delayGi;
//                } else if (days == 0) {
//                    gi = todayGI;
//                } else if (days == 1) {
//                    gi = tomorrowGI;
//                } else if (days <= 5) {
//                    gi = afterGI;
//                }else{
//                    gi=laterGI;
//                }
//            }
//
//            LogAdapter.ChildItem ci=new LogAdapter.ChildItem();
//            ci.title=bookBean.getTitle();
//            try {
//                if(bookBean.getDate()!=null) {
//                    ci.hint = sdf.format(sdf.parse(bookBean.getDate()));
//                }
//
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            ci.id=bookBean.getId();
//            ci.top=bookBean.getIstop();
//            ci.mid = bookBean.getMid();
//            gi.items.add(ci);
//
//        }
//
//
//        for (Iterator<LogAdapter.GroupItem> iterator = groupItemMap.values().iterator(); iterator.hasNext(); ) {
//            LogAdapter.GroupItem next = iterator.next();
//
//            if(Constant.notebook_delay.equals(next.type)){   //延期的类型有值的时候才加入到界面上显示。
//                if(next.items.size()>0){
//                    result.add(next);
//                }
//            }else{
//                result.add(next);
//            }
//
//        }
//
//        return result;
//    }

    /**
     * 将 notebookbean 的集合转成 GroupItem 集合
     * @param noteBookBeans
     * @return
     */
    private List<LogAdapter.GroupItem> changeNoteBookBeans2GroupItemList(ArrayList<NoteBookBean> noteBookBeans) {
        String nowDateStr=TimeUtil.sdf.format(new Date());

//        int
        List<LogAdapter.GroupItem> result=new ArrayList<LogAdapter.GroupItem>();
        Map<String,LogAdapter.GroupItem> groupItemMap=new LinkedHashMap<String, LogAdapter.GroupItem>() ;


        LogAdapter.GroupItem delayGi = new LogAdapter.GroupItem();
        delayGi.title = "备忘录";
        delayGi.type = Constant.notebook_delay;
        delayGi.items = new ArrayList<LogAdapter.ChildItem>();
        groupItemMap.put(Constant.notebook_delay, delayGi);




        for (Iterator<NoteBookBean> iterator = noteBookBeans.iterator(); iterator.hasNext(); ) {
            NoteBookBean bookBean = iterator.next();
            LogAdapter.GroupItem gi=delayGi;
            LogAdapter.ChildItem ci=new LogAdapter.ChildItem();
            ci.title=bookBean.getTitle();
            try {
                if(StringUtils.isNotBlank(bookBean.getDate())) {
                    ci.hint = sdf.format(sdf.parse(bookBean.getDate()));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            ci.id=bookBean.getId();
            ci.top=bookBean.getIstop();
            ci.mid=bookBean.getMid();
            gi.items.add(ci);
        }

        for (Iterator<LogAdapter.GroupItem> iterator = groupItemMap.values().iterator(); iterator.hasNext(); ) {
            LogAdapter.GroupItem next = iterator.next();
            result.add(next);


//            if(Constant.notebook_delay.equals(next.type)){   //延期的类型有值的时候才加入到界面上显示。
//                if(next.items.size()>0){
//                    result.add(next);
//                }
//            }else{
//                result.add(next);
//            }

        }

        return result;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        // if(noteBookBeans.size()==0){
        getMemosFromServer(1);
        // }
        MobclickAgent.onPageStart(mPageName);
        MobclickAgent.onResume(getActivity());
        setUserInfo();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.d(TAG, hidden + "");
    }

    @Override
    public void onFooterRefresh(PullToRefreshView view) {
        if (currentPage*pageSize<recordCount) {
            getMemosFromServer(currentPage+1);
        }else{
            inItRefreshView();
        }
    }

    @Override
    public void onHeaderRefresh(PullToRefreshView view) {
        getMemosFromServer(1);
    }

    private void inItRefreshView(){
        mPullToRefreshView.onHeaderRefreshComplete(TimeUtil.getRefreshTimeStr());
        mPullToRefreshView.onFooterRefreshComplete();
        mPullToRefreshView.onHeaderRefreshComplete();
    }
}
