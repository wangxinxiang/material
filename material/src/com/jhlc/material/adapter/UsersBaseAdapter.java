package com.jhlc.material.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.GsonBuilder;
import com.jhlc.material.R;
import com.jhlc.material.ZXLApplication;
import com.jhlc.material.bean.PostBackDataBean;
import com.jhlc.material.bean.Userlist;
import com.jhlc.material.db.SetNewMsgDB;
import com.jhlc.material.db.UserListDB;
import com.jhlc.material.service.LoaderBusiness;
import com.jhlc.material.utils.Constant;
import com.jhlc.material.utils.LogZ;
import com.jhlc.material.utils.MYURL;
import com.jhlc.material.utils.PreferenceUtils;
import com.jhlc.material.utils.StringUtils;
import com.jhlc.material.view.RoundImageView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/10/8.
 */
public class UsersBaseAdapter implements UsersAdapterComment{
    private AdapterCallback myCallback;
    private HolderView     holderView = null;
    private LayoutInflater inflater;
    private Context mContent;
    private boolean isshowdetail  = false;
    private boolean is_delete    = false;
    private int     UpOrDown     = -1;
    private SetNewMsgDB newMsgDB ;
    private UserListDB userListDB;

    private List<Userlist> userlist;

    public UsersBaseAdapter(Context content, List<Userlist> userlist,int UpOrDown) {

        this.userlist = userlist;
        this.mContent = content;
        this.UpOrDown = UpOrDown;
        inflater = (LayoutInflater) content.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        newMsgDB = new SetNewMsgDB(mContent);
        userListDB = new UserListDB(mContent);
    }

    public void setUserlist(List<Userlist> userlist) {
        this.userlist = userlist;
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
    public void notifyDataSetChanged() {

    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        // LogZ.d("UpUserAdapter--> ",""+position);

        if(convertView == null ){
            holderView = new HolderView();
            convertView = inflater.inflate(R.layout.my_users_items, null);

            holderView.ll_detail_info   = (LinearLayout) convertView.findViewById(R.id.ll_detail_info);

            holderView.round_head_image = (RoundImageView) convertView.findViewById(R.id.round_head_image);
            holderView.tv_user_name     = (TextView) convertView.findViewById(R.id.tv_user_name);
            holderView.tv_onexecute_num = (TextView) convertView.findViewById(R.id.tv_onexecute_num);
            holderView.tv_overtime_num  = (TextView) convertView.findViewById(R.id.tv_overtime_num);
            holderView.tv_noreport_num  = (TextView) convertView.findViewById(R.id.tv_noreport_num);
            holderView.img_new_msg      = (ImageView) convertView.findViewById(R.id.img_new_msg);
            holderView.ibtn_delete_users  = (ImageButton) convertView.findViewById(R.id.ibtn_delete_users);
            holderView.ll_inventation_info   = (LinearLayout) convertView.findViewById(R.id.ll_inventation_info);
            holderView.invent_accept_msg   = (TextView) convertView.findViewById(R.id.invent_accept_msg);
            holderView.ibtn_invent_accept   = (Button) convertView.findViewById(R.id.ibtn_invent_accept);

            holderView.apply_num= (TextView) convertView.findViewById(R.id.tv_apply_num);
            holderView.apply= (TextView) convertView.findViewById(R.id.tv_apply);


            convertView.setTag(holderView);
        }else{
            holderView = (HolderView) convertView.getTag();
        }
        //设置头像
        final Userlist user = userlist.get(position);
        if(userlist.size()>0&& user !=null&&!"".equals(user.getHeadimg())){
            LoaderBusiness.loadImage(MYURL.img_HEAD + user.getHeadimg(), holderView.round_head_image);
        }else{
            holderView.round_head_image.setImageDrawable(this.mContent.getResources().getDrawable(R.drawable.defult_user_headimg) );
        }

        holderView.tv_user_name    .setText(user.getName());
        holderView.tv_onexecute_num.setText(user.getOnexecutenum());
        holderView.tv_overtime_num .setText(user.getOvertimenum());
        holderView.tv_noreport_num .setText(user.getNoreportworknum());

        //检查是否应该显示申请完成字段
        if(StringUtils.isNotBlank(user.getApplycompletenum())&&!"0".equals(user.getApplycompletenum())){
            holderView.apply_num.setText(user.getApplycompletenum());
            holderView.apply_num.setVisibility(View.VISIBLE);
            holderView.apply.setVisibility(View.VISIBLE);
        }else{
            holderView.apply_num.setVisibility(View.INVISIBLE);
            holderView.apply.setVisibility(View.INVISIBLE);
        }

        //检查本地数据库是否有新消息
        if(newMsgDB.getByName(user)){
            holderView.img_new_msg.setVisibility(View.VISIBLE);
        } else {
            holderView.img_new_msg.setVisibility(View.GONE);
        }

        //  LogZ.d("getMsgnum--> ",userlist.get(position).getName()+" "+ userlist.get(position).getMsgnum());
//        if("0".equals(userlist.get(position).getMsgnum())){
//            holderView.img_new_msg.setVisibility(View.GONE);
//        } else {
//            holderView.img_new_msg.setVisibility(View.VISIBLE);
//        }

        if(isshowdetail){
            holderView.ll_detail_info.setVisibility(View.VISIBLE);
        } else {
            holderView.ll_detail_info.setVisibility(View.GONE);
        }

        //  删除上下级用户
        if(is_delete){
            holderView.ibtn_delete_users.setVisibility(View.VISIBLE);
        } else {
            holderView.ibtn_delete_users.setVisibility(View.GONE);
        }

        //增加是否需要显示邀请的判定
        if(!user.getIspass().equals("3")){
            holderView.ll_inventation_info.setVisibility(View.VISIBLE);
            holderView.ll_detail_info.setVisibility(View.GONE);
            if(Constant.ZXL_UP_USER == UpOrDown) {
                if ("2".equals(user.getIspass())) {
                    holderView.invent_accept_msg.setVisibility(View.VISIBLE);
                    holderView.ibtn_invent_accept.setVisibility(View.VISIBLE);
                    holderView.invent_accept_msg.setText("请求添加你为下属");
                } else {
                    holderView.invent_accept_msg.setVisibility(View.VISIBLE);
                    holderView.invent_accept_msg.setText("等待验证");
                    holderView.ibtn_invent_accept.setVisibility(View.GONE);
                }
            }
            if(Constant.ZXL_DOWN_USER == UpOrDown) {
                if ("1".equals(user.getIspass())) {
                    holderView.invent_accept_msg.setVisibility(View.VISIBLE);
                    holderView.ibtn_invent_accept.setVisibility(View.VISIBLE);
                    holderView.invent_accept_msg.setText("请求添加您为上级");
                } else {
                    holderView.invent_accept_msg.setVisibility(View.VISIBLE);
                    holderView.invent_accept_msg.setText("等待验证");
                    holderView.ibtn_invent_accept.setVisibility(View.GONE);
                }
            }
        }else{//否则的情况要设置，不然view重用的时候会有显示问题
            holderView.ll_inventation_info.setVisibility(View.GONE);
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
                        deleteUsers(user);
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
        holderView.ibtn_invent_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                acceptInvetation(user.getUsid(),user.getUserid());

            }
        });

