package com.jhlc.material.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jhlc.material.R;
import com.jhlc.material.bean.Executelist;
import com.jhlc.material.db.SetNewMsgDB;
import com.jhlc.material.service.LoaderBusiness;
import com.jhlc.material.utils.*;
import com.jhlc.material.view.CustomDigitalClock;

import java.util.ArrayList;

public class WorkDetailAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private Context mContent;
    private String  imageurl;
    private BaseAdapter ba;
    private int UpOrDown = -1;
    private ArrayList<Executelist> showExecutelists = new ArrayList<Executelist>();
    private SetNewMsgDB newMsgDB;

    public WorkDetailAdapter(ArrayList<Executelist> showExecutelists, Context content,int UpOrDown) {
        ba = this;
        this.UpOrDown   = UpOrDown;
        this.showExecutelists = showExecutelists;
        this.mContent   = content;
        newMsgDB        = new SetNewMsgDB(content);
        inflater        = (LayoutInflater) content.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return showExecutelists.size();
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
        HolderView holderView ;
        if(convertView == null){
            holderView = new HolderView();
            if(Constant.ZXL_UP_USER == UpOrDown){
                convertView = inflater.inflate(R.layout.up_user_items, null);
            } else if(Constant.ZXL_DOWN_USER == UpOrDown){
                convertView = inflater.inflate(R.layout.down_user_items, null);
            }
            holderView.rl_lastreply_content  = (RelativeLayout) convertView.findViewById(R.id.rl_lastreply_content);
            holderView.rl_lastreply_content_other  = (RelativeLayout) convertView.findViewById(R.id.rl_lastreply_content_other);
            holderView.rl_lastreply_content_system  = (RelativeLayout) convertView.findViewById(R.id.rl_lastreply_content_system);

            holderView.rl_static_time  = (RelativeLayout) convertView.findViewById(R.id.rl_static_time);
            holderView.tv_work_state_s = (TextView) convertView.findViewById(R.id.tv_work_state_s);
            holderView.tv_day_count    = (TextView) convertView.findViewById(R.id.tv_day_count_s);
            holderView.tv_hour_count   = (TextView) convertView.findViewById(R.id.tv_hour_count_s);
            holderView.tv_minute_count = (TextView) convertView.findViewById(R.id.tv_minute_count_s);
            holderView.tv_second_count = (TextView) convertView.findViewById(R.id.tv_second_count_s);

            holderView.customDigitalClock    = (CustomDigitalClock) convertView.findViewById(R.id.custom_digital_clock);

            holderView.tv_task               = (TextView) convertView.findViewById(R.id.tv_my_task);
            holderView.tv_result             = (TextView) convertView.findViewById(R.id.tv_lastreply_content);
            holderView.tv_lastreply_content_other       = (TextView) convertView.findViewById(R.id.tv_lastreply_content_other);
            holderView.tv_report_time        = (TextView) convertView.findViewById(R.id.tv_lastreply_time);
            holderView.tv_sendtask_time      = (TextView) convertView.findViewById(R.id.tv_sendtask_time);
            holderView.tv_limittime_complete = (TextView) convertView.findViewById(R.id.tv_limittime_complete);

            holderView.iv_other_userhead     = (ImageView) convertView.findViewById(R.id.iv_other_userhead);
            holderView.iv_my_userhead        = (ImageView) convertView.findViewById(R.id.iv_my_userhead);
            holderView.iv_my_userhead2       = (ImageView) convertView.findViewById(R.id.iv_my_userhead2);
            holderView.iv_my_userhead2_other = (ImageView) convertView.findViewById(R.id.iv_my_userhead2_other);

            holderView.img_new_msg1           = (ImageView) convertView.findViewById(R.id.img_new_msg1);
            holderView.img_new_msg2           = (ImageView) convertView.findViewById(R.id.img_new_msg2);
            holderView.img_new_msg3           = (ImageView) convertView.findViewById(R.id.img_new_msg3);

            convertView.setTag(holderView);
        } else {
            holderView = (HolderView) convertView.getTag();
        }

        if(Constant.ZXL_UP_USER == UpOrDown){
            LoaderBusiness.loadImage(MYURL.img_HEAD + PreferenceUtils.getInstance().getHeadImage(),holderView.iv_my_userhead2);
            LoaderBusiness.loadImage(MYURL.img_HEAD + PreferenceUtils.getInstance().getHeadImage(),holderView.iv_my_userhead);
            LoaderBusiness.loadImage(MYURL.img_HEAD + imageurl, holderView.iv_other_userhead);
            LoaderBusiness.loadImage(MYURL.img_HEAD + imageurl, holderView.iv_my_userhead2_other);

        } else if(Constant.ZXL_DOWN_USER == UpOrDown){
            LoaderBusiness.loadImage(MYURL.img_HEAD + imageurl,holderView.iv_my_userhead2);
            LoaderBusiness.loadImage(MYURL.img_HEAD + imageurl,holderView.iv_my_userhead);
            LoaderBusiness.loadImage(MYURL.img_HEAD + PreferenceUtils.getInstance().getHeadImage(), holderView.iv_other_userhead);
            LoaderBusiness.loadImage(MYURL.img_HEAD + PreferenceUtils.getInstance().getHeadImage(), holderView.iv_my_userhead2_other);

        }

        holderView.tv_task.setText(showExecutelists.get(position).getMessage());

        // 判断是否有最后的回复 如果有这 显示相应的ＶＩＥＷ
        String reportmessage = showExecutelists.get(position).getReportmessage();
        // 没有最后的回复
        if("".equals(reportmessage)){
            holderView.rl_lastreply_content.setVisibility(View.GONE);
            holderView.rl_lastreply_content_other.setVisibility(View.GONE);
            holderView.tv_report_time.setVisibility(View.GONE);
            holderView.rl_lastreply_content_system.setVisibility(View.GONE);
            //根据 workid 判断是否这条 任务是新消息
            if(newMsgDB.getByWorkID(showExecutelists.get(position).getWorkid())){
                holderView.img_new_msg1.setVisibility(View.VISIBLE);
                holderView.img_new_msg2.setVisibility(View.GONE);
                holderView.img_new_msg3.setVisibility(View.GONE);
            } else {
                holderView.img_new_msg1.setVisibility(View.GONE);
                holderView.img_new_msg2.setVisibility(View.GONE);
                holderView.img_new_msg3.setVisibility(View.GONE);
            }
        } else {
            holderView.tv_report_time.setVisibility(View.VISIBLE);
            holderView.rl_lastreply_content_system.setVisibility(View.GONE);
            //根据 workid 判断是否这条 任务是新消息
            if(newMsgDB.getByWorkID(showExecutelists.get(position).getWorkid())){
                if(Constant.ZXL_UP_USER == UpOrDown){
                    if ("任务已完成".equals(reportmessage) || reportmessage.contains("今天未汇报工作进度")) {        //任务完成的红点显示
                        holderView.img_new_msg1.setVisibility(View.VISIBLE);
                        holderView.img_new_msg2.setVisibility(View.GONE);
                        holderView.img_new_msg3.setVisibility(View.GONE);
                        newMsgDB.deleteByWorkID(showExecutelists.get(position).getWorkid());
                    } else {
                        holderView.img_new_msg1.setVisibility(View.GONE);
                        holderView.img_new_msg2.setVisibility(View.GONE);
                        holderView.img_new_msg3.setVisibility(View.VISIBLE);
                    }
                } else if(Constant.ZXL_DOWN_USER == UpOrDown){
                    holderView.img_new_msg1.setVisibility(View.GONE);
                    holderView.img_new_msg2.setVisibility(View.VISIBLE);
                    holderView.img_new_msg3.setVisibility(View.GONE);
                }
            } else {
                holderView.img_new_msg1.setVisibility(View.GONE);
                holderView.img_new_msg2.setVisibility(View.GONE);
                holderView.img_new_msg3.setVisibility(View.GONE);
            }

            // 判断 最后回复的是 本人还是另外一个人
            if(Constant.ZXL_UP_USER == UpOrDown){
                if(showExecutelists.get(position).getReporttypeid().equals("1")){
                    holderView.rl_lastreply_content.setVisibility(View.GONE);
                    holderView.rl_lastreply_content_other.setVisibility(View.VISIBLE);
                    selectViewWhenUp(holderView, reportmessage);
                }else if(showExecutelists.get(position).getReporttypeid().equals("0")){
                    holderView.rl_lastreply_content.setVisibility(View.VISIBLE);
                    holderView.rl_lastreply_content_other.setVisibility(View.GONE);
                   // Log.e("Constant.getExprImage(content)",reportmessage+" - "+Constant.getExprImage(reportmessage));
                    // 处理文字 和 表情的显示
                    selectViewWhenDown(holderView, reportmessage);
                }else if (showExecutelists.get(position).getReporttypeid().equals("2")){
                    holderView.rl_lastreply_content.setVisibility(View.GONE);
                    holderView.rl_lastreply_content_other.setVisibility(View.GONE);
                    holderView.rl_lastreply_content_system.setVisibility(View.VISIBLE);
                    ((TextView)convertView.findViewById(R.id.tv_lastreply_content_system)).setText(reportmessage);
                }
            } else if(Constant.ZXL_DOWN_USER == UpOrDown){
                if(showExecutelists.get(position).getReporttypeid().equals("1")){
                    holderView.rl_lastreply_content.setVisibility(View.GONE);
                    holderView.rl_lastreply_content_other.setVisibility(View.VISIBLE);
                    selectViewWhenUp(holderView, reportmessage);
                }else if(showExecutelists.get(position).getReporttypeid().equals("0")){
                    holderView.rl_lastreply_content_other.setVisibility(View.GONE);
                    holderView.rl_lastreply_content.setVisibility(View.VISIBLE);
                   // Log.e("Constant.getExprImage(content)",reportmessage+" - "+Constant.getExprImage(reportmessage));
                    selectViewWhenDown(holderView, reportmessage);
                }else if (showExecutelists.get(position).getReporttypeid().equals("2")){
                    holderView.rl_lastreply_content.setVisibility(View.GONE);
                    holderView.rl_lastreply_content_other.setVisibility(View.GONE);
                    holderView.rl_lastreply_content_system.setVisibility(View.VISIBLE);
                    ((TextView)convertView.findViewById(R.id.tv_lastreply_content_system)).setText(reportmessage);
                }
            }

           /* holderView.tv_report_time.setText(TimeUtil.getInterval(showExecutelists.get(position).getReporttime().replace("/", "-"),
                    showExecutelists.get(position).getCurrenttime().replace("/", "-")));*/

            holderView.tv_report_time.setText(TimeUtil.getSimpleChatTime(showExecutelists.get(position).getReporttime()));
        }

//        holderView.tv_sendtask_time.setText(TimeUtil.getSendTime(showExecutelists.get(position).getStarttime().replace("/", "-")));
        //修改任务发起时间，统一显示格式
        holderView.tv_sendtask_time.setText(TimeUtil.getSimpleChatTime(showExecutelists.get(position).getStarttime()));
        String enddays = showExecutelists.get(position).getEnddays();
        try {
            holderView.tv_limittime_complete.setText((int)(Float.parseFloat(enddays))+"天内完成");
        } catch (NumberFormatException e) {
            //
        }

        /**
         * 设置倒计时
         */
        setCountdown(position, holderView);

        return convertView;
    }

    private void setCountdown(int position, HolderView holderView) {
        String statu = showExecutelists.get(position).getStatu();
        String applyComplete=showExecutelists.get(position).getApplycomplete();

        String currenttime = showExecutelists.get(position).getCurrenttime().replace("/","-");
        String endtime = showExecutelists.get(position).getEndtime().replace("/","-");
        String starttime = showExecutelists.get(position).getStarttime().replace("/","-");
        String completetime = showExecutelists.get(position).getCompletetime().replace("/","-");
        String applytime = showExecutelists.get(position).getApplytime().replace("/","-");
        long locationtime = System.currentTimeMillis();

        /**
         * "onexecutelist": [
         {
         "workid": "7e6ae6ed-3ef4-4f44-b15c-fa221628dd9c",
         "message": "\u5341\u516b\u7f57\u6c49\u8bbe\u8ba1",
         "starttime": "2014/12/11 15:15:48",
         "endtime": "2014/12/12 15:15:48",
         "enddays": "1.00",
         "completetime": "",
         "statu": "0",
         "reportmessage": "\u7533\u8bf7\u5b8c\u6210\u4efb\u52a1",
         "reporttime": "2014/12/11 15:25:23",
         "currenttime": "2014/12/11 15:46:41",
         "reporttypeid": "0",
         "applycomplete": "1"
         },
         */
        if("1".equals(applyComplete)){//如果是申请完成任务状态，倒计时要暂停,用reporttime和完成时间做差值
            holderView.customDigitalClock.setVisibility(View.GONE);
            holderView.rl_static_time.setVisibility(View.VISIBLE);
            String difftime[] = TimeUtil.getDataDiff(applytime,endtime).split("-");
            if (TimeUtil.compareTime(applytime,endtime)) {
                setTimeStyle(holderView, difftime, 3);
            }else{
                setTimeStyle(holderView, difftime, 2);
            }
        }else {

            if ("0".equals(statu) &&  //处理 计时显示情况
                    TimeUtil.getMilliSecond(currenttime, endtime) > 0)  {  //规定时间内正在进行中 显示倒计时
                holderView.customDigitalClock.setVisibility(View.VISIBLE);
                holderView.rl_static_time.setVisibility(View.GONE);
                holderView.customDigitalClock.setEndTime(locationtime + TimeUtil.getLongDiff(endtime, currenttime));
                holderView.customDigitalClock.setOnExecuteBG(0);
                LogZ.d("time--> ", endtime + "  " + currenttime);
            } else if ("2".equals(statu)) {   //在规定时间内已经完成 显示提前多长时间完成
                holderView.customDigitalClock.setVisibility(View.GONE);
                holderView.rl_static_time.setVisibility(View.VISIBLE);
                String difftime[] = TimeUtil.getDataDiff(completetime, endtime).split("-");
                setTimeStyle(holderView, difftime, 2);
            } else if ("3".equals(statu)) {   //超过规定时间完成 显示超过多长时间才完成
                holderView.customDigitalClock.setVisibility(View.GONE);
                holderView.rl_static_time.setVisibility(View.VISIBLE);
                String difftime[] = TimeUtil.getDataDiff(completetime, endtime).split("-");
//          String difftime[] = TimeUtil.getDataDiff("2014-10-22 22:25:00","2014-10-23 23:34:00").split("-");
                System.out.println(difftime[0] + "----" + difftime[1] + "-" + difftime[2] + "-" + difftime[3]);
                setTimeStyle(holderView, difftime, 3);
            } else {  //超过规定时间 未完成
                holderView.customDigitalClock.setVisibility(View.VISIBLE);
                holderView.rl_static_time.setVisibility(View.GONE);
                holderView.customDigitalClock.setEndTime(locationtime + TimeUtil.getLongDiff(endtime, currenttime));
                holderView.customDigitalClock.setOnExecuteBG(2);
            }
        }
    }

    private void selectViewWhenDown(HolderView holderView, String reportmessage) {
        if(null != Constant.getExprImage(reportmessage)){
            holderView.tv_result.setText("");
            holderView.tv_result.setBackgroundResource(Constant.getExprImage(reportmessage));
        } else {
            holderView.tv_result.setText(reportmessage);
            holderView.tv_result.setBackgroundDrawable(null);
        }
    }

    private void selectViewWhenUp(HolderView holderView, String reportmessage) {
        // 处理文字 和 表情的显示
        if(null != Constant.getExprImage(reportmessage)){
            holderView.tv_lastreply_content_other.setText("");
            holderView.tv_lastreply_content_other.setBackgroundResource(Constant.getExprImage(reportmessage));
        } else {
            holderView.tv_lastreply_content_other.setText(reportmessage);
            holderView.tv_lastreply_content_other.setBackgroundDrawable(null);
        }
    }

    public void setUserHead(String workuserhead) {
        this.imageurl   = workuserhead;
    }

    class HolderView{
        CustomDigitalClock customDigitalClock;
        RelativeLayout rl_lastreply_content,rl_lastreply_content_other,rl_lastreply_content_system;
        RelativeLayout rl_static_time;
        TextView tv_task,tv_result,tv_limittime_complete,tv_lastreply_content_other;
        TextView tv_report_time,tv_sendtask_time;
        ImageView iv_other_userhead,iv_my_userhead,iv_my_userhead2,iv_my_userhead2_other;
        TextView tv_work_state_s,tv_day_count,tv_hour_count,tv_minute_count,tv_second_count;
        ImageView img_new_msg1,img_new_msg2,img_new_msg3;
    }

    public void setTimeStyle(HolderView holderView, String difftime[], int state){

        holderView.tv_day_count.setText(difftime[0]);
        holderView.tv_hour_count.setText(difftime[1]);
        holderView.tv_minute_count.setText(difftime[2]);
        holderView.tv_second_count.setText(difftime[3]);

        int bgid = R.drawable.time_blue_bg;

        switch (state){
            case 0:
                bgid = R.drawable.time_blue_bg;
                //取消倒计时显示
                holderView.tv_work_state_s.setText("");
                break;
            case 2:
                bgid = R.drawable.time_green_bg;
                holderView.tv_work_state_s.setText("提前");
                break;
            case 3:
                bgid = R.drawable.time_red_bg;
                holderView.tv_work_state_s.setText("超时");
                break;
        }
        holderView.tv_day_count   .setBackgroundResource(bgid);
        holderView.tv_hour_count  .setBackgroundResource(bgid);
        holderView.tv_minute_count.setBackgroundResource(bgid);
        holderView.tv_second_count.setBackgroundResource(bgid);
    }


}
