package com.example.administrator.assetsmanagement.bean.CategoryTree;

/**
 * 节点选择与取消事件处理接口
 * Created by Administrator on 2017/11/11 0011.
 */

public interface CategoryNodeSelected {
    void checked(AssetCategory node, int position);

    void cancelCheck(AssetCategory node, int position);
}
