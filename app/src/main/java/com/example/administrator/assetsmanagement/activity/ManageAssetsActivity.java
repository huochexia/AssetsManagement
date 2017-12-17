package com.example.administrator.assetsmanagement.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.administrator.assetsmanagement.Interface.ToolbarClickListener;
import com.example.administrator.assetsmanagement.MainActivity;
import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.base.ParentWithNaviActivity;
import com.example.administrator.assetsmanagement.bean.AssetInfo;
import com.example.administrator.assetsmanagement.bean.Person;
import com.readystatesoftware.viewbadger.BadgeView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;

/**
 * 管理资产，列示管理权限范围
 * Created by Administrator on 2017/11/4 0004.
 */

public class ManageAssetsActivity extends ParentWithNaviActivity {
    @BindView(R.id.iv_assets_turn_over)
    ImageView ivAssetsTurnOver;
    @BindView(R.id.iv_assets_receive)
    ImageView ivAssetsReceive;
    @BindView(R.id.iv_assets_repaired)
    ImageView ivAssetsRepaired;
    @BindView(R.id.iv_assets_lose)
    ImageView ivAssetsLose;
    @BindView(R.id.iv_assets_scrapped)
    ImageView ivAssetsScrapped;
    @BindView(R.id.iv_assets_approval)
    ImageView ivAssetsApproval;
    @BindView(R.id.iv_assets_recycle)
    ImageView ivAssetsRecycle;

    BadgeView badgeView;

    @Override
    public String title() {
        return "管理资产";
    }

    @Override
    public Object left() {
        return R.drawable.ic_left_navi;
    }

    @Override
    public Object right() {
        return R.drawable.ic_search_db;
    }

    @Override
    public ToolbarClickListener getToolbarListener() {
        return new ToolbarClickListener() {
            @Override
            public void clickLeft() {
                finish();
            }

            @Override
            public void clickRight() {
                startActivity(SearchAssetsActivity.class, null, false);
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        queryCountOfReceiver();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assets_manage);
        ButterKnife.bind(this);
        initNaviView();
        glideImage();
        badgeView = new BadgeView(this, ivAssetsReceive);// 将需要设置角标的View 传递进去
//        queryCountOfReceiver();
    }

    /**
     * 利用Glide框架加载图片，防止内存溢出
     */
    private void glideImage() {
        Glide.with(this).load(R.drawable.assets_turn_over).into(ivAssetsTurnOver);
        Glide.with(this).load(R.drawable.assets_receiver).into(ivAssetsReceive);
        Glide.with(this).load(R.drawable.assets_repair).into(ivAssetsRepaired);
        Glide.with(this).load(R.drawable.assets_losed).into(ivAssetsLose);
        Glide.with(this).load(R.drawable.assets_baofei).into(ivAssetsScrapped);
        Glide.with(this).load(R.drawable.approval).into(ivAssetsApproval);
        Glide.with(this).load(R.drawable.assets_recycle).into(ivAssetsRecycle);
    }


    @OnClick({R.id.iv_assets_turn_over, R.id.iv_assets_receive, R.id.iv_assets_repaired,
            R.id.iv_assets_lose, R.id.iv_assets_scrapped, R.id.iv_assets_approval,
            R.id.iv_assets_recycle})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_assets_turn_over:
                startActivity(AssetsTurnOverActivity.class, null, false);
                break;
            case R.id.iv_assets_receive:
                startActivity(AssetReceiverActivity.class,null,false);
                break;
            case R.id.iv_assets_repaired:
                startActivity(AssetRepairActivity.class, null, false);
                break;
            case R.id.iv_assets_lose:
                startActivity(AssetLoseActivity.class, null, false);
                break;
            case R.id.iv_assets_scrapped:
                startActivity(AssetBaofeiActivity.class, null, false);
                break;
            case R.id.iv_assets_approval:
                break;
            case R.id.iv_assets_recycle:
                break;
        }
    }

    private void queryCountOfReceiver() {
        List<BmobQuery<AssetInfo>> and = new ArrayList<>();
        BmobQuery<AssetInfo> query1 = new BmobQuery<>();
        Person person = MainActivity.getCurrentPerson();
        query1.addWhereEqualTo("mNewManager", person);
        BmobQuery<AssetInfo> query2 = new BmobQuery<>();
        query2.addWhereEqualTo("mStatus", 4);
        and.add(query1);
        and.add(query2);
        BmobQuery<AssetInfo> query = new BmobQuery<>();
        query.and(and);
        query.count(AssetInfo.class, new CountListener() {
            @Override
            public void done(final Integer integer, BmobException e) {
                runOnMain(new Runnable() {
                    @Override
                    public void run() {
                        badgeView.setTextSize(19);// 设置文本大小
                        badgeView.setTextColor(Color.GREEN);
                        badgeView.setBadgePosition(BadgeView.POSITION_TOP_LEFT);// 设置在右上角
                        badgeView.setText(integer+""); // 设置要显示的文本
                        badgeView.show();// 将角标显示出来
                    }
                });
            }
        });
    }

}
