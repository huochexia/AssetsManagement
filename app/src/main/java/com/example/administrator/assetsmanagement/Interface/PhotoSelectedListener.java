package com.example.administrator.assetsmanagement.Interface;

import com.example.administrator.assetsmanagement.bean.AssetPicture;

import java.io.File;

/**
 * Created by Administrator on 2017/11/19 0019.
 */

public interface PhotoSelectedListener {
     void selected(String imageNum,File imageFile);
}
