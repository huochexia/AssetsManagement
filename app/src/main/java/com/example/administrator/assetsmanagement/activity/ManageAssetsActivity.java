package com.example.administrator.assetsmanagement.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.administrator.assetsmanagement.FlashActivity;
import com.example.administrator.assetsmanagement.Interface.ToolbarClickListener;
import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.base.ParentWithNaviActivity;
import com.example.administrator.assetsmanagement.bean.AssetInfo;
import com.example.administrator.assetsmanagement.bean.Manager.Person;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.readystatesoftware.viewbadger.BadgeView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
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
    @BindView(R.id.ll_manage_asset_approval)
    LinearLayout llManageAssetApproval;
    @BindView(R.id.ll_manage_asset_recycle)
    LinearLayout llManageAssetRecycle;

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
        return R.drawable.whitescan;
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
                customScan();
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
        if (!FlashActivity.mROLE.getRights().contains("审批")) {
            llManageAssetApproval.setVisibility(View.INVISIBLE);
        }
        if (!FlashActivity.mROLE.getRights().contains("处置")) {
            llManageAssetRecycle.setVisibility(View.INVISIBLE);
        }
        badgeView = new BadgeView(this, ivAssetsReceive);// 将需要设置角标的View 传递进去
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
                startActivity(AssetReceiverActivity.class, null, false);
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
                startActivity(ApprovalAssetActivity.class,null,false);
                break;
            case R.id.iv_assets_recycle:
                break;
        }
    }

    /**
     * 获取将要接收的资产数量，做为角标显示出来。查找状态为4待移交或者为6维送移交的
     */
    private void queryCountOfReceiver() {
        List<BmobQuery<AssetInfo>> or  = new ArrayList<>();
        //第一组or关系,状态为4 or 6
        BmobQuery<AssetInfo> query1 = new BmobQuery<>();
        query1.addWhereEqualTo("mStatus", 4);
        BmobQuery<AssetInfo> query2 = new BmobQuery<>();
        query2.addWhereEqualTo("mStatus", 6);
        or.add(query1);
        or.add(query2);
        BmobQuery<AssetInfo> first = new BmobQuery<>();
        first.or(or);
        //第二组 and关系，新管理 和第一组结果
        List<BmobQuery<AssetInfo>> and = new ArrayList<>();
        BmobQuery<AssetInfo> query3 = new BmobQuery<>();
        Person person = BmobUser.getCurrentUser(Person.class);
        query3.addWhereEqualTo("mNewManager", person);
        and.add(query3);
        and.add(first);
        //最后结果
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
                        badgeView.setText(integer+ ""); // 设置要显示的文本
                        badgeView.show();// 将角标显示出来
                    }
                });
            }
        });
    }

    /**
     * 扫描二维码点击事件
     */
    public void customScan() {
        new IntentIntegrator(this)
                .setOrientationLocked(false)
                .setCaptureActivity(CustomScanActivity.class) // 设置自定义的activity是CustomActivity
                .initiateScan(); // 初始化扫描
    }

    // 通过 onActivityResult的方法获取 扫描回来的 值
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(this, "内容为空", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "扫描成功", Toast.LENGTH_LONG).show();
                // ScanResult 为 获取到的字符串
                String ScanResult = intentResult.getContents();
                Bundle bundle = new Bundle();
                bundle.putString("assetNum", ScanResult);
                startActivity(SingleAssetInfoActivity.class, bundle, false);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
