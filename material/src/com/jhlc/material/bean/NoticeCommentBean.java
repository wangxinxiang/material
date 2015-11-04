package com.jhlc.material.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2015/10/21.
 */
public class NoticeCommentBean {
    @SerializedName("code")
    private String code;
    @SerializedName("msg")
    private String msg;
    @SerializedName("opcode")
    private String opcode;
    @SerializedName("status")
    private String status;
    @SerializedName("GetComment")
    private GetCommentBean[] GetComments ;
    @SerializedName("GetNoticeComment")
    private GetCommentBean[] GetNoticeComment ;

    public void setCode(String code){
        this.code = code;
    }
    public String getCode(){
        return this.code;
    }
    public void setMsg(String msg){
        this.msg = msg;
    }
    public String getMsg(){
        return this.msg;
    }
    public void setOpcode(String opcode){
        this.opcode = opcode;
    }
    public String getOpcode(){
        return this.opcode;
    }
    public void setStatus(String status){
        this.status = status;
    }
    public String getStatus(){
        return this.status;
    }

    public GetCommentBean[] getGetComments() {
        return GetComments;
    }

    public void setGetComments(GetCommentBean[] getComments) {
        GetComments = getComments;
    }

    public GetCommentBean[] getGetNoticeComment() {
        return GetNoticeComment;
    }

    public void setGetNoticeComment(GetCommentBean[] getNoticeComment) {
        GetNoticeComment = getNoticeComment;
    }
}
