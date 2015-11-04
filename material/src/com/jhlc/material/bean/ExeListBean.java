package com.jhlc.material.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2015/9/14 0014.
 */
public class ExeListBean extends BaseBean {
    @SerializedName("AllAreaWork")
    private ExeItemBean[] AllAreaWork;

    public ExeItemBean[] getAllAreaWork() {
        return AllAreaWork;
    }

    public void setAllAreaWork(ExeItemBean[] allAreaWork) {
        AllAreaWork = allAreaWork;
    }
}
