package com.example.administrator.assetsmanagement.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.activity.CategorySettingActivity;
import com.example.administrator.assetsmanagement.activity.DepartmentSettingActivity;
import com.example.administrator.assetsmanagement.activity.LocationSettingActivity;
import com.example.administrator.assetsmanagement.activity.PersonSettingActivity;
import com.example.administrator.assetsmanagement.base.BaseFragment;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 基础设置，主要有资产位置、类别、部门及管理员的增加，修改和删除
 * Created by Administrator on 2017/11/4 0004.
 */

public class BaseSettingFragment extends BaseFragment {
    Unbinder unbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_baseset, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    public static BaseSettingFragment newInstance() {
        BaseSettingFragment fragment = new BaseSettingFragment();
        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.iv_base_set_location, R.id.iv_base_set_department, R.id.iv_base_set_person, R.id.iv_base_set_categray})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_base_set_location:
                startActivity(LocationSettingActivity.class,null);
                break;
            case R.id.iv_base_set_department:
                startActivity(DepartmentSettingActivity.class,null);
                break;
            case R.id.iv_base_set_person:
//                startActivity(PersonSettingActivity.class,null);
                break;
            case R.id.iv_base_set_categray:
                startActivity(CategorySettingActivity.class,null);
                break;
        }
    }
}
