package com.example.administrator.assetsmanagement.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.assetsmanagement.Interface.AssetSelectedListener;
import com.example.administrator.assetsmanagement.Interface.ToolbarClickListener;
import com.example.administrator.assetsmanagement.MainActivity;
import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.adapter.AssetRecyclerViewAdapter;
import com.example.administrator.assetsmanagement.base.ParentWithNaviActivity;
import com.example.administrator.assetsmanagement.bean.AssetInfo;
import com.example.administrator.assetsmanagement.bean.AssetPicture;
import com.example.administrator.assetsmanagement.bean.Person;
import com.example.administrator.assetsmanagement.treeUtil.BaseNode;
import com.example.administrator.assetsmanagement.treeUtil.NodeHelper;
import com.example.administrator.assetsmanagement.utils.AssetsUtil;
import com.example.administrator.assetsmanagement.utils.LineEditText;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * 资产移交，整批（位置、名称或全部）获得资产进行移交。只有正常状态下的资产才可以移交，非正常资产只
 * 有转变为正常或者通过扫码单独移交。如果某位置的资产只能移交部分，那么就只能采取个别资产移交，因为
 * 一资产一编号。对于某一位置的可以选择只移交某一种资产。但是这一种资产必须整体移交，如果个别的不移交，
 * 则可以先整体然后个别的再反移交一次
 * Created by Administrator on 2017/12/8.
 */

public class AssetsTurnOverActivity extends ParentWithNaviActivity {
    public static final int REQUEST_SELECTED = 100;
    public static final int REQUEST_RECEIVE_LOCATION = 101;
    public static final int REQUEST_RECEIVE_DEPT = 102;
    public static final int REQUEST_RECEIVE_MANAGER = 103;
    public static final int REQUEST_NAME =104;

    public static final int SEARCH_LOCATION = 1;
    public static final int SEARCH_NAME= 2;
    public static final int SEARCH_ALL = 3;



    private BaseNode mNode;//接收传入位置信息的节点
    private BaseNode mNewLocation, mNewDept;
    private int select_type = SEARCH_LOCATION; //拟选择的类型，位置、名称或全部
    private Person  mNewManager;
    @BindView(R.id.ll_assets_turn_over_top)
    LinearLayout mLlAssetsTurnOverTop;

    @BindView(R.id.tv_receive_new_location)
    TextView mTvReceiveNewLocation;
    @BindView(R.id.tv_receive_new_dept)
    TextView mTvReceiveNewDept;
    @BindView(R.id.tv_receive_new_manager)
    TextView mTvNewManager;
    @BindView(R.id.iv_left_navi)
    ImageView ivLeftNavi;
    @BindView(R.id.btn_search_name)
    FancyButton mBtnSearchName;
    @BindView(R.id.btn_search_location)
    FancyButton mBtnSearchLocation;
    @BindView(R.id.tv_search_content)
    TextView mTvSearchContent;
    @BindView(R.id.btn_search_start)
    Button mBtnSearchStart;


    @BindView(R.id.btn_receive_manager)
    FancyButton mBtnTurnOverReceiver;
    @BindView(R.id.btn_turn_over_ok)
    Button mBtnTurnOverOk;

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
    private boolean isSingle = false;//判断是单体还是整体
    private AssetPicture mPicture;


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

                    case R.id.rb_turn_over_picture:
                        changeBtnStatus();
                        clearLists();
                        select_type = SEARCH_NAME;
                        mTvSearchContent.setText("");
                        mBtnSearchName.setVisibility(View.VISIBLE);
                        break;
                    case R.id.rb_turn_over_all:
                        changeBtnStatus();
                        clearLists();
                        select_type= SEARCH_ALL;
                        mTvSearchContent.setText("全部正常资产");
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
        mBtnSearchName.setVisibility(View.GONE);
        mBtnTurnOverOk.setEnabled(false);
    }

    /**
     * 处理所有按钮事件
     *
     * @param view
     */

    @OnClick({ R.id.btn_search_location,R.id.btn_search_name,R.id.btn_search_start,
            R.id.btn_receive_manager, R.id.btn_turn_over_ok, R.id.btn_receive_location,
            R.id.btn_receive_dept})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_search_location:
                clearLists();
                getSelectedInfo(SelectedTreeNodeActivity.SEARCH_LOCATION, false, REQUEST_SELECTED);
                break;
            case R.id.btn_search_name:
                clearLists();
                Intent intentPhoto = new Intent(this, SelectAssetsPhotoActivity.class);
                intentPhoto.putExtra("isRegister", false);
                startActivityForResult(intentPhoto,REQUEST_NAME );
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
                if (selectedAssets.size() > 0) {
                    if (mNewManager != null) {
                        if (flag == 1 && (mNewLocation == null || mNewDept == null)) {
                            toast("新登记资产必须分配位置和部门！");
                        } else {
                            if (flag == 1) {//新登记资产为添加
                                AssetsUtil.insertBmobLibrary(this, updateAllSelectedAssetInfo(assetsList, selectedAssets));
                            } else {//原有资产移交为变更
                                AssetsUtil.updateBmobLibrary(this, updateAllSelectedAssetInfo(assetsList, selectedAssets));
                            }
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
            case REQUEST_NAME:
                if (resultCode == SelectAssetsPhotoActivity.RESULT_OK) {
                    Bundle  bundle = data.getBundleExtra("assetpicture");
                    mPicture = (AssetPicture) bundle.getSerializable("imageFile");
                    mTvSearchContent.setText(mPicture.getImageNum());
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
     * 用于处理查询结果的异步处理器
     */
    SearchHandler handler = new SearchHandler();

    class SearchHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AssetsUtil.SEARCH_ONE_ASSET:
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
        Person current = BmobUser.getCurrentUser(Person.class);
        switch (select_type) {
            case SEARCH_LOCATION:
                if (mNode != null) {
                    AssetsUtil.AndQueryAssets(this, "mLocationNum", mNode.getId(),
                            "mOldManager", current,"mStatus",0, handler);
                }
                break;
            case SEARCH_NAME:
                AssetsUtil.AndQueryAssets(this, "mPicture",mPicture,
                        "mOldManager", current,"mStatus",0, handler);
                break;
            case SEARCH_ALL:
                AssetsUtil.AndQueryAssets(this, "mStatus",0,
                        "mOldManager", current, handler);
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
            if (flag == 1) {
                if (asset.getQuantity() == 1) {
                    updateAssetInfo(asset);
                    objects.add(asset);
                    assetsList.remove(asset);
                } else {
                    List<BmobObject> selectObject = updateAllSameImangeNumAssets(assetsList,
                            asset.getPicture().getImageNum());
                    objects.addAll(selectObject);
                }
            } else {
                List<BmobObject> selectObject = updateAllSameImangeNumAssets(assetsList,
                        asset.getPicture().getImageNum());
                objects.addAll(selectObject);
            }

        }
        return objects;
    }


}
