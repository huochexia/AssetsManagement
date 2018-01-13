package com.example.administrator.assetsmanagement.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.example.administrator.assetsmanagement.Interface.ToolbarClickListener;
import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.base.ParentWithNaviActivity;
import com.example.administrator.assetsmanagement.bean.AssetInfo;
import com.example.administrator.assetsmanagement.bean.DepartmentTree.Department;
import com.example.administrator.assetsmanagement.bean.DepartmentTree.DepartmentNodeHelper;
import com.example.administrator.assetsmanagement.bean.LocationTree.Location;
import com.example.administrator.assetsmanagement.bean.LocationTree.LocationNodeHelper;
import com.example.administrator.assetsmanagement.bean.Person;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 单个资产通过扫描直接获得，或者维修移送时
 * Created by Administrator on 2017/12/26.
 */

public class SingleAssetTransferActivity extends ParentWithNaviActivity {
    public static final int REQUEST_RECEIVE_LOCATION = 101;
    public static final int REQUEST_RECEIVE_DEPT = 102;
    public static final int REQUEST_RECEIVE_MANAGER = 103;

    @BindView(R.id.tv_single_asset_new_location)
    TextView tvSingleAssetNewLocation;
    @BindView(R.id.tv_single_asset_new_department)
    TextView tvSingleAssetNewDepartment;
    @BindView(R.id.tv_single_asset_new_manager)
    TextView tvSingleAssetNewManager;

    Location newLoction;//节点对象，用于接收返回节点值
    Department newDepartment;
    Person mNewManager;
    AssetInfo mSingleasset;

    @Override
    public String title() {
        return "资产移交";
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
        setContentView(R.layout.activity_single_asset_transfer);
        ButterKnife.bind(this);
        initNaviView();
        Bundle bundle = getBundle();
        mSingleasset = (AssetInfo) bundle.getSerializable("asset");


    }

    @OnClick({R.id.btn_single_asset_new_location, R.id.btn_single_asset_new_department,
            R.id.btn_single_asset_new_manager, R.id.btn_single_asset_transfer_ok})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_single_asset_new_location:
                getSelectedInfo(SelectedTreeNodeActivity.SEARCH_LOCATION, false, REQUEST_RECEIVE_LOCATION);
                break;
            case R.id.btn_single_asset_new_department:
                getSelectedInfo(SelectedTreeNodeActivity.SEARCH_DEPARTMENT, false, REQUEST_RECEIVE_DEPT);
                break;
            case R.id.btn_single_asset_new_manager:
                Intent intent1 = new Intent(SingleAssetTransferActivity.this, ManagerListActivity.class);
                startActivityForResult(intent1, REQUEST_RECEIVE_MANAGER);
                break;
            case R.id.btn_single_asset_transfer_ok:
                Location l = new Location();
                if (newLoction != null) {
                    l.setObjectId(newLoction.getObjectId());//因为节点属性，防止死循环
                    mSingleasset.setLocation(l);
                }
                Department d = new Department();
                if (newDepartment != null) {
                    d.setObjectId(newDepartment.getObjectId());//因为节点属性，防止死循环}
                    mSingleasset.setDepartment(d);
                }
                if (mNewManager != null) {
                    mSingleasset.setNewManager(mNewManager);
                    //如果是正常资产移交，则改变为4待移交状态；如果是维送状态，则不变
                    if (mSingleasset.getStatus() == 0) {
                        mSingleasset.setStatus(4);
                    }

                } else {
                    toast("接收人不能为空！");
                    return;
                }
                mSingleasset.update(mSingleasset.getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            finish();
                        } else {
                            toast("变更失败！");
                        }
                    }
                });
                break;
        }
    }

    /**
     * 获取拟查询信息
     *
     * @param type
     * @param isPerson
     */
    private void getSelectedInfo(int type, boolean isPerson, int requestCode) {
        Intent intent = new Intent(SingleAssetTransferActivity.this, SelectedTreeNodeActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("person", isPerson);
        startActivityForResult(intent, requestCode);
    }

    /**
     * 处理返回结果
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case REQUEST_RECEIVE_LOCATION:
                if (resultCode == SelectedTreeNodeActivity.SEARCH_RESULT_OK) {
                    newLoction = (Location) data.getSerializableExtra("node");
                    tvSingleAssetNewLocation.setText(LocationNodeHelper.getSearchContentName(newLoction));
                }
                break;
            case REQUEST_RECEIVE_DEPT:
                if (resultCode == SelectedTreeNodeActivity.SEARCH_RESULT_OK) {
                    newDepartment = (Department) data.getSerializableExtra("node");
                    tvSingleAssetNewDepartment.setText(DepartmentNodeHelper.getSearchContentName(newDepartment));
                }
                break;
            case REQUEST_RECEIVE_MANAGER:
                if (resultCode == ManagerListActivity.SEARCH_OK) {
                    mNewManager = (Person) data.getSerializableExtra("manager");
                    tvSingleAssetNewManager.setText(mNewManager.getUsername());
                }
                break;

        }
    }
}
