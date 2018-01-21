package com.example.administrator.assetsmanagement.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.administrator.assetsmanagement.Interface.AssetItemClickListener;
import com.example.administrator.assetsmanagement.Interface.AssetSelectedListener;
import com.example.administrator.assetsmanagement.Interface.ToolbarClickListener;
import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.adapter.AssetRecyclerViewAdapter;
import com.example.administrator.assetsmanagement.base.ParentWithNaviActivity;
import com.example.administrator.assetsmanagement.bean.AssetInfo;
import com.example.administrator.assetsmanagement.bean.AssetPicture;
import com.example.administrator.assetsmanagement.bean.DepartmentTree.Department;
import com.example.administrator.assetsmanagement.bean.DepartmentTree.DepartmentNodeHelper;
import com.example.administrator.assetsmanagement.bean.LocationTree.Location;
import com.example.administrator.assetsmanagement.bean.LocationTree.LocationNodeHelper;
import com.example.administrator.assetsmanagement.bean.Manager.Person;
import com.example.administrator.assetsmanagement.utils.AssetsUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;

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
    public static final int REQUEST_NAME = 104;

    public static final int SEARCH_LOCATION = 1;
    public static final int SEARCH_NAME = 2;
    public static final int SEARCH_ALL = 3;
    @BindView(R.id.loading_asset_progress)
    ProgressBar loadingAssetProgress;
    @BindView(R.id.ll_query_condition)
    LinearLayout mLlQueryCondition;


    private Location oldLocation;//接收传入位置信息的节点
    private Location mNewLocation;
    private Department mNewDept;
    private int select_type = SEARCH_LOCATION; //拟选择的类型，位置、名称或全部
    private Person mNewManager;
    @BindView(R.id.ll_assets_turn_over_top)
    LinearLayout mLlAssetsTurnOverTop;

    @BindView(R.id.tv_receive_new_location)
    TextView mTvReceiveNewLocation;
    @BindView(R.id.tv_receive_new_dept)
    TextView mTvReceiveNewDept;
    @BindView(R.id.tv_receive_new_manager)
    TextView mTvNewManager;
    @BindView(R.id.btn_search_name)
    Button mBtnSearchName;
    @BindView(R.id.btn_search_location)
    Button mBtnSearchLocation;
    @BindView(R.id.tv_search_content)
    TextView mTvSearchContent;
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
     * 说明：如果资产状态为0、1、4的资产可进行移交。目前暂定为丢失、已报废、待报废（审批中）的资产
     * 不能进行移交，如果要移交的话，先变更为正常0，再进行移交，移交后，再调整为原状态。
     */
    private List<AssetInfo> assetsList = new ArrayList<>();//原始列表
    private List<AssetInfo> temp_list = new ArrayList<>();//中转列表
    private List<AssetInfo> selectedAssets = new ArrayList<>();//选择列表

    private AssetRecyclerViewAdapter adapter;
    private RecyclerView mRcTurnOverList;
    private Integer flag = 0;//标志，等于1是为新登记资产。
    private AssetPicture mPicture;
    private AssetInfo assetInfo;


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
            flag = bundle.getInt("flag");
            if (flag == 1) {
                mLlAssetsTurnOverTop.setVisibility(View.GONE);
                mBtnTurnOverOk.setEnabled(true);
                assetsList = (List<AssetInfo>) bundle.getSerializable("newasset");
                temp_list.addAll(AssetsUtil.GroupAfterMerge(AssetsUtil.deepCopy(assetsList)));
                setListAdapter();
            }
        }
    }

    /**
     * 重新进入时，刷新
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        //新登记的，在这里还没有存入数据库中，所以不用从重新加载查询。
        if (flag != 1) {
            clearLists();
            getSearchResultList();
            loadingAssetProgress.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 创建适配器
     */
    private void setListAdapter() {
        adapter = new AssetRecyclerViewAdapter(this, temp_list, false);
        adapter.getAssetSelectListener(new AssetSelectedListener() {
            @Override
            public void selectAsset(AssetInfo assetInfo,int position) {
                selectedAssets.add(assetInfo);
            }

            @Override
            public void cancelAsset(AssetInfo assetInfo) {
                selectedAssets.remove(assetInfo);
            }
        });
        mRcTurnOverList.setAdapter(adapter);

        //设置长按事件
        adapter.setAssetItemClickListener(new AssetItemClickListener() {
            @Override
            public void onClick(AssetInfo asset) {
                assetInfo = asset;
            }
        });
        //设置上下文菜单事件
        adapter.setMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case 0:
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("picture", assetInfo.getPicture());
                        bundle.putString("title", assetInfo.getAssetName());
                        startActivity(AssetPictureActivity.class, bundle, false);
                        return true;
                    case 1:
                        Bundle bundle1 = new Bundle();
                        bundle1.putInt("flag", 0);
                        bundle1.putSerializable("picture", assetInfo.getPicture());
                        bundle1.putString("para", "mStatus");
                        bundle1.putSerializable("value", assetInfo.getStatus());
                        bundle1.putString("para1", "mOldManager");
                        Person person = BmobUser.getCurrentUser(Person.class);
                        bundle1.putSerializable("value1", person);
                        startActivity(MakingLabelActivity.class, bundle1, false);
                        return true;
                    default:
                        return true;
                }
            }
        });
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
                        mLlQueryCondition.setVisibility(View.VISIBLE);
                        mBtnSearchLocation.setVisibility(View.VISIBLE);
                        mBtnSearchName.setVisibility(View.GONE);
                        break;

                    case R.id.rb_turn_over_picture:
                        changeBtnStatus();
                        clearLists();
                        select_type = SEARCH_NAME;
                        mTvSearchContent.setText("");
                        mLlQueryCondition.setVisibility(View.VISIBLE);
                        mBtnSearchName.setVisibility(View.VISIBLE);
                        mBtnSearchLocation.setVisibility(View.GONE);
                        break;
                    case R.id.rb_turn_over_all:
                        changeBtnStatus();
                        clearLists();
                        select_type = SEARCH_ALL;
                        mTvSearchContent.setText("管理的全部资产");
                        getSearchResultList();
                        loadingAssetProgress.setVisibility(View.VISIBLE);
                        break;

                }
            }
        });
    }

    /**
     * 改变按钮状态
     */
    private void changeBtnStatus() {
        mLlQueryCondition.setVisibility(View.INVISIBLE);
        mBtnTurnOverOk.setEnabled(false);
    }

    /**
     * 处理所有按钮事件
     *
     * @param view
     */

    @OnClick({R.id.btn_search_location, R.id.btn_search_name,
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
                startActivityForResult(intentPhoto, REQUEST_NAME);
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
                    //轮查状态是否可以进行移交
                    for (AssetInfo ass : selectedAssets) {
                        if (ass.getStatus() == 2 || ass.getStatus() == 3 ||ass.getStatus()==5) {
                            toast("丢失、待报废、已报废资产不能进行移交！");
                            return;
                        }
                    }
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
                            temp_list.addAll(AssetsUtil.GroupAfterMerge(AssetsUtil.deepCopy(assetsList)));
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

    /**
     * 接收返回结果
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            switch (requestCode) {
                case REQUEST_SELECTED:
                    if (resultCode == SelectedTreeNodeActivity.SEARCH_RESULT_OK) {
                        oldLocation = (Location) data.getSerializableExtra("node");
                        mTvSearchContent.setText(LocationNodeHelper.getSearchContentName(oldLocation));
                    }
                    break;
                case REQUEST_NAME:
                    if (resultCode == SelectAssetsPhotoActivity.RESULT_OK) {

                        Bundle bundle = data.getBundleExtra("assetpicture");
                        mPicture = (AssetPicture) bundle.getSerializable("imageFile");
                        mTvSearchContent.setText(mPicture.getImageNum());

                    }
                    break;
                case REQUEST_RECEIVE_LOCATION:
                    if (resultCode == SelectedTreeNodeActivity.SEARCH_RESULT_OK) {

                        mNewLocation = (Location) data.getSerializableExtra("node");
                        mTvReceiveNewLocation.setText(LocationNodeHelper.getSearchContentName(mNewLocation));

                    }
                    break;
                case REQUEST_RECEIVE_DEPT:
                    if (resultCode == SelectedTreeNodeActivity.SEARCH_RESULT_OK) {
                        mNewDept = (Department) data.getSerializableExtra("node");
                        mTvReceiveNewDept.setText(DepartmentNodeHelper.getSearchContentName(mNewDept));
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
                    } else {
                        toast("查询结束，没有符合条件的数据！");
                        loadingAssetProgress.setVisibility(View.GONE);
                        return;
                    }
                    temp_list.addAll(AssetsUtil.GroupAfterMerge(AssetsUtil.deepCopy(assetsList)));
                    setListAdapter();
                    loadingAssetProgress.setVisibility(View.GONE);
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
        List<AssetInfo> allList = new ArrayList<>();
        AssetsUtil.count = 0;
        switch (select_type) {
            case SEARCH_LOCATION:
                if (oldLocation != null) {
                    AssetsUtil.AndQueryAssets(this, "mLocation", oldLocation,
                            "mOldManager", current, handler, allList);
                }
                break;
            case SEARCH_NAME:
                AssetsUtil.AndQueryAssets(this, "mPicture", mPicture,
                        "mOldManager", current, handler, allList);
                break;
            case SEARCH_ALL:
                AssetsUtil.AndQueryAssets(this, "mOldManager", current, handler, allList);
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
     * 修改资产信息,因为位置和部门有节点属性，即父节点。在Bmob存储中造成自循环，最终导致内存溢出。
     * 所以在保存资产信息的位置和部门属性值时，新构造一个对象，传入其唯一objectId，
     */
    private void updateAssetInfo(AssetInfo asset) {
        if (mNewLocation != null) {
            Location l = new Location();
            l.setObjectId(mNewLocation.getObjectId());
            asset.setLocation(l);
        }
        if (mNewDept != null) {
            Department d = new Department();
            d.setObjectId(mNewDept.getObjectId());
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
     * 遍历资产列表，修改所有图片编号等于传入编号的资产，且状态也相同的资产，并返回对象列表
     *
     * @param list     为所有列表项
     * @param imageNum 为选择的列表项的图片编号
     * @param status   为选择的列表项的状态
     * @return
     */
    private List<BmobObject> updateAllSameImangeNumAssets(List<AssetInfo> list, String imageNum,
                                                          Integer status) {
        List<BmobObject> objects = new ArrayList<>();
        List<AssetInfo> updated = new ArrayList<>();
        for (AssetInfo asset : list) {
            if (imageNum.equals(asset.getPicture().getImageNum()) && asset.getStatus().equals(status)) {
                updateAssetInfo(asset);
                objects.add(asset);
                updated.add(asset);//将已经更新的资产暂时存入临时列表中以备移除
            }
        }
        list.removeAll(updated);
        return objects;
    }

    /**
     * 遍历所有资产，修改已选择的资产信息。考虑可能是合并数量后的资产
     * 如果是合并数量的资产，还需要分别处理
     *
     * @param assetsList     为所有列表项
     * @param selectedAssets 为已被选择的列表项
     */
    private List<BmobObject> updateAllSelectedAssetInfo(List<AssetInfo> assetsList, List<AssetInfo> selectedAssets) {
        List<BmobObject> objects = new ArrayList<>();
        for (AssetInfo asset : selectedAssets) {
            List<BmobObject> selectObject = updateAllSameImangeNumAssets(assetsList,
                    asset.getPicture().getImageNum(), asset.getStatus());
            objects.addAll(selectObject);

        }
        return objects;
    }


}
