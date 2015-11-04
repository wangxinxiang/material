package com.jhlc.material.bean;

import com.google.gson.annotations.SerializedName;

/**
 "workusername": "\u65b9\u8def",
 "workuserhead": "/UserImage/201411270402074201.JPG",
 "msgnum": "0",
 "onexecutenum": "1",
 "overtimenum": "1",
 "workOknum": "3",
 "allworknum": "4",
 "allnodayreportworknum": "0",
 * */
public class WorksBean  extends PostBackDataBean {
    @SerializedName("workusername")
    private String workusername;
    @SerializedName("workuserhead")
    private String workuserhead;
    @SerializedName("msgnum")
    private String msgnum;
    @SerializedName("onexecutenum")
    private String onexecutenum;
    @SerializedName("overtimenum")
    private String overtimenum;


    ///完成
    @SerializedName("workOknum")
    private String workOknum;

    //总数
    @SerializedName("allworknum")
    private String allworknum;

    //未汇报
    @SerializedName("allnodayreportworknum")
    private String allnodayreportworknum;

    @SerializedName("onexecutelist")
    private Executelist[] onexecuteLists;
    @SerializedName("overtimenumlist")
    private Executelist[] overtimenumList;

    @SerializedName("workOknumlist")
    private Executelist[] workOknumlist;

    @SerializedName("allworknumlist")
    private Executelist[] allworknumlist;

    @SerializedName("allnodayreportworknumlist")
    private Executelist[] allnodayreportworknumlist;


    public String getWorkusername() {
        return workusername;
    }

    public void setWorkusername(String workusername) {
        this.workusername = workusername;
    }

    public String getWorkuserhead() {
        return workuserhead;
    }

    public void setWorkuserhead(String workuserhead) {
        this.workuserhead = workuserhead;
    }

    public String getMsgnum() {
        return msgnum;
    }

    public void setMsgnum(String msgnum) {
        this.msgnum = msgnum;
    }

    public String getOnexecutenum() {
        return onexecutenum;
    }

    public void setOnexecutenum(String onexecutenum) {
        this.onexecutenum = onexecutenum;
    }

    public String getOvertimenum() {
        return overtimenum;
    }

    public void setOvertimenum(String overtimenum) {
        this.overtimenum = overtimenum;
    }



    public Executelist[] getOnexecuteLists() {
        return onexecuteLists;
    }

    public void setOnexecuteLists(Executelist[] onexecuteLists) {
        this.onexecuteLists = onexecuteLists;
    }

    public Executelist[] getOvertimenumList() {
        return overtimenumList;
    }

    public void setOvertimenumList(Executelist[] overtimenumList) {
        this.overtimenumList = overtimenumList;
    }



    public String getWorkOknum() {
        return workOknum;
    }

    public void setWorkOknum(String workOknum) {
        this.workOknum = workOknum;
    }

    public String getAllworknum() {
        return allworknum;
    }

    public void setAllworknum(String allworknum) {
        this.allworknum = allworknum;
    }

    public String getAllnodayreportworknum() {
        return allnodayreportworknum;
    }

    public void setAllnodayreportworknum(String allnodayreportworknum) {
        this.allnodayreportworknum = allnodayreportworknum;
    }

    public Executelist[] getWorkOknumlist() {
        return workOknumlist;
    }

    public void setWorkOknumlist(Executelist[] workOknumlist) {
        this.workOknumlist = workOknumlist;
    }

    public Executelist[] getAllworknumlist() {
        return allworknumlist;
    }

    public void setAllworknumlist(Executelist[] allworknumlist) {
        this.allworknumlist = allworknumlist;
    }

    public Executelist[] getAllnodayreportworknumlist() {
        return allnodayreportworknumlist;
    }

    public void setAllnodayreportworknumlist(Executelist[] allnodayreportworknumlist) {
        this.allnodayreportworknumlist = allnodayreportworknumlist;
    }
}
