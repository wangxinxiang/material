package com.jhlc.material.utils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 104468 on 2014/10/24.
 */
public class StringUtils {
    /**
     * 手机号验证
     *
     * @param  str
     * @return 验证通过返回true
     */
    public static boolean isMobile(String str) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^[1][3,4,5,8][0-9]{9}$"); // 验证手机号
        m = p.matcher(str);
        b = m.matches();
        return b;
    }

    public static boolean isBlank(String str){
        if(str==null||"".equals(str)){
            return true;
        }else{
            return false;
        }
    }

    public static boolean isNotBlank(String str){
        if(str!=null&&!"".equals(str)){
            return true;
        }else{
            return false;
        }
    }

    //输入arraylist动态数组 返回 Integer类的数组 大小由输入类型的大小确定
    public static Integer[] changeIntegerArray(ArrayList<Integer> input){
        Integer[] result=new Integer[input.size()];
        for (int i = 0; i < input.size(); i++) {
            Integer integer = input.get(i);
            result[i]=integer;
        }
        return result;
    }

    public static String getLocalType(String mType) {
        if("today".equals(mType)){
            return "1";
        }else if("tomorrow".equals(mType)){
            return "2";
        }else if("recent".equals(mType)){
            return "3";
        }else if("later".equals(mType)){
            return "4";
        }else{
            return "later";
        }
    }
}
