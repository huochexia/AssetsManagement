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

import com.example.administrator.assetsmanagement.Interface.AssetSelectedListener;
import com.example.administrator.assetsmanagement.Interface.ToolbarClickListener;
import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.adapter.AssetRecyclerViewAdapter;
import com.example.administrator.assetsmanagement.base.ParentWithNaviActivity;
import com.example.administrator.assetsmanagement.bean.AssetInfo;
import com.example.administrator.assetsmanagement.bean.Person;
import com.example.administrator.assetsmanagement.treeUtil.BaseNode;
import com.example.administrator.assetsmanagement.treeUtil.NodeHelper;
import com.example.administrator.assetsmanagement.utils.AssetsUtil;
import com.example.administrator.assetsmanagement.utils.LineEditText;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.UpdateListener;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * 资产移交，可以单个资产（通过编号或扫码）获得资产，也可以整批（位置和管理员）获得资产进行移
 * 交。如果某位置的资产只能移交部分，那么就只能采取个别资产移交，因为一资产一编号。对于某一位置
 * 的可以选择只移交某一种资产。但是这一种资产必须整体移交，如果个别的不移交，则可以先整体然后个别
 * 的再反移交一次
 * Created by Administrator on 2017/12/8.
 */

public class AssetsTurnOverActivity extends ParentWithNaviActivity {
    public static final int REQUEST_SELECTED = 100;
    public static final int REQUEST_RECEIVE_LOCATION = 101;
    public static final int REQUEST_RECEIVE_DEPT = 102;
    public static final int REQUEST_RECEIVE_MANAGER = 103;
    public static final int REQUEST_SELECTE_MANAGER = 104;

    public static final int SEARCH_LOCATION = 1;
    public static final int SEARCH_DEPARTMENT = 3;
    public static final int SEARCH_MANAGER = 4;


    private BaseNode mNode;//接收传入位置、部门和原管理员信息的节点
    private BaseNode mNewLocation, mNewDept;
    private int select_type = SEARCH_LOCATION; //拟选择的类型，位置、部门、管理员
    private Person mOldManager, mNewManager;
    @BindView(R.id.ll_assets_turn_over_top)
    LinearLayout mLlAssetsTurnOverTop;
    @BindView(R.id.et_search_asset_num)
    LineEditText mEtSearchAssetNum;
    @BindView(R.id.tv_receive_new_location)
    TextView mTvReceiveNewLocation;
    @BindView(R.id.tv_receive_new_dept)
    TextView mTvReceiveNewDept;
    @BindView(R.id.tv_receive_new_manager)
    TextView mTvNewManager;
    @BindView(R.id.iv_left_navi)
    ImageView ivLeftNavi;
    @BindView(R.id.rg_assets_turn_over)
    RadioGroup mRgAssetsTurnOver;
    @BindView(R.id.iv_barcode_2d)
    ImageView mIvBarcode2d;
    @BindView(R.id.btn_single_asset_search)
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


    @BindView(R.id.btn_receive_manager)
    FancyButton mBtnTurnOverReceiver;
    @BindView(R.id.btn_turn_over_ok)
    FancyButton mBtnTurnOverOk;

    @BindView(R.id.rl_single_asset)
    RelativeLayout mRlSingleAsset;
    @BindView(R.id.ll_overall_asset)
    LinearLayout mLlOverallAsset;
    @BindView(R.id.rg_assets_turn_over_range)
    RadioGroup mRgAssetsTurnOverRange;

    /**
     * 传入的是原始列表，深度复制为中转列表，如果是整体移交则将中转列表汇总。列表适配器使用的是中转
     * 列表。用户选择内容的存入选择列表中。然后依据选择列表的内容在原始列表中进行更新，然后在从原始
     * 列表中去除选择列表
     */
    private List<AssetInfo> assetsList = new ArrayList<>();//原始列表
    private List<AssetInfo> temp_list = new ArrayList<>();//中转列表
    private List<AssetInfo> selectedAssets = new ArrayList<>();//选择列表

    private AssetRecyclerViewAdapter adapter;
    private RecyclerView mRcTurnOverList;
    private Integer flag = 0;//标志，等于1是为新登记资产。
    private boolean isSingle = true;//判断是单体还是整体

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
        LinearLayoutManager ll = new LinearLayoutManager(AssetsTurnOverActivity.this);
        mRcTurnOverList.setLayoutManager(ll);

