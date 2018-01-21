package com.example.administrator.assetsmanagement;

import android.app.Application;
import android.content.Context;

import cn.bmob.v3.Bmob;


/**
 *
 * Created by Administrator on 2017/12/16 0016.
 */

public class AssetManagerApplication extends Application {
    public static final String COMPANY = "河北省税务干部学校";
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        //通过AppId连接Bmob云端
        Bmob.initialize(this, "380957d6de57719b6496a4f78dd09a60");

    }
    public static Context getContext(){
        return context;
    }

}
