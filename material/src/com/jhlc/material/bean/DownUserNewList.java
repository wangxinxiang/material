package com.jhlc.material.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2015/10/23.
 * 下级新信息
 */
public class DownUserNewList {
    @SerializedName("departmentname")
    private String departmentname;
    @SerializedName("personinfor")
    private UserNewMsgList[] personinfor;

    public String getDepartmentname() {
        return departmentname;
    }

    public void setDepartmentname(String departmentname) {
        this.departmentname = departmentname;
    }

    public UserNewMsgList[] getPersoninfor() {
        return personinfor;
    }

    public void setPersoninfor(UserNewMsgList[] personinfor) {
        this.personinfor = personinfor;
    }
}
