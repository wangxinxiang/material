package com.jhlc.material.bean;

import com.google.gson.annotations.SerializedName;

/**
 "workid": "4",
 "message": "ceshi",
 "starttime": "2014/10/22 13:12:00",
 "endtime": "2014/10/21 0:00:00",
 "enddays": "",
 "completetime": "",
 "statu": "0",
 "reportmessage": "",
 "reporttime": ""
 */
public class Executelist {
    @SerializedName("workid")
    private String workid;
    @SerializedName("message")
    private String message;
    @SerializedName("starttime")
    private String starttime;
    @SerializedName("endtime")
    private String endtime;
    @SerializedName("enddays")
    private String enddays;
    @SerializedName("completetime")
    private String completetime;
    @SerializedName("currenttime")
    private String currenttime;
    @SerializedName("statu")
    private String statu;
    @SerializedName("reportmessage")
    private String reportmessage;
    @SerializedName("reporttime")
    private String reporttime;
    @SerializedName("reporttypeid")
    private String reporttypeid;
    //申请完成任务
    @SerializedName("applycomplete")
    private String applycomplete;
    //申请完成任务
    @SerializedName("applytime")
    private String applytime;

    public String getWorkid() {
        return workid;
    }

    public void setWorkid(String workid) {
        this.workid = workid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getEnddays() {
        return enddays;
    }

    public void setEnddays(String enddays) {
        this.enddays = enddays;
    }

    public String getCompletetime() {
        return completetime;
    }

    public void setCompletetime(String completetime) {
        this.completetime = completetime;
    }

    public String getCurrenttime() {
        return currenttime;
    }

    public void setCurrenttime(String currenttime) {
        this.currenttime = currenttime;
    }

    public String getStatu() {
        return statu;
    }

    public void setStatu(String statu) {
        this.statu = statu;
    }

    public String getReportmessage() {
        return reportmessage;
    }

    public void setReportmessage(String reportmessage) {
        this.reportmessage = reportmessage;
    }

    public String getReporttime() {
        return reporttime;
    }

    public void setReporttime(String reporttime) {
        this.reporttime = reporttime;
    }

    public String getReporttypeid() {
        return reporttypeid;
    }

    public void setReporttypeid(String reporttypeid) {
        this.reporttypeid = reporttypeid;
    }

    public String getApplycomplete() {
        return applycomplete;
    }

    public void setApplycomplete(String applycomplete) {
        this.applycomplete = applycomplete;
    }

    public String getApplytime() {
        return applytime;
    }

    public void setApplytime(String applytime) {
        this.applytime = applytime;
    }
}
