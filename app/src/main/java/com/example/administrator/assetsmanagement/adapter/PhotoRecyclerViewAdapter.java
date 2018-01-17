package com.example.administrator.assetsmanagement.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.example.administrator.assetsmanagement.Interface.PhotoSelectedListener;
import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.bean.AssetPicture;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 照片适配器:主要用于资产登记图片选择列表,
 * Created by Administrator on 2017/11/19 0019.
 */

public class PhotoRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public final static int TYPE_NORMAL = 1; // 正常
    public final static int TYPE_FOOTER = 2;//底部--往往是loading_more

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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh =null;
        View view;
        switch (viewType) {
            case TYPE_NORMAL:
                view = mInflater.inflate(R.layout.item_select_photo, parent, false);
                vh = new PhotoViewHolder(view);
                return vh;
            case TYPE_FOOTER:
                view = mInflater.inflate(R.layout.recyclerview_footer, parent, false);
                vh = new FooterViewHolder(view);
                return vh;
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        //这时候 article是 null，先把 footer 处理了
        if (holder instanceof FooterViewHolder) {
            ((FooterViewHolder) holder).mProgressBar.setVisibility(View.VISIBLE);
            return;
        }
        if (holder instanceof PhotoViewHolder) {
            PhotoViewHolder newholder = (PhotoViewHolder)holder;
            Glide.with(mContext).load(mPictureList.get(position).getImageUrl())
                    .placeholder(R.drawable.pictures_no)
                    .into(newholder.assetPhoto);
            //图片选择与不选择时角图和过滤色不同
            if (mPictureList.get(position).getSelected()) {
                newholder.selected.setImageResource(R.drawable.pictures_selected);
                newholder.assetPhoto.setColorFilter(null);
            } else {
                newholder.selected.setImageResource(R.drawable.picture_unselected);
                newholder.assetPhoto.setColorFilter(Color.parseColor("#77000000"));
            }
            newholder.assetPhoto.setOnClickListener(new View.OnClickListener() {
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

    }

    @Override
    public int getItemCount() {
        return mPictureList.size();
    }

    @Override
    public int getItemViewType(int position) {
        AssetPicture picture = mPictureList.get(position);
        if (picture==null) {
            return TYPE_FOOTER;
        } else {
            return TYPE_NORMAL;
        }
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

    /**
     * Footer ViewHolder
     */
    class FooterViewHolder extends RecyclerView.ViewHolder {
        ProgressBar mProgressBar;
        public FooterViewHolder(View itemView) {
            super(itemView);
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.rcv_load_more);
        }
    }
}
