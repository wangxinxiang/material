package com.jhlc.material.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2015/9/23 0023.
 */
public class SupFullAndNot  {
    @SerializedName("NotFullLog")
    private List<SupLogByOffice> NotFullLog;

    @SerializedName("Fulllog")
    private List<SupLogByOffice> Fulllog;

    public List<SupLogByOffice> getNotFullLog() {
        return NotFullLog;
    }

    public void setNotFullLog(List<SupLogByOffice> notFullLog) {
        NotFullLog = notFullLog;
    }

    public List<SupLogByOffice> getFulllog() {
        return Fulllog;
    }

    public void setFulllog(List<SupLogByOffice> fulllog) {
        Fulllog = fulllog;
    }
}
