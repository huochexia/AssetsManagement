package com.example.administrator.assetsmanagement.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.base.BaseFragment;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.bmob.v3.BmobUser;

/**
 * Created by Administrator on 2017/11/4 0004.
 */

public class PersonSettingFragment extends BaseFragment {
    Unbinder unbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_person_set, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    public static PersonSettingFragment newInstance() {
        PersonSettingFragment fragment = new PersonSettingFragment();
        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.btn_repair_password, R.id.btn_logout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_repair_password:
                break;
            case R.id.btn_logout:
                BmobUser.logOut();
                getActivity().finish();
                break;
        }
    }
}
