package com.example.administrator.assetsmanagement.Interface;

import com.example.administrator.assetsmanagement.treeUtil.BaseNode;

/**
 * 树状结构中对节点点击的处理事件
 * Created by Administrator on 2017/11/11 0011.
 */

public interface OnTreeNodeClickListener {
    void onClick(BaseNode node,int position);

}
