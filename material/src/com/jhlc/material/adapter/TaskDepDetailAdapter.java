package com.jhlc.material.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.jhlc.material.R;
import com.jhlc.material.bean.DepTaskDetailProgress;
import com.jhlc.material.utils.ViewHolder;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/9/14 0014.
 */
public class TaskDepDetailAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<DepTaskDetailProgress> mList;
    private boolean isEdit;
    private OnEditListener onEditListener;
    private OnDeleteListener onDeleteListener;

    public interface OnEditListener {
        public void editClick(int position, String id, String context, String time);
    }

    public interface OnDeleteListener {
        public void deleteClick(int position, String id, String context, String time);
    }

    public void setOnItemEditClickListener(OnEditListener listener) {
        this.onEditListener = listener;
    }

    public void setOnItemDeleteClickListener(OnDeleteListener listener) {
        this.onDeleteListener = listener;
    }

    public TaskDepDetailAdapter(Context context, ArrayList<DepTaskDetailProgress> list, boolean isEdit) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        this.mList = list;
        this.isEdit = isEdit;

    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public DepTaskDetailProgress getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_task_dep_detail, null);
        }

        TextView tv_time = ViewHolder.get(convertView, R.id.tv_task_dep_time);
        TextView tv_context = ViewHolder.get(convertView, R.id.tv_task_dep_context);
        Button btn_edit = ViewHolder.get(convertView, R.id.btn_task_dep_edit);
        Button btn_delete = ViewHolder.get(convertView, R.id.btn_task_dep_delete);

        final DepTaskDetailProgress item = mList.get(position);
        if (isEdit) {
            btn_edit.setVisibility(View.VISIBLE);
            btn_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(mContext, "点击 item的编辑", Toast.LENGTH_SHORT).show();
                    onEditListener.editClick(position, item.getId(), item.getEditMessage(), item.getUpdateTime());
                }
            });

            btn_delete.setVisibility(View.VISIBLE);
            btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDeleteListener.deleteClick(position, item.getId(), item.getEditMessage(), item.getUpdateTime());
                }
            });
        }

        tv_time.setText(item.getUpdateTime());
        tv_context.setText(item.getEditMessage());
        return convertView;
    }


}
