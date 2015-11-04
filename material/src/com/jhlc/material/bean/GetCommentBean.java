package com.jhlc.material.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2015/10/21.
 */
public class GetCommentBean {
    @SerializedName("username")
    private String username;
    @SerializedName("commentcontent")
    private String commentcontent;
    @SerializedName("timedes")
    private String timedes;

    public void setUsername(String username){
        this.username = username;
    }
    public String getUsername(){
        return this.username;
    }
    public void setCommentcontent(String commentcontent){
        this.commentcontent = commentcontent;
    }
    public String getCommentcontent(){
        return this.commentcontent;
    }
    public void setTimedes(String timedes){
        this.timedes = timedes;
    }
    public String getTimedes(){
        return this.timedes;
    }
}
