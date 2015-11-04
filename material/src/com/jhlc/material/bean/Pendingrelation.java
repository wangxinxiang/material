package com.jhlc.material.bean;

import com.google.gson.annotations.SerializedName;

/**
 "pendingrelation": [
 {
 "usid": "4f185ded-1650-4c68-aa53-030145c1bc80",
 "IsPass": "1",
 "username": "\u9876\u9876\u9876",
 "userid": "db521c6e-ca69-45e8-902e-e0d16d4292fd",
 "HeadImg": ""
 },
 {
 "usid": "d1dc562a-b112-465f-94ec-e55fdaceb447",
 "IsPass": "1",
 "username": "\u9648\u6210",
 "userid": "c8fc9a6a-7e7b-4a3a-a359-e0923a5711b4",
 "HeadImg": ""
 },
 {
 "usid": "6f3d1a15-dcfc-45d8-9ca7-55d163d3f154",
 "IsPass": "1",
 "username": "\u6d4b\u8bd5\u4e0a\u7ea7",
 "userid": "50c30fbd-bb0e-4237-b890-f080b123e3e6",
 "HeadImg": ""
 }
 ]
 */
public class Pendingrelation {
    @SerializedName("usid")
    private String usid;
    @SerializedName("IsPass")
    private String IsPass;
    @SerializedName("username")
    private String username;
    @SerializedName("userid")
    private String userid;
    @SerializedName("HeadImg")
    private String HeadImg;
    @SerializedName("upordown")
    private String upordown;

    public String getUsid() {
        return usid;
    }

    public void setUsid(String usid) {
        this.usid = usid;
    }

    public String getIsPass() {
        return IsPass;
    }

    public void setIsPass(String isPass) {
        IsPass = isPass;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getHeadImg() {
        return HeadImg;
    }

    public void setHeadImg(String headImg) {
        HeadImg = headImg;
    }

    public String getUpordown() {
        return upordown;
    }

    public void setUpordown(String upordown) {
        this.upordown = upordown;
    }
}
