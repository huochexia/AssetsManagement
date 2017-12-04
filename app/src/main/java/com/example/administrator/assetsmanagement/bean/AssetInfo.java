package com.example.administrator.assetsmanagement.bean;

import cn.bmob.v3.BmobObject;

/**
 * 资产信息：记录每一个资产的基本信息，包括：名称、所在位置编号、部门编号、管理员、资产类别编号、
 * 图片编号、状态等
 * Created by Administrator on 2017/11/8.
 */

public class AssetInfo extends BmobObject implements Cloneable {
    /*
    资产名称
     */
    private String mAssetName;
    /*
    资产位置编号
     */
    private String mLocationNum;
    /*
    资产所属部门编号
     */
    private String mDeptNum;
    /*
    资产管理员编号
     */
    private String mManagerNum;
    /*
    资产类别编号
     */
    private String mCategoryNum;
    /*
           资产所属图片
            */
    private AssetPicture mPicture;


    /*
    资产状态：0正常，1损坏，2丢失，3待报废：即管理员提交报废，但还没有得到批准；4待移交，即管理员准
    备移交，但接受者还没有确认
     */
    private Integer mStatus = 0;
    /*
    资产登记日期（如2017-11-08）
     */
    private String mRegisterDate;
    /*
    资产编号：当前系统时间字符串+“-”+该资产的序列号。
    */
    private String mAssetsNum;
    /*
    备注内容
     */
    private String mComment;

    /**
     * setter 和 getter 方法
     */
    public String getmAssetName() {
        return mAssetName;
    }

    public void setmAssetName(String mAssetName) {
        this.mAssetName = mAssetName;
    }

    public String getmLocationNum() {
        return mLocationNum;
    }

    public void setmLocationNum(String mLocationNum) {
        this.mLocationNum = mLocationNum;
    }

    public String getmDeptNum() {
        return mDeptNum;
    }

    public void setmDeptNum(String mDeptNum) {
        this.mDeptNum = mDeptNum;
    }

    public String getmManagerNum() {
        return mManagerNum;
    }

    public void setmManagerNum(String mManagerNum) {
        this.mManagerNum = mManagerNum;
    }

    public String getmCategoryNum() {
        return mCategoryNum;
    }

    public void setmCategoryNum(String mCategoryNum) {
        this.mCategoryNum = mCategoryNum;
    }

    public Integer getmStatus() {
        return mStatus;
    }

    public void setmStatus(Integer mStatus) {
        this.mStatus = mStatus;
    }

    public String getmRegisterDate() {
        return mRegisterDate;
    }

    public void setmRegisterDate(String mRegisterDate) {
        this.mRegisterDate = mRegisterDate;
    }

    public String getmAssetsNum() {
        return mAssetsNum;
    }

    public void setmAssetsNum(String mAssetsNum) {
        this.mAssetsNum = mAssetsNum;
    }

    public String getmComment() {
        return mComment;
    }

    public void setmComment(String mComment) {
        this.mComment = mComment;
    }

    public AssetPicture getPicture() {
        return mPicture;
    }

    public void setPicture(AssetPicture picture) {
        mPicture = picture;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
