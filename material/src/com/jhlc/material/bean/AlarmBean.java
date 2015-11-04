package com.jhlc.material.bean;

import com.google.gson.annotations.SerializedName;

/**
 */
public class AlarmBean {
    @SerializedName("workid")
    private String workid;
    @SerializedName("soleid")
    private int soleid;
    @SerializedName("currenttime")
    private String currenttime;
    @SerializedName("statu")
    private String statu;
    @SerializedName("endtime")
    private String endtime;


    public String getWorkid() {
        return workid;
    }

    public void setWorkid(String workid) {
        this.workid = workid;
    }

    public int getSoleid() {
        return soleid;
    }

    public void setSoleid(int soleid) {
        this.soleid = soleid;
    }

    public String getCurrenttime() {
        return currenttime;
    }

    public void setCurrenttime(String currenttime) {
        this.currenttime = currenttime;
    }

    public String getStatu() {
        return statu;
    }

    public void setStatu(String statu) {
        this.statu = statu;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }
}
