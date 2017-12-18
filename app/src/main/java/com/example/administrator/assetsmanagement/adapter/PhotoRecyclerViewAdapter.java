package com.example.administrator.assetsmanagement.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.bumptech.glide.Glide;
import com.example.administrator.assetsmanagement.Interface.PhotoSelectedListener;
import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.bean.AssetPicture;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;

/**
 * 照片适配器:主要用于资产登记图片选择列表,
 * Created by Administrator on 2017/11/19 0019.
 */

public class PhotoRecyclerViewAdapter extends RecyclerView.Adapter<PhotoRecyclerViewAdapter.PhotoViewHolder> {
    Context mContext;
    List<AssetPicture> mPictureList;
    LayoutInflater mInflater;
    PhotoSelectedListener listener;
    Map<Integer,Boolean> map ;
    public PhotoRecyclerViewAdapter(Context context, List<AssetPicture> list) {
        mContext = context;
        mPictureList = list;
        mInflater = LayoutInflater.from(mContext);
        initMap();
    }

    private void initMap() {
        map = new HashMap<>();
        for(int i = 0; i<mPictureList.size();i++) {
            map.put(i, false);
        }
    }

    public void getSelectedListener(PhotoSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_select_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PhotoViewHolder holder, final int position) {
        Glide.with(mContext).load(mPictureList.get(position).getImageUrl())
                .placeholder(R.drawable.pictures_no)
                .into(holder.assetPhoto);
        //图片选择与不选择时角图和过滤色不同
        if (mPictureList.get(position).getSelected()) {
            holder.selected.setImageResource(R.drawable.pictures_selected);
            holder.assetPhoto.setColorFilter(null);
        } else {
            holder.selected.setImageResource(R.drawable.picture_unselected);
            holder.assetPhoto.setColorFilter(Color.parseColor("#77000000"));
        }
        holder.assetPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    if (!mPictureList.get(position).getSelected()) {
                        for (int i = 0; i < mPictureList.size(); i++) {
                            mPictureList.get(i).setSelected(false);
                        }
                        mPictureList.get(position).setSelected(true);
                        listener.selectPhoto(mPictureList.get(position));
                        notifyDataSetChanged();
                    }

            }
        });

    }

    @Override
    public int getItemCount() {
        return mPictureList.size();
    }


    /**
     * ViewHolder
     */
    class PhotoViewHolder extends RecyclerView.ViewHolder {

        ImageView assetPhoto;
        ImageButton selected;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            selected = (ImageButton) itemView.findViewById(R.id.rb_selected_photo);
            assetPhoto = (ImageView) itemView.findViewById(R.id.iv_selected_image);
        }

    }

}
