package com.example.administrator.assetsmanagement.bean;

import com.example.administrator.assetsmanagement.treeUtil.Node;

/**
 * 位置：包含上一级位置，名称，编号
 */
public class Location extends Node<String> {
    private String id;
    private String parentId;
    private String locationName;
    private String locationNumber;

    public String getId() {
        return id;
    }

    public void setId(String  id) {
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

    public String getLocationNumber() {
        return locationNumber;
    }

    public void setLocationNumber(String locationNumber) {
        this.locationNumber = locationNumber;
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

    @Override
    public String get_id() {
        return id;
    }

    @Override
    public String get_parentId() {
        return parentId;
    }

    @Override
    public String get_label() {
        return locationName;
    }

    @Override
    public boolean parent(Node dest) {
        if (id.equals ((String) dest.get_parentId())) {
            return true;
        }

        return false;
    }

    @Override
    public boolean child(Node dest) {
        if (parentId.equals((String) dest.get_id())) {
            return true;
        }
        return false;
    }
}