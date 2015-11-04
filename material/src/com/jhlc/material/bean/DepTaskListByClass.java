package com.jhlc.material.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;


//各类 的处室任务 包括年度 季度 月度 专项
public class DepTaskListByClass {
    @SerializedName("worktag")
    private String worktag;

    @SerializedName("workmessage")
    private List<DepTaskItem> workmessage;

    public List<DepTaskItem> getWorkmessage() {
        return workmessage;
    }

    public void setWorkmessage(List<DepTaskItem> workmessage) {
        this.workmessage = workmessage;
    }

    public String getWorktag() {
        return worktag;
    }

    public void setWorktag(String worktag) {
        this.worktag = worktag;
    }
}
