package com.example.administrator.assetsmanagement.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.assetsmanagement.Interface.ToolbarClickListener;
import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.base.ParentWithNaviActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by Administrator on 2017/12/8.
 */

public class AssetsTurnOverActivity extends ParentWithNaviActivity {
    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.iv_left_navi)
    ImageView ivLeftNavi;

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
    }

    @OnClick({R.id.btn_turn_over_search, R.id.btn_search_location, R.id.btn_register_category, R.id.btn_search_name, R.id.btn_search_manager, R.id.btn_search_dept, R.id.btn_turn_over_receiver, R.id.btn_turn_over_ok, R.id.btn_turn_over_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_turn_over_search:
                break;
            case R.id.btn_search_location:
                break;
            case R.id.btn_register_category:
                break;
            case R.id.btn_search_name:
                break;
            case R.id.btn_search_manager:
                break;
            case R.id.btn_search_dept:
                break;
            case R.id.btn_turn_over_receiver:
                break;
            case R.id.btn_turn_over_ok:
                break;
            case R.id.btn_turn_over_cancel:
                break;
        }
    }
}
