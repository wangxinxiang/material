package com.jhlc.material.bean;

import com.google.gson.annotations.SerializedName;

//处室任务 各项进展
public class DepTaskDetailProgress {
    @SerializedName("UpdateTime")
    private String UpdateTime;
    @SerializedName("EditMessage")
    private String EditMessage;
    @SerializedName("id")
    private String id;

    public String getUpdateTime() {
        return UpdateTime;
    }

    public void setUpdateTime(String updateTime) {
        UpdateTime = updateTime;
    }

    public String getEditMessage() {
        return EditMessage;
    }

    public void setEditMessage(String editMessage) {
        EditMessage = editMessage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
