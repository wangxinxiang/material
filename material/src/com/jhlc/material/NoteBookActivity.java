package com.jhlc.material;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.google.gson.GsonBuilder;
import com.jhlc.material.adapter.NoteBookAdapter;
import com.jhlc.material.bean.NoteBookBean;
import com.jhlc.material.bean.NoteBookUpdate;
import com.jhlc.material.db.NoteBookDB;
import com.jhlc.material.service.LoaderBusiness;
import com.jhlc.material.utils.*;
import com.jhlc.material.view.RoundImageView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NoteBookActivity extends Activity {
    private TextView        tv_user_name;
    private RoundImageView  img_head_photo;
    private NoteBookAdapter noteBookAdapter;
    private ListView        ls_notebook;
    private NoteBookDB      noteBookDB = new NoteBookDB(this);
    private static String   path = ImageUtils.path;//sd路径
    private ArrayList<NoteBookBean> noteBookBeans = new ArrayList<NoteBookBean>();

    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_tab_notebook);
        bindData();

        //testupdate
        setContentList();

    }

    private void bindData() {
        tv_user_name = (TextView) findViewById(R.id.tv_user_name);

        img_head_photo = (RoundImageView) findViewById(R.id.img_head_photo);
        img_head_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                  startActivity(new Intent(NoteBookActivity.this, SettingActivity.class));
              }

        });

    }


    // 增量更新
    private void autoupdate() {
        ArrayList<NoteBookBean> result= noteBookDB.findNeedUpdate();


        String test=getMemos(result, PreferenceUtils.getInstance().getUserName(), Constant.EID);
        ZXLApplication.getInstance().showTextToast("找到需要更新的记录："+result.size()+"json:"+test);
        /*AsyncHttpClient client = new AsyncHttpClient();




        RequestParams params = new RequestParams();
        try {
            //String m_szAndroidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            params.put("opcode", "multi_add_memos");
            String userName = PreferenceUtils.getInstance().getUserName();
            params.put("Username", userName);
            params.put("eid", Constant.EID);
            params.put("memos", getMemos(result, userName, Constant.EID));

        } catch (Exception e) {
            e.printStackTrace();
        }

        client.post(MYURL.URL_HEAD, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                pd = ProgressDialog.show(NoteBookActivity.this, "正在提交建议,请稍后...", "请等待", true);

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                LogZ.d("upLoadImage--> ", new String(responseBody));
                //{"code":100,"msg":"success","opcode":"reguser_eid_success","headimg":"/UserImage/201411080338542207.JPG"}

                PostBackDataBean postBackDataBean = new GsonBuilder().create().fromJson(new String(responseBody), PostBackDataBean.class);





            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {


            }
        });*/
    }

    private String getMemos(ArrayList<NoteBookBean> result, String userName, String eid) {
        StringBuilder sb=new StringBuilder();
        sb.append("{\"username\":\"").append(userName).append("\",\"eid\":\"").append(eid).append("\",\"list\":").append(getUpdateNoteBookMemos(result)).append("}");
        return sb.toString();
    }

    private String getUpdateNoteBookMemos(ArrayList<NoteBookBean> result) {
        List<NoteBookUpdate> noteBookUpdateList=changeDBToBean(result);
        String temp=new GsonBuilder().create().toJson(noteBookUpdateList);
        LogZ.d("lyjtest","转换得到的json："+temp);
        return temp;
    }

    private List<NoteBookUpdate> changeDBToBean(ArrayList<NoteBookBean> result) {
        List<NoteBookUpdate> noteBookUpdateList=new ArrayList<NoteBookUpdate>();
        for (Iterator<NoteBookBean> iterator = result.iterator(); iterator.hasNext(); ) {
            NoteBookBean db = iterator.next();
            noteBookUpdateList.add(changedB(db));
        }
        return noteBookUpdateList;
    }

    private NoteBookUpdate changedB(NoteBookBean db) {
        NoteBookUpdate dbu=new NoteBookUpdate();
        dbu.setMid(db.getMid());
        dbu.setLocalid(String.valueOf(db.getId()));
        try {
            dbu.setMsg(URLEncoder.encode(db.getTitle(),"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        dbu.setOpcode(db.getOpcode());
        dbu.setTime(db.getDate());
        dbu.setType(getServerType(db.getType()));
        return dbu;
    }

    private String getServerType(String type) {
       if("0".equals(type)){
           return "today";
       }else if("1".equals(type)){
           return "tomorrow";
       }else if("2".equals(type)){
           return "recent";
       }else if("3".equals(type)){
           return "later";
       }else{
           return "later";
       }
    }


    private void setContentList() {
        noteBookBeans = noteBookDB.getNoteBook(0);
        LogZ.d("UpUserAdapter--> ",""+noteBookBeans.size());

        noteBookAdapter = new NoteBookAdapter(noteBookBeans,this);
        ls_notebook = (ListView)findViewById(R.id.ls_notebook);
        ls_notebook.setAdapter(noteBookAdapter);
        autoupdate();

    }

    private void setUserInfo() {
        try {
            LogZ.d("getIsLogin--> ",""+PreferenceUtils.getInstance().getIsLogin());
            LoaderBusiness.loadImage(MYURL.URL_HEAD + PreferenceUtils.getInstance().getHeadImage(), img_head_photo);

            if(PreferenceUtils.getInstance().getIsLogin()) {
                tv_user_name.setText(PreferenceUtils.getInstance().getUserName());
                LogZ.d("getUserName--> ",""+PreferenceUtils.getInstance().getUserName());
            } else {
                tv_user_name.setText("我");
                LogZ.d("getIsLogin--> ","img_head_photo.setBackgroundResource(R.drawable.defult_user_headimg)");

            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {

        super.onResume();
        Log.v("NoteBookActivity--> ", "onResume");

        setUserInfo();

    }
}