package com.jhlc.material;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.jhlc.material.adapter.NoticeCommentAdapter;
import com.jhlc.material.bean.GetCommentBean;
import com.jhlc.material.bean.GetNoticeBean;
import com.jhlc.material.bean.NoticeCommentBean;
import com.jhlc.material.bean.NoticeListBean;
import com.jhlc.material.utils.Constant;
import com.jhlc.material.utils.LogZ;
import com.jhlc.material.utils.MYURL;
import com.jhlc.material.utils.PreferenceUtils;
import com.jhlc.material.utils.TimeUtil;
import com.jhlc.material.view.ListViewForScrollView;
import com.jhlc.material.volley.VolleyInterface;
import com.jhlc.material.volley.VolleyRequest;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/10/9.
 * 公告
 */
public class NoticeActivity extends Activity {
    private static String TAG = "NoticeActivity";
    private TextView title, content, time;
    private ListView commentListView;      //评论列表
    private NoticeCommentAdapter adapter;

    private LinearLayout commentBox;       //评论栏
    private EditText et_notice_detail_comment;
    private TextView tv_notice_detail_send;
    private boolean commentIsShow = false;

    private NoticeListBean noticeListBean;
    private ArrayList<GetCommentBean> commentBeans = new ArrayList<>();

    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice_detail);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getInfo();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void initView() {
        noticeListBean = (NoticeListBean) getIntent().getSerializableExtra("notice");
        commentListView = (ListView) findViewById(R.id.list_notice_detail_comment);
        commentBox = (LinearLayout) findViewById(R.id.ll_notice_detail_comment);

        initListView();         //初始化listView
        initTitle();            //标题设置
        initComment();          //初始化评论栏

        time = (TextView) findViewById(R.id.tv_notice_detail_time);
        title = (TextView) findViewById(R.id.tv_notice_detail_title);
        content = (TextView) findViewById(R.id.tv_notice_detail_content);
        time.setText(noticeListBean.getAddtime());
        title.setText(noticeListBean.getTitle());
        content.setText(noticeListBean.getContent());
    }


    //标题设置
    private void initTitle() {
        TextView tv_title = (TextView) findViewById(R.id.tv_title_centre);
        findViewById(R.id.tv_title_name).setVisibility(View.GONE);
        tv_title.setText("公告详情");
        tv_title.setVisibility(View.VISIBLE);
        ImageButton ibtn_close_edit = (ImageButton) findViewById(R.id.ibtn_close_edit);
        ibtn_close_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initListView() {
        commentBox.getMeasuredHeight();


        View head = View.inflate(this, R.layout.notice_detail_head, null);
        commentListView.addHeaderView(head);
        adapter = new NoticeCommentAdapter(this, commentBeans);
        commentListView.setAdapter(adapter);
        /**
         * 评论嵌套时的时限方式，暂不用
         */
        adapter.setListener(new NoticeCommentAdapter.CommentAdapterListener() {
            @Override
            public void commitOnListener(int position) {
                showCommentBox();
            }
        });
    }

    /**
     * 评论栏初始化设置
     */
    private void initComment() {
        et_notice_detail_comment = (EditText) findViewById(R.id.et_notice_detail_comment);
        tv_notice_detail_send = (TextView) findViewById(R.id.tv_notice_detail_send);

        tv_notice_detail_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = et_notice_detail_comment.getText().toString().trim();
                LogZ.d(TAG, comment);
                if ("".equals(comment)) {
                    ZXLApplication.getInstance().showTextToast("评论不能为空");
                } else {
                    commitComment(comment);
                    et_notice_detail_comment.setText("");       //隐藏键盘
                    InputMethodManager imm = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });

//        Button ibtn_submit_edit = (Button) findViewById(R.id.ibtn_submit_edit);
//        ibtn_submit_edit.setVisibility(View.VISIBLE);
//        ibtn_submit_edit.setText("评论");
//        ibtn_submit_edit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                showCommentBox();
//                showAnimation();
//            }
//        });

        /**
         * 设置评论listView底部的不会被评论栏掩盖
         */
        commentBox.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (isFirst) {
                    isFirst = false;
                    int height = commentBox.getHeight();
                    View view  = new View(NoticeActivity.this);
                    AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                            height);
                    view.setLayoutParams(layoutParams);
                    commentListView.addFooterView(view);
                }

            }
        });
    }

    /**
     * 获取评论信息
     */
    private void getInfo() {
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("opcode", "GetComment");
        hashMap.put("eid", Constant.EID);
        hashMap.put("noticeid", noticeListBean.getNoticeid());

        VolleyRequest.RequestPost(this, MYURL.URL_HEAD, "", hashMap, new VolleyInterface<NoticeCommentBean>(NoticeCommentBean.class, TAG) {
            @Override
            public void onMySuccess(NoticeCommentBean result) {
                commentBeans.clear();
                Collections.addAll(commentBeans, result.getGetComments());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onMyError(VolleyError error) {

            }
        });
    }

    /**
     * 提交评论
     */
    private void commitComment(String comment) {
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("opcode", "GetNoticeComment");
        hashMap.put("Username", PreferenceUtils.getUserName());
        hashMap.put("eid", Constant.EID);
        hashMap.put("commentcontent", comment);
        hashMap.put("addtime", TimeUtil.sdf.format(new Date()));
        hashMap.put("noticeid", noticeListBean.getNoticeid());

        VolleyRequest.RequestPost(this, MYURL.URL_HEAD, "", hashMap, new VolleyInterface<NoticeCommentBean>(NoticeCommentBean.class, TAG) {
            @Override
            public void onMySuccess(NoticeCommentBean result) {
                commentBeans.clear();
                ZXLApplication.getInstance().showTextToast(result.getMsg());
                Collections.addAll(commentBeans, result.getGetNoticeComment());
                adapter.notifyDataSetChanged();

//                showCommentBox();
//                showAnimation();
            }

            @Override
            public void onMyError(VolleyError error) {

            }
        });
    }

    /**
     * 对评论栏进行显隐
     */
    private void showCommentBox() {
        if (commentIsShow) {
            ScaleAnimation mHiddenAction = new ScaleAnimation(Animation.RELATIVE_TO_SELF,
                    1.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                    0.0f);
            mHiddenAction.setDuration(500);
            commentBox.startAnimation(mHiddenAction);
            commentBox.setVisibility(View.GONE);
            commentIsShow = false;
        } else {
            ScaleAnimation mShowAction = new ScaleAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 1.2f, Animation.RELATIVE_TO_SELF,
                    0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
            mShowAction.setDuration(500);
            commentBox.startAnimation(mShowAction);
            commentBox.setVisibility(View.VISIBLE);
            commentIsShow = true;
        }

    }

    /**
     * 显示动画
     */
    private void showAnimation() {
        ObjectAnimator showAnimation;
        if (commentIsShow){
            commentIsShow = false;
            showAnimation = ObjectAnimator.ofFloat(commentBox, "translationX", commentBox.getTranslationX(), -1000f);
            showAnimation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    commentBox.setVisibility(View.GONE);
                }
            });
        } else {
            commentIsShow = true;
            commentBox.setVisibility(View.VISIBLE);
            showAnimation = ObjectAnimator.ofFloat(commentBox, "translationX", -1000f, 0f);
        }
        showAnimation.setDuration(500);
        showAnimation.setInterpolator(new AccelerateInterpolator());
        showAnimation.start();
    }

//    @Override
//    public void onBackPressed() {
//        if (commentIsShow) {
////            showCommentBox();                //当评论栏存在时按后退键就是隐藏评论栏
//            showAnimation();
//        } else {
//            super.onBackPressed();
//        }
//
//
//    }

}