        Bundle bundle = getBundle();
        if (bundle != null) {
            flag = bundle.getInt("turn_over");
            isSingle = bundle.getBoolean("oneOrAll");
            if (flag == 1) {
                mLlAssetsTurnOverTop.setVisibility(View.GONE);
                mBtnTurnOverOk.setEnabled(true);
                assetsList = (List<AssetInfo>) bundle.getSerializable("assets");
                if (isSingle) {
                    //单体移交因为不进行汇总操作，所以不需要复制
                    temp_list.addAll(assetsList);
                } else {
                    temp_list.addAll(AssetsUtil.mergeAndSum(AssetsUtil.deepCopy(assetsList)));
                }
                setListAdapter();
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
                selectedAssets.add(assetInfo);
            }

            @Override
            public void cancelAsset(AssetInfo assetInfo) {
                selectedAssets.remove(assetInfo);
            }
        });
        mRcTurnOverList.setAdapter(adapter);
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
                        isSingle = true;
                        clearLists();
                        break;
                    case R.id.rb_overall_assets:
                        mLlOverallAsset.setVisibility(View.VISIBLE);
                        mRlSingleAsset.setVisibility(View.GONE);
                        mBtnTurnOverOk.setEnabled(false);
                        isSingle = false;
                        clearLists();
                        break;
                    case R.id.rb_new_assets:

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
                        clearLists();
                        select_type = SEARCH_LOCATION;
                        mTvSearchContent.setText("");
                        mBtnSearchLocation.setVisibility(View.VISIBLE);
                        break;

                    case R.id.rb_turn_over_manager:
                        changeBtnStatus();
                        clearLists();
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

    @OnClick({R.id.btn_single_asset_search, R.id.btn_search_location, R.id.btn_search_manager,
            R.id.btn_search_start, R.id.btn_receive_manager, R.id.btn_turn_over_ok, R.id.btn_receive_location,
            R.id.btn_receive_dept})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_single_asset_search:
                clearLists();
                searchAssets("mAssetsNum", mEtSearchAssetNum.getText().toString().trim());
                break;
            case R.id.btn_search_location:
                clearLists();
                getSelectedInfo(SelectedTreeNodeActivity.SEARCH_LOCATION, false, REQUEST_SELECTED);
                break;
            case R.id.btn_search_manager:
                clearLists();
