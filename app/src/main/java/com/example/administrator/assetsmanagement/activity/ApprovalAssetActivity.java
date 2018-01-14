package com.example.administrator.assetsmanagement.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.administrator.assetsmanagement.Interface.AssetSelectedListener;
import com.example.administrator.assetsmanagement.Interface.ToolbarClickListener;
import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.adapter.AssetRecyclerViewAdapter;
import com.example.administrator.assetsmanagement.base.ParentWithNaviActivity;
import com.example.administrator.assetsmanagement.bean.AssetInfo;
import com.example.administrator.assetsmanagement.bean.Person;
import com.example.administrator.assetsmanagement.utils.AssetsUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Administrator on 2017/12/31 0031.
 */

public class ApprovalAssetActivity extends ParentWithNaviActivity {

    List<AssetInfo> baofeiAssets;
    List<AssetInfo>  mSelectedAssets= new ArrayList<>();
    AssetRecyclerViewAdapter adapter;
    @BindView(R.id.rc_approval_assets_list)
    RecyclerView mRcApprovalAssetsList;

    @Override
    public String title() {
        return "审批拟报废资产";
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
        setContentView(R.layout.activity_approval_assets);
        ButterKnife.bind(this);
        initNaviView();
        LinearLayoutManager ll = new LinearLayoutManager(this);
        mRcApprovalAssetsList.setLayoutManager(ll);
        List<AssetInfo> allList = new ArrayList<>();
        AssetsUtil.count=0;
        AssetsUtil.AndQueryAssets(ApprovalAssetActivity.this,"mStatus",3,handler,allList);

    }

    @OnClick({R.id.btn_approval_asset_ok, R.id.btn_approval_asset_cancel})
    public void onViewClicked(View view) {
        List<BmobObject> objects = new ArrayList<>();
        switch (view.getId()) {
            case R.id.btn_approval_asset_ok:
                for (AssetInfo asset : mSelectedAssets) {
                    asset.setStatus(5);
                    objects.add(asset);
                }
                break;
            case R.id.btn_approval_asset_cancel:
                for (AssetInfo asset : mSelectedAssets) {
                    asset.setStatus(0);
                    objects.add(asset);
                }
                break;
        }
        AssetsUtil.updateBmobLibrary(this,objects);
        baofeiAssets.removeAll(mSelectedAssets);
        mSelectedAssets.clear();
        adapter.initMap();
        adapter.notifyDataSetChanged();
    }

    ApprovalHandler handler = new ApprovalHandler();
    class ApprovalHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AssetsUtil.SEARCH_ONE_ASSET:
                    baofeiAssets = (List<AssetInfo>) msg.getData().getSerializable("assets");
                    adapter = new AssetRecyclerViewAdapter(ApprovalAssetActivity.this,
                            baofeiAssets,false);
                    adapter.getAssetSelectListener(new AssetSelectedListener() {
                        @Override
                        public void selectAsset(AssetInfo assetInfo) {
                            mSelectedAssets.add(assetInfo);
                        }

                        @Override
                        public void cancelAsset(AssetInfo assetInfo) {
                            mSelectedAssets.remove(assetInfo);
                        }
                    });
                    mRcApprovalAssetsList.setAdapter(adapter);
                    break;

            }
        }
    }
}
