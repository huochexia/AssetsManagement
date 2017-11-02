package com.example.administrator.assetsmanagement.treeUtil;

import com.example.administrator.assetsmanagement.treeUtil.Node;

public class Location extends Node<Integer> {
    private int id;
    private int parentId;
    private String locationName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentId() {
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

    public Location() {

    }

    public Location(int id, int parentId, String locationName) {
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