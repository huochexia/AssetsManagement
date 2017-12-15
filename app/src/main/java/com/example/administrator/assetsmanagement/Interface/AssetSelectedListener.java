package com.example.administrator.assetsmanagement.Interface;

import com.example.administrator.assetsmanagement.bean.AssetInfo;

/**
 * 资产选择事件处理接口
 * Created by Administrator on 2017/12/15.
 */

public interface AssetSelectedListener {
    void selectAsset(AssetInfo assetInfo);

    void cancelAsset(AssetInfo assetInfo);
}
