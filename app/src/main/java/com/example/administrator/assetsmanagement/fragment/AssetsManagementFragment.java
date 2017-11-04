package com.example.administrator.assetsmanagement.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.activity.ManageAssetsActivity;
import com.example.administrator.assetsmanagement.activity.RegisterAssetsActivity;
import com.example.administrator.assetsmanagement.activity.SearchAssetsActivity;
import com.example.administrator.assetsmanagement.base.BaseFragment;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2017/11/4 0004.
 */

public class AssetsManagementFragment extends BaseFragment {
    Unbinder unbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_manager, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    public static AssetsManagementFragment newInstance() {
        AssetsManagementFragment fragment = new AssetsManagementFragment();
        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.iv_assets_register, R.id.iv_assets_management, R.id.iv_assets_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_assets_register:
                startActivity(RegisterAssetsActivity.class,null);
                break;
            case R.id.iv_assets_management:
                startActivity(ManageAssetsActivity.class,null);
                break;
            case R.id.iv_assets_search:
                startActivity(SearchAssetsActivity.class,null);
                break;
        }
    }
}
