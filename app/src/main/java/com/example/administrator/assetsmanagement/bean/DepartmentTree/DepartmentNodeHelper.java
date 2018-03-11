package com.example.administrator.assetsmanagement.bean.DepartmentTree;


import android.support.annotation.NonNull;

import com.example.administrator.assetsmanagement.bean.DepartmentTree.Department;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 *
 * @description 节点帮助类
 */
public class DepartmentNodeHelper {
    /**
     * 得到确定关系并排好序的节点列表
     */
    public static  List<Department> getSortNodes(List<Department> datas, int defaultExpandLevel) {
        List<Department> result = new ArrayList<>();
        try {
            List<Department> nodes = convertDatas2Nodes(datas);
            //获得所有根节点
            List<Department> rootNodes = getRootNodes(nodes);
            // 排序以及设置Node间关系
            for (Department node : rootNodes) {
                addNode(result, node, defaultExpandLevel, 1);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 过滤出所有可见的Node
     *
     * @param nodes
     * @return
     */
    public static List<Department> filterVisibleNode(List<Department> nodes) {
        List<Department> result = new ArrayList<>();

        for (Department node : nodes) {
            // 如果为跟节点，或者上层目录为展开状态
            if (node.isRoot() || node.isParentExpand()) {
                setNodeIcon(node);
                result.add(node);
            }
        }
        return result;
    }

    /**
     * 将服务器端获取的数据转化为Node,并确定它们之间的关系
     */
    private static  List<Department> convertDatas2Nodes(List<Department> datas) throws IllegalAccessException {
           return getNodesRelation(datas);
    }

    /**
     * 获取节点之间的关系
     *
     * @param nodes
     * @return
     */
    @NonNull
    private static List<Department> getNodesRelation(List<Department> nodes) {
        //设置节点之间的父子关系
        for (int i = 0; i < nodes.size(); i++) {
            Department n = nodes.get(i);
            for (int j = i + 1; j < nodes.size(); j++) {
                Department m = nodes.get(j);
                if (m.getId().equals(n.getParentId())) {
                    m.getChildren().add(n);
                    n.setParent(m);
                } else if (m.getParentId().equals(n.getId())) {
                    n.getChildren().add(m);
                    m.setParent(n);
                }

            }
        }

        return nodes;
    }

    /**
     * 设置节点的图标
     *
     * @param node
     */
    private static void setNodeIcon(Department node) {
        if (node.getChildren().size() > 0 && node.isExpand()) {
            node.setIcon(node.iconExpand);
        } else if (node.getChildren().size() > 0 && !node.isExpand()) {
            node.setIcon(node.iconNoExpand);
        } else {
            node.setIcon(-1);
        }
    }

    /**
     * 获得所有根节点
     *
     * @param nodes
     * @return
     */
    private static List<Department> getRootNodes(List<Department> nodes) {
        List<Department> root = new ArrayList<>();
        for (Department node : nodes) {
            if (node.isRoot())
                root.add(node);
        }
        return root;
    }

    /**
     * 把一个节点上的所有的内容都挂上去
     */
    private static void addNode(List<Department> nodes, Department node,
                                    int defaultExpandLeval, int currentLevel) {
        nodes.add(node);
//如果默认展开层级大于或者当前节点层级，那么设置当前层级是展开的，否则设置是闭合的
        if (defaultExpandLeval >= currentLevel) {
            node.setExpand(true);
        }

        if (node.isLeaf())
            return;
        for (int i = 0; i < node.getChildren().size(); i++) {
            addNode(nodes, node.getChildren().get(i), defaultExpandLeval,
                    currentLevel + 1);
        }
    }

    /**
     * 获得节点的父节点，以及父节点的父节点
     * @param nodes
     * @param node
     *
     */
    public static void getAllParents(List<Department> nodes, Department node) {
        nodes.add(node);
        if (node.getParent() == null) {
            return;
        }
        getAllParents(nodes,node.getParent());
    }
    /**
     * 显示要查找的内容，传入的节点是201室，得到它的完整链内容。比如 A座-2楼-201室。
     *
     * @param Department
     */
    public static String getSearchContentName(Department Department) {
        StringBuilder buffer = new StringBuilder();
        List<Department> nodes = new ArrayList<>();
        DepartmentNodeHelper.getAllParents(nodes, Department);
        int i = nodes.size();
        while (i > 0) {
            i--;
            buffer.append(nodes.get(i).getDepartmentName());
            if (i != 0)
                buffer.append("-");
        }
        return buffer.toString();
    }
    /**
     * 获得查询对象的ID
     *
     * @param Department
     * @return
     */
    public String getSearchContentId(Department Department) {
        StringBuilder buffer = new StringBuilder();
        List<Department> nodes = new ArrayList<>();
        DepartmentNodeHelper.getAllParents(nodes, Department);
        int i = nodes.size();
        while (i > 0) {
            i--;
            buffer.append(nodes.get(i).getId());
            if (i != 0)
                buffer.append("-");
        }
        return buffer.toString();
    }
}
