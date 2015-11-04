package com.jhlc.material.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by licheng on 28/4/15.
 */
public class OfficeAreaBean {
    @SerializedName("ID")
    private String ID;
    @SerializedName("AreaName")
    private String AreaName;

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
}
