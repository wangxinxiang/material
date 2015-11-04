
package com.jhlc.material.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jhlc.material.R;
import com.jhlc.material.bean.WorkDetailList;
import com.jhlc.material.service.LoaderBusiness;
import com.jhlc.material.utils.*;
import com.jhlc.material.view.RoundImageView;

import java.util.LinkedList;

public class ChatMsgViewAdapterProblem extends BaseAdapter {
	public static interface IMsgViewType {
		int IMVT_COM_MSG = 0;
		int IMVT_TO_MSG = 1;
	}

    private static final String TAG = ChatMsgViewAdapterProblem.class.getSimpleName();

    private LinkedList<WorkDetailList> workDetailLists = new LinkedList<WorkDetailList>();

    private Context ctx;

    private LayoutInflater mInflater;

    private int UpOrDown;
    private String headimage;
    private String lasttime = "1970-01-01 00:00:00";
    private String currenttime = "";

    public ChatMsgViewAdapterProblem(Context context, LinkedList<WorkDetailList> workDetailLists, int UpOrDown, String headimage) {
        ctx = context;
        this.headimage = headimage;
        this.workDetailLists = workDetailLists;
        this.UpOrDown = UpOrDown;
    }

    public int getCount() {
        return workDetailLists.size();
    }

    public Object getItem(int position) {
        return workDetailLists.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public void setCurrenttime(String currenttime) {
        this.currenttime = currenttime;
    }

    public int getViewTypeCount() {

		return 2;
	}


    public int getItemViewType(int position) {
        // TODO Auto-generated method stub

        if (workDetailLists.get(position).getReporttypeid().equals("0")) {
            return IMsgViewType.IMVT_COM_MSG;
        }else{
            return IMsgViewType.IMVT_TO_MSG;
        }

    }


    public View getView(int position, View convertView, ViewGroup parent) {

    	String isComMsg = workDetailLists.get(position).getReporttypeid();
    	ViewHolder viewHolder = null;
	    if (convertView==null) {
            viewHolder = new ViewHolder();
            LayoutInflater mInflater = LayoutInflater.from(ctx);

            if(Constant.ZXL_UP_USER == UpOrDown){
                if (isComMsg.equals("1")) {
                    convertView = mInflater.inflate(R.layout.chatting_item_msg_text_left, null);
                }else if (isComMsg.equals("0")){
                    convertView = mInflater.inflate(R.layout.chatting_item_msg_text_right, null);
                }
            } else if(Constant.ZXL_DOWN_USER == UpOrDown){
                if (isComMsg.equals("1")) {
                    convertView = mInflater.inflate(R.layout.chatting_item_msg_text_right, null);
                }else if (isComMsg.equals("0")){
                    convertView = mInflater.inflate(R.layout.chatting_item_msg_text_left, null);
                }
            }

            viewHolder.iv_my_userhead    = (RoundImageView) convertView.findViewById(R.id.iv_my_userhead);
            viewHolder.iv_other_userhead = (RoundImageView) convertView.findViewById(R.id.iv_other_userhead);
			viewHolder.tvSendTime        = (TextView) convertView.findViewById(R.id.tv_sendtime);
			viewHolder.tvUserName        = (TextView) convertView.findViewById(R.id.tv_username);
			viewHolder.tvContent         = (TextView) convertView.findViewById(R.id.tv_chatcontent);
			viewHolder.isComMsg          = isComMsg;
            convertView.setTag(viewHolder);
	    }else{
	        viewHolder = (ViewHolder) convertView.getTag();
	    }

        if(null != viewHolder.iv_other_userhead) {
            LoaderBusiness.loadImage(MYURL.URL_HEAD + headimage, viewHolder.iv_other_userhead);
        }
        if(null != viewHolder.iv_my_userhead) {
            LoaderBusiness.loadImage(MYURL.URL_HEAD + PreferenceUtils.getInstance().getHeadImage(),viewHolder.iv_my_userhead);
        }
        String reporttime = workDetailLists.get(position).getReporttime().trim();
        Log.e("ChatMsgViewAdapter","--"+reporttime);
        if(null != reporttime && TimeUtil.getLongDiff(reporttime.replace("/", "-"),lasttime.replace("/","-")) > 3*60*1000) {
            viewHolder.tvSendTime.setVisibility(View.VISIBLE);
            viewHolder.tvSendTime.setText(TimeUtil.getChatTime(TimeUtil.getMilliSecond(reporttime.replace("/", "-")),
                    TimeUtil.getMilliSecond(currenttime.replace("/","-"))));
            lasttime = reporttime;
        }

	    viewHolder.tvUserName.setText("");
        String content = workDetailLists.get(position).getReportmessage();
        LogZ.d("headimagcontent--> ", content);

        if(null != Constant.getExprImage(content)){
            viewHolder.tvContent.setText("");
            viewHolder.tvContent.setBackgroundResource(Constant.getExprImage(content));
        } else {
            viewHolder.tvContent.setText(content);
            viewHolder.tvContent.setBackground(null);
        }
	    return convertView;
    }

    static class ViewHolder {
        public RoundImageView iv_other_userhead,iv_my_userhead;
        public TextView tvSendTime;
        public TextView tvUserName;
        public TextView tvContent;
        public String  isComMsg = "";
    }


}