//                getSelectedInfo(SelectedTreeNodeActivity.SEARCH_MANAGER, true,REQUEST_SELECTED);
                Intent intent = new Intent(AssetsTurnOverActivity.this, ManagerListActivity.class);
                startActivityForResult(intent, REQUEST_SELECTE_MANAGER);
                break;

            case R.id.btn_search_start:
                clearLists();
                getSearchResultList();
                break;
            case R.id.btn_receive_location:
                getSelectedInfo(SelectedTreeNodeActivity.SEARCH_LOCATION, false, REQUEST_RECEIVE_LOCATION);
                break;
            case R.id.btn_receive_dept:
                getSelectedInfo(SelectedTreeNodeActivity.SEARCH_DEPARTMENT, false, REQUEST_RECEIVE_DEPT);
                break;
            case R.id.btn_receive_manager:
                Intent intent1 = new Intent(AssetsTurnOverActivity.this, ManagerListActivity.class);
                startActivityForResult(intent1, REQUEST_RECEIVE_MANAGER);
                break;
            case R.id.btn_turn_over_ok:
                //如果是新登记的，位置和部门不能为空。
                // 这里比较复杂，因为新登记时创建的适配器对应的是assetsList列表。
                //已登记资产移交时创建的适配器对应的是temp_list列表
                if (selectedAssets.size()>0) {
                    if (mNewManager != null) {
                        if (flag == 1 && (mNewLocation == null || mNewDept == null)) {
                            toast("新登记资产必须分配位置和部门！");
                        } else {
                            updateBmobLibrary(updateAllSelectedAssetInfo(assetsList,selectedAssets));
                            temp_list.clear();
                            if (isSingle) {
                                temp_list.addAll(assetsList);
                            } else {
                                temp_list.addAll(AssetsUtil.mergeAndSum(AssetsUtil.deepCopy(assetsList)));
                            }
                            adapter.initMap();
                            adapter.notifyDataSetChanged();
                        }
                        initAssetsInfo();
                    } else {
                        toast("请选择接受人！");
                    }
                } else {
                    toast("没有选择资产！");
                }
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_SELECTED:
                if (resultCode == SelectedTreeNodeActivity.SEARCH_RESULT_OK) {
                    mNode = (BaseNode) data.getSerializableExtra("node");
                    mTvSearchContent.setText(NodeHelper.getSearchContentName(mNode));
                }
                break;
            case REQUEST_SELECTE_MANAGER://原管理员
                if (resultCode == ManagerListActivity.SEARCH_OK) {
                    mOldManager = (Person) data.getSerializableExtra("manager");
                    mTvSearchContent.setText(mOldManager.getUsername());
                }
                break;
            case REQUEST_RECEIVE_LOCATION:
                if (resultCode == SelectedTreeNodeActivity.SEARCH_RESULT_OK) {
                    mNewLocation = (BaseNode) data.getSerializableExtra("node");
                    mTvReceiveNewLocation.setText(NodeHelper.getSearchContentName(mNewLocation));
                }
                break;
            case REQUEST_RECEIVE_DEPT:
                if (resultCode == SelectedTreeNodeActivity.SEARCH_RESULT_OK) {
                    mNewDept = (BaseNode) data.getSerializableExtra("node");
                    mTvReceiveNewDept.setText(NodeHelper.getSearchContentName(mNewDept));
                }
                break;
            case REQUEST_RECEIVE_MANAGER:
                if (resultCode == ManagerListActivity.SEARCH_OK) {
                    mNewManager = (Person) data.getSerializableExtra("manager");
                    mTvNewManager.setText(mNewManager.getUsername());
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
    private void getSelectedInfo(int type, boolean isPerson, int requestCode) {
        Intent intent = new Intent(AssetsTurnOverActivity.this, SelectedTreeNodeActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("person", isPerson);
        startActivityForResult(intent, requestCode);
    }



    /**
     * 按编号查询资产
     *
     * @param
     */
    private void searchAssets(String para, String id) {
        BmobQuery<AssetInfo> query = new BmobQuery<>();
        query.addWhereEqualTo(para, id);
        query.include("mPicture");
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

    /**
     * 按人员查询
     *
     * @param para
     * @param person
     */
    private void searchAssets(String para, Person person) {
        BmobQuery<AssetInfo> query = new BmobQuery<>();
        query.addWhereEqualTo(para, person);
        query.include("mPicture");
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
    SearchHandler handler = new SearchHandler();

    class SearchHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    //因为经过适配器处理后的列表是汇总后的，所以要保持原有列表用于修改管理员，这样
                    //就必须复制一份临时列表用于处理显示。
                    assetsList = (List<AssetInfo>) msg.getData().getSerializable("assets");
                    if (assetsList.size() > 0) {
                        mBtnTurnOverOk.setEnabled(true);
                    }
                    temp_list.addAll(AssetsUtil.mergeAndSum(AssetsUtil.deepCopy(assetsList)));
                    setListAdapter();
                    break;
            }
        }
    }

    /**
     * 清空列表
     */
    private void clearLists() {
        if (adapter != null) {
            assetsList.clear();
            temp_list.clear();
            adapter.notifyDataSetChanged();
            mBtnTurnOverOk.setEnabled(false);
        }
    }

    /**
     * 获取查询结果
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
                if (mOldManager != null) {
                    searchAssets("mOldManager", mOldManager);
                }
                break;
        }

    }

    /**
     * 初始化资产的新信息,也就是还原最初状态，避免错误修改
     */
    private void initAssetsInfo() {
        mNewDept = null;
        mNewLocation = null;
        mNewManager = null;
        mTvReceiveNewLocation.setText("");
        mTvReceiveNewDept.setText("");
        mTvNewManager.setText("");
        selectedAssets.clear();
    }

    /**
     * 修改资产信息
     */
    private void updateAssetInfo(AssetInfo asset) {
        if (mNewLocation != null) {
            asset.setLocationNum(mNewLocation.getId());
        }
        if (mNewDept != null) {
            asset.setDeptNum(mNewDept.getId());
        }
        asset.setNewManager(mNewManager);
        asset.setStatus(4);
    }

    /**
     * 遍历资产列表，修改所有图片编号等于传入编号的资产，并返回对象列表
     */
    private List<BmobObject> updateAllSameImangeNumAssets(List<AssetInfo> list, String imageNum) {
        List<BmobObject> objects = new ArrayList<>();
        List<AssetInfo> updated = new ArrayList<>();
        for (AssetInfo asset : list) {
            if (imageNum.equals(asset.getPicture().getImageNum())) {
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
            if (asset.getQuantity() == 1) {
                updateAssetInfo(asset);
                objects.add(asset);
                assetsList.remove(asset);
            } else {
                List<BmobObject> selectObject = updateAllSameImangeNumAssets(assetsList,
                        asset.getPicture().getImageNum());
                objects.addAll(selectObject);
            }
        }
        return objects;
    }

    /**
     * 将修改好的资产列表保存入数据库
     */
    private void updateBmobLibrary(List<BmobObject> objects) {
        if (objects.size() <= 50) {//如果资产少于等于50时
            new BmobBatch().updateBatch(objects).doBatch(new QueryListListener<BatchResult>() {
                @Override
                public void done(List<BatchResult> list, BmobException e) {
                    if (e == null) {
                        toast("移交更新成功");
                    } else {
                        toast("移交更新失败:" + e.toString());
                    }
                }
            });

        } else {
            //TODO:如果资产大于50时分批处理
            batchUpdate(objects);
        }

    }
//    /**
//     * 个别资产更新
//     *
//     * @param
//     * @param map
//     */
//    private void updateSinglerAssets(Map<Integer, Boolean> map) {
//        List<BmobObject> objects = new ArrayList<>();
//        Iterator iterator = map.entrySet().iterator();
//        while (iterator.hasNext()) {
//            Map.Entry entry = (Map.Entry) iterator.next();
//            int key = (int) entry.getKey();
//            Boolean value = (Boolean) entry.getValue();
//            AssetInfo asset = assetsList.get(key);
//            if (value == true) {
//                if (mNewLocation != null) {
//                    asset.setLocationNum(mNewLocation.getId());
//                }
//                if (mNewDept != null) {
//                    asset.setDeptNum(mNewDept.getId());
//                }
//                asset.setNewManager(mNewManager);
//                asset.setStatus(4);
//                objects.add(assetsList.get(key));
//            }
//        }
//        new BmobBatch().updateBatch(objects).doBatch(new QueryListListener<BatchResult>() {
//            @Override
//            public void done(List<BatchResult> list, BmobException e) {
//                if (e == null) {
//                    toast("移交更新成功");
//                } else {
//                    toast("移交更新失败:" + e.toString());
//                }
//            }
//        });
//        adapter.removeSelectedItem();
//
//    }
//
//    /**
//     * 这是整体变更资产信息。因为依据图片编号。要是个别变更，要依据资产编号
//     *
//     * @param assets
//     */
//    private void updateAllAssets(List<AssetInfo> assets, List<String> number) {
//
//        List<BmobObject> objects = new ArrayList<>();
//        for (String num : number) {
//            for (AssetInfo asset : assets) {
//                //只对选择的资产变更。比较资产图片编号
//                if (num.equals(asset.getPicture().getImageNum())) {
//                    if (mNewLocation != null) {
//                        asset.setLocationNum(mNewLocation.getId());
//                    }
//                    if (mNewDept != null) {
//                        asset.setDeptNum(mNewDept.getId());
//                    }
//                    asset.setNewManager(mNewManager);
//                    asset.setStatus(4);
//                    objects.add(asset);
//                }
//
//            }
//        }
//
//        if (objects.size() <= 50) {//如果资产少于等于50时
//            new BmobBatch().updateBatch(objects).doBatch(new QueryListListener<BatchResult>() {
//                @Override
//                public void done(List<BatchResult> list, BmobException e) {
//                    if (e == null) {
//                        toast("移交更新成功");
//                    } else {
//                        toast("移交更新失败:" + e.toString());
//                    }
//                }
//            });
//
//        } else {
//            //TODO:如果资产大于50时分批处理
//            batchUpdate(objects);
//        }
//    }

    /**
     * 分批处理修改,求出50的倍数和余数。先是倍数，如果余数，再处理余数的，最后处理最后一个。因为List
     * 的subList(fromIndex,toIndex)中不包含toIndex.
     */
    private void batchUpdate(List<BmobObject> objects) {
        int size = objects.size();
        int m = size / 50;//倍数
        int y = size % 50;//余数
        //整50的倍数量更新
        for (int i = 0; i < m; i++) {
            final int finalI = i + 1;
            int fromIndex = 50 * i;
            int toIndex = fromIndex + 49;
            new BmobBatch().updateBatch(objects.subList(fromIndex, toIndex)).doBatch(new QueryListListener<BatchResult>() {
                @Override
                public void done(List<BatchResult> list, BmobException e) {
                    if (e == null) {
                        toast("第" + finalI + "批量更新成功");
                    } else {
                        toast("第" + finalI + "批量更新失败:" + e.toString());
                    }
                }
            });

        }
        //余数量批量更新
        if (y > 0) {
            new BmobBatch().updateBatch(objects.subList(50 * m - 1, size - 1)).doBatch(new QueryListListener<BatchResult>() {
                @Override
                public void done(List<BatchResult> list, BmobException e) {
                    if (e == null) {
                        toast("最后一批量更新成功");
                    } else {
                        toast("最后一批量更新失败:" + e.toString());
                    }
                }
            });

        }
        //最后一个
        BmobObject object = objects.get(size - 1);
        String objectId = object.getObjectId();
        object.update(objectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    toast("最后一个更新成功");
                } else {
                    toast("最后一个更新失败:" + e.toString());
                }
            }

        });

    }
}
