package com.example.administrator.assetsmanagement.bean;

import com.example.administrator.assetsmanagement.treeUtil.Node;

/**
 * 位置：包含上一级位置，名称，编号
 */
public class Location extends Node<Integer> {
    private Integer id;
    private Integer parentId;
    private String locationName;
    private String locationNumber;

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
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

    public Location(Integer id, Integer parentId, String locationName) {
        this.id = id;
        this.parentId = parentId;
        this.locationName = locationName;
    }

    @Override
    public Integer get_id() {
        return id;
    }

    @Override
    public Integer get_parentId() {
        return parentId;
    }

    @Override
    public String get_label() {
        return locationName;
    }

    @Override
    public boolean parent(Node dest) {
        if (id == ((Integer) dest.get_parentId()).intValue()) {
            return true;
        }

        return false;
    }

    @Override
    public boolean child(Node dest) {
        if (parentId == ((Integer) dest.get_id()).intValue()) {
            return true;
        }
        return false;
    }
}