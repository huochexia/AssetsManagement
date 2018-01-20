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
import com.example.administrator.assetsmanagement.utils.AssetsUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobObject;

/**
 * 单个资产通过扫描直接获得后移交，或者维修移送;通过选择明细后，进行打印并移交时启动
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

    Location newLocation;//节点对象，用于接收返回节点值
    Department newDepartment;
    Person mNewManager;
    List<AssetInfo> mAssetInfoList;
    int flag;//是否新登记资产的标志，1为新登记。

    @Override
    public String title() {
        return "选择移交";
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
        flag = bundle.getInt("flag", 0);
        mAssetInfoList = (List<AssetInfo>) bundle.getSerializable("assets");
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
                //如果是新登记的，位置和部门不能为空。
                if (mAssetInfoList.size() > 0) {
                    //轮查状态是否可以进行移交
                    for (AssetInfo ass : mAssetInfoList) {
                        if (ass.getStatus() == 2 || ass.getStatus() == 3) {
                            toast("丢失、待报废资产不能进行移交！");
                            return;
                        }
                    }
                    if (mNewManager != null) {
                        if (flag == 1 && (newLocation == null || newDepartment == null)) {
                            toast("新登记资产必须分配位置和部门！");
                        } else {
                            if (flag == 1) {//新登记资产为添加
                                AssetsUtil.insertBmobLibrary(this, updateAllAssets(mAssetInfoList));
                                clearView();
                            } else {//原有资产移交为变更
                                AssetsUtil.updateBmobLibrary(this, updateAllAssets(mAssetInfoList));
                                clearView();
                            }
                        }

                    } else {
                        toast("请选择接受人！");
                    }
                } else {
                    toast("没有选择资产！");
                }
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
                    newLocation = (Location) data.getSerializableExtra("node");
                    tvSingleAssetNewLocation.setText(LocationNodeHelper.getSearchContentName(newLocation));
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

    /**
     * 修改资产信息,因为位置和部门有节点属性，即父节点。在Bmob存储中造成自循环，最终导致内存溢出。
     * 所以在保存资产信息的位置和部门属性值时，新构造一个对象，传入其唯一objectId，
     */
    private void updateAssetInfo(AssetInfo asset) {
        if (newLocation != null) {
            Location l = new Location();
            l.setObjectId(newLocation.getObjectId());
            asset.setLocation(l);
        }
        if (newDepartment != null) {
            Department d = new Department();
            d.setObjectId(newDepartment.getObjectId());
            asset.setDepartment(d);
        }
        asset.setNewManager(mNewManager);
        //如果资产状态为0或4,9时，移交确认后状态改为4；如果资产状态为1时，移交确认后改为6。
        // 目前暂定为丢失、已报废、待报废（审批中）的资产不能进行移交，如果要移交的话，先
        //变更为正常0，再进行移交，移交后，再调整为原状态。
        if (asset.getStatus() == 0 || asset.getStatus() == 4 || asset.getStatus() == 9) {
            asset.setStatus(4);
        }
        if (asset.getStatus() == 1) {
            asset.setStatus(6);
        }

    }

    /**
     * 遍历资产列表，修改资产
     *
     * @param list 为所有列表项
     * @return
     */
    private List<BmobObject> updateAllAssets(List<AssetInfo> list) {
        List<BmobObject> objects = new ArrayList<>();
        for (AssetInfo asset : list) {
            updateAssetInfo(asset);
            objects.add(asset);
        }
        return objects;
    }

    /**
     * 初始化界面
     */
    private void clearView() {
        newDepartment=null;
        newLocation=null;
        mNewManager=null;
        tvSingleAssetNewLocation.setText("");
        tvSingleAssetNewDepartment.setText("");
        tvSingleAssetNewManager.setText("");
    }

}
