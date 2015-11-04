package com.jhlc.material.bean;

import com.google.gson.annotations.SerializedName;

//各项的任务 item项 包括完成状态和标题
public class DepTaskItem {
    @SerializedName("Message")
    private String Message;

    @SerializedName("IsComplate")
    private String IsComplate;

    @SerializedName("office_name")
    private String office_name;

    @SerializedName("id")
    private String id;

    public DepTaskItem() {
        super();
    }

    //构造方法 用于构造一个空的数据 作为提示内容
    public DepTaskItem(String message, String isComplate, String office_name, String id) {
        this.Message = message;
        this.IsComplate = isComplate;
        this.office_name = office_name;
        this.id = id;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getIsComplate() {
        return IsComplate;
    }

    public void setIsComplate(String isComplate) {
        IsComplate = isComplate;
    }

    public String getOffice_name() {
        return office_name;
    }

    public void setOffice_name(String office_name) {
        this.office_name = office_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
