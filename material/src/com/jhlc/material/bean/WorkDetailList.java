package com.jhlc.material.bean;

import com.google.gson.annotations.SerializedName;

/**
 "reportmessage": "huibao_03",
 "reporttime": "2014/10/22 23:17:00"
 "reporttypeid": "0"   1表示上级  0表示下级
 */
public class WorkDetailList {
    @SerializedName("reportmessage")
    private String reportmessage;
    @SerializedName("reporttime")
    private String reporttime;
    @SerializedName("reporttypeid")
    private String reporttypeid;
    @SerializedName("bigimage")
    private String bigimage;
    @SerializedName("smallimage")
    private String smallimage;

    public String getReporttypeid() {
        return reporttypeid;
    }

    public void setReporttypeid(String reporttypeid) {
        this.reporttypeid = reporttypeid;
    }

    public String getReportmessage() {
        return reportmessage;
    }

    public void setReportmessage(String reportmessage) {
        this.reportmessage = reportmessage;
    }

    public String getReporttime() {
        return reporttime;
    }

    public void setReporttime(String reporttime) {
        this.reporttime = reporttime;
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
