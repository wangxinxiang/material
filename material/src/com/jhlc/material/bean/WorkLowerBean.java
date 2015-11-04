package com.jhlc.material.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2015/10/24.
 */
public class WorkLowerBean {
    @SerializedName("departmentname")
    private String office_name;
    @SerializedName("personinfor")
    private WorkLowerPersonInfor[] userid;

    public String getOffice_name() {
        return office_name;
    }

    public void setOffice_name(String office_name) {
        this.office_name = office_name;
    }

    public WorkLowerPersonInfor[] getUserid() {
        return userid;
    }

    public void setUserid(WorkLowerPersonInfor[] userid) {
        this.userid = userid;
    }
}
