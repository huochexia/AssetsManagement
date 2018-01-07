package com.example.administrator.assetsmanagement.treeUtil;


import android.support.annotation.NonNull;

import com.example.administrator.assetsmanagement.treeUtil.annotation.TreeNodeIconCollape;
import com.example.administrator.assetsmanagement.treeUtil.annotation.TreeNodeIconExpand;
import com.example.administrator.assetsmanagement.treeUtil.annotation.TreeNodeId;
import com.example.administrator.assetsmanagement.treeUtil.annotation.TreeNodeIsLast;
import com.example.administrator.assetsmanagement.treeUtil.annotation.TreeNodeName;
import com.example.administrator.assetsmanagement.treeUtil.annotation.TreeNodePId;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by H
 *
 * @description 节点帮助类
 */
public class NodeHelper {
    /**
     * 得到确定关系并排好序的节点列表
     */
    public static <T> List<BaseNode> getSortNodes(List<T> datas, int defaultExpandLevel) {
        List<BaseNode> result = new ArrayList<>();
        try {
            List<BaseNode> nodes = convertDatas2Nodes(datas);
            //获得所有根节点
            List<BaseNode> rootNodes = getRootNodes(nodes);
            // 排序以及设置Node间关系
            for (BaseNode node : rootNodes) {
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
    public static List<BaseNode> filterVisibleNode(List<BaseNode> nodes) {
        List<BaseNode> result = new ArrayList<>();

        for (BaseNode node : nodes) {
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
    private static <T> List<BaseNode> convertDatas2Nodes(List<T> datas) throws IllegalAccessException {
        List<BaseNode> nodes = new ArrayList<>();
        BaseNode node = null;
        for (T t : datas) {
            String id = "";
            String pId = "";
            String name = "";
            int iconExpand = -1;
            int iconNoExpand = -1;
            Boolean isLast =false;
            node = null;
            Class c = t.getClass();
            Field fields[] = c.getDeclaredFields();
            for (Field field : fields) {
                if (field.getAnnotation(TreeNodeId.class) != null) {
                    //设置访问权限，强制性的可以访问
                    field.setAccessible(true);
                    id = (String) field.get(t);
                }

                if (field.getAnnotation(TreeNodePId.class) != null) {
                    //设置访问权限，强制性的可以访问
                    field.setAccessible(true);
                    pId = (String) field.get(t);
                }

                if (field.getAnnotation(TreeNodeName.class) != null) {
                    //设置访问权限，强制性的可以访问
                    field.setAccessible(true);
                    name = (String) field.get(t);
                }
                if (field.getAnnotation(TreeNodeIconExpand.class) != null) {
                    field.setAccessible(true);
                    iconExpand = (int) field.get(t);
                }
                if (field.getAnnotation(TreeNodeIconCollape.class) != null) {
                    field.setAccessible(true);
                    iconNoExpand = (int) field.get(t);
                }
                if (field.getAnnotation(TreeNodeIsLast.class) != null) {
                    field.setAccessible(true);
                    isLast = (Boolean) field.get(t);
                }
            }
            node = new BaseNode(id, pId, name);
            node.iconExpand = iconExpand;
            node.iconNoExpand = iconNoExpand;
            node.isLast = isLast;
            nodes.add(node);
        }
        return getNodesRelation(nodes);

    }

    /**
     * 获取节点之间的关系
     *
     * @param nodes
     * @return
     */
    @NonNull
    private static List<BaseNode> getNodesRelation(List<BaseNode> nodes) {
        //设置节点之间的父子关系
        for (int i = 0; i < nodes.size(); i++) {
            BaseNode n = nodes.get(i);
            for (int j = i + 1; j < nodes.size(); j++) {
                BaseNode m = nodes.get(j);
                if (m.getId().equals(n.getpId())) {
                    m.getChildren().add(n);
                    n.setParent(m);
                } else if (m.getpId().equals(n.getId())) {
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
    private static void setNodeIcon(BaseNode node) {
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
    private static List<BaseNode> getRootNodes(List<BaseNode> nodes) {
        List<BaseNode> root = new ArrayList<>();
        for (BaseNode node : nodes) {
            if (node.isRoot())
                root.add(node);
        }
        return root;
    }

    /**
     * 把一个节点上的所有的内容都挂上去
     */
    private static  void addNode(List<BaseNode> nodes, BaseNode node,
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
    public static  void getAllParents(List<BaseNode> nodes, BaseNode node) {
        nodes.add(node);
        if (node.getParent() == null) {
            return;
        }
        getAllParents(nodes,node.getParent());
    }
    /**
     * 显示要查找的内容，传入的节点是201室，得到它的完整链内容。比如 A座-2楼-201室。
     *
     * @param baseNode
     */
    public static String getSearchContentName(BaseNode baseNode) {
        StringBuffer buffer = new StringBuffer();
        List<BaseNode> nodes = new ArrayList<>();
        NodeHelper.getAllParents(nodes, baseNode);
        int i = nodes.size();
        while (i > 0) {
            i--;
            buffer.append(nodes.get(i).getName());
            if (i != 0)
                buffer.append("-");
        }
        return buffer.toString();
    }
    /**
     * 获得查询对象的ID
     *
     * @param baseNode
     * @return
     */
    public String getSearchContentId(BaseNode baseNode) {
        StringBuffer buffer = new StringBuffer();
        List<BaseNode> nodes = new ArrayList<>();
        NodeHelper.getAllParents(nodes, baseNode);
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
