package com.jhlc.material.bean;

import com.google.gson.annotations.SerializedName;

/**
 */
public class NoteBookServerMemo {
    @SerializedName("ID")
    private String ID;
    @SerializedName("UserID")
    private String UserID;
    @SerializedName("MemoContent")
    private String MemoContent;
    @SerializedName("CreateTime")
    private String CreateTime;
    @SerializedName("OverTime")
    private String OverTime;
    @SerializedName("MType")
    private String MType;
    @SerializedName("top")
    private String top;
    @SerializedName("LocalID")
    private String LocalID;


    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getMemoContent() {
        return MemoContent;
    }

    public void setMemoContent(String memoContent) {
        MemoContent = memoContent;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public String getMType() {
        return MType;
    }

    public void setMType(String MType) {
        this.MType = MType;
    }

    public String getTop() {
        return top;
    }

    public void setTop(String top) {
        this.top = top;
    }

    public String getLocalID() {
        return LocalID;
    }

    public void setLocalID(String localID) {
        LocalID = localID;
    }

    public String getOverTime() {
        return OverTime;
    }

    public void setOverTime(String overTime) {
        OverTime = overTime;
    }
}
