package com.jhlc.material.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jhlc.material.R;
import com.jhlc.material.bean.GetCommentBean;
import com.jhlc.material.utils.ViewHolder;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/10/9.
 */
public class NoticeCommentAdapter  extends BaseAdapter{

    private Context mContext;
    private LayoutInflater mInflater;
    private CommentAdapterListener listener;
    private ArrayList<GetCommentBean> commentBeans = new ArrayList<>();

    public NoticeCommentAdapter(Context mContext, ArrayList<GetCommentBean> commentBeans) {
        this.mContext = mContext;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.commentBeans = commentBeans;
    }

    @Override
    public int getCount() {
        return commentBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_notice_comment, null);
        }
        TextView phone = ViewHolder.get(convertView, R.id.tv_item_notice_comment_phone);
        TextView time = ViewHolder.get(convertView, R.id.tv_item_notice_comment_time);
        TextView content = ViewHolder.get(convertView, R.id.tv_item_notice_comment_content);
        TextView commit = ViewHolder.get(convertView, R.id.tv_item_notice_comment_commit);      //提交按钮
        LinearLayout add = ViewHolder.get(convertView, R.id.ll_item_notice_comment_add);    //被评论填充地

        GetCommentBean commentBean = commentBeans.get(position);
        phone.setText(commentBean.getUsername());
        time.setText(commentBean.getTimedes());
        content.setText(commentBean.getCommentcontent());

        commit.setOnClickListener(new View.OnClickListener() {      //点击回复显示回复框
            @Override
            public void onClick(View v) {
                listener.commitOnListener(position);
            }
        });

        return convertView;
    }


    public void setListener(CommentAdapterListener listener) {
        this.listener = listener;
    }

    public interface CommentAdapterListener{
        void commitOnListener(int position);
    }
}
