package com.example.administrator.assetsmanagement;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.example.administrator.assetsmanagement.base.BaseActivity;
import com.example.administrator.assetsmanagement.bean.Person;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

import static cn.bmob.v3.BmobUser.getCurrentUser;

/**
 * Created by Administrator on 2017/12/16 0016.
 */

public class LoginActivity extends BaseActivity {


    @BindView(R.id.et_username)
    EditText mEtUsername;
    @BindView(R.id.et_password)
    EditText mEtPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }


    @OnClick({R.id.btn_login, R.id.btn_register,R.id.tv_forget_password})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_login:

                login(mEtUsername.getText().toString(), mEtPassword.getText().toString(), new LogInListener() {
                    @Override
                    public void done(Object o, BmobException e) {
                        if (e == null) {
                            //登录成功
                            startActivity(FlashActivity.class, null, true);
//                            startActivity(MainActivity.class, null, true);
                        } else {
                            toast(e.getMessage() + "(" + e.getErrorCode() + ")");
                        }
                    }

                    @Override
                    public void done(Object o, Object o2) {

                    }
                });
                break;
            case R.id.btn_register:
                startActivity(RegisterManagerActivity.class, null, false);
                break;
            case R.id.tv_forget_password:
                toast("近期推出，敬请期待");
                break;
        }
    }

    public void login(String username, String password, final LogInListener listener) {
        if (TextUtils.isEmpty(username)) {
            listener.done(null, new BmobException(1000, "请填写用户名"));
            return;
        }
        if (TextUtils.isEmpty(password)) {
            listener.done(null, new BmobException(1000, "请填写密码"));
            return;
        }
        BmobUser.loginByAccount(username, password, new LogInListener<Person>() {

            @Override
            public void done(Person user, BmobException e) {
                if (user != null) {
                    listener.done(getCurrentUser(), null);
                } else {
                    listener.done(user, e);
                }
            }
        });
    }

    @OnClick(R.id.tv_forget_password)
    public void onViewClicked() {
    }
}
