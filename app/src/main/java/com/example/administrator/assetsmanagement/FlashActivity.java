package com.example.administrator.assetsmanagement;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.administrator.assetsmanagement.base.BaseActivity;
import com.example.administrator.assetsmanagement.bean.Manager.Person;
import com.example.administrator.assetsmanagement.bean.Manager.Role;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;


/**
 * Created by Administrator on 2017/12/26.
 */

public class FlashActivity extends BaseActivity {
    ImageView mImageView;
    Handler handler;
    Runnable runnable;

    public static Role mROLE;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash);
        mImageView = (ImageView) findViewById(R.id.bing_pic_img);
        Glide.with(this).load(R.drawable.flashimage).into(mImageView);
        handler = new Handler();
        queryRole();
        runnable = new Runnable() {
            @Override
            public void run() {
                startMainOrLoginActivity();
            }
        };
        handler.postDelayed(runnable, 3000);

    }

    /**
     * 如果用户在每次打开你的应用程序时都要登录，这将会直接影响到你应用的用户体验。为了避免这种
     * 情况，你可以使用缓存的CurrentUser对象。缓存的用户有效期为1年。
     * 每当你应用的用户注册成功或是第一次登录成功，都会在本地磁盘中有一个缓存的用户对象，
     * 这样，你可以通过获取这个缓存的用户对象来进行登录：
     */
    private void startMainOrLoginActivity() {

        BmobUser bmobUser = BmobUser.getCurrentUser();
        if (bmobUser != null) {
            // 允许用户使用应用
            startActivity(MainActivity.class,null,true);

        } else {
            //缓存用户对象为空时， 可打开用户注册界面…
            startActivity(LoginActivity.class,null,true);
        }
    }


    /**
     * 获取当前用户的权限
     */
    public static void queryRole() {
        final Person person = BmobUser.getCurrentUser(Person.class);
        BmobQuery<Role> query = new BmobQuery<>();
        query.addWhereEqualTo("user", person);
        query.findObjects(new FindListener<Role>() {
            @Override
            public void done(final List<Role> list, BmobException e) {
                if (list != null && list.size() > 0) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            mROLE = list.get(0);
                        }
                    });

                }
            }
        });
    }

}
