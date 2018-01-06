package com.example.administrator.assetsmanagement.bean.LocationTree;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.assetsmanagement.R;

import java.util.List;

/**
 * Created by Administrator on 2017/11/11 0011.
 */

public class LocationCheckboxNodeAdapter extends LocationTreeNodeAdapter<LocationCheckboxNodeAdapter.MyViewHolder> {
    LocationNodeSelected selectedListener;

    public LocationCheckboxNodeAdapter(Context context, List<Location> datas, int defaultExpandLevel, int iconExpand, int iconNoExpand) {
        super(context, datas, defaultExpandLevel, iconExpand, iconNoExpand);
    }

    public LocationCheckboxNodeAdapter(Context context, List<Location> datas, int defaultExpandLevel) {
        super(context, datas, defaultExpandLevel);
    }

    @Override
    public void onBindViewHolder(final Location node, RecyclerView.ViewHolder holder, final int position) {
        final MyViewHolder myViewHolder = (MyViewHolder) holder;
        myViewHolder.mNodeName.setText(node.getLocationName());
        if (node.isChecked()) {
            myViewHolder.mSelected.setChecked(true);
        } else {
            myViewHolder.mSelected.setChecked(false);
        }

        if (node.getIcon() != -1) {
            myViewHolder.icon.setVisibility(View.VISIBLE);
            myViewHolder.icon.setImageResource(node.getIcon());
        } else {
            myViewHolder.icon.setVisibility(View.INVISIBLE);
        }
        myViewHolder.mSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheck = myViewHolder.mSelected.isChecked();
                if (isCheck) { //如果checkbox的状态是选中的，那么除了被选中的那条数据，其他Node节点的checkbox状态都为false
                    for (int i = 0; i < mAllNodes.size(); i++) {
                        if ((mAllNodes.get(i)).getId().equals(node.getId())) {
                            (mAllNodes.get(i)).setChecked(isCheck);
                        } else {
                            (mAllNodes.get(i)).setChecked(false);
                        }
                    }
                    selectedListener.checked(node,position);
                } else {//如果checkbox的状态是非选中的，所有Node节点checkbox状态都为false
                    for (int i = 0; i < mAllNodes.size(); i++) {
                        if ((mAllNodes.get(i)).getId().equals(node.getId())) {
                            (mAllNodes.get(i)).setChecked(isCheck);
                        }
                    }
                    selectedListener.cancelCheck(node,position);
                }
                notifyDataSetChanged();
            }
        });
        myViewHolder.mSelected.setChecked(node.isChecked());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.tree_item_checkbox, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    /**
     * 设置选择框监听器
     * @param selectedListener
     */
    public void setCheckBoxSelectedListener(LocationNodeSelected selectedListener) {
        this.selectedListener = selectedListener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView mNodeName;
        CheckBox mSelected;

        public MyViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.iv_tree_item_checkbox_icon);
            mNodeName = (TextView) itemView.findViewById(R.id.tv_tree_item_checkbox_name);
            mSelected = (CheckBox) itemView.findViewById(R.id.cb_tree_item_checkbox_select);
        }
    }
}
