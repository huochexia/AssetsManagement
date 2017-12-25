package com.example.administrator.assetsmanagement.bean;

import com.example.administrator.assetsmanagement.treeUtil.annotation.TreeNodeId;
import com.example.administrator.assetsmanagement.treeUtil.annotation.TreeNodeName;
import com.example.administrator.assetsmanagement.treeUtil.annotation.TreeNodePId;

import cn.bmob.v3.BmobObject;

/**
 * 位置信息
 */
public class Location extends BmobObject {
    @TreeNodeId
    private String id;
    @TreeNodePId
    private String parentId;
    @TreeNodeName
    private String locationName;

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

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }


    /**
     * 构造方法
     */
    public Location() {

    }

    public Location(String id, String parentId, String locationName) {
        this.id = id;
        this.parentId = parentId;
        this.locationName = locationName;
    }


}