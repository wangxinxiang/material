package com.jhlc.material.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

//处室任务的bean
public class DepById extends BaseBean{
    @SerializedName("GetAllOfficeworkByDepartment")
    private List<DepTaskListByClass> GetAllOfficeworkByDepartment;

    public List<DepTaskListByClass> getGetAllOfficeworkByDepartment() {
        return GetAllOfficeworkByDepartment;
    }

    public void setGetAllOfficeworkByDepartment(List<DepTaskListByClass> getAllOfficeworkByDepartment) {
        GetAllOfficeworkByDepartment = getAllOfficeworkByDepartment;
    }
}
