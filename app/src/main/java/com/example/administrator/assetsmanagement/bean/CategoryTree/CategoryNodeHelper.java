package com.example.administrator.assetsmanagement.bean.CategoryTree;


import android.support.annotation.NonNull;


import java.util.ArrayList;
import java.util.List;

/**
 * 
 *
 * @description 节点帮助类
 */
public class CategoryNodeHelper {
    /**
     * 得到确定关系并排好序的节点列表
     */
    public static  List<AssetCategory> getSortNodes(List<AssetCategory> datas, int defaultExpandLevel) {
        List<AssetCategory> result = new ArrayList<>();
        try {
            List<AssetCategory> nodes = convertDatas2Nodes(datas);
            //获得所有根节点
            List<AssetCategory> rootNodes = getRootNodes(nodes);
            // 排序以及设置Node间关系
            for (AssetCategory node : rootNodes) {
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
    public static List<AssetCategory> filterVisibleNode(List<AssetCategory> nodes) {
        List<AssetCategory> result = new ArrayList<>();

        for (AssetCategory node : nodes) {
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
    private static  List<AssetCategory> convertDatas2Nodes(List<AssetCategory> datas) throws IllegalAccessException {
           return getNodesRelation(datas);
    }

    /**
     * 获取节点之间的关系
     *
     * @param nodes
     * @return
     */
    @NonNull
    private static List<AssetCategory> getNodesRelation(List<AssetCategory> nodes) {
        //设置节点之间的父子关系
        for (int i = 0; i < nodes.size(); i++) {
            AssetCategory n = nodes.get(i);
            for (int j = i + 1; j < nodes.size(); j++) {
                AssetCategory m = nodes.get(j);
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
    private static void setNodeIcon(AssetCategory node) {
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
    private static List<AssetCategory> getRootNodes(List<AssetCategory> nodes) {
        List<AssetCategory> root = new ArrayList<>();
        for (AssetCategory node : nodes) {
            if (node.isRoot())
                root.add(node);
        }
        return root;
    }

    /**
     * 把一个节点上的所有的内容都挂上去
     */
    private static void addNode(List<AssetCategory> nodes, AssetCategory node,
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
    public static void getAllParents(List<AssetCategory> nodes, AssetCategory node) {
        nodes.add(node);
        if (node.getParent() == null) {
            return;
        }
        getAllParents(nodes,node.getParent());
    }
    /**
     * 显示要查找的内容，传入的节点是201室，得到它的完整链内容。比如 A座-2楼-201室。
     *
     * @param AssetCategory
     */
    public static String getSearchContentName(AssetCategory AssetCategory) {
        StringBuilder buffer = new StringBuilder();
        List<AssetCategory> nodes = new ArrayList<>();
        CategoryNodeHelper.getAllParents(nodes, AssetCategory);
        int i = nodes.size();
        while (i > 0) {
            i--;
            buffer.append(nodes.get(i).getCategoryName());
            if (i != 0)
                buffer.append("-");
        }
        return buffer.toString();
    }
    /**
     * 获得查询对象的ID
     *
     * @param AssetCategory
     * @return
     */
    public String getSearchContentId(AssetCategory AssetCategory) {
        StringBuilder buffer = new StringBuilder();
        List<AssetCategory> nodes = new ArrayList<>();
        CategoryNodeHelper.getAllParents(nodes, AssetCategory);
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
