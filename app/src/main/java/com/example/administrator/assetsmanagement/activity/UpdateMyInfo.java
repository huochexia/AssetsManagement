package com.example.administrator.assetsmanagement.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.example.administrator.assetsmanagement.Interface.ToolbarClickListener;
import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.base.ParentWithNaviActivity;
import com.example.administrator.assetsmanagement.bean.DepartmentTree.Department;
import com.example.administrator.assetsmanagement.bean.Person;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2017/12/22.
 */

public class UpdateMyInfo extends ParentWithNaviActivity {
    public static final int REQUSET_DEPARTMENT = 1;
    Department node;//接收返回的部门节点信息
    @BindView(R.id.btn_update_my_department)
    Button btnUpdateMyDepartment;

    @Override
    public String title() {
        return "变更信息";
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
        setContentView(R.layout.activity_repair_person_info);
        ButterKnife.bind(this);
        initNaviView();
        final Person person = BmobUser.getCurrentUser(Person.class);
        BmobQuery<Person> query = new BmobQuery<>();
        query.addWhereEqualTo("objectId", person.getObjectId());
        query.include("department");
        query.findObjects(new FindListener<Person>() {
            @Override
            public void done(final List<Person> list, BmobException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnUpdateMyDepartment.setText("我所在部门："+list.get(0).getDepartment().getDepartmentName());
                    }
                });
            }
        });

    }

    @OnClick({R.id.btn_update_my_department, R.id.btn_repair_password})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_update_my_department:
                Intent intent = new Intent(UpdateMyInfo.this, SelectedTreeNodeActivity.class);
                intent.putExtra("type", SelectedTreeNodeActivity.SEARCH_DEPARTMENT);
                intent.putExtra("person", false);
                startActivityForResult(intent, REQUSET_DEPARTMENT);
                break;
            case R.id.btn_repair_password:
                startActivity(RepairPWActivity.class, null, false);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUSET_DEPARTMENT:
                if (resultCode == SelectedTreeNodeActivity.SEARCH_RESULT_OK) {
                    node = (Department) data.getSerializableExtra("node");
                }
                Person person = new Person();
                //因为Department是节点，它包含父节点属性，直接保存会产生死循环，所以新生成一个对象，
                //只赋值给它objectId。即指针
                Department newDepartment = new Department();
                newDepartment.setObjectId(node.getObjectId());
                person.setDepartment(newDepartment);
                BmobUser bmobUser = BmobUser.getCurrentUser(Person.class);
                person.update(bmobUser.getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            toast("更新用户信息成功");
                           btnUpdateMyDepartment.setText("我所在部门："+node.getDepartmentName());
                        } else {
                            toast("更新用户信息失败:" + e.getMessage());
                        }
                    }
                });
                break;
        }
    }
}
