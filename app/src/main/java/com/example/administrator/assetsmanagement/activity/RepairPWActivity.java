package com.example.administrator.assetsmanagement.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.assetsmanagement.Interface.ToolbarClickListener;
import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.base.ParentWithNaviActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2017/12/22.
 */

public class RepairPWActivity extends ParentWithNaviActivity {
    @BindView(R.id.et_old_password)
    EditText etOldPassword;
    @BindView(R.id.et_new_password)
    EditText etNewPassword;
    @BindView(R.id.et_password_again)
    EditText etPasswordAgain;

    @Override
    public String title() {
        return "修改密码";
    }

    @Override
    public Object left() {
        return R.drawable.ic_left_navi;
    }

    @Override
    public ToolbarClickListener getToolbarListener() {
        return new ToolbarClickListener() {
            @Override
            public void clickLeft() {
                finish();
            }

            @Override
            public void clickRight() {

            }
        };
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_password);
        ButterKnife.bind(this);
        initNaviView();
    }

    @OnClick({R.id.btn_repair_pw_ok, R.id.btn_repair_pw_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_repair_pw_ok:
                repairPW(etOldPassword.getText().toString(),etNewPassword.getText().toString()
                        ,etPasswordAgain.getText().toString());
                break;
            case R.id.btn_repair_pw_cancel:
                finish();
                break;
        }
    }

    private void repairPW(String OldPw, String NewPw, String AgainNewPw) {
        if (TextUtils.isEmpty(OldPw)) {
            toast("请填写旧密码！");
            return;
        }
        if (TextUtils.isEmpty(NewPw)) {
            toast("请填写新密码！");
            return;
        }
        if (TextUtils.isEmpty(AgainNewPw)) {
            toast("请填写第二遍新密码！");
            return;
        }
        if (!NewPw.equals(AgainNewPw)) {
            toast("两次输入的新密码不一致，请重新输入！");
            return;
        }
        updateCurrentUserPwd(OldPw, NewPw);
    }


    /**
     * 修改当前用户密码
     *
     * @return void
     * @throws
     */
    private void updateCurrentUserPwd(String oldPw, String newPw) {

        BmobUser.updateCurrentUserPassword(oldPw, newPw, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    //密码修改成功后，注销当前用户
                    toast("密码修改成功，可以用新密码进行登录");
                    BmobUser.logOut();
                    finish();

                } else {
                    if (e.getErrorCode() == 206) {
                        Toast.makeText(RepairPWActivity.this,"请退出重新登录后，" +
                                "再修改密码",Toast.LENGTH_LONG).show();
                    }else {
                        toast("错误信息:"+e.getMessage());
                    }
                }
            }
        });
    }
}
