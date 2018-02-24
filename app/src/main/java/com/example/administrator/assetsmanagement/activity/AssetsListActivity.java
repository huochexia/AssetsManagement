package com.example.administrator.assetsmanagement.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.administrator.assetsmanagement.Interface.AssetItemClickListener;
import com.example.administrator.assetsmanagement.Interface.ToolbarClickListener;
import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.adapter.AssetRecyclerViewAdapter;
import com.example.administrator.assetsmanagement.base.ParentWithNaviActivity;
import com.example.administrator.assetsmanagement.bean.AssetInfo;
import com.example.administrator.assetsmanagement.bean.AssetPicture;
import com.example.administrator.assetsmanagement.bean.CategoryTree.AssetCategory;
import com.example.administrator.assetsmanagement.bean.DepartmentTree.Department;
import com.example.administrator.assetsmanagement.bean.LocationTree.Location;
import com.example.administrator.assetsmanagement.bean.Manager.Person;
import com.example.administrator.assetsmanagement.utils.AssetsUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 资产列表
 * Created by Administrator on 2018/1/12 0012.
 */

public class AssetsListActivity extends ParentWithNaviActivity {
    @BindView(R.id.rc_query_result_list)
    RecyclerView mRcQueryResultList;
    @BindView(R.id.query_progress)
    ProgressBar queryProgress;
    //动态显示列表
    private String title;
    int condition;//查询条件
    Serializable value;//查询内容，可能是位置、部门、类别等
    String para1;//参数名
    List<AssetInfo> mResultList;
    AssetRecyclerViewAdapter mAdapter;
    private AssetInfo assetInfo;

    @Override
    public String title() {
        return title;
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
        setContentView(R.layout.activity_assets_query_result);
        ButterKnife.bind(this);

        LinearLayoutManager ll = new LinearLayoutManager(this);
        mRcQueryResultList.setLayoutManager(ll);

        Bundle bundle = getBundle();
        condition = bundle.getInt("condition");//得到查询条件
        value = bundle.getSerializable("content");
        List<AssetInfo> allList = new ArrayList<>();
        AssetsUtil.count = 0;
        switch (condition) {
            case QueryAssetsActivity.ASSET_LOCATION:
                title = ((Location) value).getLocationName();
                AssetsUtil.AndQueryAssets(AssetsListActivity.this,
                        "mLocation", value, handler, allList);
                para1 = "mLocation";
                break;
            case QueryAssetsActivity.ASSET_DEPARTMENT:
                AssetsUtil.AndQueryAssets(AssetsListActivity.this,
                        "mDepartment", value, handler, allList);
                title = ((Department) value).getDepartmentName();
                para1 = "mDepartment";
                break;
            case QueryAssetsActivity.ASSET_CATEGORY:
                AssetsUtil.AndQueryAssets(AssetsListActivity.this,
                        "mCategory", value, handler, allList);
                title = ((AssetCategory) value).getCategoryName();
                para1 = "mCategory";
                break;
            case QueryAssetsActivity.ASSET_MANAGER:
                AssetsUtil.AndQueryAssets(AssetsListActivity.this,
                        "mOldManager", value, handler, allList);
                title = ((Person) value).getUsername();
                para1 = "mOldManager";
                break;
            case QueryAssetsActivity.ASSET_PICTURE:
                AssetsUtil.AndQueryAssets(AssetsListActivity.this,
                        "mPicture", value, handler, allList);
                title = ((AssetPicture) value).getAssetName();
                para1 = "mPicture";
                break;
        }
        initNaviView();

    }

    mergeListHandler handler = new mergeListHandler();

    class mergeListHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AssetsUtil.SEARCH_ONE_ASSET:
                    mResultList = (List<AssetInfo>) msg.getData().getSerializable("assets");
                    if (mResultList.size() == 0) {
                        toast("查询结束，没有符合条件的数据！");
                    }
                    mAdapter = new AssetRecyclerViewAdapter(AssetsListActivity.this,
                            AssetsUtil.GroupAfterMerge(mResultList), true);
                    mRcQueryResultList.setAdapter(mAdapter);
                    queryProgress.setVisibility(View.GONE);
                    mAdapter.setAssetItemClickListener(new AssetItemClickListener() {
                        @Override
                        public void onClick(AssetInfo asset) {
                            assetInfo = asset;
                        }
                    });
                    mAdapter.setMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case 0:
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("picture", assetInfo.getPicture());
                                    startActivity(AssetPictureActivity.class, bundle, false);
                                    return true;
                                case 1:
                                    Bundle bundle1 = new Bundle();
                                    bundle1.putInt("flag", 0);//仅是查询，可以打印但不能移交
                                    bundle1.putSerializable("picture", assetInfo.getPicture());
                                    bundle1.putSerializable("value",value);
                                    bundle1.putString("para",para1);
                                    bundle1.putSerializable("value1",assetInfo.getStatus());
                                    bundle1.putString("para1","mStatus");
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
}
