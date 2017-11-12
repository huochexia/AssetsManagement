package com.example.administrator.assetsmanagement.bean;

import com.example.administrator.assetsmanagement.treeUtil.annotation.TreeNodeId;
import com.example.administrator.assetsmanagement.treeUtil.annotation.TreeNodeName;
import com.example.administrator.assetsmanagement.treeUtil.annotation.TreeNodePId;

import cn.bmob.v3.BmobObject;

/**部门信息，继承自节点（Node)类，实现树型管理。
 * Created by Administrator on 2017/11/8.
 */

public class Department extends BmobObject {
    @TreeNodeId
    private String id;
    @TreeNodePId
    private String parentId;
    @TreeNodeName
    private String departmentName;
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

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

//    public String getDepartmentNumber() {
//        return departmentNumber;
//    }
//
//    public void setDepartmentNumber(String departmentNumber) {
//        this.departmentNumber = departmentNumber;
//    }

    public Department() {
    }

    public Department(String id, String parentId, String departmentName) {
        this.id = id;
        this.parentId = parentId;
        this.departmentName = departmentName;
    }


}
