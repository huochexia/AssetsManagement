package com.example.administrator.assetsmanagement;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.administrator.assetsmanagement.activity.RegisterAssetsActivity;
import com.example.administrator.assetsmanagement.bean.Person;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;


/**
 * Created by Administrator on 2017/12/16 0016.
 */

public class AssetManagerApplication extends Application {
    public static final int REQUEST_PERSON =1;
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
