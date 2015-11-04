package com.jhlc.material.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2015/9/23 0023.
 */
public class DepTaskAllDetail {
    @SerializedName("Addtime")
    private String Addtime;
    @SerializedName("Message")
    private String Message;
    @SerializedName("OfficeWorkprogress")
    private List<DepTaskDetailProgress> OfficeWorkprogress;

    public String getAddtime() {
        return Addtime;
    }

    public void setAddtime(String addtime) {
        Addtime = addtime;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public List<DepTaskDetailProgress> getOfficeWorkprogress() {
        return OfficeWorkprogress;
    }

    public void setOfficeWorkprogress(List<DepTaskDetailProgress> officeWorkprogress) {
        OfficeWorkprogress = officeWorkprogress;
    }
}
