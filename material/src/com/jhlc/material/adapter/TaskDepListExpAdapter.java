package com.jhlc.material.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.jhlc.material.R;
import com.jhlc.material.bean.DepTaskItem;
import com.jhlc.material.bean.DepTaskListByClass;
import com.jhlc.material.utils.ViewHolder;


import java.util.ArrayList;


public class TaskDepListExpAdapter extends BaseExpandableListAdapter {
    private static String TAG = "TaskDepListExpAdapter";
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<DepTaskListByClass> list;
    private boolean isAdd = false;//标志位 判断是否显示 添加任务按钮
    private OnAddListener listener;

    public interface OnAddListener {
        public void addClick(int position,String wrokTag);
    }

    public void setOnChildAddListener(OnAddListener listener) {
        this.listener = listener;
    }

    //构造方法传入 context上下文 和list对象 是否可添加任务的布尔值
    public TaskDepListExpAdapter(Context context, ArrayList<DepTaskListByClass> list, boolean isAdd) {
        this.list = list;
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        this.isAdd = isAdd;
        Log.d(TAG, TAG);
    }
    
    
    @Override
    public int getGroupCount() {
        return list.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
//        Log.d(TAG,"groupPosition="+groupPosition+" size="+list.get(groupPosition).getWorkmessage().size())   ;
        return list.get(groupPosition).getWorkmessage().size();

    }

    @Override
    public DepTaskListByClass getGroup(int groupPosition) {
        return list.get(groupPosition);
    }


    @Override
    public DepTaskItem getChild(int groupPosition, int childPosition) {
        return list.get(groupPosition).getWorkmessage().get(childPosition);
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
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_task_dep_group, parent, false);
        } 

        TextView tv_group_title=ViewHolder.get(convertView,R.id.tv_group_title);
        ImageButton ibtn_group_add=ViewHolder.get(convertView,R.id.ibtn_group_add);

        tv_group_title.setText(getClassByTag(list.get(groupPosition).getWorktag()));

        if (isAdd) {
            //如果可编辑 显示按钮 并且绑定点击事件 实现跳转
            ibtn_group_add.setVisibility(View.VISIBLE);
            ibtn_group_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    listener.addClick(groupPosition, getClassByTag(list.get(groupPosition).getWorktag()));
                }
            });
        }


//     //判断图标剪头的方向
//        if (isExpanded) {
//            groupHolder.imageView.setImageResource(R.drawable.expand_down);
//        } else {
//            groupHolder.imageView.setImageResource(R.drawable.expand_up);
//        }


        return convertView;
    }

    private String getClassByTag(String worktag) {
        if (worktag.equals("1")){
            return "年度任务";
        }else if (worktag.equals("2")){
            return "季度任务";
        }else if(worktag.equals("3")){
            return "月度任务";
        }else if (worktag.equals("4")){
            return "专项任务";
        }
        return "";
    }

    //返回子级视图
    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_task_dep_child, null);
        } 

        TextView tv_child_name= ViewHolder.get(convertView,R.id.tv_child_name);
        TextView tv_child_state=ViewHolder.get(convertView,R.id.tv_child_state);
        
        DepTaskItem item = list.get(groupPosition).getWorkmessage().get(childPosition);
        tv_child_name.setText(item.getMessage());
        if (item.getIsComplate().equals("1")) {
            //1 完成状态 字体蓝色
            tv_child_state.setText("已完成");
            tv_child_state.setTextColor(mContext.getResources().getColor(R.color.blue_light));

        } else  if (item.getIsComplate().equals("0")){
            //0 未完成 灰色字体
            tv_child_state.setText("未完成");
            tv_child_state.setTextColor(mContext.getResources().getColor(R.color.tv_gray));
        } else {
            //2 数据为空
            tv_child_state.setText(item.getIsComplate());
            tv_child_state.setTextColor(mContext.getResources().getColor(R.color.tv_gray));
        }
//        Log.d(TAG, "getChildVeiw=" + convertView.toString());
        return convertView;
    }

    //Whether the child at the specified position is selectable.
    //设置指定position位置的子项是否可选
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
