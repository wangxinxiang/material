package com.jhlc.material.view;


import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.jhlc.material.R;

import java.util.Calendar;

public class CustomDigitalClock  extends LinearLayout {

    Calendar mCalendar;
    private final static String m12 = "h:mm aa";
    private final static String m24 = "k:mm";
    private FormatChangeObserver mFormatChangeObserver;

    private Runnable mTicker;
    private Handler mHandler;
    private long endTime;
    private long currTime;
    private ClockListener mClockListener;

    private boolean mTickerStopped = false;
    private TextView tv_work_state,time_20_day,time_20_hour, time_20_min,time_20_sec;

    @SuppressWarnings("unused")
    private String mFormat;

    public CustomDigitalClock(Context context) {
        super(context);
        initClock(context);
    }

    public CustomDigitalClock(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 使用layoutinflater把布局加载到本ViewGroup
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.digitalclock, this);

        tv_work_state = (TextView) layout.findViewById(R.id.tv_work_state);
        time_20_day   = (TextView) layout.findViewById(R.id.tv_day_count);
        time_20_hour  = (TextView) layout.findViewById(R.id.tv_hour_count);
        time_20_min   = (TextView) layout.findViewById(R.id.tv_minute_count);
        time_20_sec   = (TextView) layout.findViewById(R.id.tv_second_count);

        initClock(context);
    }

    private void initClock(Context context) {

        if (mCalendar == null) {
            mCalendar = Calendar.getInstance();
        }

        mFormatChangeObserver = new FormatChangeObserver();
        getContext().getContentResolver().registerContentObserver(Settings.System.CONTENT_URI, true, mFormatChangeObserver);

        setFormat();
    }

    @Override
    protected void onAttachedToWindow() {
        mTickerStopped = false;
        super.onAttachedToWindow();
        mHandler = new Handler();

        /**
         * requests a tick on the next hard-second boundary
         */
        mTicker = new Runnable() {
            public void run() {
                if (mTickerStopped)
                    return;
                //TODO:这里应该先获得服务器的当前时间，然后再执行runable线程
                long currentTime = System.currentTimeMillis();
//                long currentTime = currTime;

                if (currentTime / 1000 == endTime / 1000 - 5 * 60) {
                    mClockListener.remainFiveMinutes();
                }
                long distanceTime = endTime - currentTime;
                long overtime=0;
                if(distanceTime<0){
                    overtime=Math.abs(distanceTime);
                }
                distanceTime /= 1000;
               // LogZ.d("distanceTime--> ",distanceTime+"");
//                if (distanceTime == 0) {
//                    //setText("00:00:00");
//                   // onDetachedFromWindow();
////                    mClockListener.timeEnd();
//                } else
                if (distanceTime <= 0) {
                    //setText("00:00:00");
                    //setText(dealTime(distanceTime));
                    String[] strarray= dealTime(Math.abs(distanceTime)).split("-");
                    time_20_day .setText(strarray[0]);
                    time_20_hour.setText(strarray[1]);
                    time_20_min .setText(strarray[2]);
                    time_20_sec .setText(strarray[3]);
                    setOnExecuteBG(2);
                } else {
                    //setText(dealTime(distanceTime));
                    String[] strarray= dealTime(Math.abs(distanceTime)).split("-");
                    time_20_day .setText(strarray[0]);
                    time_20_hour.setText(strarray[1]);
                    time_20_min .setText(strarray[2]);
                    time_20_sec .setText(strarray[3]);

                }
                invalidate();
                long now = SystemClock.uptimeMillis();
                long next = now + (1000 - now % 1000);
                mHandler.postAtTime(mTicker, next);
            }
        };
        mTicker.run();
    }

    /**
     * deal time string
     *
     * @param time
     * @return
     */
    public static String dealTime(long time) {
        long day = time / (24 * 60 * 60);
        long hours = (time % (24 * 60 * 60)) / (60 * 60);
        long minutes = ((time % (24 * 60 * 60)) % (60 * 60)) / 60;
        long second = ((time % (24 * 60 * 60)) % (60 * 60)) % 60;
        String dayStr = String.valueOf(day);
        String hoursStr = timeStrFormat(String.valueOf(hours));
        String minutesStr = timeStrFormat(String.valueOf(minutes));
        String secondStr = timeStrFormat(String.valueOf(second));
        return dayStr+"-"+hoursStr+"-"+minutesStr+"-"+secondStr;
    }

    /**
     * format time
     *
     * @param timeStr
     * @return
     */
    private static String timeStrFormat(String timeStr) {
        switch (timeStr.length()) {
            case 1:
                timeStr = "0" + timeStr;
                break;
        }
        return timeStr;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mTickerStopped = true;
    }

    /**
     * Clock end time from now on.
     *
     * @param endTime
     */
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
    public void setCurrTime(long currTime) {
        this.currTime = currTime;
    }


    /**
     * Pulls 12/24 mode from system settings
     */
    private boolean get24HourMode() {
        return android.text.format.DateFormat.is24HourFormat(getContext());
    }

    private void setFormat() {
        if (get24HourMode()) {
            mFormat = m24;
        } else {
            mFormat = m12;
        }
    }

    private class FormatChangeObserver extends ContentObserver {
        public FormatChangeObserver() {
            super(new Handler());
        }

        @Override
        public void onChange(boolean selfChange) {
            setFormat();
        }
    }

    public void setClockListener(ClockListener clockListener) {
        this.mClockListener = clockListener;
    }

    public interface ClockListener{
        void timeEnd();
        void remainFiveMinutes();
    }

    /**
     *  根据状态设置textview背景颜色
     * */

    public void setOnExecuteBG(int state){
        int bgid = R.drawable.time_blue_bg;

        switch (state){
            case 0:
                bgid = R.drawable.time_blue_bg;
                //取消倒计时3个字文字显示
                tv_work_state.setText("");
                break;
            case 1:
                bgid = R.drawable.time_green_bg;
                tv_work_state.setText("提前");
                break;
            case 2:
                bgid = R.drawable.time_red_bg;
                tv_work_state.setText("超时");
                break;
        }
        time_20_day.setBackgroundResource(bgid);
        time_20_hour.setBackgroundResource(bgid);
        time_20_min.setBackgroundResource(bgid);
        time_20_sec.setBackgroundResource(bgid);
    }

    public void statictime(String time){
//        mTickerStopped = true;
//        String[] strarray= time.split("-");
//        time_20_day .setText(strarray[0]);
//        time_20_hour.setText(strarray[1]);
//        time_20_min .setText(strarray[2]);
//        time_20_sec .setText(strarray[3]);
    }

}