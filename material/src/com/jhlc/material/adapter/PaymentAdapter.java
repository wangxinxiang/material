package com.jhlc.material.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.gson.GsonBuilder;
import com.jhlc.material.R;
import com.jhlc.material.ZXLApplication;
import com.jhlc.material.bean.Paymentlist;
import com.jhlc.material.bean.PostBackDataBean;
import com.jhlc.material.bean.Userlist;
import com.jhlc.material.db.SetNewMsgDB;
import com.jhlc.material.db.UserListDB;
import com.jhlc.material.service.LoaderBusiness;
import com.jhlc.material.utils.Constant;
import com.jhlc.material.utils.LogZ;
import com.jhlc.material.utils.MYURL;
import com.jhlc.material.utils.PreferenceUtils;
import com.jhlc.material.view.RoundImageView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;

import java.util.ArrayList;

public class PaymentAdapter extends BaseAdapter {
    private MyCallback myCallback;
    private HolderView holderView = null;
    private LayoutInflater inflater;
    private Context mContent;
    private boolean isshowdetail = false;
    private boolean is_delete = false;
    private int UpOrDown = -1;
    private SetNewMsgDB newMsgDB;
    private UserListDB userListDB;

    private ArrayList<Paymentlist> paymentlists = new ArrayList<Paymentlist>();

    public PaymentAdapter(Context content, ArrayList<Paymentlist> paymentlists, int UpOrDown) {
        this.paymentlists = paymentlists;
        this.mContent = content;
        this.UpOrDown = UpOrDown;
        inflater = (LayoutInflater) content.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        newMsgDB = new SetNewMsgDB(mContent);
        userListDB = new UserListDB(mContent);
    }

    @Override
    public int getCount() {
        return paymentlists.size();
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
        this.isshowdetail = isshowdetail;
    }

    public boolean isIsshowdetail() {
        return isshowdetail;
    }

    public void setIs_delete(boolean is_delete) {
        this.is_delete = is_delete;
    }

