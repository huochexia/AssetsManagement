package com.example.administrator.assetsmanagement.bean;

import com.example.administrator.assetsmanagement.treeUtil.Node;

/**部门信息，继承自节点（Node)类，实现树型管理。
 * Created by Administrator on 2017/11/8.
 */

public class Department extends Node {
    private Integer id;
    private Integer parentId;
    private String departmentName;
    private String departmentNumber;

    @Override
    public Object get_id() {
        return id;
    }

    @Override
    public Object get_parentId() {
        return parentId;
    }

    @Override
    public String get_label() {
        return departmentName;
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
