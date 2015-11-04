package com.jhlc.material.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

//处室任务的bean
public class DepByName extends BaseBean{
    @SerializedName("WorkAllmessage")
    private List<DepTaskListByClass> WorkAllmessage;

    public List<DepTaskListByClass> getWorkAllmessage() {
        return WorkAllmessage;
    }

    public void setWorkAllmessage(List<DepTaskListByClass> workAllmessage) {
        WorkAllmessage = workAllmessage;
    }
}
