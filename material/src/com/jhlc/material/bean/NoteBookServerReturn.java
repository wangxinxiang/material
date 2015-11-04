package com.jhlc.material.bean;

import com.google.gson.annotations.SerializedName;

/**
 */
public class NoteBookServerReturn extends PostBackDataBean{
    @SerializedName("list")
    private NoteBookServerKV[] list;


    public NoteBookServerKV[] getList() {
        return list;
    }

    public void setList(NoteBookServerKV[] list) {
        this.list = list;
    }
}
