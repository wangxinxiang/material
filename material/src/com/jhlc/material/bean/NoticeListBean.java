package com.jhlc.material.bean;

import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/10/21.
 */
public class NoticeListBean implements Serializable{
    @SerializedName("title")
    private String title;
    @SerializedName("Addtime")
    private String Addtime;
    @SerializedName("content")
    private String content;
    @SerializedName("noticeid")
    private String noticeid;

    public void setTitle(String title){
        this.title = title;
    }
    public String getTitle(){
        return this.title;
    }
    public void setAddtime(String Addtime){
        this.Addtime = Addtime;
    }
    public String getAddtime(){
        return this.Addtime;
    }
    public void setContent(String content){
        this.content = content;
    }
    public String getContent(){
        return this.content;
    }
    public void setNoticeid(String noticeid){
        this.noticeid = noticeid;
    }
    public String getNoticeid(){
        return this.noticeid;
    }

}
