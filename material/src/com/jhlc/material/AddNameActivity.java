package com.jhlc.material;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;


public class AddNameActivity extends Activity{
    private ZXLApplication application = ZXLApplication.getInstance();
    private EditText       et_myname;
    private Intent         intent;
    private String         username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_myname_dialog);
        initView();
        setListener();

    }

    private void setListener() {
        findViewById(R.id.tv_edit_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.btn_next_step).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = et_myname.getText().toString().trim();
                if(checkName()) {
                    intent = new Intent(AddNameActivity.this, AddHeadImageActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private boolean checkName(){
        if(null == username || "".equals(username)){
            application.showTextToast("请输入你的姓名");
            return false;
        } else if(username.length() > 12){
            application.showTextToast("长度不能大于12");
            return false;
        }
        return true;
    }

    private void initView() {
        et_myname = (EditText) findViewById(R.id.et_myname);
        et_myname.requestFocus();
        et_myname.setSelection(0);
    }

}
