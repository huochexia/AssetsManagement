package com.example.administrator.assetsmanagement.bean;

import cn.bmob.v3.BmobObject;

/**
 * 资产管理员信息：姓名，所属部门，角色编号 。该表的Id即为管理员的编号，其自动增加，而且不重复。
 * Created by Administrator on 2017/11/8.
 */

public class AssetsManager extends BmobObject {
    /*
    管理员姓名
     */
    private String managerName;

    /*
    管理员所属部门编号
     */
    private String deptNumber;
    /*
    角色编号
     */
    private String roleNumber;
    /**
     * setter 和getter方法
     */
    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }



    public String getDeptNumber() {
        return deptNumber;
    }

    public void setDeptNumber(String deptNumber) {
        this.deptNumber = deptNumber;
    }

    public String getRoleNumber() {
        return roleNumber;
    }

    public void setRoleNumber(String roleNumber) {
        this.roleNumber = roleNumber;
    }
}
