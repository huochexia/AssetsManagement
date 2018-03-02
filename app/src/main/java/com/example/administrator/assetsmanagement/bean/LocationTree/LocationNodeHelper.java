package com.example.administrator.assetsmanagement.bean.LocationTree;


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

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * 
 *
 * 节点帮助类
 */
public class LocationNodeHelper {
    /**
     * 得到确定关系并排好序的节点列表
     */
    public static  List<Location> getSortNodes(List<Location> datas, int defaultExpandLevel) {
        List<Location> result = new ArrayList<>();
        try {
            List<Location> nodes = convertDatas2Nodes(datas);
            //获得所有根节点
            List<Location> rootNodes = getRootNodes(nodes);
            // 排序以及设置Node间关系
            for (Location node : rootNodes) {
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
     */
    public static List<Location> filterVisibleNode(List<Location> nodes) {
        List<Location> result = new ArrayList<>();

        for (Location node : nodes) {
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
    private static  List<Location> convertDatas2Nodes(List<Location> datas) throws IllegalAccessException {
           return getNodesRelation(datas);
    }

    /**
     * 获取节点之间的关系
     *
     * @param nodes
     * @return
     */
    @NonNull
    private static List<Location> getNodesRelation(List<Location> nodes) {
        //设置节点之间的父子关系
        for (int i = 0; i < nodes.size(); i++) {
            Location n = nodes.get(i);
            for (int j = i + 1; j < nodes.size(); j++) {
                Location m = nodes.get(j);
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
    private static void setNodeIcon(Location node) {
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
    private static List<Location> getRootNodes(List<Location> nodes) {
        List<Location> root = new ArrayList<>();
        for (Location node : nodes) {
            if (node.isRoot())
                root.add(node);
        }
        return root;
    }

    /**
     * 把一个节点上的所有的内容都挂上去
     */
    private static void addNode(List<Location> nodes, Location node,
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
    public static void getAllParents(List<Location> nodes, Location node) {
        nodes.add(node);
        if (node.getParent() == null) {
            return;
        }
        getAllParents(nodes,node.getParent());
    }
    /**
     * 如果是通过形成树状列表后，选择某节点，可以通过这个方法得到它的完整链内容。比如 A座-2楼-201室。
     *
     * @param Location
     */
    public static String getSearchContentName(Location Location) {
        StringBuffer buffer = new StringBuffer();
        List<Location> nodes = new ArrayList<>();
        LocationNodeHelper.getAllParents(nodes, Location);
        int i = nodes.size();
        while (i > 0) {
            i--;
            buffer.append(nodes.get(i).getLocationName());
            if (i != 0)
                buffer.append("-");
        }
        return buffer.toString();
    }

    /**
     * 通过某一位置的父ID,递归查询数据库，得到某一地点的完整路径名称
     * @param location
     * @return
     */
    public static String getAllContentName(Location location, final StringBuffer buffer) {
        BmobQuery<Location> query = new BmobQuery<>();
        if (location.getParentId().equals("0")) {
            return buffer.toString();
        }else {
            query.addQueryKeys("locationName");
            query.addWhereEqualTo("id", location.getParentId());
            query.findObjects(new FindListener<Location>() {
                @Override
                public void done(final List<Location> list, BmobException e) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            buffer.insert(0,list.get(0).getLocationName());
                            getAllContentName(list.get(0),buffer);
                        }
                    }).start();
                }
            });

        }
        return buffer.toString();
    }
}
