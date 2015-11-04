package com.jhlc.material.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2015/9/23 0023.
 */
public class SupBean extends BaseBean {
    @SerializedName("log")
    private SupFullAndNot log;

    public SupFullAndNot getLog() {
        return log;
    }

    public void setLog(SupFullAndNot log) {
        this.log = log;
    }
}
