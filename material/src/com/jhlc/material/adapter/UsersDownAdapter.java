package com.jhlc.material.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jhlc.material.R;
import com.jhlc.material.bean.DownUserList;
import com.jhlc.material.bean.OfficeBean;
import com.jhlc.material.bean.OfficeUserBean;
import com.jhlc.material.bean.Userlist;
import com.jhlc.material.db.SetNewMsgDB;
import com.jhlc.material.db.UserListDB;
import com.jhlc.material.service.LoaderBusiness;
import com.jhlc.material.utils.Constant;
import com.jhlc.material.utils.MYURL;

import com.jhlc.material.utils.StringUtils;
import com.jhlc.material.utils.ViewHolder;
import com.jhlc.material.view.RoundImageView;

import java.util.ArrayList;
import java.util.Arrays;


public class UsersDownAdapter extends BaseExpandableListAdapter implements UsersAdapterComment{
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<DownUserList> mList;
    private UsersBaseAdapter usersBaseAdapter;
    private SetNewMsgDB newMsgDB ;

    public UsersDownAdapter(Context context, ArrayList<DownUserList> list) {
        this.mList = list;
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        usersBaseAdapter = new UsersBaseAdapter(mContext, null, 0);
        newMsgDB = new SetNewMsgDB(mContext);
    }

    @Override
    public int getGroupCount() {
        return mList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mList.get(groupPosition).getPersoninfor().length;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mList.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {

        return mList.get(groupPosition).getPersoninfor()[childPosition];
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    public void setIsshowdetail(boolean isshowdetail) {
        usersBaseAdapter.setIsshowdetail(isshowdetail);
    }

    public boolean isIsshowdetail() {
        return usersBaseAdapter.isIsshowdetail();
    }

    public void setIs_delete(boolean is_delete) {
        usersBaseAdapter.setIs_delete(is_delete);
    }

    public boolean getIs_delete() {
        return usersBaseAdapter.getIs_delete();
    }


    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_down_group, parent, false);
        }
        TextView tv_group_office_name = ViewHolder.get(convertView, R.id.tv_log_group_office_name);
        ImageView img_group_indicate = ViewHolder.get(convertView, R.id.img_log_group_expand);
        ImageView iv_red_point = ViewHolder.get(convertView, R.id.iv_red_point);

        DownUserList bean = mList.get(groupPosition);
        tv_group_office_name.setText(bean.getDepartmentname() + "(" + bean.getPersonnum() + ")");
        //判断图标剪头的方向
        if (isExpanded) {
            img_group_indicate.setImageResource(R.drawable.expand_down);
        } else {
            img_group_indicate.setImageResource(R.drawable.expand_up);
        }

        //TODO 根据是否有新消息显示红点
        if (newMsgDB.getByDepartmentName(bean.getDepartmentname())) {
            iv_red_point.setVisibility(View.VISIBLE);
        } else {
            iv_red_point.setVisibility(View.GONE);
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        usersBaseAdapter.setUserlist(Arrays.asList(mList.get(groupPosition).getPersoninfor()));
        return usersBaseAdapter.getView(childPosition, convertView, parent);

    }

    @Override
    public boolean isChildSelectable(int groupPosition,
                                     int childPosition) {
        return true;
    }


    public void setMyCallback(AdapterCallback myCallback) {
        usersBaseAdapter.setMyCallback(myCallback);
    }
}