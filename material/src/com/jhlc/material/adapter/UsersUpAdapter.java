package com.jhlc.material.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.gson.GsonBuilder;
import com.jhlc.material.ZXLApplication;
import com.jhlc.material.bean.PostBackDataBean;
import com.jhlc.material.bean.Userlist;
import com.jhlc.material.db.SetNewMsgDB;
import com.jhlc.material.db.UserListDB;
import com.jhlc.material.service.LoaderBusiness;
import com.jhlc.material.utils.*;
import com.jhlc.material.view.RoundImageView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

public class UsersUpAdapter extends BaseAdapter implements UsersAdapterComment{
    private Context        mContent;
    private UsersBaseAdapter usersBaseAdapter;
    private List<Userlist> userlist = new ArrayList<Userlist>();

    public UsersUpAdapter(Context content, List<Userlist> userlist) {

        this.userlist = userlist;
        this.mContent = content;
        usersBaseAdapter = new UsersBaseAdapter(mContent, userlist, 1);
    }

    @Override
    public int getCount() {
        return userlist.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setIsshowdetail(boolean isshowdetail) {
        usersBaseAdapter.setIsshowdetail(isshowdetail);
    }

    public boolean isIsshowdetail() {
        return usersBaseAdapter.isIsshowdetail();
    }

    public void setIs_delete(boolean is_delete) {
        usersBaseAdapter.setIs_delete(is_delete);
    }

    public boolean getIs_delete() {
        return usersBaseAdapter.getIs_delete();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LogZ.d("UpUserAdapter--> ",""+position);
        return usersBaseAdapter.getView(position, convertView, parent);
    }

    public void setMyCallback(AdapterCallback myCallback) {
        usersBaseAdapter.setMyCallback(myCallback);
    }
}
