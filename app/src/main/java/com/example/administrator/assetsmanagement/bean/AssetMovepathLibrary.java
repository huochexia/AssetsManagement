package com.example.administrator.assetsmanagement.bean;

import cn.bmob.v3.BmobObject;

/**资产移动记录库，记录资产移动轨迹
 * Created by Administrator on 2017/11/8.
 */

public class AssetMovepathLibrary extends BmobObject {
    /*
    资产编号
     */
    private String assetNumber;
    /*
    原管理员编号
     */
    private String giveManager;
    /*
    接收管理员编号
     */
    private String  acceptManager;
    /*
    接收日期
     */
    private String accepteDate;

    public String getAssetNumber() {
        return assetNumber;
    }

    public void setAssetNumber(String assetNumber) {
        this.assetNumber = assetNumber;
    }

    public String getGiveManager() {
        return giveManager;
    }

    public void setGiveManager(String giveManager) {
        this.giveManager = giveManager;
    }

    public String getAcceptManager() {
        return acceptManager;
    }

    public void setAcceptManager(String acceptManager) {
        this.acceptManager = acceptManager;
    }

    public String getAccepteDate() {
        return accepteDate;
    }

    public void setAccepteDate(String accepteDate) {
        this.accepteDate = accepteDate;
    }
}
