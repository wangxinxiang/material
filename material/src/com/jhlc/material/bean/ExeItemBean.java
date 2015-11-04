package com.jhlc.material.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2015/9/14 0014.
 */
public class ExeItemBean {
    @SerializedName("office_id")
    private String office_id;

    @SerializedName("office_name")
    private String office_name;

    @SerializedName("AreaName")
    private String AreaName;

    @SerializedName("AreaLevel")
    private String AreaLevel;

    @SerializedName("completed")
    private String completed;

    @SerializedName("completing")
    private String completing;

    public String getOffice_id() {
        return office_id;
    }

    public void setOffice_id(String office_id) {
        this.office_id = office_id;
    }

    public String getOffice_name() {
        return office_name;
    }

    public void setOffice_name(String office_name) {
        this.office_name = office_name;
    }

    public String getAreaName() {
        return AreaName;
    }

    public void setAreaName(String areaName) {
        AreaName = areaName;
    }

    public String getAreaLevel() {
        return AreaLevel;
    }

    public void setAreaLevel(String areaLevel) {
        AreaLevel = areaLevel;
    }

    public String getCompleted() {
        return completed;
    }

    public void setCompleted(String completed) {
        this.completed = completed;
    }

    public String getCompleting() {
        return completing;
    }

    public void setCompleting(String completing) {
        this.completing = completing;
    }
}
