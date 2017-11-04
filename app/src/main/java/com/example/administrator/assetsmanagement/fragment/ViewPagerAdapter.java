package com.example.administrator.assetsmanagement.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/4 0004.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {
    List<Fragment>  mFragments;
    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
        mFragments = new ArrayList<>();
    }

    public void addFragment(Fragment fragment) {
        mFragments.add(fragment);
    }
    @Override
    public Fragment getItem(int position) {
        return  mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

}
