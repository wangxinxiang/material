package com.jhlc.material;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.jhlc.material.adapter.AdapterCallback;
import com.jhlc.material.adapter.UsersAdapterComment;
import com.jhlc.material.adapter.UsersBaseAdapter;
import com.jhlc.material.bean.UserBean;
import com.jhlc.material.bean.Userlist;
import com.jhlc.material.db.SetNewMsgDB;
import com.jhlc.material.db.UserListDB;
import com.jhlc.material.utils.Constant;
import com.jhlc.material.utils.LogZ;
import com.jhlc.material.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Administrator on 2015/10/7.
 */
public abstract class BaseFragment extends Fragment {
    private ImageButton ibtn_add_users, ibtn_delete_users;
    private CheckBox cb_detail_info;
    private Button ibtn_refresh;
    protected ListView ls_user_list;
    protected View view;
    protected int UpOrDown = -1;

    protected SetNewMsgDB newMsgDB;

    protected UsersAdapterComment usersAdapter;  //用户item的adapter抽取


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        getInfo();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    protected void initView() {
        ibtn_add_users = (ImageButton) view.findViewById(R.id.ibtn_add_users);
        ibtn_delete_users = (ImageButton) view.findViewById(R.id.ibtn_delete_users);
        ls_user_list = (ListView) view.findViewById(R.id.ls_user_list);
        cb_detail_info = (CheckBox) view.findViewById(R.id.cb_detail_info);
        newMsgDB = new SetNewMsgDB(getActivity());
        ibtn_refresh = (Button) view.findViewById(R.id.ibtn_refresh);

        /**
         *  注册广播： 用于接收新消息出发，
         * */
        IntentFilter filter = new IntentFilter(Constant.WorkItemsNewMsgAction);
        getActivity().registerReceiver(broadcastReceiver, filter);
    }


    //监听到有 新消息 则刷新列表 进行未读标识
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // 查询新来的消息 的任务ID 是否在本界面
            int upordown = intent.getIntExtra("upordown", -1);
//             ZXLApplication.getInstance().showTextToast("已经获得广播："+ upordown);
            if(UpOrDown == upordown){
                getInfo();
            }
        }
    };

    protected void initListener() {


        cb_detail_info.setChecked(PreferenceUtils.isNeedShowReport());
        cb_detail_info.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                PreferenceUtils.getInstance().setNeedShowReport(isChecked);
                showDetail(isChecked);

            }
        });

        ibtn_add_users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (PreferenceUtils.getIsLogin()) {
                    intent = new Intent(getActivity(), AddUsersActivity.class);
                    intent.putExtra("UpOrDown", UpOrDown);
                    startActivityForResult(intent, Constant.RequestCode);
                } else {
                    intent = new Intent(getActivity(), MyInfoActivity.class);
                    intent.putExtra("login", true);
                    startActivity(intent);
                }

            }
        });


        ibtn_delete_users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != usersAdapter && usersAdapter.getIs_delete()) {
                    usersAdapter.setIs_delete(false);
                    usersAdapter.notifyDataSetChanged();
                } else if (null != usersAdapter) {
                    usersAdapter.setIs_delete(true);
                    usersAdapter.notifyDataSetChanged();
                }
            }
        });

        usersAdapter.setMyCallback(new AdapterCallback() {
            @Override
            public void refreshView() {
                getInfo();
            }
        });

        ibtn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInfo();
            }
        });
    }

    /**
     * 必须实现，设置上级还是下级
     *
     * @param upOrDown UP = 1, DOWN = 0;
     */
    protected void setUpOrDown(int upOrDown) {
        UpOrDown = upOrDown;
    }


    /**
     * 要显示详细信息时的方法
     *
     * @param isChecked 是否要显示
     */
    protected void showDetail(boolean isChecked) {
        if (null == usersAdapter) return;
        if (PreferenceUtils.isNeedShowReport()) { //显示VIEW
            usersAdapter.setIsshowdetail(true);
        } else {
            usersAdapter.setIsshowdetail(false);
        }
        usersAdapter.notifyDataSetChanged();
    }

    /**
     * 从服务器获取数据
     * 刷新界面不在这里实现
     */
    public abstract void getInfo();

    /**
     * 有新消息时更新红点
     * 如果是邀请信息则要重新获取信息，如果是任务信息则不用
     */
    public void refreshNewMsg() {
        if (newMsgDB.getByWorkID(Constant.RenWu)) {
            getInfo();
        }

    }

    /**
     * 当没有用户时界面的显示效果
     */
    protected void showNoUsers() {
        view.findViewById(R.id.insertll).setVisibility(View.VISIBLE);
        Button addUser = (Button) view.findViewById(R.id.btn_adduser);
        TextView adduser_tip = (TextView) view.findViewById(R.id.tv_adduser_tip);

        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddUsersActivity.class);
                intent.putExtra("UpOrDown", UpOrDown);
                startActivityForResult(intent, Constant.RequestCode);
            }
        });
        if (Constant.ZXL_UP_USER == UpOrDown) {
            adduser_tip.setText("目前您还没有上级");
            addUser.setText("添加上级");

        } else if (Constant.ZXL_DOWN_USER == UpOrDown) {
            adduser_tip.setText("目前您还没有下属");
            addUser.setText("添加下属");
        }
    }

}
