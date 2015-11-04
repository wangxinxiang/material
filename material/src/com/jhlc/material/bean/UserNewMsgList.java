package com.jhlc.material.bean;

import com.google.gson.annotations.SerializedName;

/**
 {"username":"3000","noreadcount":"1"}
 */
public class UserNewMsgList {
    @SerializedName("username")
    private String username;
    @SerializedName("noreadcount")
    private String    noreadcount;
    @SerializedName("id")
    private String    id;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNoreadcount() {
        return noreadcount;
    }

    public void setNoreadcount(String noreadcount) {
        this.noreadcount = noreadcount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
