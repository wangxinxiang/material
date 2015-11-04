package com.jhlc.material.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 104468 on 2014/12/7.
 * 用于增量更新备忘录的bean
 */
public class NoteBookUpdate {
    @SerializedName("msg")
    private String msg;
    @SerializedName("time")
    private String time;
    @SerializedName("type")
    private String type;
    @SerializedName("top")
    private String top;
    @SerializedName("localid")
    private String localid ;
    @SerializedName("opcode")
    private String opcode ;
    @SerializedName("mid")
    private String mid ;
    @SerializedName("overtime")
    private String overtime ;


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTop() {
        return top;
    }

    public void setTop(String top) {
        this.top = top;
    }

    public String getLocalid() {
        return localid;
    }

    public void setLocalid(String localid) {
        this.localid = localid;
    }

    public String getOpcode() {
        return opcode;
    }

    public void setOpcode(String opcode) {
        this.opcode = opcode;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getOvertime() {
        return overtime;
    }

    public void setOvertime(String overtime) {
        this.overtime = overtime;
    }
}
