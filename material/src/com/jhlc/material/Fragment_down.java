package com.jhlc.material;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.android.volley.VolleyError;
import com.jhlc.material.adapter.UsersDownAdapter;
import com.jhlc.material.bean.DownUserList;
import com.jhlc.material.bean.LogBean;
import com.jhlc.material.bean.OfficeBean;
import com.jhlc.material.bean.UserBean;
import com.jhlc.material.bean.Userlist;
import com.jhlc.material.utils.Constant;
import com.jhlc.material.utils.LogZ;
import com.jhlc.material.utils.MYURL;
import com.jhlc.material.utils.PreferenceUtils;
import com.jhlc.material.volley.VolleyInterface;
import com.jhlc.material.volley.VolleyRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/9/14 0014.
 */
public class Fragment_down extends BaseFragment{
    private static String TAG = "Fragment_down";
    private UsersDownAdapter adapter;

    private ArrayList<DownUserList> mList = new ArrayList<DownUserList>();
    private String userName=PreferenceUtils.getUserName();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_down, null);
        return view;
    }

    @Override
    protected void initView() {
        super.initView();
        setUpOrDown(Constant.ZXL_DOWN_USER);
        adapter = new UsersDownAdapter(getContext(), mList);
        ((ExpandableListView)ls_user_list).setAdapter(adapter);
        usersAdapter = adapter;
    }

    @Override
    protected void initListener() {
        super.initListener();
        ((ExpandableListView)ls_user_list).setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                //只有在ispass等于3的情况下才会响应item事件
                Userlist userlist = mList.get(groupPosition).getPersoninfor()[childPosition];
                if(userlist != null && "3".equals(userlist.getIspass())) {
                    //如果是刚通过邀请，就要删除小红点。
                    newMsgDB.deleteByUserId(userlist.getUserid());
                    Intent intent = new Intent(getActivity(), WorkItemsDetailActivity.class);
                    intent.putExtra("UpOrDown", UpOrDown);
                    intent.putExtra("workusername", userlist.getName());
//                newMsgDB.deleteByName(userlist.get(position).getName());
                    LogZ.d("NewMessage--> ", userlist.getName());
                    startActivityForResult(intent, Constant.RequestCode);
                }
                return true;
            }
        });

    }

    @Override
    public void getInfo() {
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("opcode", "get_login_userindex");
        hashMap.put("eid", Constant.EID);
        hashMap.put("Username", userName);
        hashMap.put("typeid", UpOrDown + "");

        VolleyRequest.RequestPost(getContext(), MYURL.URL_HEAD, "", hashMap, new VolleyInterface<UserBean>(UserBean.class, TAG) {
            @Override
            public void onMySuccess(UserBean result) {
                mList.clear();
                DownUserList[] userlists = result.getDownuserlist();
                Collections.addAll(mList, userlists);
                adapter.notifyDataSetChanged();
                /**
                 * 将数据显示
                 */
                if(usersAdapter!=null) {//会出现nullpoint错误
                    adapter.notifyDataSetChanged();
                    if(mList.size()==0){
                        showNoUsers();
                    }else{
                        view.findViewById(R.id.insertll).setVisibility(View.GONE);
                    }

                }
            }

            @Override
            public void onMyError(VolleyError error) {

            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.d(TAG, hidden + "");
    }
}
