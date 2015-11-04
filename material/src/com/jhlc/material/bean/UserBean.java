package com.jhlc.material.bean;

import com.google.gson.annotations.SerializedName;

/**
 "code": "100",
 "msg": "success",
 "opcode": "login_success",
 "upuserlist": []
 */
public class UserBean extends PostBackDataBean {
    @SerializedName("username")
    private String username;
    @SerializedName("upuserlist")
    private Userlist[] userlists;
    @SerializedName("downuserlist")
    private DownUserList[] downuserlist;
    @SerializedName("list")
    private Paymentlist[] paymentlists;
    @SerializedName("version")
    private String version;
    @SerializedName("androiddownload")
    private String download;
    @SerializedName("pendingrelation")
    private Pendingrelation[] pendingrelation;



    public Paymentlist[] getPaymentlists() {
        return paymentlists;
    }

    public void setPaymentlists(Paymentlist[] paymentlists) {
        this.paymentlists = paymentlists;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Userlist[] getUserlists() {
        return userlists;
    }

    public void setUserlists(Userlist[] userlists) {
        this.userlists = userlists;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDownload() {
        return download;
    }

    public void setDownload(String download) {
        this.download = download;
    }

    public Pendingrelation[] getPendingrelation() {
        return pendingrelation;
    }

    public void setPendingrelation(Pendingrelation[] pendingrelation) {
        this.pendingrelation = pendingrelation;
    }

    public DownUserList[] getDownuserlist() {
        return downuserlist;
    }

    public void setDownuserlist(DownUserList[] downuserlist) {
        this.downuserlist = downuserlist;
    }
}
