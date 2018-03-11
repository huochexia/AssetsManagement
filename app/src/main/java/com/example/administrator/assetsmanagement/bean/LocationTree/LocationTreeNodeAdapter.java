package com.example.administrator.assetsmanagement.bean.LocationTree;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by  on 2016/8/3.
 *
 * @description 适配器类
 */
public abstract class LocationTreeNodeAdapter<M extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected Context mContext;
    protected LayoutInflater mInflater;
    //所有可见节点
    protected List<Location> mVisiableNodes = new ArrayList<>();
    //所有节点
    protected List<Location> mAllNodes = new ArrayList<>();
    //节点点击事件
    private OnLocationNodeClickListener onNodeClickListener;
    //默认不展开
    private int defaultExpandLevel = 0;
    //默认展开图片和不展开图片
    private int iconExpand = -1, iconNoExpand = -1;

    public  LocationTreeNodeAdapter(Context context, List<Location> datas, int defaultExpandLevel, int iconExpand, int iconNoExpand) {
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
        this.iconExpand = iconExpand;
        this.iconNoExpand = iconNoExpand;
        this.defaultExpandLevel = defaultExpandLevel;
        mAllNodes = LocationNodeHelper.getSortNodes(datas, defaultExpandLevel);
        for (Location node : mAllNodes) {
            node.iconNoExpand = iconNoExpand;
            node.iconExpand = iconExpand;
        }
        mVisiableNodes = LocationNodeHelper.filterVisibleNode(mAllNodes);
    }

    public LocationTreeNodeAdapter(Context context, List<Location> datas, int defaultExpandLevel) {
        this(context, datas, defaultExpandLevel, -1, -1);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Location node = mVisiableNodes.get(position);
        //根据节点所在层级设置它的左边距
        holder.itemView.setPadding(node.getLevel() * 100, 3, 3, 3);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandOrCollapse(position);
                if (onNodeClickListener != null)
                    //对item的点击事件暂时没有用到
                    onNodeClickListener.onClick(mVisiableNodes.get(position), position);
            }
        });
        onBindViewHolder(node, holder, position);
    }

    /**
     * 抽象方法
     *
     * @param node
     * @param holder
     * @param position
     */
    public abstract void onBindViewHolder(Location node, RecyclerView.ViewHolder holder, int position);

    /**
     * 设置点击事件监听器
     *
     * @param onTreeNodeClickListener
     */
    public void setOnTreeNodeClickListener(
            OnLocationNodeClickListener onTreeNodeClickListener) {
        this.onNodeClickListener = onTreeNodeClickListener;
    }

    @Override
    public int getItemCount() {
        return mVisiableNodes.size();
    }

    /**
     * 相应ListView的点击事件 展开或关闭某节点
     *
     * @param position
     */
    public void expandOrCollapse(int position) {
        Location n = mVisiableNodes.get(position);

        if (n != null) {// 排除传入参数错误异常
            if (!n.isLeaf()) {
                n.setExpand(!n.isExpand());
                mVisiableNodes = LocationNodeHelper.filterVisibleNode(mAllNodes);
                notifyDataSetChanged();// 刷新视图
            }
        }
    }

    /**
     * 删除指定节点
     * @param node
     */
    public void deleteNode(Location node) {
        mAllNodes.remove(node);
        for (int j = 0; j < mAllNodes.size(); j++) {
            Location m = mAllNodes.get(j);
            m.getChildren().clear();
            m.iconExpand = iconExpand;
            m.iconNoExpand = iconNoExpand;
        }

        mAllNodes = LocationNodeHelper.getSortNodes(mAllNodes, defaultExpandLevel);
        mVisiableNodes = LocationNodeHelper.filterVisibleNode(mAllNodes);
        notifyDataSetChanged();
    }

    /**
     * 刷新数据
     * @param index
     * @param nodes
     */
    private void notifyData(int index, List<Location> nodes) {
        for (int i = 0; i < nodes.size(); i++) {
            Location node = nodes.get(i);
            node.getChildren().clear();//清空，后面重新建立关系
            node.iconExpand = iconExpand;
            node.iconNoExpand = iconNoExpand;
        }
        for (int j = 0; j < mAllNodes.size(); j++) {
            Location m = mAllNodes.get(j);
            m.getChildren().clear();
            m.iconExpand = iconExpand;
            m.iconNoExpand = iconNoExpand;
        }
        if (index != -1) {
            mAllNodes.addAll(index, nodes);
        } else {
            mAllNodes.addAll(nodes);
        }
        mAllNodes = LocationNodeHelper.getSortNodes(mAllNodes, defaultExpandLevel);
        mVisiableNodes = LocationNodeHelper.filterVisibleNode(mAllNodes);
        notifyDataSetChanged();
    }

    /**
     * 指定位置添加数据,并刷新可以指定显示层级
     * @param index
     * @param datas
     * @param defaultExpandLevel
     */
    public void addData(int index, List<Location> datas, int defaultExpandLevel) {
        this.defaultExpandLevel = defaultExpandLevel;
        notifyData(index, datas);
    }

    /**
     * 指定位置添加数据，并刷新
     * @param index
     * @param datas
     */
    public void addData(int index, List<Location> datas) {
        notifyData(index, datas);
    }

    /**
     * 消除所有数据，重新添加
     * @param datas
     * @param defaultExpandLevel
     */
    public void addDataAll(List<Location> datas, int defaultExpandLevel) {
        datas.clear();
        addData(-1, datas, defaultExpandLevel);
    }

    /**
     * 添加数据并刷新
     * @param datas
     */
    public void addData(List<Location> datas) {
        notifyData(-1, datas);
    }

    /**
     * 添加数据并刷新，指定显示层级
     * @param datas
     * @param defaultExpandLevel
     */
    public void addData(List<Location> datas, int defaultExpandLevel) {
        this.defaultExpandLevel = defaultExpandLevel;
        addData(datas);
    }

    /**
     * 指定位置添加一个节点，并刷新
     * @param index
     * @param node
     */
    public void addData(int index, Location node) {
        List<Location> nodes = new ArrayList<>();
        nodes.add(node);
        notifyData(index, nodes);
    }

    /**
     * 添加数据并刷新 可指定刷新后显示层级
     *
     * @param node
     * @param defaultExpandLevel
     */
    public void addData(int index, Location node, int defaultExpandLevel) {
        this.defaultExpandLevel = defaultExpandLevel;
        addData(index, node);
    }

    /**
     * 获取排序后的所有节点
     * @return
     */
    public List<Location> getAllNodes() {
        if (mAllNodes == null) {//防止空指针异常
            mAllNodes = new ArrayList<>();
        }
        return mAllNodes;

    }
}
