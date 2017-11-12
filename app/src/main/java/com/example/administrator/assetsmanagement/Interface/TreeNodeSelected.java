package com.example.administrator.assetsmanagement.Interface;

import com.example.administrator.assetsmanagement.treeUtil.BaseNode;

/**
 * Created by Administrator on 2017/11/11 0011.
 */

public interface TreeNodeSelected {
    void checked(BaseNode node,int position);

    void cancelCheck(BaseNode node, int position);
}
