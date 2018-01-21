package com.example.administrator.assetsmanagement.bean.Manager;

import com.example.administrator.assetsmanagement.bean.DepartmentTree.Department;
import com.example.administrator.assetsmanagement.treeUtil.annotation.TreeNodeId;
import com.example.administrator.assetsmanagement.treeUtil.annotation.TreeNodeIsLast;
import com.example.administrator.assetsmanagement.treeUtil.annotation.TreeNodeName;
import com.example.administrator.assetsmanagement.treeUtil.annotation.TreeNodePId;

import java.io.Serializable;

import cn.bmob.v3.BmobUser;

/**
 * 人员信息
 */
public class Person extends BmobUser implements Serializable{
    @TreeNodeId
    private String id;
    @TreeNodePId
    private String parentId;
    @TreeNodeName
    private String nodename;
    @TreeNodeIsLast
    private Boolean isPerson = true;
    private Boolean isSelected = false;

    private Department department;

    private String acronym;//首字母

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    public String getNodename() {
        return nodename;
    }


    public void setNodename(String nodename) {
        this.nodename = nodename;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
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

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
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