        /*BadgeView badge5 = new BadgeView(this, holderView.round_head_image);
        badge5.setText("37");
        badge5.setBackgroundResource(R.drawable.red_dot);
        badge5.setTextSize(16);
        badge5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                badge5.toggle(true);
            }
        });*/

        return convertView;
    }


    private void deleteUsers(final Userlist user ) {
        // 发送请求到服务器
        AsyncHttpClient client = new AsyncHttpClient();
        // 创建请求参数
        RequestParams params = new RequestParams();
        params.put("opcode", "delete_user");
        params.put("Username", PreferenceUtils.getInstance().getUserName());
        params.put("delete_name", user.getName());
        params.put("eid", Constant.EID);
        params.put("typeid", UpOrDown);
        params.put("invite", 1);//任务删除用户
        // 执行post方法
        client.post(MYURL.URL_HEAD, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // 设置值
                LogZ.d("getUpUserData--> ", "typeid" + UpOrDown + "  " + new String(responseBody));

                PostBackDataBean postBackDataBean = new GsonBuilder().create().fromJson(new String(responseBody), PostBackDataBean.class);
                if (postBackDataBean.getCode() == 100) {
                    ZXLApplication.getInstance().showTextToast("删除成功");
                    userListDB.deleteByName(user.getName());

                    myCallback.refreshView();
                } else if (-1 == postBackDataBean.getCode()) {
                    ZXLApplication.getInstance().showTextToast("服务器错误！");
                }
                newMsgDB.delete(user);//删除用户时候要同时删除该用户对应的消息，否则消息提示会出错。
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // 打印错误信息
                error.printStackTrace();

            }

        });
    }

    private void acceptInvetation(final String usid,final String userid) {
        // 发送请求到服务器
        AsyncHttpClient client = new AsyncHttpClient();
        // 创建请求参数
        RequestParams params = new RequestParams();
        params.put("opcode", "agree_relation");
        params.put("Username", PreferenceUtils.getUserName());
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
                LogZ.d("getUpUserData--> acceptInvetation", "typeid"+UpOrDown+"  "+new String(responseBody));

                PostBackDataBean postBackDataBean = new GsonBuilder().create().fromJson(new String(responseBody), PostBackDataBean.class);
                if(postBackDataBean.getCode() == 100){
                    ZXLApplication.getInstance().showTextToast("接受邀请！");
                    newMsgDB.deleteByUserId(userid);
                    // userListDB.deleteByName(deletename);
                    myCallback.refreshView();
                    Intent intent = new Intent(Constant.CancleTabFlagAction);       //消除主界面红点
                    mContent.sendBroadcast(intent);
                } else if(-1 == postBackDataBean.getCode()){
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

    public void setMyCallback(AdapterCallback myCallback) {
        this.myCallback = myCallback;
    }







    class HolderView{
        private LinearLayout ll_detail_info;
        private LinearLayout ll_inventation_info;
        private TextView invent_accept_msg;
        private Button ibtn_invent_accept;
        private RoundImageView round_head_image;
        private TextView tv_user_name;
        private TextView tv_onexecute_num,tv_overtime_num,tv_noreport_num;
        private ImageButton ibtn_delete_users;
        private ImageView img_new_msg;
        private TextView apply_num;
        private TextView apply;
    }

}
