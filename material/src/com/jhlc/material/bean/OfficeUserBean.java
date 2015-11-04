package com.jhlc.material.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 104468 on 2015/3/12.
 */
public class OfficeUserBean {
    @SerializedName("username")
    private String username;
    @SerializedName("headimage")
    private String headimage;
    @SerializedName("job")
    private String job;
    @SerializedName("area")
    private String area;

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHeadimage() {
        return headimage;
    }

    public void setHeadimage(String headimage) {
        this.headimage = headimage;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }
}
