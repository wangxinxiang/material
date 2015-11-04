package com.jhlc.material.bean;

import com.google.gson.annotations.SerializedName;

/**
 {"upuserlist":[],"downuserlist":[{"username":"3000","noreadcount":"1"}]}
 "expenseupuserlist": [
 {
 "name": "董事长",
 "msgnum": "2"
 }
 ],
 "expensedownuserlist": [
 {
 "name": "小明",
 "msgnum": "1"
 }
 ]

 */
public class NewMessageBean extends PostBackDataBean{
    @SerializedName("upuserlist")
    private UserNewMsgList[]   upUserNewMsgList;
    @SerializedName("downuserlist")
    private DownUserNewList[] downUserNewMsgList;
    @SerializedName("expenseupuserlist")
    private UserNewMsgList[] expenseupuserlist;
    @SerializedName("expensedownuserlist")
    private UserNewMsgList[] expensedownuserlist;
    @SerializedName("WorkSuperiors")
    private String WorkSuperiors;
    @SerializedName("WorkLower")
    private WorkLowerBean[] WorkLower;
    @SerializedName("ExpenseSuperiors")
    private String ExpenseSuperiors;
    @SerializedName("ExpenseLower")
    private String ExpenseLower;

    public UserNewMsgList[] getUpUserNewMsgList() {
        return upUserNewMsgList;
    }

    public void setUpUserNewMsgList(UserNewMsgList[] upUserNewMsgList) {
        this.upUserNewMsgList = upUserNewMsgList;
    }

    public DownUserNewList[] getDownUserNewMsgList() {
        return downUserNewMsgList;
    }

    public void setDownUserNewMsgList(DownUserNewList[] downUserNewMsgList) {
        this.downUserNewMsgList = downUserNewMsgList;
    }

    public UserNewMsgList[] getExpenseupuserlist() {
        return expenseupuserlist;
    }

    public void setExpenseupuserlist(UserNewMsgList[] expenseupuserlist) {
        this.expenseupuserlist = expenseupuserlist;
    }

    public UserNewMsgList[] getExpensedownuserlist() {
        return expensedownuserlist;
    }

    public void setExpensedownuserlist(UserNewMsgList[] expensedownuserlist) {
        this.expensedownuserlist = expensedownuserlist;
    }

    public String getWorkSuperiors() {
        return WorkSuperiors;
    }

    public void setWorkSuperiors(String workSuperiors) {
        WorkSuperiors = workSuperiors;
    }

    public WorkLowerBean[] getWorkLower() {
        return WorkLower;
    }

    public void setWorkLower(WorkLowerBean[] workLower) {
        WorkLower = workLower;
    }

    public String getExpenseSuperiors() {
        return ExpenseSuperiors;
    }

    public void setExpenseSuperiors(String expenseSuperiors) {
        ExpenseSuperiors = expenseSuperiors;
    }

    public String getExpenseLower() {
        return ExpenseLower;
    }

    public void setExpenseLower(String expenseLower) {
        ExpenseLower = expenseLower;
    }
}
