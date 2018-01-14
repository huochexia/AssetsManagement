package com.example.administrator.assetsmanagement.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.administrator.assetsmanagement.Interface.ToolbarClickListener;
import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.adapter.AssetRecyclerViewAdapter;
import com.example.administrator.assetsmanagement.base.ParentWithNaviActivity;
import com.example.administrator.assetsmanagement.bean.AssetInfo;
import com.example.administrator.assetsmanagement.utils.AssetsUtil;
import com.example.administrator.assetsmanagement.utils.LineEditText;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * 资产报修处理：查找资产确定报修，只能对管理员（当前用户）名下的资产进行报修。然后选择是否移送。
 * 如果移送则选择接收人。接收人在接收资产中进行确认，则该资产归维修人管理。
 * 当维修人修理完后，如果该资产归自己名下，则选择是否移送。如果移送则该资产需要接收人进行确认。
 * <p>
 * Created by Administrator on 2017/12/13.
 */

public class AssetRepairActivity extends ParentWithNaviActivity {
    @BindView(R.id.rv_single_asset_manage)
    RecyclerView rvSingleAssetManage;
    @BindView(R.id.btn_single_asset_manage_ok)
    Button btnSingleAssetManageOk;
    @BindView(R.id.btn_single_asset_manage_cancel)
    Button btnSingleAssetManageCancel;
    List<AssetInfo> list;
    AssetRecyclerViewAdapter adapter;
    @BindView(R.id.et_search_asset_num)
    LineEditText etSearchAssetNum;
    private String ScanResult;
    private AssetInfo asset;

    private boolean willRepair ;//用于判断这个资产是将要维修移送。
    @Override
    public String title() {
        return "资产报修";
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
        setContentView(R.layout.activity_single_asset_manage);
        ButterKnife.bind(this);
        initNaviView();
        btnSingleAssetManageOk.setText("报修");
        btnSingleAssetManageOk.setEnabled(false);
        btnSingleAssetManageCancel.setText("修好");
        btnSingleAssetManageCancel.setEnabled(false);
        LinearLayoutManager ll = new LinearLayoutManager(this);
        rvSingleAssetManage.setLayoutManager(ll);
    }

    @OnClick({R.id.iv_barcode_2d, R.id.btn_single_asset_search, R.id.btn_single_asset_manage_ok,
            R.id.btn_single_asset_manage_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_barcode_2d:
                customScan();
                break;
            case R.id.btn_single_asset_search:
                String number = etSearchAssetNum.getText().toString();
                List<AssetInfo> allList = new ArrayList<>();
                AssetsUtil.count=0;
                AssetsUtil.AndQueryAssets(this, "mAssetsNum", number, handler,allList);
                break;
            case R.id.btn_single_asset_manage_ok:
                AssetsUtil.changeAssetStatus(this, list.get(0), 1);
                willRepair = true;
                turnOverDialog();
                list.clear();
                adapter.notifyDataSetChanged();
                break;
            case R.id.btn_single_asset_manage_cancel:
                AssetsUtil.changeAssetStatus(this, list.get(0), 0);
                String m1=list.get(0).getOldManager().getObjectId();
                String currentUser = BmobUser.getCurrentUser().getObjectId();
                if (m1.equals(currentUser)) {
                    willRepair = false;
                    turnOverDialog();
                }
                list.clear();
                adapter.notifyDataSetChanged();
                break;
        }
    }

    /**
     * 是否移送对话框
     */
    private void turnOverDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("是否移送?");
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (willRepair) {
                    //如果维修需要移送维修点，则状态改为6，维送
                    asset.setStatus(6);
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable("asset",asset);
                startActivity(SingleAssetTransferActivity.class,bundle,false);
            }
        });
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }

    RepairHandler handler = new RepairHandler();

    class RepairHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AssetsUtil.SEARCH_ONE_ASSET:
                    list = (List<AssetInfo>) msg.getData().getSerializable("assets");
                    asset = list.get(0);
                    //如果有资产且其状态为损坏时，修好按钮可用；只有正常状态下的资产可以报修，待移交、
                    //待报废等非正常状态下的资产不能报修
                    adapter = new AssetRecyclerViewAdapter(AssetRepairActivity.this,
                            list, true);
                    rvSingleAssetManage.setAdapter(adapter);
                    if (asset.getStatus() == 1) {
                        btnSingleAssetManageCancel.setEnabled(true);
                    } else if (asset.getStatus() == 0) {
                        String manager = asset.getOldManager().getObjectId();
                        if (!manager.equals(BmobUser.getCurrentUser().getObjectId())) {
                            toast("对不起，您不是该资产管理员！");
                            return;
                        } else {
                            btnSingleAssetManageOk.setEnabled(true);
                        }
                    } else {
                        btnSingleAssetManageOk.setEnabled(false);
                        btnSingleAssetManageCancel.setEnabled(false);
                    }

                    break;
            }
        }
    }
    /**
     * 扫描二维码点击事件
     */
    public  void customScan() {
        new IntentIntegrator(this)
                .setOrientationLocked(false)
                .setCaptureActivity(CustomScanActivity.class) // 设置自定义的activity是CustomActivity
                .initiateScan(); // 初始化扫描
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(this, "内容为空", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "扫描成功", Toast.LENGTH_LONG).show();
                // ScanResult 为 获取到的字符串
                ScanResult = intentResult.getContents();
                etSearchAssetNum.setText(ScanResult);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
