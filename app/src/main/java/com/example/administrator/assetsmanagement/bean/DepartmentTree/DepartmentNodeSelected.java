package com.example.administrator.assetsmanagement.bean.DepartmentTree;

import com.example.administrator.assetsmanagement.bean.LocationTree.Location;

/**
 * 节点选择与取消事件处理接口
 * Created by Administrator on 2017/11/11 0011.
 */

public interface DepartmentNodeSelected {
    void checked(Department node, int position);

    void cancelCheck(Department node, int position);
}
