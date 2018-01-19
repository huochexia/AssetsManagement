package com.example.administrator.assetsmanagement.bean;

import com.example.administrator.assetsmanagement.bean.CategoryTree.AssetCategory;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * 资产图片信息类：包括资产的图片、图片的编号和所属类别编号，每种资产都应有其对应的图片
 * Created by Administrator on 2017/11/8.
 */

public class AssetPicture extends BmobObject implements Serializable, Cloneable {

    /*
    图片文件全路径
     */
    private String imageUrl;
    /*
    图片编号
     */
    private String imageNum;
    /*
    资产类别
     */
    private AssetCategory category;
    /*
    是否选择
     */
    private Boolean isSelected = false;

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    /**
     * 构造函数
     */
    public AssetPicture() {
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * setter 和getter 方法
     */

    public String getImageNum() {
        return imageNum;
    }

    public void setImageNum(String imageNum) {
        this.imageNum = imageNum;
    }

    public AssetCategory getCategory() {
        return category;
    }

    public void setCategory(AssetCategory category) {
        this.category = category;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
