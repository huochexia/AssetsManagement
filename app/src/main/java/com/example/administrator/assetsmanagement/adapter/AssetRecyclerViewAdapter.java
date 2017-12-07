package com.example.administrator.assetsmanagement.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.bean.AssetInfo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 资产列表适配器，显示查询资产的名称及数量。传入的资产列表中包含的每一个资产，有些资产属于同样的，
 * 只是编号不同，需要将这类资产汇总数量
 * Created by Administrator on 2017/12/6.
 */

public class AssetRecyclerViewAdapter extends RecyclerView.Adapter<AssetRecyclerViewAdapter.AssetHolder> {
    List<AssetInfo> assetInfoList;
    Context mContext;
    LayoutInflater layoutInflater;

    Map map = new HashMap();


    public AssetRecyclerViewAdapter(Context context, List<AssetInfo> list) {
        mContext=context;
        assetInfoList = sumQuantity(list);
        layoutInflater = LayoutInflater.from(mContext);
    }
    @Override
    public AssetHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_asset, parent,false);
        return new AssetHolder(view);
    }

    @Override
    public void onBindViewHolder(AssetHolder holder, int position) {
        holder.serial_number.setText((position + 1)+"");
        holder.assetName.setText(assetInfoList.get(position).getmAssetName());
        holder.assetQuantity.setText(assetInfoList.get(position).getQuantity()+"");
    }

    @Override
    public int getItemCount() {
        return assetInfoList.size();
    }

    /**
     * 计算同样资产的数量
     *
     * @param list
     */
    private Integer sum = 1;

    private List<AssetInfo> sumQuantity(List<AssetInfo> list) {
        List<AssetInfo> list1 = list;
        Iterator it = list.iterator();
        while (it.hasNext()) {
            AssetInfo asset = (AssetInfo) it.next();
            String key = asset.getmPicture();
            if (map.get(key) == null) {
                map.put(key, asset);
            } else {
                AssetInfo asset1 = (AssetInfo) map.get(key);
                asset1.setQuantity(asset1.getQuantity() + 1);
                map.put(key, asset1);
            }

        }
        list1.clear();//最后清空后遍历map，存入List中
        for (Object obj : map.keySet()) {
            list1.add((AssetInfo) map.get(obj));
        }
        return list1;
    }

    /**
     * 自定义ViewHolder
     */
    class AssetHolder extends RecyclerView.ViewHolder {
        TextView serial_number;
        TextView assetName;
        TextView assetQuantity;

        public AssetHolder(View itemView) {
            super(itemView);
            serial_number = (TextView) itemView.findViewById(R.id.tv_assets_item_serial);
            assetName = (TextView) itemView.findViewById(R.id.tv_assets_item_name);
            assetQuantity = (TextView) itemView.findViewById(R.id.tv_assets_item_quantity);
        }
    }
}
