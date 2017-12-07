package com.example.administrator.assetsmanagement.Interface;

import com.example.administrator.assetsmanagement.treeUtil.BaseNode;

/**
 * 节点选择与取消事件处理接口
 * Created by Administrator on 2017/11/11 0011.
 */

public interface TreeNodeSelected {
    void checked(BaseNode node,int position);

    void cancelCheck(BaseNode node, int position);
}
