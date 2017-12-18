package com.example.administrator.assetsmanagement.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.administrator.assetsmanagement.Interface.ToolbarClickListener;
import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.adapter.AssetRecyclerViewAdapter;
import com.example.administrator.assetsmanagement.base.ParentWithNaviActivity;
import com.example.administrator.assetsmanagement.bean.AssetInfo;
import com.example.administrator.assetsmanagement.utils.AssetsUtil;
import com.example.administrator.assetsmanagement.utils.LineEditText;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by Administrator on 2017/12/13.
 */

public class AssetBaofeiActivity extends ParentWithNaviActivity {
    @BindView(R.id.rv_single_asset_manage)
    RecyclerView rvSingleAssetManage;
    @BindView(R.id.btn_single_asset_manage_ok)
    FancyButton btnSingleAssetManageOk;
    @BindView(R.id.btn_single_asset_manage_cancel)
    FancyButton btnSingleAssetManageCancel;
    List<AssetInfo> list;
    AssetRecyclerViewAdapter adapter;
    @BindView(R.id.et_search_asset_num)
    LineEditText etSearchAssetNum;

    @Override
    public String title() {
        return "资产报废";
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
        btnSingleAssetManageOk.setText("报废");
        btnSingleAssetManageOk.setEnabled(false);
        btnSingleAssetManageCancel.setText("重用");
        btnSingleAssetManageCancel.setEnabled(false);
        LinearLayoutManager ll = new LinearLayoutManager(this);
        rvSingleAssetManage.setLayoutManager(ll);
    }

    @OnClick({R.id.iv_barcode_2d, R.id.btn_single_asset_search, R.id.btn_single_asset_manage_ok,
            R.id.btn_single_asset_manage_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_barcode_2d:
                break;
            case R.id.btn_single_asset_search:
                String number = etSearchAssetNum.getText().toString();
                AssetsUtil.AndQueryAssets(this,"mAssetsNum",number,handler);
//                searchAssets("mAssetsNum",etSearchAssetNum.getText().toString() );
                break;
            case R.id.btn_single_asset_manage_ok:
                AssetsUtil.changeAssetStatus(this,list.get(0),3);
                list.clear();
                adapter.notifyDataSetChanged();
                break;
            case R.id.btn_single_asset_manage_cancel:
                AssetsUtil.changeAssetStatus(this,list.get(0),0);
                list.clear();
                adapter.notifyDataSetChanged();
                break;
        }
    }


    RepairHandler handler = new RepairHandler();

    class RepairHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    list = (List<AssetInfo>) msg.getData().getSerializable("assets");
                    AssetInfo asset = list.get(0);
                    //如果有资产且其状态为待报废时，“重用”按钮可用；丢失状态和待移交状态下的资产均
                    // 不能进行待报废处理。
                    adapter = new AssetRecyclerViewAdapter(AssetBaofeiActivity.this,
                    list, true);
                    rvSingleAssetManage.setAdapter(adapter);
                    String manager = asset.getOldManager().getObjectId();
                    if (!manager.equals(BmobUser.getCurrentUser().getObjectId())) {
                        toast("对不起，您不是该资产管理员！");
                        return;
                    } else {
                        if (asset.getStatus() == 3) {
                            btnSingleAssetManageCancel.setEnabled(true);
                        } else if(asset.getStatus()!=2 && asset.getStatus()!=4) {
                            btnSingleAssetManageOk.setEnabled(true);
                        }
                    }
                    break;
            }
        }
    }
}
