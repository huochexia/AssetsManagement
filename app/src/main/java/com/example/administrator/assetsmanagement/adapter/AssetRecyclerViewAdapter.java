package com.example.administrator.assetsmanagement.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.assetsmanagement.Interface.AssetItemClickListener;
import com.example.administrator.assetsmanagement.Interface.AssetSelectedListener;
import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.bean.AssetInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 资产列表适配器，显示查询资产的名称及数量。传入的资产列表中包含的每一个资产，有些资产属于同样的，
 * 只是编号不同，需要将这类资产汇总数量.点击结果列表，显示该项的图片
 * Created by Administrator on 2017/12/6.
 */

public class AssetRecyclerViewAdapter extends RecyclerView.Adapter<AssetRecyclerViewAdapter.AssetHolder> {
    List<AssetInfo> assetInfoList;
    Context mContext;
    LayoutInflater layoutInflater;
    boolean isSearch;
    Map<Integer, Boolean> map = new HashMap<>();
    AssetSelectedListener listener;
    AssetItemClickListener clickListener;
    MenuItem.OnMenuItemClickListener menuClickLister;//弹出菜单项事件

    public AssetRecyclerViewAdapter(Context context, List<AssetInfo> list, boolean isSearch) {
        mContext = context;
        assetInfoList = list;
        layoutInflater = LayoutInflater.from(mContext);
        this.isSearch = isSearch;
        initMap();
    }

    public void initMap() {
        for (int i = 0; i < assetInfoList.size(); i++) {
            map.put(i, false);
        }
    }

    /**
     * 选择事件监听器
     *
     * @param listener
     */
    public void getAssetSelectListener(AssetSelectedListener listener) {
        this.listener = listener;
    }

    /**
     * 点击事件监听器
     *
     * @param
     */
    public void setAssetItemClickListener(AssetItemClickListener listener) {
        this.clickListener = listener;
    }

    public void setMenuItemClickListener(MenuItem.OnMenuItemClickListener listener) {
        this.menuClickLister = listener;
    }

    @Override
    public AssetHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_asset, parent, false);
        return new AssetHolder(view);
    }

    @Override
    public void onBindViewHolder(final AssetHolder holder, final int position) {
        if (isSearch) {
            holder.selected.setVisibility(View.INVISIBLE);
        } else {
            holder.selected.setVisibility(View.VISIBLE);
        }
        holder.selected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean ischeck = holder.selected.isChecked();
                map.put(position, ischeck);
                if (ischeck) {
                    listener.selectAsset(assetInfoList.get(position));
                } else {
                    listener.cancelAsset(assetInfoList.get(position));
                }
            }
        });


        if (map.get(position) == null) {
            map.put(position, false);
        }
        holder.selected.setChecked(map.get(position));

        holder.serial_number.setText((position + 1) + "");
        holder.assetName.setText(assetInfoList.get(position).getAssetName());
        holder.assetQuantity.setText(assetInfoList.get(position).getQuantity() + "");
        switch (assetInfoList.get(position).getStatus()) {
            case 0:
                holder.assetStatus.setText("正常");
                break;
            case 1:
                holder.assetStatus.setText("损坏");
                break;
            case 2:
                holder.assetStatus.setText("丢失");
                break;
            case 3:
                holder.assetStatus.setText("待报废");
                break;
            case 4:
                holder.assetStatus.setText("待移交");
                break;
            case 5:
                holder.assetStatus.setText("已报废");
                break;
            case 6:
                holder.assetStatus.setText("维修移交");
                break;
            case 9:
                holder.assetStatus.setText("新登记");
                break;
        }
        holder.assetName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                clickListener.onClick(assetInfoList.get(position));
                return false;
            }
        });

    }


    @Override
    public int getItemCount() {
        return assetInfoList.size();
    }


    /**
     * 自定义ViewHolder
     */
    class AssetHolder extends RecyclerView.ViewHolder {
        LinearLayout item;
        TextView serial_number;
        TextView assetName;
        TextView assetQuantity;
        TextView assetStatus;
        CheckBox selected;

        public AssetHolder(View itemView) {
            super(itemView);
            item = (LinearLayout) itemView.findViewById(R.id.ll_asset_item);
            serial_number = (TextView) itemView.findViewById(R.id.tv_assets_item_serial);
            assetName = (TextView) itemView.findViewById(R.id.tv_assets_item_name);
            assetQuantity = (TextView) itemView.findViewById(R.id.tv_assets_item_quantity);
            assetStatus = (TextView) itemView.findViewById(R.id.tv_assets_item_status);
            selected = (CheckBox) itemView.findViewById(R.id.cb_assets_item);
            //设置上下文菜单
            itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    MenuItem picture = menu.add(0, 0, 0, "图片");
                    MenuItem printer = menu.add(0, 1, 0, "明细");
                    picture.setOnMenuItemClickListener(menuClickLister);
                    printer.setOnMenuItemClickListener(menuClickLister);

                }
            });

        }

    }

}
