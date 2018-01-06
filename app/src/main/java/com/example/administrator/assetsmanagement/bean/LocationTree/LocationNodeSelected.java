package com.example.administrator.assetsmanagement.bean.LocationTree;

import com.example.administrator.assetsmanagement.treeUtil.BaseNode;

/**
 * 节点选择与取消事件处理接口
 * Created by Administrator on 2017/11/11 0011.
 */

public interface LocationNodeSelected {
    void checked(Location node, int position);

    void cancelCheck(Location node, int position);
}
