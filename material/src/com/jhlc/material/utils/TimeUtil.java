package com.jhlc.material.utils;

import android.util.Log;

import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtil {
	public static String TIME = "06:00:00--08:00:00";
	public static int SPANTIME = 2;
	public static int SPANDAY = 3;
    private static long lastRefreshTime;

    public static SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static SimpleDateFormat serverTimeSdf=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    public static SimpleDateFormat fileKeySdf=new SimpleDateFormat("yyyyMMddHHmmss");
    static  String regularExpression = "时间";
    public static SimpleDateFormat chatSdf=new SimpleDateFormat("M-d 时间HH:mm");

    public static SimpleDateFormat getRefreshTimeFormat(){
        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:ss");
        return format;
    }

    public static String getRefreshTimeStr(){
        Date date = new Date();
      /*  if(date.getTime()-PreferenceUtils.getLong(PreferenceUtils.LAST_REFRESH_TIME , 0)<60*1000){
            return "刚刚更新";
        }*/
        lastRefreshTime = date.getTime();
        return "上次更新于:今天 "+TimeUtil.formateSimpleChatTime(date);
    }

	/**
	 * @param time
	 *            设定的时间
	 * 
	 * @return对比时间 如果选择的time在当前日期SPANTIME个小时之后 返回真
	 */
	public static boolean timeCompare(String time) {

		Date setdate = parseTime(time.trim());
		Date currentdate = new Date();

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentdate);
		calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY)
				+ SPANTIME);

		currentdate = calendar.getTime();

		if (setdate.after(currentdate)) {
			Log.e("测试1如果选择的time在当前日期SPANTIME个小时之后 返回真", "t");
			return true;
		}
		Log.e("测试1", "f");
		return false;

	}

	/**
	 * @param time
	 *            设定的时间
	 * 
	 * @return对比时间 如果选择的time没有超过从当前时间算起的3天 返回真
	 */
	public static boolean timeCompare3Days(String time) {

		Date setdate = parseTime(time);
		Date currentdate = new Date();

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(setdate);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)
				- SPANDAY);

		setdate = calendar.getTime();
		Log.e("测试2", "currentdate" + currentdate.toGMTString() + "      "
				+ setdate.toGMTString());

		if (setdate.before(currentdate)) {
			Log.e("测试2", " 如果选择的time没有超过从当前时间算起的3天 返回真");
			return true;

		}
		Log.e("测试2", "f");
		return false;

	}

	/**
	 * @return解析当前时间为date
	 */
	public static Date parseTime(String time) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		Date date = null;
		try {
			date = sdf.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * @return格式化当前日期和时间为字符串
	 */
	private static String mCurrentTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currenttime = df.format(new Date());
		return currenttime;
	}

	/**
	 * @return格式化当前日期为字符串
	 */
	public static String getCurrentTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd ");
		String currenttime = df.format(new Date());
		return currenttime;
	}

    /**
     * 返回两个时间之间的差值，注意，返回时间可能为负值，当数值为负时
     * @param startTime
     * @param endTime
     * @return
     */
    public static long getDaysBetween(String startTime,String endTime){
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        Date endDate = null;
        try {
            endDate = df.parse(endTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date startDate= null;
        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
        try {
            startDate = df1.parse(startTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long l=endDate.getTime()-startDate.getTime();
        long day=l/(24*60*60*1000);
        return day;
    }


    /**
     * 返回两个时间之间的差值，注意，返回时间可能为负值，当数值为负时
     * @param startTime
     * @param endTime
     * @return
     */
    public static long[] getDaysHoursBetween(String startTime,String endTime){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date endDate = null;
        try {
            endDate = df.parse(endTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date startDate= null;
        try {
            startDate = df.parse(startTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long l=endDate.getTime()-startDate.getTime();
        long day=l/(24*60*60*1000);
        long hour=(l/(60*60*1000)-day*24);
        return new long[]{day,hour};
    }

    public static String getDataDiff(String starttime, String endtime){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = null;
        try {
            now = df.parse(endtime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date= null;
        try {
            date = df.parse(starttime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long l=Math.abs(now.getTime()-date.getTime());
        long day=l/(24*60*60*1000);
        long hour=(l/(60*60*1000)-day*24);
        long min=((l/(60*1000))-day*24*60-hour*60);
        long s=(l/1000-day*24*60*60-hour*60*60-min*60);
        System.out.println(day+"-"+hour+"-"+min+"-"+s);
        return day+"-"+hour+"-"+min+"-"+s;
    }

    public static long getLongDiff(String starttime, String endtime){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = null;
        try {
            now = df.parse(endtime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date= null;
        try {
            date = df.parse(starttime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long l=date.getTime() - now.getTime();

        return l;
    }
    //改成23点
    public static boolean getDataDiff18(String starttime, String endtime){

        String endtime1[] = endtime.split(" ");
        String endtime2 = endtime1[0].replace("/","-")+" 23:00:00";

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = null;
        try {
            now = df.parse(endtime2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date= null;
        try {
            date = df.parse(starttime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long l=now.getTime()-date.getTime();
        if(l>23*60*60*1000){
            //并且当前时间 大于18 点
            if(TimeUtil.compareTime(endtime,endtime2)){
                return true;
            } else {
                return  false;
            }
        } else {
            return false;
        }

    }

    public static String getSimpleChatTime(String reportTime){
        try {
            Date date=TimeUtil.serverTimeSdf.parse(reportTime);
            return formateSimpleChatTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getFileKeyByReportTime(String reportTime){
        try {
            Date date=TimeUtil.serverTimeSdf.parse(reportTime);
            return fileKeySdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 根据当前时间获得一个文件名 格式：yyyyMMddHHmmss
     * @return
     */
    public static String getFileKeyTimeStr(){
            return fileKeySdf.format(new Date());

    }

    public static String formateSimpleChatTime(Date date) {
        Calendar nowCal=Calendar.getInstance();
        nowCal.setTime(date);
        String result=chatSdf.format(date);
        int hour=nowCal.get(Calendar.HOUR_OF_DAY);
        String replace=" ";
        if(hour>=0&&hour<6){
            replace="凌晨";
        }
        if(hour>=6&&hour<8){
            replace="早上";
        }
        if(hour>=8&&hour<11){
            replace="上午";
        }
        if(hour>=11&&hour<13){
            replace="中午";
        }
        if(hour>=13&&hour<18){
            replace="下午";
        }
        if(hour>=18&&hour<24){
            replace="晚上";
        }
        System.out.println("replace = " + replace);
        System.out.println("regularExpression = " + regularExpression);
        System.out.println("result1111 = " + result);

        result=result.replaceAll("时间", replace);
        return result;
    }


    //时间戳格式转换
    public static String getChatTime(long timesamp,long currenttimesamp) {
        String result = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        Date today = new Date(currenttimesamp);
        Date otherDay = new Date(timesamp);
        long time = currenttimesamp - timesamp;
        int temp = Integer.parseInt(sdf.format(today))
                - Integer.parseInt(sdf.format(otherDay));
        Log.e("getChatTime","time--> "+time+"temp--> " +temp);
        if(time/1000 < 10 && time/1000 >= 0) {
            //如果时间间隔小于10秒则显示“刚刚”time/10得出的时间间隔的单位是秒
            result ="刚刚";
        } else if(time/1000 < 60 && time/1000 > 0) {
            //如果时间间隔小于60秒则显示多少秒前
            int se = (int) ((time%60000)/1000);
            result = se + "秒前";
        } else if(time/60000 < 60 && time/60000 > 0) {
            //如果时间间隔小于60分钟则显示多少分钟前
            int m = (int) ((time%3600000)/60000);//得出的时间间隔的单位是分钟
            result = m + "分钟前";
        } else if(time/3600000 < 24 && time/3600000 >= 0) {
            //如果时间间隔小于24小时则显示多少小时前
            int h = (int) (time/3600000);//得出的时间间隔的单位是小时
            int m = (int) ((time%3600000)/60000);//得出的时间间隔的单位是分钟
            if(m!=0) {
                result = h + "小时" + m + "分钟前";
            } else {
                result = h + "小时前";
            }
        } else if(temp == 1) {
            result = "昨天 " + getHourAndMin(timesamp);
        } else if(temp == 2) {
            result = "前天 " + getHourAndMin(timesamp);
        } else {
            result = getTime(timesamp);
        }

        /*switch (temp) {
            case 1:
                result = "昨天 " + getHourAndMin(timesamp);
                break;
            case 2:
                result = "前天 " + getHourAndMin(timesamp);
                break;

            default:
                result = getTime(timesamp);
                break;
        }*/
        return result;
    }

    public static String getHourAndMin(long time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(new Date(time));
    }

    public static String getTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
        return format.format(new Date(time));
    }

    /**
     *   今天  昨天
     上午：8点——11点
     中午：11点——13点
     下午：13点——18点
     晚上：18点——24点
     今天上午： date+"08:00:00" ~  date+"11:00:00"
     * */
    public static String getInterval(String createtime,String currenttime) { //传入的时间格式必须类似于2012-8-21 17:53:20这样的格式
            String interval = null;

            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ParsePosition pos = new ParsePosition(0);
            Date d1 = (Date) sd.parse(createtime, pos);
            ParsePosition currpos = new ParsePosition(0);
            Date curr = (Date) sd.parse(currenttime, currpos);

            LogZ.d("getReportmessage",""+sd.format(new Date()) );

            //用现在距离1970年的时间间隔new Date().getTime()减去以前的时间距离1970年的时间间隔d1.getTime()得出的就是以前的时间与现在时间的时间间隔
            long time = curr.getTime() - d1.getTime();// 得出的时间间隔是毫秒

            if(time/1000 < 10 && time/1000 >= 0) {
                //如果时间间隔小于10秒则显示“刚刚”time/10得出的时间间隔的单位是秒
                interval ="刚刚";
            }

            if(time/3600000 < 24 && time/3600000 >= 0) {
                //如果时间间隔小于24小时则显示多少小时前
                int h = (int) (time/3600000);//得出的时间间隔的单位是小时
                interval = h + "小时前";
            } else {
                //大于24小时，则显示正常的时间，但是不显示秒
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                ParsePosition pos2 = new ParsePosition(0);
                Date d2 = (Date) sdf.parse(createtime, pos2);

                interval = sdf.format(d2);
            }

            if(time/60000 < 60 && time/60000 > 0) {
                //如果时间间隔小于60分钟则显示多少分钟前
                int m = (int) ((time%3600000)/60000);//得出的时间间隔的单位是分钟
                interval = m + "分钟前";
            }
            if(time/1000 < 60 && time/1000 > 0) {
                //如果时间间隔小于60秒则显示多少秒前
                int se = (int) ((time%60000)/1000);
                interval = se + "秒前";
            }

            return interval;
        }

    /**
     * @return解析当前时间为指定
     */
    public static String getSendTime(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat df_work = new SimpleDateFormat("yyyy-MM-dd ahh:mm");
        return df_work.format(date);
    }

    /**
     * @return解析当前时间为指定
     */
    public static long getMilliSecond(String starttime,String endtime) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = df.parse(starttime);
            date2 = df.parse(endtime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date2.getTime() - date1.getTime();
    }

    /**
     * @return解析当前时间为指定
     */
    public static long getMilliSecond(String time) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = df.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    /**
     * @return比较两个时间的大小
     */
    public static boolean compareTime(String time1,String time2) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1 = null;
        Date date2 = null;

        try {
            date1 = df.parse(time1.replace("/","-"));
            date2 = df.parse(time2.replace("/","-"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(date1.after(date2)) {
            return true;
        } else {
            return false;
        }
    }


    private String getDate() {
        Calendar c = Calendar.getInstance();

        String year = String.valueOf(c.get(Calendar.YEAR));
        String month = String.valueOf(c.get(Calendar.MONTH));
        String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH) + 1);
        String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
        String mins = String.valueOf(c.get(Calendar.MINUTE));

        StringBuffer sbBuffer = new StringBuffer();
        sbBuffer.append(year + "-" + month + "-" + day + " " + hour + ":" + mins);

        return sbBuffer.toString();
    }


    public static void main(String[] args) {
        System.out.println("ddd");
        String completetime="2014-11-24 1:38:02";
        String endtime=sdf.format(new Date());
       String result= getDataDiff(completetime, endtime);
        System.out.println("result = " + result);

        Calendar temp=Calendar.getInstance();
        temp.set(Calendar.DATE,temp.get(Calendar.DATE)+1);
        result=TimeUtil.sdf.format(temp.getTime());
        System.out.println("result add date1= " + result);

        String testStr="2014-11-24 12:00:00";
        try {
            Date date=sdf.parse(testStr);
            String testresulte=serverTimeSdf.format(date);
            System.out.println("testresulte = " + testresulte);
            String testresulte2=chatSdf.format(date);
            System.out.println("testresulte2 = " + testresulte2);
            String result333=getSimpleChatTime("2014/11/24 12:00:00");
            System.out.println("result333 = " + result333);

            String tempsss="11月24日 时间12:00".replaceAll("时间", "下午");
            System.out.println("tempsss = " + tempsss);
            String testcode="文字编码,?%";
            String coderesult=URLEncoder.encode(testcode, "utf-8");
            System.out.println("coderesult = " + coderesult);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
