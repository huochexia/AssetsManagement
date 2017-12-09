package com.example.administrator.assetsmanagement.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.assetsmanagement.Interface.ToolbarClickListener;
import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.adapter.AssetRecyclerViewAdapter;
import com.example.administrator.assetsmanagement.base.ParentWithNaviActivity;
import com.example.administrator.assetsmanagement.bean.AssetInfo;
import com.example.administrator.assetsmanagement.treeUtil.BaseNode;
import com.example.administrator.assetsmanagement.treeUtil.NodeHelper;
import com.example.administrator.assetsmanagement.utils.LineEditText;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * 资产移交，可以单个资产（通过编号或扫码）获得资产，也可以整批（位置、部门和管理员）获得资产进行移
 * 交。选择接收人并确定后，将修改资产的mNewManager属性接收人，同时将状态改为4待移交。
 * Created by Administrator on 2017/12/8.
 */

public class AssetsTurnOverActivity extends ParentWithNaviActivity {
    public static final int SELECTED_REQUEST = 100;
    public static final int RECEIVER_SELECT = 101;

    public static final int SEARCH_LOCATION = 1;
    public static final int SEARCH_DEPARTMENT = 3;
    public static final int SEARCH_MANAGER = 4;


    private BaseNode mNode;//接收传入位置、部门和原管理员信息的节点
    private int select_type = SEARCH_LOCATION; //拟选择的类型，位置、部门、管理员
    private BaseNode mReceiver;//拟接收者的信息节点

    @BindView(R.id.et_search_asset_num)
    LineEditText mEtSearchAssetNum;
    @BindView(R.id.tv_turn_over_receiver)
    TextView mTvTurnOverReceiver;
    @BindView(R.id.iv_left_navi)
    ImageView ivLeftNavi;
    @BindView(R.id.rg_assets_turn_over)
    RadioGroup mRgAssetsTurnOver;
    @BindView(R.id.iv_barcode_2d)
    ImageView mIvBarcode2d;
    @BindView(R.id.btn_turn_over_search)
    FancyButton mBtnTurnOverSearch;
    @BindView(R.id.btn_search_location)
    FancyButton mBtnSearchLocation;
    @BindView(R.id.btn_register_category)
    FancyButton mBtnRegisterCategory;
    @BindView(R.id.btn_search_name)
    FancyButton mBtnSearchName;
    @BindView(R.id.btn_search_manager)
    FancyButton mBtnSearchManager;
    @BindView(R.id.btn_search_dept)
    FancyButton mBtnSearchDept;
    @BindView(R.id.tv_search_content)
    TextView mTvSearchContent;
    @BindView(R.id.btn_search_start)
    FancyButton mBtnSearchStart;


    @BindView(R.id.btn_turn_over_receiver)
    FancyButton mBtnTurnOverReceiver;
    @BindView(R.id.btn_turn_over_ok)
    FancyButton mBtnTurnOverOk;

    @BindView(R.id.rl_single_asset)
    RelativeLayout mRlSingleAsset;
    @BindView(R.id.ll_overall_asset)
    LinearLayout mLlOverallAsset;
    @BindView(R.id.rg_assets_turn_over_range)
    RadioGroup mRgAssetsTurnOverRange;


