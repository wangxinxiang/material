package com.jhlc.material.bean;

import com.google.gson.annotations.SerializedName;

/**
 */
public class NoteBookServerKV {
    @SerializedName("key")
    private String key;
    @SerializedName("value")
    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
