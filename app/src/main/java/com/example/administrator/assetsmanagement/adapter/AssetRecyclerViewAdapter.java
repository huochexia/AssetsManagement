package com.example.administrator.assetsmanagement.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.activity.AssetPictureActivity;
import com.example.administrator.assetsmanagement.bean.AssetInfo;
import com.example.administrator.assetsmanagement.bean.AssetPicture;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;

/**
 * 资产列表适配器，显示查询资产的名称及数量。传入的资产列表中包含的每一个资产，有些资产属于同样的，
 * 只是编号不同，需要将这类资产汇总数量.点击结果列表，显示该项的图片
 * Created by Administrator on 2017/12/6.
 */

public class AssetRecyclerViewAdapter extends RecyclerView.Adapter<AssetRecyclerViewAdapter.AssetHolder> {
    List<AssetInfo> assetInfoList;
    Context mContext;
    LayoutInflater layoutInflater;

    Map map = new HashMap();

    String title;
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
    public void onBindViewHolder(AssetHolder holder, final int position) {
        holder.serial_number.setText((position + 1)+"");
        holder.assetName.setText(assetInfoList.get(position).getmAssetName());
        holder.assetQuantity.setText(assetInfoList.get(position).getQuantity()+"");
        holder.assetStatus.setText(assetInfoList.get(position).getmStatus()+"");
        holder.assetName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title=assetInfoList.get(position).getmAssetName();
                BmobQuery<AssetPicture> query = new BmobQuery<>();
                query.addWhereEqualTo("imageNum", assetInfoList.get(position).getmPicture());
                query.findObjects(mContext, new FindListener<AssetPicture>() {
                    @Override
                    public void onSuccess(List<AssetPicture> list) {
                        Message msg = new Message();
                        msg.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("image", (Serializable) list);
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });

            }
        });
    }

    CustomHandler handler = new CustomHandler();
    class CustomHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    List<AssetPicture> list1 = (List<AssetPicture>) msg.getData().getSerializable("image");
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("picture",list1.get(0));
                    bundle.putString("title",title);
                    Intent intent = new Intent(mContext, AssetPictureActivity.class);
                    intent.putExtra(mContext.getPackageName(), bundle);
                    mContext.startActivity(intent);
                    break;

            }
        }
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
     * 下载BmobFile文件
     *
     * @param picture
     */
    private void downloadFile(AssetPicture picture) {

            final File imagefile = new File(mContext.getCacheDir() + picture.getImageFile().getFilename());
            picture.getImageFile().download(mContext, imagefile, new DownloadFileListener() {
                @Override
                public void onSuccess(String s) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message msg = new Message();
                            msg.what = 2;
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("file", imagefile);
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }
                    }).start();

                }

                @Override
                public void onFailure(int i, String s) {

                }
            });


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
        public AssetHolder(View itemView) {
            super(itemView);
            item = (LinearLayout) itemView.findViewById(R.id.ll_asset_item);
            serial_number = (TextView) itemView.findViewById(R.id.tv_assets_item_serial);
            assetName = (TextView) itemView.findViewById(R.id.tv_assets_item_name);
            assetQuantity = (TextView) itemView.findViewById(R.id.tv_assets_item_quantity);
            assetStatus = (TextView) itemView.findViewById(R.id.tv_assets_item_status);
        }
    }
}
