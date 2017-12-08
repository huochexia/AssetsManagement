package com.example.administrator.assetsmanagement.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.example.administrator.assetsmanagement.Interface.ToolbarClickListener;
import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.base.ParentWithNaviActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 管理资产，列示管理权限范围
 * Created by Administrator on 2017/11/4 0004.
 */

public class ManageAssetsActivity extends ParentWithNaviActivity {
    @Override
    public String title() {
        return "管理资产";
    }

    @Override
    public Object left() {
        return R.drawable.ic_left_navi;
    }

    @Override
    public Object right() {
        return R.drawable.ic_search_db;
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
                startActivity(SearchAssetsActivity.class, null, false);
            }
        };
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_scope);
        ButterKnife.bind(this);
        initNaviView();
    }

    @OnClick(R.id.iv_assets_turn_over)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_assets_turn_over:
                startActivity(AssetsTurnOverActivity.class,null,false);
                break;
        }
    }
}
