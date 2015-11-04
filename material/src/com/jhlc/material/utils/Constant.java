package com.jhlc.material.utils;



import com.jhlc.material.R;

import java.util.HashMap;

//保存常量
public class Constant {
    public static String EID = "128";
    public static int ZXL_UP_USER = 1;
    public static int ZXL_DOWN_USER = 0;
    public static int ZXL_UP_USER_NEW_MSG = 10;
    public static int ZXL_DOWN_USER_NEW_MSG = 0;

    public static String NewMessageServiceAction = "com.jhlc.material.service.NewMessageService";

    public static String TabUserNewMsgAction = "tabuser.broadcast.action";
    public static String TabReimNewMsgAction = "tabreim.broadcast.action";
    public static String WorkItemsNewMsgAction = "workitem.broadcast.action";
    public static String PaymentNewMsgAction = "payment.broadcast.action";
    public static String RenWu = "RenWu";
    public static String BaoXiao = "BaoXiao";
    public static int WhichTabHidden_renwu = 10;
    public static int WhichTabHidden_baoxiao = 0;
    public static int WhichTabShow_renwu = 10;
    public static int WhichTabShow_baoxiao = 0;

    public static String CancleTabFlagAction = "cancletabflag.broadcast.action";

    public static String ChangeHeadImage = "ChangeHeadImage";
    public static String uploadPic = "uploadPic";
    public static String testupload = "testupload";
    public static String downloadFromServer = "downloadFromServer";

    public static int RequestCode = 111;
    public static int ResultCode  = 100;
    //从settingactivity请求，到SettingAddHeadImageActivity
    public static int settting_changeheadimg  = 180;

    //从Constant请求，到SettingAddHeadImageActivity
    public static int progressReport_upload  = 280;

    public static String notebook_delay="-1";
    public static String notebook_today="1";
    public static String notebook_tomorrow="2";
    public static String notebook_recent="3";
    public static String notebook_later="4";

    public static Integer getExprImage(String exprcode){
        HashMap<String,Integer> expressionHashs = new HashMap<String,Integer>();
        expressionHashs.put("[笑脸]", R.drawable.expression_01);
        expressionHashs.put("[龇牙]",R.drawable.expression_02);
        expressionHashs.put("[阴险]",R.drawable.expression_03);
        expressionHashs.put("[胜利]",R.drawable.expression_04);
        expressionHashs.put("[好的]", R.drawable.expression_05);
        expressionHashs.put("[强]",  R.drawable.expression_06);
        expressionHashs.put("[握手]",R.drawable.expression_07);
        expressionHashs.put("[折磨]",R.drawable.expression_08);
        expressionHashs.put("[流汗]",R.drawable.expression_09);
        expressionHashs.put("[发怒]",R.drawable.expression_10);
        expressionHashs.put("[咒骂]",R.drawable.expression_11);
        expressionHashs.put("[菜刀]",R.drawable.expression_12);
        expressionHashs.put("[敲打]",R.drawable.expression_13);
        expressionHashs.put("[弱]",  R.drawable.expression_14);
        return expressionHashs.get(exprcode);
    }
}
