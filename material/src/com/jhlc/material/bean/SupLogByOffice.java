package com.jhlc.material.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2015/9/23 0023.
 */
public class SupLogByOffice {
    @SerializedName("officename")
    private String officename;

    @SerializedName("officeperson")
    private List<SupItem> officeperson;

    public String getOfficename() {
        return officename;
    }

    public void setOfficename(String officename) {
        this.officename = officename;
    }

    public List<SupItem> getOfficeperson() {
        return officeperson;
    }

    public void setOfficeperson(List<SupItem> officeperson) {
        this.officeperson = officeperson;
    }
}
