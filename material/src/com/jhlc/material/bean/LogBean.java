package com.jhlc.material.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by 104468 on 2015/3/12.
 */
public class LogBean extends BaseBean {
    @SerializedName("list")
    private List<OfficeBean> list;

    public List<OfficeBean> getList() {
        return list;
    }

    public void setList(List<OfficeBean> list) {
        this.list = list;
    }
}
