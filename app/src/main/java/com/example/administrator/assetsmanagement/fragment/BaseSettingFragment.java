package com.example.administrator.assetsmanagement.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.base.BaseFragment;

/**
 * Created by Administrator on 2017/11/4 0004.
 */

public class BaseSettingFragment extends BaseFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_baseset, container, false);
        return view;
    }

    public static BaseSettingFragment newInstance() {
        BaseSettingFragment fragment = new BaseSettingFragment();
        return fragment;
    }
}