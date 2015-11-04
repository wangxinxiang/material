package com.jhlc.material;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.android.volley.VolleyError;
import com.jhlc.material.adapter.NoticeAdapter;
import com.jhlc.material.bean.DownUserList;
import com.jhlc.material.bean.GetNoticeBean;
import com.jhlc.material.bean.NoticeListBean;
import com.jhlc.material.bean.UserBean;
import com.jhlc.material.utils.Constant;
import com.jhlc.material.utils.MYURL;
import com.jhlc.material.utils.PreferenceUtils;
import com.jhlc.material.volley.VolleyInterface;
import com.jhlc.material.volley.VolleyRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/9/14 0014.
 */
public class Fragment_notice extends Fragment {
    private static String TAG = "Fragment_notice";
    private View view;
    private ListView list_notice;
    private NoticeAdapter noticeAdapter;
    private List<NoticeListBean> noticeListBeans = new ArrayList<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notice, null);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        list_notice = (ListView) view.findViewById(R.id.list_notice);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_title_single);
        tv_title.setText(R.string.fragment_notice_title);
        noticeAdapter = new NoticeAdapter(getActivity(), noticeListBeans);
        list_notice.setAdapter(noticeAdapter);

        initListener();

    }

    private void initListener() {
        list_notice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), NoticeActivity.class);
                intent.putExtra("notice", noticeListBeans.get(i));
                startActivity(intent);
            }
        });
    }


    public void getInfo() {
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("opcode", "GetNotice");
        hashMap.put("username", "");

        VolleyRequest.RequestPost(getContext(), MYURL.URL_HEAD, "", hashMap, new VolleyInterface<GetNoticeBean>(GetNoticeBean.class, TAG) {
            @Override
            public void onMySuccess(GetNoticeBean result) {
                noticeListBeans.clear();
                Collections.addAll(noticeListBeans, result.getGetNotices());
                noticeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onMyError(VolleyError error) {

            }
        });
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.d(TAG, hidden + "");
    }


    @Override
    public void onResume() {
        super.onResume();
        getInfo();
        Log.d(TAG, "onResume");
    }
}
