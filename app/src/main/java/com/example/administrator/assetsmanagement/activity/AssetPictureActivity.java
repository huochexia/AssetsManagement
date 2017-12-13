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
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;

/**
 * 显示某个资产的照片
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
        AssetPicture photo = (AssetPicture) bundle.getSerializable("picture");
        Glide.with(this).load(photo.getImageUrl()).placeholder(R.drawable.pictures_no).into(ivAssetPicture);
    }


}
