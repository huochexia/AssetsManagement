package com.example.administrator.assetsmanagement.bean;

import com.example.administrator.assetsmanagement.bean.CategoryTree.AssetCategory;
import com.example.administrator.assetsmanagement.bean.DepartmentTree.Department;
import com.example.administrator.assetsmanagement.bean.LocationTree.Location;
import com.example.administrator.assetsmanagement.bean.Manager.Person;

import cn.bmob.v3.BmobObject;

/**
 * 资产信息：记录每一个资产的基本信息，包括：名称、所在位置编号、部门编号、管理员、资产类别编号、
 * 图片编号、状态等
 * Created by Administrator on 2017/11/8.
 */

public class AssetInfo extends BmobObject implements Cloneable {

    /*
    资产位置
     */
    private Location mLocation;
    /*
    资产所属部门
     */
    private Department mDepartment;
    /*
    新资产管理员
     */
    private Person mNewManager;
    /*
    原资产管理员,正常情况下新管理员与原管理一致。待移交状态下，新管理员为拟接收的管理员。这
    样设计是为了解决，拟接收人员不能接收资产时，只有新管理员接收了该资产后，旧管理员才会变更为新管理
    员；如果未接收原管理员不变。如果要退回原管理员，则新管理员清空。
    管理员
     */
    private  Person mOldManager;
    /*
    资产类别
     */
    private AssetCategory mCategory;
    /*
     资产所属图片，它是识别资产是否属于一种的标志
     */
    private  AssetPicture mPicture;

    /*
    资产状态：0正常，1损坏，2丢失，3待报废：即管理员提交报废，但还没有得到批准；4待移交，即管理员准
    备移交，但接受者还没有确认,5已经批准报废,6维修并移交，9新登记未移交
     */
    private Integer mStatus ;
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
    该资产数量，它永远是1。因为一资产一编号，设计它主要是为了方便汇总同类资产
     */
    private Integer quantity = 1;
    /*
    资产单价
     */
    private Float price =0.0f;
    /*
    是否固定资产
     */
    private Boolean isFixedAsset;

    public Boolean getFixedAsset() {
        return isFixedAsset;
    }

    public void setFixedAsset(Boolean fixedAsset) {
        isFixedAsset = fixedAsset;
    }

   

    public Location getLocation() {
        return mLocation;
    }

    public void setLocation(Location location) {
        mLocation = location;
    }

    public Department getDepartment() {
        return mDepartment;
    }

    public void setDepartment(Department department) {
        mDepartment = department;
    }

    public Person getNewManager() {
        return mNewManager;
    }

    public void setNewManager(Person newManager) {
        mNewManager = newManager;
    }

    public Person getOldManager() {
        return mOldManager;
    }

    public void setOldManager(Person oldManager) {
        mOldManager = oldManager;
    }

    public AssetCategory getCategory() {
        return mCategory;
    }

    public void setCategory(AssetCategory category) {
        mCategory = category;
    }

    public AssetPicture getPicture() {
        return mPicture;
    }

    public void setPicture(AssetPicture picture) {
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

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
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
