package com.example.administrator.assetsmanagement.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.administrator.assetsmanagement.Interface.ToolbarClickListener;
import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.base.ParentWithNaviActivity;
import com.example.administrator.assetsmanagement.bean.AssetPicture;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.listener.DownloadFileListener;

/**
 * Created by Administrator on 2017/12/8.
 */

public class AssetPictureActivity extends ParentWithNaviActivity {
    String title;
    @BindView(R.id.iv_asset_picture)
    ImageView ivAssetPicture;

    @Override
    public String title() {
        return title;
    }

    @Override
    public Object left() {
        return R.drawable.ic_left_navi;
    }

    @Override
    public ToolbarClickListener getToolbarListener() {
        return new ToolbarClickListener() {
            @Override
            public void clickLeft() {
                finish();
            }

            @Override
            public void clickRight() {

            }
        };
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_picture);
        ButterKnife.bind(this);
        Bundle bundle = getBundle();
        title = bundle.getString("title");
        initNaviView();
        downloadFile((AssetPicture) bundle.getSerializable("picture"));
    }

    /**
     * 获得图片文件
     *
     * @param picture
     */
    private void downloadFile(AssetPicture picture) {
        final File imagefile = new File(this.getCacheDir() + picture.getImageFile().getFilename());
        picture.getImageFile().download(this, imagefile, new DownloadFileListener() {
            @Override
            public void onSuccess(String s) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message msg = new Message();
                        msg.what = 1;
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

    PictureHandler handler = new PictureHandler();

    class PictureHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    File file = (File) msg.getData().getSerializable("file");
                    Glide.with(AssetPictureActivity.this).load(file).centerCrop().into(ivAssetPicture);
                    break;

            }
        }
    }
}
