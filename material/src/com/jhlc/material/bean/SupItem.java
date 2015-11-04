package com.jhlc.material.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2015/9/23 0023.
 */
public class SupItem {
    @SerializedName("job")
    private String job;

    @SerializedName("username")
    private String username;

    @SerializedName("Lacklog")
    private String Lacklog;

    @SerializedName("HeadImg")
    private String HeadImg;


    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLacklog() {
        return Lacklog;
    }

    public void setLacklog(String lacklog) {
        Lacklog = lacklog;
    }

    public String getHeadImg() {
        return HeadImg;
    }

    public void setHeadImg(String headImg) {
        HeadImg = headImg;
    }
}
