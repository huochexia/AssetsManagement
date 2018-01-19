package com.example.administrator.assetsmanagement.utils;

import com.example.administrator.assetsmanagement.bean.AssetPicture;

/**
 * Created by Administrator on 2018/1/19.
 */

public class PictureReceiveEvent {
    AssetPicture assetPicture;
    public PictureReceiveEvent(AssetPicture picture) {
        assetPicture = picture;
    }
    public AssetPicture getAssetPicture() {
        return assetPicture;
    }
}
