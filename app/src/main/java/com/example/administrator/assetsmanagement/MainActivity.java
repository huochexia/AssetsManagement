package com.example.administrator.assetsmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.ToxicBakery.viewpager.transforms.RotateDownTransformer;
import com.example.administrator.assetsmanagement.Interface.ToolbarClickListener;
import com.example.administrator.assetsmanagement.activity.CustomScanActivity;
import com.example.administrator.assetsmanagement.activity.SingleAssetInfoActivity;
import com.example.administrator.assetsmanagement.adapter.ViewPagerAdapter;
import com.example.administrator.assetsmanagement.base.ParentWithNaviActivity;
import com.example.administrator.assetsmanagement.bean.Person;
import com.example.administrator.assetsmanagement.fragment.AssetsManagementFragment;
import com.example.administrator.assetsmanagement.fragment.BaseSettingFragment;
import com.example.administrator.assetsmanagement.fragment.MySettingFragment;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MainActivity extends ParentWithNaviActivity {
    public static final int REQUEST_PERSON =1;
    private static Person currentPerson;

    @BindView(R.id.viewpager)
    ViewPager mViewpager;
    @BindView(R.id.navigation)
    BottomNavigationView mNavigation;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvToolbarTitle;

    private MenuItem mMenuItem;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            mTvToolbarTitle.setText(item.getTitle());
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mViewpager.setCurrentItem(0);
                    return true;
                case R.id.navigation_dashboard:
                    mViewpager.setCurrentItem(1);
                    return true;
                case R.id.navigation_notifications:
                    mViewpager.setCurrentItem(2);
                    return true;
            }
            return false;
        }

    };
    @Override
    public String title() {
        return "欢迎使用资产管理系统";
    }

     @Override
    public ToolbarClickListener getToolbarListener() {
        return new ToolbarClickListener() {
            @Override
            public void clickLeft() {

            }

            @Override
            public void clickRight() {

            }
        };
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getPerson();
        initNaviView();
        mViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (mMenuItem != null) {
                    mMenuItem.setChecked(false);
                } else {
                     mNavigation.getMenu().getItem(0).setChecked(false);
                }
                mMenuItem = mNavigation.getMenu().getItem(position);
                mMenuItem.setChecked(true);
                mTvToolbarTitle.setText(mMenuItem.getTitle());
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        setViewpager(mViewpager);
        //增加ViewPager转换动画
        mViewpager.setPageTransformer(true,new RotateDownTransformer());
    }

    /**
     * 给ViewPager添加内容
     */
    public void setViewpager(ViewPager viewpager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(AssetsManagementFragment.newInstance());
        adapter.addFragment(BaseSettingFragment.newInstance());
        adapter.addFragment(MySettingFragment.newInstance());
        viewpager.setAdapter(adapter);
    }
    /**
     * 实现再按一次返回键退出程序
     */
    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                toast("再按一次退出程序！");
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public static Person getCurrentPerson() {
        return currentPerson;
    }
    /**
     * 获取当前用户的Person对象
     */
    private void getPerson() {
        BmobQuery<Person> queryPerson = new BmobQuery<>();
        String id = BmobUser.getCurrentUser().getObjectId();
        queryPerson.addWhereEqualTo("objectId", id);
        queryPerson.findObjects(new FindListener<Person>() {
            @Override
            public void done(final List<Person> list, BmobException e) {
                if (e == null && list.size() > 0) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message msg = new Message();
                            msg.what = REQUEST_PERSON;
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("person", list.get(0));
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }
                    }).start();
                }
            }
        });
    }
    PersonHandler handler = new PersonHandler();
    class PersonHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REQUEST_PERSON:
                    currentPerson = (Person) msg.getData().getSerializable("person");
                    break;
            }
        }
    }

}
