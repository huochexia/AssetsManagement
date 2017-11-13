package com.example.administrator.assetsmanagement.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.administrator.assetsmanagement.bean.Location;
import com.example.administrator.assetsmanagement.treeUtil.BaseNode;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2017/11/12 0012.
 */

public class LocationSettingActivity extends TreeNodeSettingActivity {
    Location l1 = new Location("1", "0", "河北省税务干部学校");
    Location l2 = new Location("A", "0", "A座");
    Location l3 = new Location("01", "A", "一楼");
    Location l4 = new Location("02", "A", "二楼");
    Location l5 = new Location("101", "01", "101");
    Location l6 = new Location("201", "02", "学校");
    Location l7 = new Location("B", "0", "B座");
    Location l8 = new Location("C", "0", "C座");
    private List<Object> locationList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        locationList.add(l1);
        locationList.add(l2);
        locationList.add(l3);
        locationList.add(l4);
        locationList.add(l5);
        locationList.add(l6);
        locationList.add(l7);
        locationList.add(l8);
        treeNodeList = locationList;
        super.onCreate(savedInstanceState);

    }

    @Override
    public boolean addToBmob(BaseNode node) {
        return true;
    }

    @Override
    public boolean updateToBmob(BaseNode node) {
        return true;
    }

    @Override
    public boolean removeFromBmob(BaseNode node) {
        return true;
    }

    @Override
    public String title() {
        return "位置";
    }
}
