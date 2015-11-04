package com.jhlc.material.bean;

import com.google.gson.annotations.SerializedName;

/**
 "code": "100",
 "msg": "success",
 "opcode": "get_expense_success",
 "name": "5001",
 "headimg": "/UserImage/201411030800185759.JPG",
 "limit": "1000.00",
 "used": "0.00",
 */
public class PaymentDetaiBean extends PostBackDataBean {
    @SerializedName("name")
    private String name;
    @SerializedName("limit")
    private String limit;
    @SerializedName("used")
    private String used;
    @SerializedName("currTime")
    private String currTime;
    @SerializedName("list")
    private PaymentDetailList[] paymentDetailLists;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public String getUsed() {
        return used;
    }

    public void setUsed(String used) {
        this.used = used;
    }

    public String getCurrTime() {
        return currTime;
    }

    public void setCurrTime(String currTime) {
        this.currTime = currTime;
    }

    public PaymentDetailList[] getPaymentDetailLists() {
        return paymentDetailLists;
    }

    public void setPaymentDetailLists(PaymentDetailList[] paymentDetailLists) {
        this.paymentDetailLists = paymentDetailLists;
    }
}
