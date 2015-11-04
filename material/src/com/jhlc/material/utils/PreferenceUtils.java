package com.jhlc.material.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.jhlc.material.ZXLApplication;

import java.util.HashSet;

public class PreferenceUtils {
    public final static SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ZXLApplication.getInstance());
    public final static String LAST_REFRESH_TIME = "last_refresh_time";

    private SharedPreferences.Editor editor = sp.edit();

    private PreferenceUtils() {
    }

    public static PreferenceUtils getInstance(){
        return new PreferenceUtils();
    }


    //用户是否登录
    public void setIsLogin(boolean IsLogin){
        editor.putBoolean("IsLogin",IsLogin);
        editor.commit();
    }

    public static Boolean getIsLogin() {
        return sp.getBoolean("IsLogin", false);
    }
    //用户是否设置过头像
    public void setHeadImgHasSetted(boolean IsLogin){
        editor.putBoolean("headImgHasSetted",IsLogin);
        editor.commit();
    }

    public static Boolean getHeadImgHasSetted() {
        return sp.getBoolean("headImgHasSetted", false);
    }
    //用户是否给发票拍照
    public void setPaymentImgHasSetted(boolean IsLogin){
        editor.putBoolean("paymentImgHasSetted",IsLogin);
        editor.commit();
    }

    public static Boolean getPaymentImgHasSetted() {
        return sp.getBoolean("paymentImgHasSetted", false);
    }

    //讲拍下的发票照片保存起来，方便预览
    public void setPaymentImgFileName(String UserName) {
        editor.putString("paymentImgFileName", UserName);
        editor.commit();
    }

    public static String getPaymentImgFileName() {
        return sp.getString("paymentImgFileName", "");
    }

    //用户ID
    public void setUserID(String UserID) {
        editor.putString("UserID", UserID);
        editor.commit();
    }

    public static String getUserID() {
        return sp.getString("UserID", "");
    }


    //getServerMemo 接口用到的上次保存的查询时间
    public void setServerMemoDT(String UserName) {
        editor.putString("ServerMemoDT", UserName);
        editor.commit();
    }

    public static String getServerMemoDT() {
        return sp.getString("ServerMemoDT", "");
    }
    //用户名
    public void setUserName(String UserName) {
        editor.putString("UserName", UserName);
        editor.commit();
    }
    public static String getUserName() {
        return sp.getString("UserName", "");
    }

    public static String getPassWord() {
        return sp.getString("PassWord", "");
    }

    public void setPassWord(String PassWord) {
        editor.putString("PassWord", PassWord);
        editor.commit();
    }

    //用户头像
    public void setHeadImage(String HeadImage) {
        editor.putString("HeadImage", HeadImage);
        editor.commit();
    }

    public static String getHeadImage() {
        return sp.getString("HeadImage", "");
    }

    //用户名
    public void setAlarmConfig(String AlarmConfig) {
        editor.putString("AlarmConfig", AlarmConfig);
        editor.commit();
    }

    public static String getAlarmConfig() {
        return sp.getString("AlarmConfig", "[]");
    }

    public void setCurrentTimeMillis(long time){
        editor.putLong("CurrentTime",time);
        editor.commit();
    }
    public static long getCurrentTimeMillis(){
        return sp.getLong("CurrentTime",0);
    }
    public static Boolean getIsDownNewMsg() {
        return sp.getBoolean("IsDownNewMsg", false);
    }

    public static void setLong(final String key, final long value) {
        sp.edit().putLong(key, value).commit();
    }

    public static long getLong(final String key, final long defaultValue) {
        return sp.getLong(key, defaultValue);
    }


    public void clearSP( ) {
        editor.clear();
        editor.commit();
    }


    //用户是否需要显示任务报表
    public void setNeedShowReport(boolean flag){
        editor.putBoolean("needShowReport",flag);
        editor.commit();
    }

    public static Boolean isNeedShowReport() {
        return sp.getBoolean("needShowReport", false);
    }

    //用户是否需要更新提示
    public void setNeedUpdate(boolean IsLogin){
        editor.putBoolean("needUpdate",IsLogin);
        editor.commit();
    }

    public static Boolean isNeedUpdate() {
        return sp.getBoolean("needUpdate", true);
    }
}
