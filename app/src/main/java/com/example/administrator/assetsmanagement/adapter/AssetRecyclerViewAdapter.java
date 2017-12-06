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

import java.util.List;

/**
 * Created by Administrator on 2017/12/6.
 */

public class AssetRecyclerViewAdapter extends RecyclerView.Adapter<AssetRecyclerViewAdapter.AssetHolder> {
    List<AssetInfo> assetInfoList;
    Context mContext;
    LayoutInflater layoutInflater;
    public AssetRecyclerViewAdapter(Context context, List<AssetInfo> list) {
        mContext=context;
        assetInfoList = list;
        layoutInflater = LayoutInflater.from(mContext);
    }
    @Override
    public AssetHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_asset, parent);
        return new AssetHolder(view);
    }

    @Override
    public void onBindViewHolder(AssetHolder holder, int position) {



    }

    @Override
    public int getItemCount() {
        return assetInfoList.size();
    }

    class AssetHolder extends RecyclerView.ViewHolder {
        TextView serial_number;
        ImageView  assetthumb;
        TextView assetName;
        TextView assetQuantity;
        ImageView moreImage;
        public AssetHolder(View itemView) {
            super(itemView);
            serial_number = (TextView) itemView.findViewById(R.id.tv_assets_item_serial);
            assetthumb = (ImageView) itemView.findViewById(R.id.iv_assets_item_image);
            assetName = (TextView) itemView.findViewById(R.id.tv_assets_item_name);
            assetQuantity = (TextView) itemView.findViewById(R.id.tv_assets_item_quantity);
            moreImage = (ImageView) itemView.findViewById(R.id.iv_assets_item_more);
        }
    }
}
