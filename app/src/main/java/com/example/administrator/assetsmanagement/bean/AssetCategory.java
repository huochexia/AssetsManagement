package com.example.administrator.assetsmanagement.bean;

import cn.bmob.v3.BmobObject;

/**资产类别：包括类别名称，类别编号
 * Created by Administrator on 2017/11/8.
 */

public class AssetCategory extends BmobObject {
    /*
    资产类别
     */
    private String cateName;
    /*
    类别编号
     */
    private String cateNum;

    /**
     * setter和 getter方法
     */
    public String getCateName() {
        return cateName;
    }

    public void setCateName(String cateName) {
        this.cateName = cateName;
    }

    public String getCateNum() {
        return cateNum;
    }

    public void setCateNum(String cateNum) {
        this.cateNum = cateNum;
    }
}
