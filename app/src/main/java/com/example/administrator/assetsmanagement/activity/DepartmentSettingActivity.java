package com.example.administrator.assetsmanagement.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.administrator.assetsmanagement.Interface.ToolbarClickListener;
import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.base.ParentWithNaviActivity;
import com.example.administrator.assetsmanagement.bean.Location;
import com.example.administrator.assetsmanagement.treeUtil.BaseNode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/11/10.
 */

public class DepartmentSettingActivity extends TreeNodeSettingActivity {
    private List<Object> departList = new ArrayList<>();
    Location l1 = new Location("1", "0", "信息技术部");
    Location l2 = new Location("A", "0", "后勤部");
    Location l3 = new Location("01", "A", "餐厅");
    Location l4 = new Location("02", "A", "公寓");
    Location l5 = new Location("101", "01", "前厅");
    Location l6 = new Location("201", "02", "H1");
    Location l7 = new Location("B", "0", "教务部");
    Location l8 = new Location("C", "0", "教学部");
    
    
    @Override
    public String title() {
        return "部门";
    }

   

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        departList.add(l1);
        departList.add(l2);
        departList.add(l3);
        departList.add(l4);
        departList.add(l5);
        departList.add(l6);
        departList.add(l7);
        departList.add(l8);
        treeNodeList = departList;
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
    public boolean removeToBmob(BaseNode node) {
        return true;
    }
}
