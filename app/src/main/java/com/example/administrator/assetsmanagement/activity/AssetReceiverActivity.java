package com.example.administrator.assetsmanagement.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.example.administrator.assetsmanagement.Interface.AssetSelectedListener;
import com.example.administrator.assetsmanagement.Interface.ToolbarClickListener;
import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.adapter.AssetRecyclerViewAdapter;
import com.example.administrator.assetsmanagement.base.ParentWithNaviActivity;
import com.example.administrator.assetsmanagement.bean.AssetInfo;
import com.example.administrator.assetsmanagement.bean.AssetPicture;
import com.example.administrator.assetsmanagement.bean.Person;
import com.example.administrator.assetsmanagement.utils.AssetsUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;

/**
 * Created by Administrator on 2017/12/17 0017.
 */

public class AssetReceiverActivity extends ParentWithNaviActivity {
    @BindView(R.id.rc_receiver_assets)
    RecyclerView mRcReceiverAssets;
    List<AssetInfo> mAssetInfoList = new ArrayList<>();
    List<AssetInfo> temp_list = new ArrayList<>();
    List<AssetInfo> selectedList = new ArrayList<>();
    AssetRecyclerViewAdapter adapter;
    @BindView(R.id.btn_receive_ok)
    Button mBtnReceiverOk;

    @Override
    public String title() {
        return "资产接收";
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
        setContentView(R.layout.activity_assets_receiver);
        ButterKnife.bind(this);
        initNaviView();
        mBtnReceiverOk.setEnabled(false);

        LinearLayoutManager ll = new LinearLayoutManager(this);
        mRcReceiverAssets.setLayoutManager(ll);
        Person person = BmobUser.getCurrentUser(Person.class);
        List<AssetInfo> allList = new ArrayList<>();
        AssetsUtil.count=0;
        AssetsUtil.OrAndQueryAssets(this, "mStatus", 4,"mStatus",
                6,"mNewManager", person,  handler,allList);
    }

    @OnClick(R.id.btn_receive_ok)
    public void onViewClicked(View view) {
        if (selectedList.size() > 0) {
            AssetsUtil.updateBmobLibrary(this, updateAllSelectedAssetInfo(mAssetInfoList, selectedList));
            temp_list.clear();
            temp_list.addAll(AssetsUtil.GroupAfterMerge(AssetsUtil.deepCopy(mAssetInfoList)));
            adapter.initMap();
            adapter.notifyDataSetChanged();
        } else {
            toast("请选择要接收的资产！");
        }

    }

    /**
     * 用于处理查询结果的异步处理器
     */
    ReceiverHandler handler = new ReceiverHandler();

    class ReceiverHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AssetsUtil.SEARCH_ONE_ASSET:
                    //因为经过适配器处理后的列表是汇总后的，所以要保持原有列表用于修改管理员，这样
                    //就必须复制一份临时列表用于处理显示。
                    mAssetInfoList = (List<AssetInfo>) msg.getData().getSerializable("assets");
                    if (mAssetInfoList.size() > 0) {
                        mBtnReceiverOk.setEnabled(true);
                    }
                    temp_list.addAll(AssetsUtil.GroupAfterMerge(AssetsUtil.deepCopy(mAssetInfoList)));
                    setListAdapter();
                    break;
            }
        }
    }

    /**
     * 创建适配器
     */
    private void setListAdapter() {
        adapter = new AssetRecyclerViewAdapter(this, temp_list, false);
        adapter.getAssetSelectListener(new AssetSelectedListener() {
            @Override
            public void selectAsset(AssetInfo assetInfo) {
                selectedList.add(assetInfo);
            }

            @Override
            public void cancelAsset(AssetInfo assetInfo) {
                selectedList.remove(assetInfo);
            }
        });
        mRcReceiverAssets.setAdapter(adapter);
    }

    /**
     * 修改资产信息
     */
    private void updateAssetInfo(AssetInfo asset) {
        asset.setOldManager(BmobUser.getCurrentUser(Person.class));
        Person person = new Person();
        asset.setNewManager(person);
        //如果资产为移交状态，接收后改为0,否则改为1，维修状态。因为只有正常和维修的可以移交。
        // 丢失的不能移交，报废的暂时也不考虑移交。
        if (asset.getStatus() == 4) {
            asset.setStatus(0);
        } else if (asset.getStatus() == 6) {
            asset.setStatus(1);
        }


    }

    /**
     * 遍历资产列表，修改所有图片等于传入编号的资产，并返回对象列表
     */
    private List<BmobObject> updateAllSameImangeNumAssets(List<AssetInfo> list, AssetInfo asset1) {
        List<BmobObject> objects = new ArrayList<>();
        List<AssetInfo> updated = new ArrayList<>();
        String imageNum = asset1.getPicture().getImageNum();
        Integer  state = asset1.getStatus();
        for (AssetInfo asset : list) {
            if (imageNum.equals(asset.getPicture().getImageNum()) && asset.getStatus().equals(state)) {
                updateAssetInfo(asset);
                objects.add(asset);
                updated.add(asset);//将已经更新的资产暂时存入临时列表中以备移除
            }
        }
        list.removeAll(updated);
        return objects;
    }

    /**
     * 遍历所有资产，修改已选择的资产信息
     */
    private List<BmobObject> updateAllSelectedAssetInfo(List<AssetInfo> assetsList, List<AssetInfo> selectedAssets) {
        List<BmobObject> objects = new ArrayList<>();
        for (AssetInfo asset : selectedAssets) {
            List<BmobObject> selectObject = updateAllSameImangeNumAssets(assetsList,
                    asset);
            objects.addAll(selectObject);

        }
        return objects;
    }
}
