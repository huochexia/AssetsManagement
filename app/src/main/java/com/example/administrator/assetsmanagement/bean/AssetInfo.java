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
    新资产管理员编号
     */
    private String mNewManager;
    /*
    原资产管理员编号,正常情况下新管理员与原管理一致。待移交状态下，新管理员为拟接收的管理员。这
    样设计是为了解决，拟接收人员不能接收资产时，只有新管理员接收了该资产后，旧管理员才会变更为新管理
    员；如果未接收原管理员不变。如果要退回原管理员，则新管理员清空。
    管理员
     */
    private String mOldManager;
    /*
    资产类别编号
     */
    private String mCategoryNum;
    /*
       资产所属图片编号，它是识别资产是否属于一种的标志
     */
    private  String mPicture;

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
    /*
    该资产数量
     */
    private Integer quantity = 1;

    public String getAssetName() {
        return mAssetName;
    }

    public void setAssetName(String assetName) {
        mAssetName = assetName;
    }

    public String getLocationNum() {
        return mLocationNum;
    }

    public void setLocationNum(String locationNum) {
        mLocationNum = locationNum;
    }

    public String getDeptNum() {
        return mDeptNum;
    }

    public void setDeptNum(String deptNum) {
        mDeptNum = deptNum;
    }

    public String getNewManager() {
        return mNewManager;
    }

    public void setNewManager(String newManager) {
        mNewManager = newManager;
    }

    public String getOldManager() {
        return mOldManager;
    }

    public void setOldManager(String oldManager) {
        mOldManager = oldManager;
    }

    public String getCategoryNum() {
        return mCategoryNum;
    }

    public void setCategoryNum(String categoryNum) {
        mCategoryNum = categoryNum;
    }

    public String getPicture() {
        return mPicture;
    }

    public void setPicture(String picture) {
        mPicture = picture;
    }

    public Integer getStatus() {
        return mStatus;
    }

    public void setStatus(Integer status) {
        mStatus = status;
    }

    public String getRegisterDate() {
        return mRegisterDate;
    }

    public void setRegisterDate(String registerDate) {
        mRegisterDate = registerDate;
    }

    public String getAssetsNum() {
        return mAssetsNum;
    }

    public void setAssetsNum(String assetsNum) {
        mAssetsNum = assetsNum;
    }

    public String getComment() {
        return mComment;
    }

    public void setComment(String comment) {
        mComment = comment;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * setter 和 getter 方法
     */


    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
