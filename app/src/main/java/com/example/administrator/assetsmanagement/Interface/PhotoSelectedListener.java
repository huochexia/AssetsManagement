package com.example.administrator.assetsmanagement.Interface;

import com.example.administrator.assetsmanagement.bean.AssetPicture;

import java.io.File;

/**
 * 图片选择事件监听接口，用于登记资产时处理选择图片事件
 * Created by Administrator on 2017/11/19 0019.
 */

public interface PhotoSelectedListener {
     void selected(String imageNum,File imageFile);
}
