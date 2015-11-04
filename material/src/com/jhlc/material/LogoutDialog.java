package com.jhlc.material;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jhlc.material.db.NoteBookDB;
import com.jhlc.material.db.ProgressReportDB;
import com.jhlc.material.db.SetNewMsgDB;
import com.jhlc.material.db.UserListDB;
import com.jhlc.material.utils.Constant;
import com.jhlc.material.utils.PreferenceUtils;

/**
 */
public class LogoutDialog  extends Dialog {
    TextView tv_take_pictures,tv_user_logout,tv_edit_cancle;
    private Context context;
    private RefreshViewListener refreshViewListener;

    public LogoutDialog(Context context, int theme){
        super(context, theme);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logout_dialog);
        initView();
        getIntentData();
        setListener();
    }

    private void getIntentData() {

    }

    private void setListener() {
        tv_take_pictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddHeadImageActivity.class);
                intent.putExtra(Constant.ChangeHeadImage, Constant.ChangeHeadImage);
                context.startActivity(intent);
                dismiss();
            }
        });

        tv_user_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 清楚 配置 和数据库信息
                PreferenceUtils.getInstance().clearSP();
                new SetNewMsgDB(context).delete();
                new UserListDB(context).delete();
                new ProgressReportDB(context).delete();
                new NoteBookDB(context).delete();
                refreshViewListener.refresh();
                ZXLApplication.getInstance().showTextToast("注销成功");
                //测试提交，随意输入字符
                dismiss();

            }
        });

        tv_edit_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

            }
        });
    }

    private void initView() {
        tv_take_pictures = (TextView) findViewById(R.id.tv_take_pictures);
        tv_user_logout   = (TextView) findViewById(R.id.tv_user_logout);
        tv_edit_cancle   = (TextView) findViewById(R.id.tv_edit_cancle);
    }

    public void setRefreshViewListener(RefreshViewListener refreshViewListener) {
        this.refreshViewListener = refreshViewListener;
    }

    public interface RefreshViewListener {
        void  refresh();
    }
}
