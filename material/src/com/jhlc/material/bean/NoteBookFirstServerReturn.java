package com.jhlc.material.bean;

import com.google.gson.annotations.SerializedName;

/**
 * 第一次登录时从服务器获得最新的备忘录，只获得一次
 */
public class NoteBookFirstServerReturn extends PostBackDataBean{
    @SerializedName("dt")
    private String dt;

    @SerializedName("recordCount")
    private int recordCount;

    @SerializedName("pageSize")
    private int pageSize;

    @SerializedName("pageIndex")
    private int pageIndex;

    @SerializedName("list")
    private NoteBookServerMemo[] list;

    public NoteBookServerMemo[] getList() {
        return list;
    }

    public void setList(NoteBookServerMemo[] list) {
        this.list = list;
    }

    public String getDt() {
        return dt;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }
}
