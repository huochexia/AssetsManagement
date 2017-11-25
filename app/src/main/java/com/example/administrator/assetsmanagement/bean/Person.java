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
    private String nodename;
    @TreeNodeIsLast
    private Boolean isPerson = true;

    private List<String> role;


    public List<String> getRole() {
        return role;
    }

    public void setRole(List<String> role) {
        this.role = role;
    }


    public String getNodename() {
        return nodename;
    }


    public void setNodename(String nodename) {
        this.nodename = nodename;
    }



    public Boolean getPerson() {
        return isPerson;
    }

    public void setPerson(Boolean person) {
        isPerson = person;
    }

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

    /**
     * 构造方法
     */
    public Person() {

    }

    public Person(String id, String parentId, String nodename) {
        this.id = id;
        this.parentId = parentId;
        this.nodename = nodename;
    }


}