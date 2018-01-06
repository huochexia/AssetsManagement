package com.example.administrator.assetsmanagement.bean.CategoryTree;

import com.example.administrator.assetsmanagement.treeUtil.BaseNode;
import com.example.administrator.assetsmanagement.treeUtil.annotation.TreeNodeIconCollape;
import com.example.administrator.assetsmanagement.treeUtil.annotation.TreeNodeIconExpand;
import com.example.administrator.assetsmanagement.treeUtil.annotation.TreeNodeId;
import com.example.administrator.assetsmanagement.treeUtil.annotation.TreeNodeIsLast;
import com.example.administrator.assetsmanagement.treeUtil.annotation.TreeNodeName;
import com.example.administrator.assetsmanagement.treeUtil.annotation.TreeNodePId;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;

/**资产类别
 * Created by Administrator on 2017/11/8.
 */

public class AssetCategory extends BmobObject {
    @TreeNodeId
    private String id;
    @TreeNodePId
    private String parentId;
    @TreeNodeName
    private String categoryName;
    //节点层级
    private int level;
    //是否展开
    private boolean isExpand = false;
    //图标
    private int icon;
    //父节点
    private AssetCategory parent;
    //所有子节点集合
    private List<AssetCategory> children = new ArrayList<>();
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

    public String getcategoryName() {
        return categoryName;
    }

    public void setcategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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
            for (AssetCategory node : children) {
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

    public AssetCategory getParent() {
        return parent;
    }

    public void setParent(AssetCategory parent) {
        this.parent = parent;
    }

    public List<AssetCategory> getChildren() {
        return children;
    }

    public void setChildren(List<AssetCategory> children) {
        this.children = children;
    }

    public int getIconExpand() {
        return iconExpand;
    }

    public void setIconExpand(int iconExpand) {
        this.iconExpand = iconExpand;
    }

    public int getIconNoExpand() {
        return iconNoExpand;
    }

    public void setIconNoExpand(int iconNoExpand) {
        this.iconNoExpand = iconNoExpand;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isLast() {
        return isLast;
    }

    public void setLast(boolean last) {
        isLast = last;
    }

    public AssetCategory() {
    }

    public AssetCategory(String id, String parentId, String categoryName) {
        this.id = id;
        this.parentId = parentId;
        this.categoryName = categoryName;
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
