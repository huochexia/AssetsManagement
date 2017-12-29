package com.example.administrator.assetsmanagement;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.administrator.assetsmanagement.base.BaseActivity;
import com.example.administrator.assetsmanagement.bean.Person;
import com.example.administrator.assetsmanagement.bean.Role;

import java.io.IOException;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
        handler = new Handler();
        loadBingPic();
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
     * 加载必应每日一图
     */
    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(FlashActivity.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(getApplicationContext()).load(bingPic).into(mImageView);
                    }
                });
            }
        });

    }

    public void sendOkHttpRequest(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * 获取当前用户的权限
     */
    public
    void queryRole() {
        final Person person = BmobUser.getCurrentUser(Person.class);
        BmobQuery<Role> query = new BmobQuery<>();
        query.addWhereEqualTo("user", person);
        query.findObjects(new FindListener<Role>() {
            @Override
            public void done(final List<Role> list, BmobException e) {
                if (list != null && list.size() > 0) {
                    runOnMain(new Runnable() {
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
