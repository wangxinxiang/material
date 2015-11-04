package com.jhlc.material;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.jhlc.material.view.wheelview.WheelMain;

public class AddNoteBookActivity extends Activity{
    private View timePicker;
    private WheelMain wheelMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_notebook_dialog);
        initView();

        setListener();
    }

    private void setListener() {
        findViewById(R.id.img_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.rl_confirm_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ZXLApplication.getInstance().showTextToast(wheelMain.getTime());
            }
        });
    }

    private void initView() {
        timePicker = findViewById(R.id.timePicker);
        wheelMain = new WheelMain(timePicker);
        wheelMain.initDateTimePicker();
    }
}
