package com.example.administrator.assetsmanagement.bean;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * 资产图片信息类：包括资产的图片、图片的编号和所属类别编号，每种资产都应有其对应的图片
 * Created by Administrator on 2017/11/8.
 */

public class AssetPicture extends BmobObject implements Serializable, Cloneable {

    /*
    图片文件
     */
    private BmobFile imageFile;
    /*
    图片编号
     */
    private String imageNum;
    /*
    资产类别编号
     */
    private String categoryNum;
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

    public AssetPicture(BmobFile imageFile, String imageNum, String categoryNum) {
        this.imageFile = imageFile;
        this.imageNum = imageNum;
        this.categoryNum = categoryNum;
    }

    /**
     * setter 和getter 方法
     */
    public BmobFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(BmobFile imageFile) {
        this.imageFile = imageFile;
    }

    public String getImageNum() {
        return imageNum;
    }

    public void setImageNum(String imageNum) {
        this.imageNum = imageNum;
    }

    public String getCategoryNum() {
        return categoryNum;
    }

    public void setCategoryNum(String categoryNum) {
        this.categoryNum = categoryNum;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
