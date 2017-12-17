package com.example.administrator.assetsmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.administrator.assetsmanagement.activity.AssetsTurnOverActivity;
import com.example.administrator.assetsmanagement.activity.SelectedTreeNodeActivity;
import com.example.administrator.assetsmanagement.base.ParentWithNaviActivity;
import com.example.administrator.assetsmanagement.bean.Department;
import com.example.administrator.assetsmanagement.bean.Person;
import com.example.administrator.assetsmanagement.treeUtil.BaseNode;
import com.example.administrator.assetsmanagement.treeUtil.NodeHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Administrator on 2017/12/16 0016.
 */

public class RegisterManagerActivity extends ParentWithNaviActivity {

    public static final int REQUEST_RECEIVE_DEPT = 100;

    @BindView(R.id.et_username)
    EditText mEtUsername;
    @BindView(R.id.et_telephone)
    EditText mEtTelephone;
    @BindView(R.id.et_password)
    EditText mEtPassword;
    @BindView(R.id.et_password_again)
    EditText mEtPasswordAgain;
    @BindView(R.id.tv_register_department)
    TextView mTvRegisterDepartment;

    Department mDepartment;
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


    public void register(String username, String telephone, String password, String pwdagain, final LogInListener listener) {
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
        if (mDepartment == null) {
            toast("请选择您所属部门！");
            return;
        }
        final Person user = new Person();
        user.setUsername(username);
        user.setMobilePhoneNumber(telephone);
        user.setPassword(password);
        user.setDepartment(mDepartment);
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

    @OnClick({R.id.btn_register_department, R.id.btn_register})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_register_department:
                Intent intent = new Intent(RegisterManagerActivity.this, SelectedTreeNodeActivity.class);
                intent.putExtra("type", SelectedTreeNodeActivity.SEARCH_DEPARTMENT);
                intent.putExtra("person", false);
                startActivityForResult(intent,REQUEST_RECEIVE_DEPT);
                break;
            case R.id.btn_register:
                register(mEtUsername.getText().toString(), mEtTelephone.getText().toString(),
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
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_RECEIVE_DEPT:
                if (resultCode == SelectedTreeNodeActivity.SEARCH_RESULT_OK) {
                    BaseNode node = (BaseNode) data.getSerializableExtra("node");
                    getDepartmentFromBmob(node);
                }
                break;

        }
    }

    private void getDepartmentFromBmob(BaseNode node) {
        BmobQuery<Department> query = new BmobQuery<>();
        query.addWhereEqualTo("id", node.getId());
        query.findObjects(new FindListener<Department>() {
            @Override
            public void done(final List<Department> list, BmobException e) {
                if (e == null && list.size() > 0) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message msg = new Message();
                            msg.what = 1;
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("department",list.get(0));
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }
                    }).start();
                }
            }
        });
    }
    RegisterHandler handler = new RegisterHandler();
    class RegisterHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mDepartment = (Department) msg.getData().getSerializable("department");
                    mTvRegisterDepartment.setText(mDepartment.getDepartmentName());
                    break;
            }
        }
    }
}
