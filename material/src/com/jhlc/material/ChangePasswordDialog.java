package com.jhlc.material;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.VolleyError;
import com.jhlc.material.bean.BaseBean;
import com.jhlc.material.bean.DownUserList;
import com.jhlc.material.bean.UserBean;
import com.jhlc.material.utils.Constant;
import com.jhlc.material.utils.MYURL;
import com.jhlc.material.utils.PreferenceUtils;
import com.jhlc.material.volley.VolleyInterface;
import com.jhlc.material.volley.VolleyRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/10/28.
 * 修改密码的dialog
 */
public class ChangePasswordDialog extends DialogFragment{
    private EditText oldPassword, newPassword, et_new_password_repeat;
    private Button submit;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_change_password, container, false);
        submit = (Button) view.findViewById(R.id.btn_submit);
        oldPassword = (EditText) view.findViewById(R.id.et_old_password);
        newPassword = (EditText) view.findViewById(R.id.et_new_password);
        et_new_password_repeat = (EditText) view.findViewById(R.id.et_new_password_repeat);
        initListener();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int style=DialogFragment.STYLE_NO_TITLE;//DialogFragment的风格类
        /*STYLE_NORMAL = 0;
         STYLE_NO_TITLE = 1; 无标题
         STYLE_NO_FRAME = 2; 窗口 陷入 父级界面中
         STYLE_NO_INPUT = 3; 窗口不能 接受点击 但是父级界面能接受
         */
        int theme = android.R.style.Theme_Holo_Light_Dialog; //亮色主题
        setStyle(style, theme);
    }

    private void initListener() {
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPW = oldPassword.getText().toString().trim();
                String newPW = newPassword.getText().toString().trim();
                String newPwRepeat = et_new_password_repeat.getText().toString().trim();
                if ("".equals(oldPW) || "".equals(newPW)) {
                    ZXLApplication.getInstance().showTextToast("密码不能为空");
                } else {
                    if (newPW.equals(newPwRepeat)) {
                        changePassword(oldPW, newPW);
                    } else {
                        ZXLApplication.getInstance().showTextToast("两次密码不一致");
                    }

                }
            }
        });
    }

    private void changePassword(String oldPassword, String newPassword) {
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("opcode", "UpdatePwd");
        hashMap.put("username", PreferenceUtils.getUserName());
        hashMap.put("oldpwd", oldPassword);
        hashMap.put("newpwd", newPassword);

        VolleyRequest.RequestPost(getContext(), MYURL.URL_HEAD, "", hashMap, new VolleyInterface<BaseBean>(BaseBean.class, "changePassword --->") {
            @Override
            public void onMySuccess(BaseBean result) {
                ZXLApplication.getInstance().showTextToast(result.getMsg());
                getDialog().dismiss();
            }

            @Override
            public void onMyError(VolleyError error) {

            }
        });
    }
}
