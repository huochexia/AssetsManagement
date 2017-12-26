package com.example.administrator.assetsmanagement;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
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
       // 如果用户在每次打开你的应用程序时都要登录，这将会直接影响到你应用的用户体验。为了避免这种
        // 情况，你可以使用缓存的CurrentUser对象。缓存的用户有效期为1年。
        //每当你应用的用户注册成功或是第一次登录成功，都会在本地磁盘中有一个缓存的用户对象，
        // 这样，你可以通过获取这个缓存的用户对象来进行登录：
        BmobUser bmobUser = BmobUser.getCurrentUser();
        if(bmobUser != null){
            // 允许用户使用应用
            Intent intent = new Intent(this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }else{
            //缓存用户对象为空时， 可打开用户注册界面…
            Intent intent = new Intent(this,LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

    }
    public static Context getContext(){
        return context;
    }

}
