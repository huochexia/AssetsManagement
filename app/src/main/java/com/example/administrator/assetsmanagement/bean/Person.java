package com.example.administrator.assetsmanagement.bean;

import com.example.administrator.assetsmanagement.treeUtil.annotation.TreeNodeId;
import com.example.administrator.assetsmanagement.treeUtil.annotation.TreeNodeIsLast;
import com.example.administrator.assetsmanagement.treeUtil.annotation.TreeNodeName;
import com.example.administrator.assetsmanagement.treeUtil.annotation.TreeNodePId;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;

/**
 * 人员信息
 */
public class Person extends BmobUser {
    @TreeNodeId
    private String id;
    @TreeNodePId
    private String parentId;
    @TreeNodeName
    private String personName;
    @TreeNodeIsLast
    private Boolean isPerson = true;

    public Boolean getPerson() {
        return isPerson;
    }

    public void setPerson(Boolean person) {
        isPerson = person;
    }



    public List<String> getAssetsList() {
        return assetsList;
    }

    public void setAssetsList(List<String> assetsList) {
        this.assetsList = assetsList;
    }

    private List<String> assetsList;//管理的资产编号列表
//    private String locationNumber;

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

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String locationName) {
        this.personName = locationName;
    }

//    public String getLocationNumber() {
//        return locationNumber;
//    }

//    public void setLocationNumber(String locationNumber) {
//        this.locationNumber = locationNumber;
//    }

    /**
     * 构造方法
     */
    public Person() {

    }

    public Person(String id, String parentId, String personName) {
        this.id = id;
        this.parentId = parentId;
        this.personName = personName;
    }


}