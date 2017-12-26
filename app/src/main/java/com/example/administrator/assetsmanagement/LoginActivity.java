package com.example.administrator.assetsmanagement;

import android.content.SharedPreferences;
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
import cn.bmob.v3.listener.SaveListener;

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



    @OnClick({R.id.btn_login, R.id.tv_register})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                login(mEtUsername.getText().toString(), mEtPassword.getText().toString(), new LogInListener() {
                    @Override
                    public void done(Object o, BmobException e) {
                        if (e == null) {
                            //登录成功

                            startActivity(MainActivity.class, null, true);
                        } else {
                            toast(e.getMessage() + "(" + e.getErrorCode() + ")");
                        }
                    }

                    @Override
                    public void done(Object o, Object o2) {

                    }
                });
                break;
            case R.id.tv_register:
                startActivity(RegisterManagerActivity.class,null,true);
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

        final Person user = new Person();
        user.setUsername(username);
        user.setPassword(password);

        user.login(new SaveListener<Person>() {
            @Override
            public void done(Person person, BmobException e) {
                if (person!=null && e == null) {
                    listener.done(getCurrentUser(), null);


                } else {
                    listener.done(user, e);
                }
            }
        });
    }

}
