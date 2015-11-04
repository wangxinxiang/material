package com.jhlc.material.bean;

import com.google.gson.annotations.SerializedName;

/**
 "SurplusExpense": "",
 "username": "5001",
 "HeadImg": "/UserImage/201411030800185759.JPG"
 */
public class Paymentlist {
    @SerializedName("usid")
    private String usid;
    @SerializedName("IsPass")
    private String IsPass;
    @SerializedName("userid")
    private String userid;
    @SerializedName("username")
    private String username;
    @SerializedName("HeadImg")
    private String HeadImg;
    @SerializedName("SurplusExpense")
    private String SurplusExpense;
    @SerializedName("ExpenseLimit")
    private String ExpenseLimit;
    @SerializedName("noreadcount")
    private String noreadcount;


    public String getSurplusExpense() {
        return SurplusExpense;
    }

    public void setSurplusExpense(String surplusExpense) {
        SurplusExpense = surplusExpense;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHeadImg() {
        return HeadImg;
    }

    public void setHeadImg(String headImg) {
        HeadImg = headImg;
    }

    public String getNoreadcount() {
        return noreadcount;
    }

    public void setNoreadcount(String noreadcount) {
        this.noreadcount = noreadcount;
    }

    public String getUsid() {
        return usid;
    }

    public void setUsid(String usid) {
        this.usid = usid;
    }

    public String getIsPass() {
        return IsPass;
    }

    public void setIsPass(String isPass) {
        IsPass = isPass;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getExpenseLimit() {
        return ExpenseLimit;
    }

    public void setExpenseLimit(String expenseLimit) {
        ExpenseLimit = expenseLimit;
    }
}
