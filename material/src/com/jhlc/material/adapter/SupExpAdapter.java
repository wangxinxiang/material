package com.jhlc.material.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jhlc.material.R;
import com.jhlc.material.bean.SupItem;
import com.jhlc.material.bean.SupLogByOffice;
import com.jhlc.material.service.LoaderBusiness;
import com.jhlc.material.utils.MYURL;
import com.jhlc.material.utils.ViewHolder;
import com.jhlc.material.view.RoundImageView;

import java.util.ArrayList;


public class SupExpAdapter extends BaseExpandableListAdapter {
    private static String TAG = "SupExpAdapter";
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<SupLogByOffice> mList;

    //构造方法传入 context上下文 和list对象 是否可添加任务的布尔值
    public SupExpAdapter(Context context, ArrayList<SupLogByOffice> list) {
        this.mList = list;
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getGroupCount() {
        return mList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mList.get(groupPosition).getOfficeperson().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mList.get(groupPosition);
    }


    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mList.get(groupPosition).getOfficeperson().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    //返回 构造的父级视图
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_sup_group, parent, false);
        }

        TextView tv_group_title = ViewHolder.get(convertView, R.id.tv_sup_group_title);
        ImageView img_group_indicate = ViewHolder.get(convertView, R.id.img_sup_group_indicate);

        tv_group_title.setText(mList.get(groupPosition).getOfficename());

        //判断图标剪头的方向
        if (isExpanded) {
            img_group_indicate.setImageResource(R.drawable.expand_down);
        } else {
            img_group_indicate.setImageResource(R.drawable.expand_up);
        }

        return convertView;
    }

    //返回子级视图
    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_sup_child, null);
        }

        RoundImageView rimg_child_image = ViewHolder.get(convertView, R.id.round_head_image_sup);
        TextView tv_child_name = ViewHolder.get(convertView, R.id.tv_sup_child_name);
        TextView tv_child_job = ViewHolder.get(convertView, R.id.tv_sup_child_job);
        TextView tv_child_lack = ViewHolder.get(convertView, R.id.tv_sup_child_lack);

        SupItem item = mList.get(groupPosition).getOfficeperson().get(childPosition);
        LoaderBusiness.loadImage(MYURL.img_HEAD + item.getHeadImg(), rimg_child_image);//设置头像

        tv_child_name.setText(item.getUsername());
        tv_child_job.setText(item.getJob());
        tv_child_lack.setText(item.getLacklog());

        return convertView;
    }

    //Whether the child at the specified position is selectable.
    //设置指定position位置的子项是否可选
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
