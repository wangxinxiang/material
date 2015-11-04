package com.jhlc.material.bean;

import com.google.gson.annotations.SerializedName;

/**
 "name": "5001",
 "headimg": "/UserImage/201410200951303261.JPG",
 "msgnum": "0",
 "onexecutenum": "0",
 "overtimenum": "0",
 "normalworkoknum": "0",
 "overtimeworkoknum": "0",
 "noreportworknum": "0",
 "worknum": "0"
 */
public class Userlist {
    @SerializedName("name")
    private String name;
    @SerializedName("headimg")
    private String headimg;
    @SerializedName("msgnum")
    private String msgnum;
    @SerializedName("onexecutenum")
    private String onexecutenum;
    @SerializedName("overtimenum")
    private String overtimenum;
    @SerializedName("normalworkoknum")
    private String normalworkoknum;
    @SerializedName("overtimeworkoknum")
    private String overtimeworkoknum;
    @SerializedName("noreportworknum")
    private String noreportworknum;
    @SerializedName("worknum")
    private String worknum;
    @SerializedName("ispass")
    private String ispass;
    @SerializedName("usid")
    private String usid;
    @SerializedName("userid")
    private String userid;
    @SerializedName("applycompletenum")
    private String applycompletenum;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadimg() {
        return headimg;
    }

    public void setHeadimg(String headimg) {
        this.headimg = headimg;
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

    public String getNormalworkoknum() {
        return normalworkoknum;
    }

    public void setNormalworkoknum(String normalworkoknum) {
        this.normalworkoknum = normalworkoknum;
    }

    public String getOvertimeworkoknum() {
        return overtimeworkoknum;
    }

    public void setOvertimeworkoknum(String overtimeworkoknum) {
        this.overtimeworkoknum = overtimeworkoknum;
    }

    public String getNoreportworknum() {
        return noreportworknum;
    }

    public void setNoreportworknum(String noreportworknum) {
        this.noreportworknum = noreportworknum;
    }

    public String getWorknum() {
        return worknum;
    }

    public void setWorknum(String worknum) {
        this.worknum = worknum;
    }

    public String getIspass() {
        return ispass;
    }

    public void setIspass(String ispass) {
        this.ispass = ispass;
    }

    public String getUsid() {
        return usid;
    }

    public void setUsid(String usid) {
        this.usid = usid;
    }

    public String getApplycompletenum() {
        return applycompletenum;
    }

    public void setApplycompletenum(String applycompletenum) {
        this.applycompletenum = applycompletenum;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
