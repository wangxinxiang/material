package com.jhlc.material;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.GsonBuilder;
import com.jhlc.material.adapter.AdapterCallback;
import com.jhlc.material.adapter.UsersUpAdapter;
import com.jhlc.material.bean.UserBean;
import com.jhlc.material.bean.Userlist;
import com.jhlc.material.utils.Constant;
import com.jhlc.material.utils.LogZ;
import com.jhlc.material.utils.MYURL;
import com.jhlc.material.utils.PreferenceUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2015/9/14 0014.
 */
public class Fragment_up extends BaseFragment {
    private static String TAG = "Fragment_up";
    private UsersUpAdapter adapter;

    private List<Userlist> userlist = new ArrayList<Userlist>();       //储存列表信息

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_up, null);
        return view;
    }

    @Override
    protected void initView() {
        super.initView();
        setUpOrDown(Constant.ZXL_UP_USER);
        adapter = new UsersUpAdapter(getActivity(), userlist);
        ls_user_list.setAdapter(adapter);
        usersAdapter = adapter;
    }


    @Override
    protected void initListener() {
        super.initListener();
        ls_user_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //只有在ispass等于3的情况下才会响应item事件
                if(userlist.size()>0&&userlist.get(position)!=null&&"3".equals(userlist.get(position).getIspass())) {
                    //如果是刚通过邀请，就要删除小红点。
                    newMsgDB.deleteByUserId(userlist.get(position).getUserid());
                    Intent intent = new Intent(getActivity(), WorkItemsDetailActivity.class);
                    intent.putExtra("UpOrDown", UpOrDown);
                    intent.putExtra("workusername", userlist.get(position).getName());
//                newMsgDB.deleteByName(userlist.get(position).getName());
                    LogZ.d("NewMessage--> ", userlist.get(position).getName());
                    startActivityForResult(intent, Constant.RequestCode);
                }
            }
        });

    }

    @Override
    public void getInfo() {

        if(!ZXLApplication.getInstance().isconnected()){
            ZXLApplication.getInstance().showNetWorkingErrorTextToast();
            return;
        }
        // 发送请求到服务器
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(10000);
        // 创建请求参数
        final RequestParams params = new RequestParams();
        params.put("opcode", "get_login_userindex");
        params.put("Username", PreferenceUtils.getUserName());
        params.put("eid", Constant.EID);
        params.put("typeid", UpOrDown);

        // 执行post方法
        client.post(MYURL.URL_HEAD, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                    view.findViewById(R.id.loading_bar_rl).setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                view.findViewById(R.id.loading_bar_rl).setVisibility(View.GONE);
                LogZ.d("lyjuserselect", "网络请求 结束。。。。。。。。...");
                // 设置值
                LogZ.d("getUpUserData--> ", "username==" + PreferenceUtils.getUserName() + "   " + new String(responseBody));
                UserBean userBean = new GsonBuilder().create().fromJson(new String(responseBody), UserBean.class);


                if(userBean == null || "login_notexist".equals(userBean.getOpcode())||-1 == userBean.getCode()){
                    ZXLApplication.getInstance().showTextToast("服务器错误！");
                    return ;
                }

                /**
                 * 将数据显示
                 */
                if(usersAdapter!=null) {//会出现nullpoint错误
                    userlist.clear();
                    Userlist[] userlists = userBean.getUserlists();
                    for (Userlist userlist1 : userlists) {
                        userlist.add(userlist1);
                    }
                    adapter.notifyDataSetChanged();
                    if(userlist.size()==0){
                        showNoUsers();
                    }else{
                        view.findViewById(R.id.insertll).setVisibility(View.GONE);
                    }

                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // 打印错误信息
                view.findViewById(R.id.loading_bar_rl).setVisibility(View.GONE);
                //  loadingPd.dismiss();
                ZXLApplication.getInstance().showNetWorkingErrorTextToast();
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
