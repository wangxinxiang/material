package com.jhlc.material.adapter;

/**
 * Created by Administrator on 2015/10/13.
 * 上下级item共同的方法
 */
public interface UsersAdapterComment {
    void setIsshowdetail(boolean isshowdetail);
    boolean isIsshowdetail();
    void setIs_delete(boolean is_delete);
    boolean getIs_delete();
    void notifyDataSetChanged();            //adapter的
    void setMyCallback(AdapterCallback myCallback);     //对邀请结果处理后的回调
}
