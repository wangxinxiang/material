package com.jhlc.material.bean;

import com.google.gson.annotations.SerializedName;

/**
 "code": "100",
 "msg": "success",
 "opcode": "work_detail_list_success",

 "workid": "12",
 "workusername": "5002",
 "worktitle": "dai 5004_01",
 "workuserhead": "/UserImage/201410200951459047.JPG",
 "starttime": "2014/10/23 19:14:00",
 "endtime": "2014/10/24 19:14:00",
 "currenttime": "2014/10/23 19:53:06",
 "completetime": "",
 "enddays": "1.00",
 "workdetaillist": [
 */
public class ReportDetailBean extends PostBackDataBean {
    @SerializedName("workid")
    private String workid;
    @SerializedName("workusername")
    private String workusername;
    @SerializedName("worktitle")
    private String worktitle;
    @SerializedName("workuserhead")
    private String workuserhead;
    @SerializedName("starttime")
    private String starttime;
    @SerializedName("endtime")
    private String endtime;
    @SerializedName("currenttime")
    private String currenttime;
    @SerializedName("completetime")
    private String completetime;
    @SerializedName("enddays")
    private String enddays;
    @SerializedName("workdetaillist")
    private WorkDetailList[] workDetailLists;
    //申请完成字段
    @SerializedName("applycomplete")
    private String applycomplete;
    //日报开关字段
    @SerializedName("dayreport")
    private String dayreport;
    //任务状态 0 正常 2 正常完成 3 超时完成
    @SerializedName("status")
    private String status;



    public String getWorkid() {
        return workid;
    }

    public void setWorkid(String workid) {
        this.workid = workid;
    }

    public String getWorkusername() {
        return workusername;
    }

    public void setWorkusername(String workusername) {
        this.workusername = workusername;
    }

    public String getWorktitle() {
        return worktitle;
    }

    public void setWorktitle(String worktitle) {
        this.worktitle = worktitle;
    }

    public String getWorkuserhead() {
        return workuserhead;
    }

    public void setWorkuserhead(String workuserhead) {
        this.workuserhead = workuserhead;
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

    public String getCurrenttime() {
        return currenttime;
    }

    public void setCurrenttime(String currenttime) {
        this.currenttime = currenttime;
    }

    public String getCompletetime() {
        return completetime;
    }

    public void setCompletetime(String completetime) {
        this.completetime = completetime;
    }

    public String getEnddays() {
        return enddays;
    }

    public void setEnddays(String enddays) {
        this.enddays = enddays;
    }

    public WorkDetailList[] getWorkDetailLists() {
        return workDetailLists;
    }

    public void setWorkDetailLists(WorkDetailList[] workDetailLists) {
        this.workDetailLists = workDetailLists;
    }

    public String getApplycomplete() {
        return applycomplete;
    }

    public void setApplycomplete(String applycomplete) {
        this.applycomplete = applycomplete;
    }

    public String getDayreport() {
        return dayreport;
    }

    public void setDayreport(String dayreport) {
        this.dayreport = dayreport;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
