package com.example.administrator.assetsmanagement.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.assetsmanagement.Interface.AssetItemClickListener;
import com.example.administrator.assetsmanagement.Interface.ToolbarClickListener;
import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.adapter.AssetRecyclerViewAdapter;
import com.example.administrator.assetsmanagement.base.ParentWithNaviActivity;
import com.example.administrator.assetsmanagement.bean.AssetInfo;
import com.example.administrator.assetsmanagement.bean.AssetPicture;
import com.example.administrator.assetsmanagement.bean.CategoryTree.AssetCategory;
import com.example.administrator.assetsmanagement.bean.CategoryTree.CategoryNodeHelper;
import com.example.administrator.assetsmanagement.bean.DepartmentTree.Department;
import com.example.administrator.assetsmanagement.bean.DepartmentTree.DepartmentNodeHelper;
import com.example.administrator.assetsmanagement.bean.LocationTree.Location;
import com.example.administrator.assetsmanagement.bean.LocationTree.LocationNodeHelper;
import com.example.administrator.assetsmanagement.bean.Person;
import com.example.administrator.assetsmanagement.utils.AssetsUtil;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * 查询资产：依据位置、类别、所属部门、管理员、资产名称和报废状态进行查询。资产图片是资产的唯一标识。
 * 图片相同，则为同一种。查询结果相同资产汇总。名称相同不一定是同一种资产。主要是因为登记时，登记人
 * 员不同可能会使用相同的名称。另目前因为使用Bmob为个人免费版不能进行模糊查询所以可能出现查无内容。
 * 拟增加二维码扫描查询功能，通过扫描二维码得到该资产的基本信息，以及该类资产的总数量，所有位置等。
 * Created by Administrator on 2017/11/4 0004.
 */

public class SearchAssetsActivity extends ParentWithNaviActivity {

    public static final int SEARCHASSETS_REQUEST = 110;
    public static final int REQUEST_SELECTE_MANAGER = 111;
    public static final int REQUEST_PICTURE = 112;
    public static final int SEARCH_LOCATION = 1;
    public static final int SEARCH_CATEGORY = 2;
    public static final int SEARCH_DEPARTMENT = 3;
    public static final int SEARCH_MANAGER = 4;
    public static final int SEARCH_NAME = 5;
    public static final int SEARCH_STATUS = 6;


    @BindView(R.id.rg_assets_search)
    RadioGroup mRgAssetsSearch;
    @BindView(R.id.btn_search_location)
    FancyButton mBtnSearchLocation;
    @BindView(R.id.btn_register_category)
    FancyButton mBtnRegisterCategory;
    @BindView(R.id.btn_search_name)
    FancyButton mBtnSearchName;
    @BindView(R.id.btn_search_manager)
    FancyButton mBtnSearchManager;
    @BindView(R.id.tv_search_content)
    TextView mTvSearchContent;
    @BindView(R.id.btn_search_dept)
    FancyButton mBtnSearchDept;
    @BindView(R.id.btn_search_start)
    Button btnSearchStart;

    public AssetInfo assetInfo;

    private Location mLocation;//接收传入的节点信息
    private Department mDepartment;
    private AssetCategory mCategory;
    private int search_type = SEARCH_LOCATION;

    private List<AssetInfo> search_result_list = new ArrayList<>();
    private AssetRecyclerViewAdapter adapter;
    private RecyclerView searchList;
    private Person person;
    private AssetPicture mPicture;

