package com.example.administrator.assetsmanagement;

import android.app.Application;
import android.content.Context;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;

/**
 * Created by Administrator on 2017/12/16 0016.
 */

public class AssetManagerApplication extends Application {
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        //通过AppId连接Bmob云端
        Bmob.initialize(this, "facbe328bdb28e7864f448ba3321339f");

    }
    public static Context getContext(){
        return context;
    }
}
