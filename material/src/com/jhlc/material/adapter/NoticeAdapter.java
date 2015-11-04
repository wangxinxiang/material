package com.jhlc.material.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jhlc.material.R;
import com.jhlc.material.bean.NoticeListBean;
import com.jhlc.material.utils.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/10/9.
 */
public class NoticeAdapter extends BaseAdapter{

    private Context mContext;
    private LayoutInflater mInflater;
    private List<NoticeListBean> noticeLists = new ArrayList<>();

    public NoticeAdapter(Context mContext, List<NoticeListBean> noticeLists) {
        this.mContext = mContext;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.noticeLists = noticeLists;
    }

    @Override
    public int getCount() {
        return noticeLists.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = mInflater.inflate(R.layout.item_notice, null);
        }
        NoticeListBean noticeListBean = noticeLists.get(i);
        TextView title = ViewHolder.get(view, R.id.tv_item_notice_title);
        TextView time = ViewHolder.get(view, R.id.tv_item_notice_time);

        String textTime = noticeListBean.getAddtime().split(" ")[0];
        time.setText(textTime);
        title.setText(noticeListBean.getTitle());
        return view;
    }
}