    @Override
    public String title() {
        return "查找资产";
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
        setContentView(R.layout.activity_assets_search);
        ButterKnife.bind(this);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/隶书.ttf");
        mTvSearchContent.setTypeface(typeface);
        initNaviView();
        initEvent();
        searchList = (RecyclerView) findViewById(R.id.rc_search_list);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        searchList.setLayoutManager(llm);
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        mRgAssetsSearch.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_assets_search_location:
                        allSetGone();
                        clearLists();
                        mBtnSearchLocation.setVisibility(View.VISIBLE);
                        search_type = SEARCH_LOCATION;
                        break;
                    case R.id.rb_assets_search_category:
                        allSetGone();
                        clearLists();
                        mBtnRegisterCategory.setVisibility(View.VISIBLE);
                        search_type = SEARCH_CATEGORY;
                        break;
                    case R.id.rb_assets_search_dept:
                        allSetGone();
                        clearLists();
                        mBtnSearchDept.setVisibility(View.VISIBLE);
                        search_type = SEARCH_DEPARTMENT;
                        break;
                    case R.id.rb_assets_search_manager:
                        allSetGone();
                        clearLists();
                        mBtnSearchManager.setVisibility(View.VISIBLE);
                        search_type = SEARCH_MANAGER;
                        break;
                    case R.id.rb_assets_search_name:
                        allSetGone();
                        clearLists();
                        mBtnSearchName.setVisibility(View.VISIBLE);
                        search_type = SEARCH_NAME;
                        break;
                    case R.id.rb_assets_search_scrap:
                        allSetGone();
                        clearLists();
                        mTvSearchContent.setText("非正常资产");
                        mTvSearchContent.setTextSize(25);
                        search_type = SEARCH_STATUS;
                        break;
                }
            }
        });
        btnSearchStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<AssetInfo> allList = new ArrayList<>();
                AssetsUtil.count=0;
                switch (search_type) {
                    case SEARCH_LOCATION:
                        if (mLocation != null) {
                            AssetsUtil.AndQueryAssets(SearchAssetsActivity.this,
                                    "mLocation", mLocation, handler,allList);
                        }
                        break;
                    case SEARCH_CATEGORY:
                        if (mCategory != null) {
                            AssetsUtil.AndQueryAssets(SearchAssetsActivity.this,
                                    "mCategory", mCategory, handler,allList);

                        }
                        break;
                    case SEARCH_DEPARTMENT:
                        if (mDepartment != null) {
                            AssetsUtil.AndQueryAssets(SearchAssetsActivity.this,
                                    "mDepartment", mDepartment, handler,allList);
                        }
                        break;
                    case SEARCH_MANAGER:
                        if (person != null) {
                            AssetsUtil.AndQueryAssets(SearchAssetsActivity.this,
                                    "mOldManager", person, handler,allList);
                        }
                        break;
                    case SEARCH_NAME:
                        if (mPicture != null) {
                            AssetsUtil.AndQueryAssets(SearchAssetsActivity.this,
                                    "mPicture", mPicture, handler,allList);
                        }
                        break;
                    case SEARCH_STATUS:
                        //所有已批准报废，所有丢失资产
                        AssetsUtil.AndQueryAssets(SearchAssetsActivity.this,
                                "mStatus", 2, "mStatus", 5, sHandler);
                        break;
                }
            }
        });
    }

    /**
     * 清空列表
     */
    private void clearLists() {
        if (adapter != null) {
            search_result_list.clear();
            adapter.notifyDataSetChanged();
        }

    }


    mergeListHandler handler = new mergeListHandler();

    class mergeListHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AssetsUtil.SEARCH_ONE_ASSET:
                    search_result_list = (List<AssetInfo>) msg.getData().getSerializable("assets");
                    adapter = new AssetRecyclerViewAdapter(SearchAssetsActivity.this,
                            AssetsUtil.GroupAfterMerge(search_result_list), true);
                    searchList.setAdapter(adapter);
                    adapter.setAssetItemClickListener(new AssetItemClickListener() {
                        @Override
                        public void onClick(AssetInfo asset) {
                            assetInfo = asset;
                        }
                    });
                    adapter.setMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case 0:
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("picture",assetInfo.getPicture());
                                    bundle.putString("title", assetInfo.getAssetName());
                                    startActivity(AssetPictureActivity.class,bundle,false);
                                    return true;
                                case 1:
                                    Bundle bundle1 = new Bundle();
                                    bundle1.putInt("flay", 0);
                                    bundle1.putSerializable("picture", assetInfo.getPicture());
                                    startActivity(MakingLabelActivity.class, bundle1, false);
                                default:
                                    return true;
                            }
                        }
                    });
                    break;

            }
        }
    }

    /**
     * 因为检查所有资产，独立列示，而不是合并列示
     */
    SingleListHandler sHandler = new SingleListHandler();

    class SingleListHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AssetsUtil.SEARCH_ONE_ASSET:
                    search_result_list = (List<AssetInfo>) msg.getData().getSerializable("assets");
                    adapter = new AssetRecyclerViewAdapter(SearchAssetsActivity.this,
                            search_result_list, true);
                    searchList.setAdapter(adapter);
                    adapter.setAssetItemClickListener(new AssetItemClickListener() {

                        @Override
                        public void onClick(AssetInfo assetInfo) {

                        }
                    });
                    adapter.setMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case 0:
                                    //do something
                                    return true;
                                case 1:
                                    //do something
                                default:
                                    return true;
                            }
                        }
                    });
            }
        }
    }

    /**
     * 隐藏所有按钮
     */
    private void allSetGone() {
        mBtnSearchLocation.setVisibility(View.GONE);
        mBtnRegisterCategory.setVisibility(View.GONE);
        mBtnSearchManager.setVisibility(View.GONE);
        mBtnSearchName.setVisibility(View.GONE);
        mBtnSearchDept.setVisibility(View.GONE);
        mTvSearchContent.setText("");
    }

    @OnClick({R.id.btn_search_location, R.id.btn_register_category, R.id.btn_search_name,
            R.id.btn_search_manager, R.id.btn_search_dept})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_search_location:
                clearLists();
                startActivity(SelectedTreeNodeActivity.SEARCH_LOCATION, false);
                break;
            case R.id.btn_register_category:
                clearLists();
                startActivity(SelectedTreeNodeActivity.SEARCH_CATEGORY, false);
                break;
            case R.id.btn_search_name:
                clearLists();

                customScan();
                break;
            case R.id.btn_search_dept:
                clearLists();
                startActivity(SelectedTreeNodeActivity.SEARCH_DEPARTMENT, false);
                break;
            case R.id.btn_search_manager:
                clearLists();
                Intent intent = new Intent(SearchAssetsActivity.this, ManagerListActivity.class);
                startActivityForResult(intent, REQUEST_SELECTE_MANAGER);
                break;

        }
    }

    private void startActivity(int type, boolean isPerson) {
        Intent intent = new Intent(SearchAssetsActivity.this, SelectedTreeNodeActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("person", isPerson);
        startActivityForResult(intent, type);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            switch (requestCode) {
                case SelectedTreeNodeActivity.SEARCH_LOCATION:
                    if (resultCode == SelectedTreeNodeActivity.SEARCH_RESULT_OK) {
                        mLocation = (Location) data.getSerializableExtra("node");
                        mTvSearchContent.setText(LocationNodeHelper.getSearchContentName(mLocation));
                    }
                    break;
                case SelectedTreeNodeActivity.SEARCH_CATEGORY:
                    if (resultCode == SelectedTreeNodeActivity.SEARCH_RESULT_OK) {
                        mCategory = (AssetCategory) data.getSerializableExtra("node");
                        mTvSearchContent.setText(CategoryNodeHelper.getSearchContentName(mCategory));
                    }
                    break;
                case SelectedTreeNodeActivity.SEARCH_DEPARTMENT:
                    if (resultCode == SelectedTreeNodeActivity.SEARCH_RESULT_OK) {
                        mDepartment = (Department) data.getSerializableExtra("node");
                        mTvSearchContent.setText(DepartmentNodeHelper.getSearchContentName(mDepartment));
                    }
                    break;
                case REQUEST_SELECTE_MANAGER:
                    if (resultCode == ManagerListActivity.SEARCH_OK) {
                        person = (Person) data.getSerializableExtra("manager");
                        mTvSearchContent.setText(person.getUsername());
                    }
                    break;
                case REQUEST_PICTURE:
                    if (resultCode == SelectAssetsPhotoActivity.RESULT_OK) {
                        Bundle bundle = data.getBundleExtra("assetpicture");
                        mPicture = (AssetPicture) bundle.getSerializable("imageFile");
                        mTvSearchContent.setText(mPicture.getImageNum());
                    }
                    break;
                default:
                    IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                    if (intentResult != null) {
                        if (intentResult.getContents() == null) {
                            Toast.makeText(this, "内容为空", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "扫描成功", Toast.LENGTH_LONG).show();
                            // ScanResult 为 获取到的字符串
                            String ScanResult = intentResult.getContents();
                            mTvSearchContent.setText("资产编号"+ScanResult);

                        }
                    } else {
                        super.onActivityResult(requestCode, resultCode, data);
                    }
            }
        }

    }

    /**
     * 扫描二维码点击事件
     */
    public void customScan() {
        new IntentIntegrator(this)
                .setOrientationLocked(false)
                .setCaptureActivity(CustomScanActivity.class) // 设置自定义的activity是CustomActivity
                .initiateScan(); // 初始化扫描
    }


}
