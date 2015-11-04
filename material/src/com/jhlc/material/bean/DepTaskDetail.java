package com.jhlc.material.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

//处室任务详情
public class DepTaskDetail extends BaseBean {
    @SerializedName("WorkAllDetailmessage")
    private List<DepTaskAllDetail> WorkAllDetailmessage;

    public List<DepTaskAllDetail> getWorkAllDetailmessage() {
        return WorkAllDetailmessage;
    }

    public void setWorkAllDetailmessage(List<DepTaskAllDetail> workAllDetailmessage) {
        WorkAllDetailmessage = workAllDetailmessage;
    }
}
