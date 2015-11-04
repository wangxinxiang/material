package com.jhlc.material.bean;

import com.google.gson.annotations.SerializedName;

/**
 {"code":"100","msg":"success","opcode":"getnewreportmessage_success","list":[{"reportmessage":"\u0073\u0061\u0064\u0066\u0061\u0073\u0066\u004f\u004b","reporttime":"2015/1/13 23:26:36","reporttypeid":"1","bigimage":"","smallimage":""}]}
 */
public class NewReportMessage  extends PostBackDataBean {
    @SerializedName("list")
    private WorkDetailList[] list;

    public WorkDetailList[] getList() {
        return list;
    }

    public void setList(WorkDetailList[] list) {
        this.list = list;
    }
}