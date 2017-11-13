package com.example.administrator.assetsmanagement.bean;

import com.example.administrator.assetsmanagement.treeUtil.annotation.TreeNodeId;
import com.example.administrator.assetsmanagement.treeUtil.annotation.TreeNodeName;
import com.example.administrator.assetsmanagement.treeUtil.annotation.TreeNodePId;

import cn.bmob.v3.BmobObject;

/**资产类别
 * Created by Administrator on 2017/11/8.
 */

public class AssetCategory extends BmobObject {
    @TreeNodeId
    private String id;
    @TreeNodePId
    private String parentId;
    @TreeNodeName
    private String categoryName;
//    private String departmentNumber;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getcategoryName() {
        return categoryName;
    }

    public void setcategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

//    public String getDepartmentNumber() {
//        return departmentNumber;
//    }
//
//    public void setDepartmentNumber(String departmentNumber) {
//        this.departmentNumber = departmentNumber;
//    }

    public AssetCategory() {
    }

    public AssetCategory(String id, String parentId, String categoryName) {
        this.id = id;
        this.parentId = parentId;
        this.categoryName = categoryName;
    }


}
