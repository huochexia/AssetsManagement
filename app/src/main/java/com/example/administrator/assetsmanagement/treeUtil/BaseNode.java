package com.example.administrator.assetsmanagement.treeUtil;


import com.example.administrator.assetsmanagement.treeUtil.annotation.TreeNodeIconCollape;
import com.example.administrator.assetsmanagement.treeUtil.annotation.TreeNodeIconExpand;
import com.example.administrator.assetsmanagement.treeUtil.annotation.TreeNodeId;
import com.example.administrator.assetsmanagement.treeUtil.annotation.TreeNodeIsLast;
import com.example.administrator.assetsmanagement.treeUtil.annotation.TreeNodeName;
import com.example.administrator.assetsmanagement.treeUtil.annotation.TreeNodePId;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 如果你觉得你的Item布局十分复杂，且布局会展示Bean的其他数据，那么为了方便，你可以让Node中
 * 包含一个泛型B， 每个Node携带与之对于的Bean的所有数据；
 */
public class BaseNode<B> implements Serializable {

    //传入一下实体bean
    public B bean;
    //节点id 和父节点pId,pId=0时为根节点
    @TreeNodeId
    private String id;
    @TreeNodePId
    private String pId;
    //节点名称
    @TreeNodeName
    private String name;
    //节点层级
    private int level;
    //是否展开
    private boolean isExpand = false;
    //图标
    private int icon;
    //父节点
    private BaseNode parent;
    //所有子节点集合
    private List<BaseNode> children = new ArrayList<>();
    /**
     * 设置开启 关闭的图片,资源ID
     */
    @TreeNodeIconExpand
    public int iconExpand = -1;
    @TreeNodeIconCollape
    public int iconNoExpand = -1;
    /**
     * 是否被checked选中
     */
    private boolean isChecked;
    /**
     * 是否为最后节点,例如：人不可能有子节点，所以转换人为节点是会改变这个值
     */
    @TreeNodeIsLast
    public boolean isLast = false;

    public boolean isChecked() {
        return isChecked;
    }
    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    /**
     * 构造器
     */
    public BaseNode() {
    }

    public BaseNode(String id, String pId, String name) {
        this.id = id;
        this.pId = pId;
        this.name = name;
    }

    public BaseNode(String id, String pId, String nam, B bean) {
        this.bean = bean;
        this.id = id;
        this.pId = pId;
        this.name = name;
    }

    /**
     * setter,getter方法
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 得到当前节点的层级，如果当前节点没有父节点，则该节点
     * 是根节点，否则当前节点的层级为当前节点父节点的层级加
     *
     * @return
     */
    public int getLevel() {
        return parent == null ? 0 : parent.getLevel() + 1;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isExpand() {
        return isExpand;
    }

    /**
     * 如果为false，则递归关闭所有子节点
     *
     * @param expand
     */
    public void setExpand(boolean expand) {
        isExpand = expand;
        if (!expand) {
            for (BaseNode node : children) {
                node.setExpand(false);
            }
        }
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public BaseNode getParent() {
        return parent;
    }

    public void setParent(BaseNode parent) {
        this.parent = parent;
    }

    public List<BaseNode> getChildren() {
        return children;
    }

    public void setChildren(List<BaseNode> children) {
        this.children = children;
    }

    /**
     * 是否为根节点
     */
    public boolean isRoot() {
        return parent == null;
    }

    /**
     * 是否为叶子节点
     */
    public boolean isLeaf() {
        return children.size() == 0;
    }

    /**
     * 是否父节点展开
     */
    public boolean isParentExpand() {
        if (parent == null)
            return false;
        return parent.isExpand();
    }

}
