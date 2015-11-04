package com.jhlc.material.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2015/10/20.
 * {
 "departmentname": "副调研员 ",
 "personnum": "2",
 "personinfor": [
 {
 "name": "洪盈盈",
 "headimg": "",
 "msgnum": "0",
 "onexecutenum": "0",
 "overtimenum": "0",
 "normalworkoknum": "0",
 "overtimeworkoknum": "0",
 "noreportworknum": "0",
 "worknum": "0",
 "userid": "62d2924d-5d36-41dd-bfa6-c9c8249737d8",
 "ispass": "3",
 "usid": "",
 "applycompletenum": "0",
 "dayreportnum": "0"
 }
 */
public class DownUserList {

    @SerializedName("departmentname")
    private String departmentname;
    @SerializedName("personnum")
    private String personnum;
    @SerializedName("personinfor")
    private Userlist[] personinfor;

    public String getDepartmentname() {
        return departmentname;
    }

    public void setDepartmentname(String departmentname) {
        this.departmentname = departmentname;
    }

    public String getPersonnum() {
        return personnum;
    }

    public void setPersonnum(String personnum) {
        this.personnum = personnum;
    }

    public Userlist[] getPersoninfor() {
        return personinfor;
    }

    public void setPersoninfor(Userlist[] personinfor) {
        this.personinfor = personinfor;
    }
}
