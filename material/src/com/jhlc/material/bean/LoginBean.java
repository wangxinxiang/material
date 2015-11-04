package com.jhlc.material.bean;

import com.google.gson.annotations.SerializedName;

/**
 {"code":100,"msg":"success","opcode":"reguser_eid_success","headimg":"/UserImage/201501121039071119.JPG","id":"4b4e14e4-d0b7-4e3a-b129-2b61cb567494"}
 */
public class LoginBean extends PostBackDataBean {
    @SerializedName("headimg")
    private String headimg;
    @SerializedName("id")
    private String id;


    @Override
    public String getHeadimg() {
        return headimg;
    }

    @Override
    public void setHeadimg(String headimg) {
        this.headimg = headimg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
