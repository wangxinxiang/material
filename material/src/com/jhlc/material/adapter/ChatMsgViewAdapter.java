
package com.jhlc.material.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jhlc.material.DisplayImageActivity;
import com.jhlc.material.R;
import com.jhlc.material.bean.WorkDetailList;
import com.jhlc.material.service.LoaderBusiness;
import com.jhlc.material.utils.*;
import com.jhlc.material.view.RoundImageView;

import java.util.HashMap;
import java.util.LinkedList;

public class ChatMsgViewAdapter extends BaseAdapter {
    HashMap<Integer,View> lmap = new HashMap<Integer,View>();
    public static interface IMsgViewType {
        int IMVT_COM_MSG = 0;
        int IMVT_TO_MSG = 1;
    }

    private static final String TAG = ChatMsgViewAdapterMapTemp.class.getSimpleName();

    private LinkedList<WorkDetailList> workDetailLists = new LinkedList<WorkDetailList>();

    private Context ctx;

    private LayoutInflater mInflater;

    private int UpOrDown;
    private String headimage;
    private String lasttime = "1970-01-01 00:00:00";
    private String currenttime = "";

    public ChatMsgViewAdapter(Context context, LinkedList<WorkDetailList> workDetailLists, int UpOrDown, String headimage) {
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

      final WorkDetailList workDetailList = workDetailLists.get(position);
        String isComMsg = workDetailList.getReporttypeid();
        ViewHolder viewHolder = null;
        if (lmap.get(position)==null) {
            viewHolder = new ViewHolder();
            LayoutInflater mInflater = LayoutInflater.from(ctx);

            if(Constant.ZXL_UP_USER == UpOrDown){
                if (isComMsg.equals("1")) {
                    convertView = mInflater.inflate(R.layout.chatting_item_msg_text_left, null);
                }else if (isComMsg.equals("0")){
                    convertView = mInflater.inflate(R.layout.chatting_item_msg_text_right, null);
                }else if (isComMsg.equals("2")){
                    convertView = mInflater.inflate(R.layout.chatting_item_msg_text_left, null);
                }
            } else if(Constant.ZXL_DOWN_USER == UpOrDown){
                if (isComMsg.equals("1")) {
                    convertView = mInflater.inflate(R.layout.chatting_item_msg_text_right, null);
                }else if (isComMsg.equals("0")){
                    convertView = mInflater.inflate(R.layout.chatting_item_msg_text_left, null);
                }else if (isComMsg.equals("2")){
                    convertView = mInflater.inflate(R.layout.chatting_item_msg_text_left, null);
                }
            }

            viewHolder.iv_my_userhead    = (RoundImageView) convertView.findViewById(R.id.iv_my_userhead);
            viewHolder.iv_other_userhead = (RoundImageView) convertView.findViewById(R.id.iv_other_userhead);
            viewHolder.tvSendTime        = (TextView) convertView.findViewById(R.id.tv_sendtime);
            viewHolder.tvUserName        = (TextView) convertView.findViewById(R.id.tv_username);
            viewHolder.tvContent         = (TextView) convertView.findViewById(R.id.tv_chatcontent);
            viewHolder.ivContent         = (ImageView) convertView.findViewById(R.id.iv_chatcontent);
            viewHolder.isComMsg          = isComMsg;

            if(isComMsg.equals("2")){//单出现系统消息时候要特别处理
                viewHolder.iv_other_userhead.setVisibility(View.GONE);
                convertView.findViewById(R.id.tv_bg).setVisibility(View.GONE);
                TextView systemMsg=(TextView)convertView.findViewById(R.id.tv_lastreply_content_system);
                systemMsg.setVisibility(View.VISIBLE);
                systemMsg.setText(workDetailList.getReportmessage());

            }

            lmap.put(position, convertView);
            convertView.setTag(viewHolder);
        }else{
            convertView = lmap.get(position);
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(null != viewHolder.iv_other_userhead) {
            LoaderBusiness.loadImage(MYURL.img_HEAD + headimage, viewHolder.iv_other_userhead);
        }
        if(null != viewHolder.iv_my_userhead) {
            LoaderBusiness.loadImage(MYURL.img_HEAD + PreferenceUtils.getInstance().getHeadImage(),viewHolder.iv_my_userhead);
        }
        String reporttime = workDetailList.getReporttime().trim();
        Log.e("ChatMsgViewAdapter", "--" + reporttime);
        //1127修改，取消掉判断
       // if(null != reporttime && TimeUtil.getLongDiff(reporttime.replace("/", "-"),lasttime.replace("/","-")) > 3*60*1000) {
            viewHolder.tvSendTime.setVisibility(View.VISIBLE);
        /*    viewHolder.tvSendTime.setText(TimeUtil.getChatTime(TimeUtil.getMilliSecond(reporttime.replace("/", "-")),
                    TimeUtil.getMilliSecond(currenttime.replace("/","-"))));*/
        viewHolder.tvSendTime.setText(TimeUtil.getSimpleChatTime(reporttime));
            lasttime = reporttime;
     //   }

        viewHolder.tvUserName.setText("");
        String content = workDetailList.getReportmessage();
        LogZ.d("headimagcontent--> ",content);

        //如果有略缩图，就用ivContent 显示
        if(StringUtils.isBlank(workDetailList.getSmallimage())){
            viewHolder.ivContent.setVisibility(View.INVISIBLE);
            if(null != Constant.getExprImage(content)){
                viewHolder.tvContent.setText("");
                viewHolder.tvContent.setBackgroundResource(Constant.getExprImage(content));
            } else {
                viewHolder.tvContent.setText(content);
                viewHolder.tvContent.setBackgroundDrawable(null);
            }
        }else{
            viewHolder.ivContent.setVisibility(View.VISIBLE);
            LoaderBusiness.loadPhotoImage(MYURL.img_HEAD +workDetailList.getSmallimage() ,viewHolder.ivContent);
        }

        viewHolder.ivContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  ZXLApplication.getInstance().showTextToast("onclick!");
                Intent intent=new Intent(ctx,DisplayImageActivity.class);
                intent.putExtra("bigurl",workDetailList.getBigimage());
                intent.putExtra("imgkey",TimeUtil.getFileKeyByReportTime(workDetailList.getReporttime()));
                //ZXLApplication.getInstance().showTextToast("传递过去的参数："+workDetailList.getBigimage());
                ctx.startActivity(intent);
            }
        });

        return convertView;
    }

    static class ViewHolder {
        public RoundImageView iv_other_userhead,iv_my_userhead;
        public TextView tvSendTime;
        public TextView tvUserName;
        public TextView tvContent;
        public ImageView ivContent;
        public String  isComMsg = "";
    }


}
