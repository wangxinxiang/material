package com.jhlc.material;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.text.format.Time;
import android.view.View;
import android.widget.TextView;

import com.jhlc.material.utils.LogZ;
import com.jhlc.material.view.wheelview.DateWheel;


/**
 * Created by 104468 on 2015/3/13.
 */
public class DatePickerActivity extends Activity{
    public final static int RESULT=1001;
    private DateWheel yaer_wheel;
    private DateWheel month_wheel;
    private DateWheel day_wheel;


    private Time time;
    private int year;
    private int month;
    private int day;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date_picker);
        initView();
        //获得系统时间
        systemTime();
        //年数据
        yaerData();
        //月数据
        monthData();
        //日数据
        dayData();

    }
    private void initView(){
        yaer_wheel = (DateWheel) findViewById(R.id.yaer_wheel);
        month_wheel = (DateWheel) findViewById(R.id.month_wheel);
        day_wheel = (DateWheel) findViewById(R.id.day_wheel);

        TextView time_submit = (TextView) findViewById(R.id.time_submit);
        time_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                intent.putExtra("date",getDate());
                setResult(RESULT,intent);
                finish();
            }
        });

        TextView time_cancle = (TextView) findViewById(R.id.time_cancle);
        time_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void systemTime(){
        time = new Time();
        time.setToNow();
        String date = getIntent().getStringExtra("date");
        LogZ.d("date","传过来的时间:"+date);
        if(date != null && !"".equals(date)){
            String s[] = date.split("/");
            year = Integer.parseInt(s[0]);
            month = Integer.parseInt(s[1]);
            day = Integer.parseInt(s[2]);
        }else{
            year = time.year;
            month = time.month + 1;
            day = time.monthDay;
        }




    }
    private void yaerData(){
        List<String> yaerData = new ArrayList<String>();
        yaerData.add(""+(year-1));
        yaerData.add(""+year);
        yaerData.add(""+(year+1));
        yaer_wheel.setData(yaerData,1);
        yaer_wheel.setOnSelectListener(new DateWheel.OnSelectListener()
        {

            @Override
            public void onSelect(String text)
            {
                year = Integer.parseInt(text);
                setDayByYearAndMonth(year,month);

            }
            @Override
            public void onShowHeadSelect(List<String> list) {
                list.remove(list.size() - 1);
                list.add(0,""+(Integer.parseInt(list.get(0)) - 1));
            }

            @Override
            public void onShowTailSelect(List<String> list) {
                list.remove(0);
                list.add(""+(Integer.parseInt(list.get(list.size() - 1)) + 1));
                /*//判断滚动年份是否大于今年,大于今年禁止滚动
                int y = Integer.parseInt(list.get(list.size()/2));
                if(y < time.year){

                }*/
            }


        });
    }
    private void monthData(){
        List<String> monthData = new ArrayList<String>();
        for (int i = 1; i <= 12; i++){
            if(i < 10){
                monthData.add("0"+i);
            }else{
                monthData.add(""+i);
            }
        }
        month_wheel.setData(monthData,month - 1);
        month_wheel.setOnSelectListener(new DateWheel.OnSelectListener()
        {

            @Override
            public void onSelect(String text)
            {
                month = Integer.parseInt(text);
                setDayByYearAndMonth(year,month);
            }

            @Override
            public void onShowHeadSelect(List<String> list) {
                /*//判断年份是否是今年
                if(year == time.year){
                    //判断滚动上一个月是否比当前月大，大于当前月禁止滚动
                    int m = Integer.parseInt(list.get(list.size()/2 - 1));
                    if(m < (time.month + 1)){
                        String tail = list.get(list.size() - 1);
                        list.remove(list.size() - 1);
                        list.add(0, tail);
                    }
                }else{
                    String tail = list.get(list.size() - 1);
                    list.remove(list.size() - 1);
                    list.add(0, tail);
                }*/
            }

            @Override
            public void onShowTailSelect(List<String> list) {
                /*//判断年份是否是今年
                if(year == time.year){
                    //判断滚动月份是否大于本月,大于本月禁止滚动
                    int m = Integer.parseInt(list.get(list.size()/2));
                    if(m < (time.month + 1)){
                        String head = list.get(0);
                        list.remove(0);
                        list.add(head);
                    }
                }else{
                    String head = list.get(0);
                    list.remove(0);
                    list.add(head);
                }*/
            }

        });
    }
    private void dayData(){
        List<String> dayData = new ArrayList<String>();
        for (int i = 1; i <= getDayByYearAndMonth(year,month); i++){
            if(i < 10){
                dayData.add("0"+i);
            }else{
                dayData.add(""+i);
            }
        }

        day_wheel.setData(dayData,day - 1);
        day_wheel.setOnSelectListener(new DateWheel.OnSelectListener()
        {

            @Override
            public void onSelect(String text)
            {
                day = Integer.parseInt(text);
            }

            @Override
            public void onShowHeadSelect(List<String> list) {
                /*//判断年份是否是今年
                if(year == time.year){
                    //判断年份是否是本月
                    if(month == (time.month + 1)) {
                        //判断滚动前一天是否比本日大，大于本日禁止滚动
                        int d = Integer.parseInt(list.get(list.size()/2 - 1));
                        if(d < time.monthDay){
                            String tail = list.get(list.size() - 1);
                            list.remove(list.size() - 1);
                            list.add(0, tail);
                        }
                    }else{
                        String tail = list.get(list.size() - 1);
                        list.remove(list.size() - 1);
                        list.add(0, tail);
                    }
                }else{
                    String tail = list.get(list.size() - 1);
                    list.remove(list.size() - 1);
                    list.add(0, tail);
                }*/

            }

            @Override
            public void onShowTailSelect(List<String> list) {
                /*//判断年份是否是今年
                if(year == time.year){
                    //判断年份是否是本月
                    if(month == (time.month + 1)) {
                        //判断滚动日期是否大于本日,大于本日禁止滚动
                        int d = Integer.parseInt(list.get(list.size()/2));
                        if(d < time.monthDay){
                            String head = list.get(0);
                            list.remove(0);
                            list.add(head);
                        }
                    }else{
                        String head = list.get(0);
                        list.remove(0);
                        list.add(head);
                    }
                }else{
                    String head = list.get(0);
                    list.remove(0);
                    list.add(head);
                }*/

            }
        });
    }

    //获得指定月内的总天数
    private int getDayByYearAndMonth(int year, int month) {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        int maxDay= a.get(Calendar.DATE);
        return maxDay;
    }
    //设置当前月的天数
    private void setDayByYearAndMonth(int year, int month){
        int maxDay = getDayByYearAndMonth(year,month);
        List<String> dayData = new ArrayList<String>();
        for (int i = 1; i <= maxDay; i++){
            if(i < 10){
                dayData.add("0"+i);
            }else{
                dayData.add(""+i);
            }
        }
        if(day > maxDay){
            day = maxDay;
        }
        day_wheel.setData(dayData,day - 1);
    }
    private String getDate() {
        StringBuffer sb = new StringBuffer();
        sb.append(year+"/");
        if(month < 10){
            sb.append("0"+month+"/");
        }else{
            sb.append(month+"/");
        }
        if(day < 10){
            sb.append("0"+day);
        }else{
            sb.append(day);
        }
        return sb.toString();
    }

}
