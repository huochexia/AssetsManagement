package com.example.administrator.assetsmanagement.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.example.administrator.assetsmanagement.Interface.ToolbarClickListener;
import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.base.ParentWithNaviActivity;
import com.example.administrator.assetsmanagement.bean.DepartmentTree.Department;
import com.example.administrator.assetsmanagement.treeUtil.BaseNode;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/12/22.
 */

public class UpdateMyInfo extends ParentWithNaviActivity {
    public static final int REQUSET_DEPARTMENT=1;
    Department node;//接收返回的部门节点信息
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
    }

    @OnClick({R.id.btn_update_my_department, R.id.btn_repair_password})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_update_my_department:
                Intent intent = new Intent(UpdateMyInfo.this, SelectedTreeNodeActivity.class);
                intent.putExtra("type", SelectedTreeNodeActivity.SEARCH_DEPARTMENT);
                intent.putExtra("person", false);
                startActivityForResult(intent,REQUSET_DEPARTMENT);
                break;
            case R.id.btn_repair_password:
                startActivity(RepairPWActivity.class,null,false);
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

                break;
        }
    }
}
