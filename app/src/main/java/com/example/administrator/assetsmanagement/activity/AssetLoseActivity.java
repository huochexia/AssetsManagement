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

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by Administrator on 2017/12/13.
 */

public class AssetLoseActivity extends ParentWithNaviActivity {
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
        return "资产挂失";
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
        btnSingleAssetManageOk.setText("挂失");
        btnSingleAssetManageOk.setEnabled(false);
        btnSingleAssetManageCancel.setText("找回");
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
                searchAssets("mAssetsNum",etSearchAssetNum.getText().toString() );
                break;
            case R.id.btn_single_asset_manage_ok:
                AssetsUtil.changeAssetStatus(this,list.get(0),2);
                list.clear();
                searchAssets("mAssetsNum",etSearchAssetNum.getText().toString() );
                break;
            case R.id.btn_single_asset_manage_cancel:
                AssetsUtil.changeAssetStatus(this,list.get(0),0);
                list.clear();
                searchAssets("mAssetsNum",etSearchAssetNum.getText().toString() );
                break;
        }
    }

    /**
     * 查询资产
     *
     * @param
     */
    private void searchAssets(String para, String id) {
        BmobQuery<AssetInfo> query = new BmobQuery<>();
        query.addWhereEqualTo(para, id);
        query.findObjects(new FindListener<AssetInfo>() {
            @Override
            public void done(final List<AssetInfo> list, BmobException e) {
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Message msg = new Message();
                                msg.what = 1;
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("assets", (Serializable) list);
                                msg.setData(bundle);
                                handler.sendMessage(msg);
                            }
                        }).start();
                    } else {
                        toast("没有符合条件的资产！");
                    }
                } else {
                    {
                        toast("查询失败，请稍后再查！");
                    }
                }
            }
        });
    }

    RepairHandler handler = new RepairHandler();

    class RepairHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    list = (List<AssetInfo>) msg.getData().getSerializable("assets");
                    AssetInfo asset = list.get(0);
                    //如果有资产且其状态为丢失时，找回按钮可用；任何状态下的资产均可能发生丢失
                    if (asset != null && asset.getStatus() == 2) {
                        btnSingleAssetManageCancel.setEnabled(true);
                    } else  {
                        btnSingleAssetManageOk.setEnabled(true);
                    }
                    adapter = new AssetRecyclerViewAdapter(AssetLoseActivity.this,
                            list, true);
                    rvSingleAssetManage.setAdapter(adapter);
                    break;
            }
        }
    }
}
