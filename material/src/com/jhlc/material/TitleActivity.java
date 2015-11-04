package com.jhlc.material;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * 管理所有引用了title视图的activity类
 */
public abstract class TitleActivity extends FragmentActivity{
    private TextView tv_title_name;
    private Button ibtn_submit_edit;
    private ImageButton ibtn_title_delete,ibtn_close_edit;
    private OnDeleteListener deleteListener;
    private OnSubmitListener submitListener;

    protected interface OnDeleteListener{
        public void deleteClick();
    }

    protected interface OnSubmitListener{
        public void submitClick();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setContentViewId());
        OnBackPressed();//返回键 功能
    }

    //设置 删除 的点击事件 需要传入 OnDeleteListener 重写deleteClick 方法完成子类所需功能
    protected void setOnDeleteListener(final OnDeleteListener listener) {
        this.deleteListener=listener;
        ibtn_title_delete= (ImageButton) findViewById(R.id.ibtn_title_delete);
        ibtn_title_delete.setVisibility(View.VISIBLE);
        ibtn_title_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.deleteClick();
            }
        });
    }


    //设置button 的点击事件 需要传入OnSubmitListener 重写submitClick 方法完成完成子类所需功能
    protected void setOnButtonListener(final OnSubmitListener listener){
        this.submitListener=listener;
        ibtn_submit_edit= (Button) findViewById(R.id.ibtn_submit_edit);
        ibtn_submit_edit.setVisibility(View.VISIBLE);
        ibtn_submit_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.submitClick();
            }
        });
    }

    //设置button 文字内容和点击事件 需要传入OnSubmitListener 重写submitClick 方法完成完成子类所需功能
    protected void setOnButtonNameAndListener(String context, final OnSubmitListener listener){
        this.submitListener=listener;
        ibtn_submit_edit= (Button) findViewById(R.id.ibtn_submit_edit);
        ibtn_submit_edit.setVisibility(View.VISIBLE);
        ibtn_submit_edit.setText(context);
        ibtn_submit_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.submitClick();
            }
        });
    }

    private void OnBackPressed() {
        ibtn_close_edit= (ImageButton) findViewById(R.id.ibtn_close_edit);
        ibtn_close_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setKeyOrBackPressed();
            }
        });
    }

    protected void setKeyOrBackPressed(){
        finish();
    }

    //监听返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            setKeyOrBackPressed();
        }
        return true;

    }

    protected abstract int setContentViewId();//抽象 子类需要重写返回 布局文件的ID

    //设置标题文字
    protected void setTitleName(String titleName){
        tv_title_name= (TextView) findViewById(R.id.tv_title_name);
        tv_title_name.setText(titleName);
    }





}
