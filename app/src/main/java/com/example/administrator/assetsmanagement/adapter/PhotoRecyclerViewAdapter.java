package com.example.administrator.assetsmanagement.adapter;

import android.content.Context;
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

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;

/**
 * 照片适配器:主要用于资产登记图片选择列表
 * Created by Administrator on 2017/11/19 0019.
 */

public class PhotoRecyclerViewAdapter extends RecyclerView.Adapter<PhotoRecyclerViewAdapter.PhotoViewHolder> {
    Context mContext;
    List<AssetPicture> mPictureList;
    LayoutInflater mInflater;
    PhotoSelectedListener listener;

    Map<String, File> mFileMap = new HashMap<>();

    public PhotoRecyclerViewAdapter(Context context, List<AssetPicture> list) {
        mContext = context;
        mPictureList = list;
        mInflater = LayoutInflater.from(mContext);
        downloadFile(mPictureList);
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
        setImageLayoutSize(holder);
        Glide.with(mContext).load(mFileMap.get(mPictureList.get(position).getImageNum()))
                .centerCrop().into(holder.assetPhoto);
        if (mPictureList.get(position).getSelected()) {
            holder.selected.setChecked(true);
        } else {
            holder.selected.setChecked(false);
        }
        holder.selected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mPictureList.get(position).getSelected()) {
                    for (int i = 0; i < mPictureList.size(); i++) {
                        mPictureList.get(i).setSelected(false);
                    }
                    mPictureList.get(position).setSelected(true);
                    String imagenum = mPictureList.get(position).getImageNum();
                    listener.selected(mPictureList.get(position).getImageNum(),mFileMap.get(imagenum));
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
     * 下载BmobFile文件
     *
     * @param mList
     */
    private void downloadFile(List<AssetPicture> mList) {

        for (final AssetPicture picture : mList) {
            final File imagefile = new File(mContext.getCacheDir() + picture.getImageFile().getFilename());
            picture.getImageFile().download(imagefile, new DownloadFileListener() {
                @Override
                public void onProgress(Integer integer, long l) {

                }

                @Override
                public void done(String s, BmobException e) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message msg = new Message();
                            msg.what = PHOTO_FILE;
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("file", imagefile);
                            bundle.putString("imageNum",picture.getImageNum());
                            msg.setData(bundle);
                            hanlder.sendMessage(msg);
                        }
                    }).start();
                }

            });
        }

    }

    /**
     * 根据屏幕大小计算并重新设置ImageView容器大小
     *
     * @param holder
     */
    private void setImageLayoutSize(PhotoViewHolder holder) {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        int height = outMetrics.heightPixels;
        FrameLayout fl = holder.mFrameLayout;
        ViewGroup.LayoutParams para = fl.getLayoutParams();
        para.width = (int) (width / 3.7);
        para.height = height / 4;
        fl.setLayoutParams(para);
    }

    /**
     * ViewHolder
     */
    class PhotoViewHolder extends RecyclerView.ViewHolder {
        RadioButton selected;
        ImageView assetPhoto;
        FrameLayout mFrameLayout;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            selected = (RadioButton) itemView.findViewById(R.id.rb_selected_photo);
            assetPhoto = (ImageView) itemView.findViewById(R.id.iv_selected_image);
            mFrameLayout = (FrameLayout) itemView.findViewById(R.id.fl_photo);
        }

    }

    public static final int PHOTO_FILE = 1;
    public MyHandler hanlder = new MyHandler();

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PHOTO_FILE:
                    String imagenum = msg.getData().getString("imageNum");
                    File imagefile = (File) msg.getData().getSerializable("file");
                    mFileMap.put(imagenum, imagefile);
                    notifyDataSetChanged();
                    break;

            }
        }
    }
}
