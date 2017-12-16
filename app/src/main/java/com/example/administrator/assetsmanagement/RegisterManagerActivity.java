package com.example.administrator.assetsmanagement;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.example.administrator.assetsmanagement.base.ParentWithNaviActivity;
import com.example.administrator.assetsmanagement.bean.Person;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Administrator on 2017/12/16 0016.
 */

public class RegisterManagerActivity extends ParentWithNaviActivity {
    @BindView(R.id.et_username)
    EditText mEtUsername;
    @BindView(R.id.et_telephone)
    EditText mEtTelephone;
    @BindView(R.id.et_password)
    EditText mEtPassword;
    @BindView(R.id.et_password_again)
    EditText mEtPasswordAgain;

    @Override
    public String title() {
        return "注册";
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_manager);
        ButterKnife.bind(this);
        initNaviView();
    }

    @OnClick(R.id.btn_register)
    public void onViewClicked(View view) {
        register(mEtUsername.getText().toString(),mEtTelephone.getText().toString(),
                mEtPassword.getText().toString(), mEtPasswordAgain.getText().toString(),
                new LogInListener() {
            @Override
            public void done(Object o, BmobException e) {
                if (e == null) {
                    startActivity(MainActivity.class, null, true);
                } else {
                    if (e.getErrorCode() == 1001) {
                        mEtPasswordAgain.setText("");
                    }
                    toast(e.getMessage() + "(" + e.getErrorCode() + ")");
                }
            }
        });
    }
    public void register(String username, String telephone,String password, String pwdagain, final LogInListener listener) {
        if (TextUtils.isEmpty(username)) {
            listener.done(null, new BmobException(1000, "请填写用户名"));
            return;
        }
        if (TextUtils.isEmpty(telephone)) {
            listener.done(null, new BmobException(1000, "请填写手机号"));
            return;
        }
        if (TextUtils.isEmpty(password)) {
            listener.done(null, new BmobException(1000, "请填写密码"));
            return;
        }
        if (TextUtils.isEmpty(pwdagain)) {
            listener.done(null, new BmobException(1000, "请填写确认密码"));
            return;
        }
        if (!password.equals(pwdagain)) {
            listener.done(null, new BmobException(1000, "两次输入的密码不一致，请重新输入"));
            return;
        }
        final Person user = new Person();
        user.setUsername(username);
        user.setMobilePhoneNumber(telephone);
        user.setPassword(password);
        user.signUp(new SaveListener<Person>() {
            @Override
            public void done(Person user, BmobException e) {
                if (e == null) {
                    listener.done(null, null);
                } else {
                    listener.done(null, e);
                }
            }
        });
    }
}
