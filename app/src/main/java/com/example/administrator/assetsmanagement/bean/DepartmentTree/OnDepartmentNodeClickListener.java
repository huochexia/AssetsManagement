package com.example.administrator.assetsmanagement.bean.DepartmentTree;

import com.example.administrator.assetsmanagement.bean.LocationTree.Location;

/**
 * 树状结构中对节点点击的处理事件
 * Created by Administrator on 2017/11/11 0011.
 */

public interface OnDepartmentNodeClickListener {
    void onClick(Department node, int position);

}
