package com.jhlc.material.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2015/9/22 0022.
 */
public class AreaInforBean {
    @SerializedName("ID")
    private String ID;
    @SerializedName("AreaName")
    private String AreaName;
    @SerializedName("AreaLevel")
    private String AreaLevel;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
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
}
