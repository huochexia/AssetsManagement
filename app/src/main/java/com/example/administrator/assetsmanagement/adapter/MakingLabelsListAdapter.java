package com.example.administrator.assetsmanagement.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.assetsmanagement.Interface.AssetSelectedListener;
import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.bean.AssetInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 制作标签时资产列表适配器
 * Created by Administrator on 2018/1/3 0003.
 */

public class MakingLabelsListAdapter extends RecyclerView.Adapter<MakingLabelsListAdapter.LabelViewHolder> {
    List<AssetInfo> mAssetInfoList;
    Context mContext;
    LayoutInflater mInflater;

    Map<Integer, Boolean> map = new HashMap<>();
    AssetSelectedListener listener;

    public MakingLabelsListAdapter(Context context, List<AssetInfo> list) {
        mContext = context;
        mAssetInfoList = list;
        mInflater = LayoutInflater.from(mContext);
        initMap();

    }

    public void initMap() {
        for (int i = 0; i < mAssetInfoList.size(); i++) {
            map.put(i, false);
        }
    }


    public void setSelectedListener(AssetSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public LabelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_asset, parent, false);

        return new LabelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LabelViewHolder holder, final int position) {
        holder.mAssetNum.setText(mAssetInfoList.get(position).getAssetsNum());
        holder.mCheckBox.setVisibility(View.VISIBLE);
        if (mAssetInfoList.get(position).getLocation() != null) {
            holder.mAssetLocation.setText(mAssetInfoList.get(position).getLocation().getLocationName());
        }
        holder.serial_number.setText((position + 1) + "");
        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                map.put(position, isChecked);
                if (isChecked) {
                    listener.selectAsset(mAssetInfoList.get(position),position);
                } else {
                    listener.cancelAsset(mAssetInfoList.get(position));
                }
            }
        });
        if (map.get(position) == null) {
            map.put(position, false);
        }
        holder.mCheckBox.setChecked(map.get(position));
    }

    @Override
    public int getItemCount() {
        return mAssetInfoList.size();
    }

    public class LabelViewHolder extends RecyclerView.ViewHolder {
        LinearLayout item;
        TextView serial_number;
        TextView mAssetNum;
        TextView mAssetLocation;
        LinearLayout mLayoutStatus;
        public CheckBox mCheckBox;

        public LabelViewHolder(View itemView) {
            super(itemView);
            item = (LinearLayout) itemView.findViewById(R.id.ll_asset_item);
            serial_number = (TextView) itemView.findViewById(R.id.tv_assets_item_serial);
            mAssetNum = (TextView) itemView.findViewById(R.id.tv_assets_item_name);
            mAssetLocation = (TextView) itemView.findViewById(R.id.tv_assets_item_quantity);
            mLayoutStatus = (LinearLayout) itemView.findViewById(R.id.ll_asset_item_status);
            mLayoutStatus.setVisibility(View.GONE);

            mCheckBox = (CheckBox) itemView.findViewById(R.id.cb_assets_item);
        }
    }

}
