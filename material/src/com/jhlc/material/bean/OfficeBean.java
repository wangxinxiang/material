package com.jhlc.material.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by 104468 on 2015/3/12.
 */
public class OfficeBean {
    @SerializedName("officename")
    private String officename;

    @SerializedName("list")
    private List<OfficeUserBean> list;

    public OfficeBean(String officename, List<OfficeUserBean> list) {
        this.officename = officename;
        this.list = list;
    }

    public OfficeBean() {
    }

    public void setOfficename(String officename) {
        this.officename = officename;
    }

    public String getOfficename() {
        return officename;
    }

    public List<OfficeUserBean> getList() {
        return list;
    }

    public void setList(List<OfficeUserBean> list) {
        this.list = list;
    }

}
