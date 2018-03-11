package com.example.administrator.assetsmanagement.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.administrator.assetsmanagement.Interface.ToolbarClickListener;
import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.base.ParentWithNaviActivity;
import com.example.administrator.assetsmanagement.bean.AssetPicture;

import butterknife.BindView;
import butterknife.ButterKnife;

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
        AssetPicture assetPicture = (AssetPicture) bundle.getSerializable("picture");
        title = assetPicture != null ? assetPicture.getAssetName() : null;
        initNaviView();
        Glide.with(this).load(assetPicture != null ? assetPicture.getImageUrl() : null).placeholder(R.drawable.pictures_no).into(ivAssetPicture);
        //5秒后自动关闭
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                finish();
            }
        };
        handler.postDelayed(runnable, 5000);
    }


}