    private List<AssetInfo> select_list;
    private AssetRecyclerViewAdapter adapter;
    RecyclerView mRcTurnOverList;
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
        setContentView(R.layout.activity_asset_turn_over);
        ButterKnife.bind(this);
        initNaviView();
        initEvent();
        mBtnTurnOverOk.setEnabled(false);
        mRcTurnOverList = (RecyclerView) findViewById(R.id.rc_turn_over_list);
        LinearLayoutManager ll = new LinearLayoutManager(this);
        mRcTurnOverList.setLayoutManager(ll);
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        mRgAssetsTurnOver.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_single_asset:
                        mRlSingleAsset.setVisibility(View.VISIBLE);
                        mLlOverallAsset.setVisibility(View.GONE);
                        mBtnTurnOverOk.setEnabled(false);
                        break;
                    case R.id.rb_overall_assets:
                        mLlOverallAsset.setVisibility(View.VISIBLE);
                        mRlSingleAsset.setVisibility(View.GONE);
                        mBtnTurnOverOk.setEnabled(false);
                        break;
                }
            }
        });
        mRgAssetsTurnOverRange.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_turn_over_location:
                        changeBtnStatus();
                        select_type = SEARCH_LOCATION;
                        mTvSearchContent.setText("");
                        mBtnSearchLocation.setVisibility(View.VISIBLE);
                        break;
                    case R.id.rb_turn_over_dept:
                        changeBtnStatus();
                        select_type = SEARCH_DEPARTMENT;
                        mTvSearchContent.setText("");
                        mBtnSearchDept.setVisibility(View.VISIBLE);
                        break;
                    case R.id.rb_turn_over_manager:
                        changeBtnStatus();
                        select_type = SEARCH_MANAGER;
                        mTvSearchContent.setText("");
                        mBtnSearchManager.setVisibility(View.VISIBLE);
                        break;

                }
            }
        });
    }

    /**
     * 改变按钮状态
     */
    private void changeBtnStatus() {
        mBtnSearchLocation.setVisibility(View.GONE);
        mBtnSearchManager.setVisibility(View.GONE);
        mBtnSearchDept.setVisibility(View.GONE);
        mBtnTurnOverOk.setEnabled(false);
    }

    /**
     * 处理所有按钮事件
     *
     * @param view
     */

    @OnClick({R.id.btn_turn_over_search, R.id.btn_search_location, R.id.btn_search_manager, R.id.btn_search_dept,
            R.id.btn_search_start, R.id.btn_turn_over_receiver, R.id.btn_turn_over_ok,
            })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_turn_over_search:
                clearLists();
                searchAssets("mAssetsNum", mEtSearchAssetNum.getText().toString().trim());
                break;
            case R.id.btn_search_location:
                getSelectedInfo(SelectedTreeNodeActivity.SEARCH_LOCATION, false);
                break;
            case R.id.btn_search_manager:
                getSelectedInfo(SelectedTreeNodeActivity.SEARCH_MANAGER, true);
                break;
            case R.id.btn_search_dept:
                getSelectedInfo(SelectedTreeNodeActivity.SEARCH_DEPARTMENT, false);
                break;
            case R.id.btn_search_start:
                getSearchResultList();
                break;
            case R.id.btn_turn_over_receiver:
                getReceiver(SelectedTreeNodeActivity.SEARCH_MANAGER, true);
                break;
            case R.id.btn_turn_over_ok:
                if (mReceiver != null) {
                    updateManager(select_list);
                    mBtnTurnOverOk.setEnabled(false);
                } else {
                    toast("请选择接受人！");
                }

                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SELECTED_REQUEST:
                if (resultCode == SelectedTreeNodeActivity.SEARCH_RESULT_OK) {
                    mNode = (BaseNode) data.getSerializableExtra("node");
                    mTvSearchContent.setText(NodeHelper.getSearchContentName(mNode));
                }
                break;
            case RECEIVER_SELECT:
                if (resultCode == SelectedTreeNodeActivity.SEARCH_RESULT_OK) {
                    mReceiver = (BaseNode) data.getSerializableExtra("node");
                    mTvTurnOverReceiver.setText(NodeHelper.getSearchContentName(mReceiver));
                }
            default:
        }
    }

    /**
     * 获取拟查询信息
     *
     * @param type
     * @param isPerson
     */
    private void getSelectedInfo(int type, boolean isPerson) {
        Intent intent = new Intent(AssetsTurnOverActivity.this, SelectedTreeNodeActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("person", isPerson);
        startActivityForResult(intent, SELECTED_REQUEST);
    }

    /**
     * 获取接收人信息
     *
     * @param type
     * @param isPerson
     */
    private void getReceiver(int type, boolean isPerson) {
        Intent intent = new Intent(AssetsTurnOverActivity.this, SelectedTreeNodeActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("person", isPerson);
        startActivityForResult(intent, RECEIVER_SELECT);
    }

    /**
     * 查询资产
     *
     * @param
     */
    private void searchAssets(String para, String id) {
        BmobQuery<AssetInfo> query = new BmobQuery<>();
        query.addWhereEqualTo(para, id);
        query.findObjects(AssetsTurnOverActivity.this, new FindListener<AssetInfo>() {
            @Override
            public void onSuccess(final List<AssetInfo> list) {
                if (list != null && list.size() > 0) {
                    Message msg = new Message();
                    msg.what = 1;
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("assets", (Serializable) list);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                } else {
                    toast("没有符合条件的资产！");
                }

            }

            @Override
            public void onError(int i, String s) {
                toast("查询失败，请稍后再查！");
            }
        });
    }

    SelectedHandler handler = new SelectedHandler();

    class SelectedHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    //因为经过适配器处理后的列表是汇总后的，所以要保持原有列表用于修改管理员，这样
                    //就必须复制一份临时列表用于处理显示。
                    select_list = (List<AssetInfo>) msg.getData().getSerializable("assets");
                    if (select_list.size() > 0) {
                        mBtnTurnOverOk.setEnabled(true);
                    }
                    List<AssetInfo> temp = new ArrayList<>();
                    for (AssetInfo asset : select_list) {
                        try {
                            AssetInfo ass1 = (AssetInfo) asset.clone();
                            temp.add(ass1);
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                        }

                    }
                    adapter = new AssetRecyclerViewAdapter(AssetsTurnOverActivity.this, temp);
                    mRcTurnOverList.setAdapter(adapter);
                    break;
            }
        }
    }

    /**
     * 清空列表
     */
    private void clearLists() {
        if (adapter != null) {
            select_list.clear();
            adapter.notifyDataSetChanged();
            mBtnTurnOverOk.setEnabled(false);
        }
    }

    /**
     *获取查询结果
     */
    private void getSearchResultList() {
        switch (select_type) {
            case SEARCH_LOCATION:
                if (mNode != null) {
                    searchAssets("mLocationNum", mNode.getId());
                }
                break;
            case SEARCH_DEPARTMENT:
                if (mNode != null) {
                    searchAssets("mDeptNum", mNode.getId());
                }
                break;
            case SEARCH_MANAGER:
                if (mNode != null) {
                    searchAssets("mOldManager", mNode.getId());
                }
                break;
        }

    }

    private void updateManager(List<AssetInfo> assets) {
        List<BmobObject> objects = new ArrayList<>();
        for (AssetInfo asset : assets) {
            asset.setNewManager(mReceiver.getId());
            asset.setStatus(4);
            objects.add(asset);
        }
        if (objects.size() <= 50) {//如果资产少于等于50时
            new BmobObject().updateBatch(this, objects, new UpdateListener() {
                @Override
                public void onSuccess() {
                    toast("批量更新成功");
                }

                @Override
                public void onFailure(int code, String msg) {
                    toast("批量更新失败:" + msg);
                }
            });
        } else {
            //TODO:如果资产大于50时分批处理
            batchUpdate(objects);
        }
    }

    /**
     * 分批处理修改,求出50的倍数和余数。先是倍数，如果余数，再处理余数的，最后处理最后一个。因为List
     * 的subList(fromIndex,toIndex)中不包含toIndex.
     */
    private void batchUpdate(List<BmobObject> objects) {
        int size = objects.size();
        int m = size/50;//倍数
        int y = size%50;//余数
        //整50的倍数量更新
        for(int i=0;i<m;i++) {
            final int finalI = i+1;
            int fromIndex = 50*i;
            int toIndex = fromIndex+49;
            new BmobObject().updateBatch(this, objects.subList(fromIndex,toIndex), new UpdateListener() {
                @Override
                public void onSuccess() {
                    toast("第"+ finalI +"批量更新成功");
                }

                @Override
                public void onFailure(int code, String msg) {
                    toast("第"+ finalI +"批量更新失败:" + msg);
                }
            });
        }
        //余数量批量更新
        if (y > 0) {
            new BmobObject().updateBatch(this, objects.subList(50*m-1,size-1), new UpdateListener() {
                @Override
                public void onSuccess() {
                    toast("最后一批量更新成功");
                }

                @Override
                public void onFailure(int code, String msg) {
                    toast("最后一批量更新失败:" + msg);
                }
            });
        }
        //最后一个
           BmobObject object = objects.get(size - 1);
            String objectId = object.getObjectId();
            object.update(this, objectId, new UpdateListener() {
                @Override
                public void onSuccess() {
                    toast("最后一个更新成功");
                }

                @Override
                public void onFailure(int i, String s) {
                    toast("最后一个更新失败:" + s);
                }
            });

    }
}
