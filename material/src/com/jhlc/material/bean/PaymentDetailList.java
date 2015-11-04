package com.jhlc.material.bean;

import com.google.gson.annotations.SerializedName;

/**
 "UserID": "22D79DCD-099F-4B7E-9B4E-609EDF4CCB0C",
 "ReceptUserID": "888A2E1C-6E90-4A8A-9175-938D0A0C3948",
 "AddTime": "2014/11/218: 57: 00",
 "Message": "报销100",
 "CurrentPosition": "0.00",
 "FileUrl": "",
 "ExpenseStatus": "1",
 "ConfirmTime": "",
 "ExpenseID": "5",
 "IsRead": "1",
 "Position": ""
 "ExpenseMoney": "100.00"
 **/

public class PaymentDetailList{
    @SerializedName("UserID")
    private String UserID;
    @SerializedName("ReceptUserID")
    private String ReceptUserID;
    @SerializedName("Position")
    private String Position;
    @SerializedName("AddTime")
    private String AddTime;
    @SerializedName("Message")
    private String Message;
    @SerializedName("CurrentPosition")
    private String CurrentPosition;
    @SerializedName("FileUrl")
    private String FileUrl;
    @SerializedName("ExpenseStatus")
    private String ExpenseStatus;
    @SerializedName("ConfirmTime")
    private String ConfirmTime;
    @SerializedName("ExpenseID")
    private String ExpenseID;
    @SerializedName("IsRead")
    private String IsRead;
    @SerializedName("ExpenseMoney")
    private String ExpenseMoney;
    @SerializedName("SysPostion")
    private String SysPostion;
    @SerializedName("bigimage")
    private String bigimage;
    @SerializedName("smallimage")
    private String smallimage;

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getReceptUserID() {
        return ReceptUserID;
    }

    public void setReceptUserID(String receptUserID) {
        ReceptUserID = receptUserID;
    }

    public String getPosition() {
        return Position;
    }

    public void setPosition(String position) {
        Position = position;
    }

    public String getAddTime() {
        return AddTime;
    }

    public void setAddTime(String addTime) {
        AddTime = addTime;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getCurrentPosition() {
        return CurrentPosition;
    }

    public void setCurrentPosition(String currentPosition) {
        CurrentPosition = currentPosition;
    }

    public String getFileUrl() {
        return FileUrl;
    }

    public void setFileUrl(String fileUrl) {
        FileUrl = fileUrl;
    }

    public String getExpenseStatus() {
        return ExpenseStatus;
    }

    public void setExpenseStatus(String expenseStatus) {
        ExpenseStatus = expenseStatus;
    }

    public String getConfirmTime() {
        return ConfirmTime;
    }

    public void setConfirmTime(String confirmTime) {
        ConfirmTime = confirmTime;
    }

    public String getExpenseID() {
        return ExpenseID;
    }

    public void setExpenseID(String expenseID) {
        ExpenseID = expenseID;
    }

    public String getIsRead() {
        return IsRead;
    }

    public void setIsRead(String isRead) {
        IsRead = isRead;
    }

    public String getExpenseMoney() {
        return ExpenseMoney;
    }

    public void setExpenseMoney(String expenseMoney) {
        ExpenseMoney = expenseMoney;
    }

    public String getSysPostion() {
        return SysPostion;
    }

    public void setSysPostion(String sysPostion) {
        SysPostion = sysPostion;
    }

    public String getBigimage() {
        return bigimage;
    }

    public void setBigimage(String bigimage) {
        this.bigimage = bigimage;
    }

    public String getSmallimage() {
        return smallimage;
    }

    public void setSmallimage(String smallimage) {
        this.smallimage = smallimage;
    }
}

