package com.jhlc.material.utils;

import android.util.Log;

/**
 * Created by 104468 on 2014/11/14.
 */
public class LogZ {
    public static boolean showFlag=true;

    public static void d(String tag,String msg){
        if (showFlag) {
            Log.d(tag,msg);
        }
    }

    public static void e(String tag,String msg){
        if (showFlag) {
            Log.e(tag,msg);
        }
    }
}