    public boolean getIs_delete() {
        return is_delete;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LogZ.d("UpUserAdapter--> ", "" + position);

        if (convertView == null) {
            holderView = new HolderView();
            convertView = inflater.inflate(R.layout.payment_users_items, null);

            holderView.ll_payment_info = (LinearLayout) convertView.findViewById(R.id.ll_payment_info);

            holderView.tv_all_money = (TextView) convertView.findViewById(R.id.tv_all_money);

            holderView.round_head_image = (RoundImageView) convertView.findViewById(R.id.round_head_image);
            holderView.tv_user_name = (TextView) convertView.findViewById(R.id.tv_user_name);
            holderView.img_new_msg = (ImageView) convertView.findViewById(R.id.img_new_msg);
            holderView.ibtn_delete_users = (ImageButton) convertView.findViewById(R.id.ibtn_delete_users);

            //邀请相关的操作
            holderView.ll_inventation_info = (RelativeLayout) convertView.findViewById(R.id.ll_inventation_info);
            holderView.invent_accept_msg = (TextView) convertView.findViewById(R.id.invent_accept_msg);
            holderView.ibtn_invent_accept = (Button) convertView.findViewById(R.id.ibtn_invent_accept);


            convertView.setTag(holderView);
        } else {
            holderView = (HolderView) convertView.getTag();
        }

        final Paymentlist paymentlist = paymentlists.get(position);
        LoaderBusiness.loadImage(MYURL.URL_HEAD + paymentlist.getHeadImg(), holderView.round_head_image);
        holderView.tv_user_name.setText(paymentlist.getUsername());
        holderView.tv_all_money.setText(paymentlist.getSurplusExpense());

        if (isshowdetail) {
            holderView.ll_payment_info.setVisibility(View.VISIBLE);
        } else {
            holderView.ll_payment_info.setVisibility(View.GONE);
        }

        if ("3".equals(paymentlist.getIsPass())) {
            if ("0".equals(paymentlist.getNoreadcount())) {
                holderView.img_new_msg.setVisibility(View.GONE);
            } else {
                holderView.img_new_msg.setVisibility(View.VISIBLE);
            }
        }else if ("1".equals(paymentlist.getIsPass())){
            holderView.img_new_msg.setVisibility(View.GONE);

        }else{
            holderView.img_new_msg.setVisibility(View.VISIBLE);
        }

        //  删除上下级用户
        if (is_delete) {
            holderView.ibtn_delete_users.setVisibility(View.VISIBLE);
        } else {
            holderView.ibtn_delete_users.setVisibility(View.GONE);
        }
        holderView.ibtn_delete_users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContent);
                builder.setMessage("确认删除该用户吗？");
                builder.setTitle("提示");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // pd = ProgressDialog.show(SettingActivity.this, "正在提交建议,请稍后...", "请等待", true);
                        deleteUsers(paymentlist.getUsername(),paymentlist.getUserid());
                        dialog.dismiss();

                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });

        //增加是否需要显示邀请的判定
        if (!paymentlist.getIsPass().equals("3")) {
            holderView.ll_inventation_info.setVisibility(View.VISIBLE);
            holderView.ll_payment_info.setVisibility(View.GONE);
            if (Constant.ZXL_UP_USER == UpOrDown) {
                if ("2".equals(paymentlist.getIsPass())) {
                    holderView.invent_accept_msg.setVisibility(View.VISIBLE);
                    holderView.ibtn_invent_accept.setVisibility(View.VISIBLE);
                    holderView.invent_accept_msg.setText("请求添加你为下属");
                } else {
                    holderView.invent_accept_msg.setVisibility(View.VISIBLE);
                    holderView.invent_accept_msg.setText("等待验证");
                    holderView.ibtn_invent_accept.setVisibility(View.GONE);
                }
            }
            if (Constant.ZXL_DOWN_USER == UpOrDown) {
                if ("1".equals(paymentlist.getIsPass())) {
                    holderView.invent_accept_msg.setVisibility(View.VISIBLE);
                    holderView.ibtn_invent_accept.setVisibility(View.VISIBLE);
                    holderView.invent_accept_msg.setText("请求添加您为上级");
                } else {
                    holderView.invent_accept_msg.setVisibility(View.VISIBLE);
                    holderView.invent_accept_msg.setText("等待验证");
                    holderView.ibtn_invent_accept.setVisibility(View.GONE);
                }
            }
        } else {//否则的情况要设置，不然view重用的时候会有显示问题
            holderView.ll_inventation_info.setVisibility(View.GONE);
        }

        holderView.ibtn_invent_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                acceptInvetation(paymentlist.getUsid(),paymentlist.getUserid(),paymentlist.getUsername());

            }
        });

        return convertView;
    }

    private void acceptInvetation(final String usid, final String userid,final String username) {
        // 发送请求到服务器
        AsyncHttpClient client = new AsyncHttpClient();
        // 创建请求参数
        RequestParams params = new RequestParams();
        params.put("opcode", "agree_relation");
        params.put("Username", PreferenceUtils.getInstance().getUserName());
        params.put("usid", usid);
        params.put("eid", Constant.EID);
        // 执行post方法
        client.post(MYURL.URL_HEAD, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // 设置值
                LogZ.d("getUpUserData--> acceptInvetation", "typeid" + UpOrDown + "  " + new String(responseBody));

                PostBackDataBean postBackDataBean = new GsonBuilder().create().fromJson(new String(responseBody), PostBackDataBean.class);
                if (postBackDataBean.getCode() == 100) {
                    ZXLApplication.getInstance().showTextToast("接受邀请！");
                    // userListDB.deleteByName(deletename);
                    Userlist temp=new Userlist();
                    temp.setName(username);
                    temp.setUserid(userid);
                    //接收成功后删除消息
                    newMsgDB.delete(temp);
                    myCallback.refreshView();
                } else if (-1 == postBackDataBean.getCode()) {
                    ZXLApplication.getInstance().showTextToast("服务器错误！");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // 打印错误信息
                error.printStackTrace();

            }

        });
    }

    class HolderView {
        private LinearLayout ll_payment_info;
        private RoundImageView round_head_image;
        private TextView tv_user_name;
        private ImageButton ibtn_delete_users;
        private ImageView img_new_msg;
        private TextView tv_all_money;

        private RelativeLayout ll_inventation_info;
        private TextView invent_accept_msg;
        private Button ibtn_invent_accept;
    }

    private void deleteUsers(final String deletename,final String userId) {
        // 发送请求到服务器
        AsyncHttpClient client = new AsyncHttpClient();
        // 创建请求参数
        RequestParams params = new RequestParams();
        params.put("opcode", "delete_user");
        params.put("Username", PreferenceUtils.getInstance().getUserName());
        params.put("delete_name", deletename);
        params.put("eid", Constant.EID);
        params.put("typeid", UpOrDown);
        params.put("invite", 2);//报销删除用户

        // 执行post方法
        client.post(MYURL.URL_HEAD, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // 设置值
                LogZ.d("getUpUserData--> ", "typeid" + UpOrDown + "  " + new String(responseBody));

                PostBackDataBean postBackDataBean = new GsonBuilder().create().fromJson(new String(responseBody), PostBackDataBean.class);
                if (postBackDataBean.getCode() == 100) {
                    ZXLApplication.getInstance().showTextToast("删除成功");
                    userListDB.deleteByName(deletename);
                    Userlist temp=   new Userlist();
                    temp.setName(deletename);
                    temp.setUserid(userId);
                    newMsgDB.delete(temp);
                    myCallback.refreshView();
                } else if (-1 == postBackDataBean.getCode()) {
                    ZXLApplication.getInstance().showTextToast("服务器错误！");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // 打印错误信息
                error.printStackTrace();

            }

        });
    }

    public void setMyCallback(MyCallback myCallback) {
        this.myCallback = myCallback;
    }

    public interface MyCallback {
        void refreshView();
    }

}
