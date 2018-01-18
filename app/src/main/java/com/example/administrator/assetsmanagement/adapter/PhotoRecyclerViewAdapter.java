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
import android.widget.TextView;

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
    Map<Integer, Boolean> map;

    //正在加载更多
    static final int LOADING_MORE = 1;
    //没有更多
    static final int NO_MORE = 2;
    //脚布局当前的状态,默认为没有更多
    int footer_state = 1;

    public PhotoRecyclerViewAdapter(Context context, List<AssetPicture> list) {
        mContext = context;
        mPictureList = list;
        mInflater = LayoutInflater.from(mContext);
        initMap();
    }

    private void initMap() {
        map = new HashMap<>();
        for (int i = 0; i < mPictureList.size(); i++) {
            map.put(i, false);
        }
    }

    public void getSelectedListener(PhotoSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh = null;
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
        if (holder instanceof FooterViewHolder) {
            if (position == 0 || getItemCount() < 15) {
                ((FooterViewHolder)holder).closeAllView();
            } else {
                switch (footer_state) {
                    case LOADING_MORE:
                        ((FooterViewHolder)holder).closeAllView();
                        ((FooterViewHolder) holder).mProgressBar.setVisibility(View.VISIBLE);
                        ((FooterViewHolder) holder).isBaseLine.setVisibility(View.VISIBLE);
                        break;
                    case NO_MORE:
                        ((FooterViewHolder)holder).closeAllView();
                        ((FooterViewHolder) holder).isBaseLine.setVisibility(View.VISIBLE);
                        ((FooterViewHolder) holder).isBaseLine.setText("没有更多的数据了！");
                        ((FooterViewHolder)holder).left_line.setVisibility(View.VISIBLE);
                        ((FooterViewHolder)holder).right_line.setVisibility(View.VISIBLE);
                        break;
                }
            }
        }
        if (holder instanceof PhotoViewHolder) {
            PhotoViewHolder newholder = (PhotoViewHolder) holder;
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

    /**
     * 改变脚布局的状态的方法,在activity根据请求数据的状态来改变这个状态
     * @param footer_state
     */
    public void changeState(int footer_state) {
        this.footer_state = footer_state;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mPictureList != null ? mPictureList.size() + 1 : 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
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
        TextView isBaseLine;
        TextView left_line;
        TextView right_line;
        public FooterViewHolder(View itemView) {
            super(itemView);
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.rcv_load_more);
            isBaseLine = (TextView) itemView.findViewById(R.id.is_base_line);
            left_line = (TextView) itemView.findViewById(R.id.tv_line1);
            right_line = (TextView) itemView.findViewById(R.id.tv_line2);
        }

        public void closeAllView() {
            mProgressBar.setVisibility(View.GONE);
            isBaseLine.setVisibility(View.GONE);
            left_line.setVisibility(View.GONE);
            right_line.setVisibility(View.GONE);
        }
    }


}
