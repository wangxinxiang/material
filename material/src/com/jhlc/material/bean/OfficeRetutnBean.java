package com.jhlc.material.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by 104468 on 2015/3/12.
 */
public class OfficeRetutnBean {
    @SerializedName("list")
    private List<OfficeBean> list;
    @SerializedName("code")
    private String code;
    @SerializedName("msg")
    private String msg;
    @SerializedName("opcode")
    private String opcode;

    @SerializedName("arealist")
    private List<OfficeAreaBean> areaBeanList;


    public List<OfficeAreaBean> getAreaBeanList() {
        return areaBeanList;
    }

    public void setAreaBeanList(List<OfficeAreaBean> areaBeanList) {
        this.areaBeanList = areaBeanList;
    }

    public List<OfficeBean> getList() {
        return list;
    }

    public void setList(List<OfficeBean> list) {
        this.list = list;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getOpcode() {
        return opcode;
    }

    public void setOpcode(String opcode) {
        this.opcode = opcode;
    }


}
