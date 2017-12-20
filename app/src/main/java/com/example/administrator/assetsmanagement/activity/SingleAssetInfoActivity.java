package com.example.administrator.assetsmanagement.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.assetsmanagement.Interface.ToolbarClickListener;
import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.base.ParentWithNaviActivity;
import com.example.administrator.assetsmanagement.bean.AssetInfo;
import com.example.administrator.assetsmanagement.utils.AssetsUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by Administrator on 2017/12/20 0020.
 */

public class SingleAssetInfoActivity extends ParentWithNaviActivity {
    String title;
    @BindView(R.id.iv_single_asset_image)
    ImageView mIvSingleAssetImage;
    @BindView(R.id.tv_scan_asset_manager)
    TextView mTvScanAssetManager;
    @BindView(R.id.tv_scan_asset_location)
    TextView mTvScanAssetLocation;
    @BindView(R.id.tv_scan_asset_department)
    TextView mTvScanAssetDepartment;
    @BindView(R.id.tv_scan_asset_register_date)
    TextView mTvScanAssetRegisterDate;
    @BindView(R.id.tv_scan_asset_state)
    TextView mTvScanAssetState;



    String assetNum ;
    AssetInfo mAssetInfo;

    @Override
    public String title() {
        return title;
    }

    @Override
    public Object left() {
        return R.drawable.ic_left_navi;
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

            }
        };
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_asset_info);
        ButterKnife.bind(this);
        Bundle bundle = getBundle();
        assetNum = bundle.getString("assetNum");
        AssetsUtil.AndQueryAssets(this,"mAssetsNum",assetNum,handler);

    }
    SingleAssetHandler handler = new SingleAssetHandler();
    class SingleAssetHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AssetsUtil.SEARCH_ONE_ASSET:
                    List<AssetInfo> asset = (List<AssetInfo>) msg.getData().getSerializable("assets");
                    if (asset.size() > 0) {
                        mAssetInfo = asset.get(0);
                        Glide.with(SingleAssetInfoActivity.this)
                                .load(mAssetInfo.getPicture().getImageUrl()).into(mIvSingleAssetImage);
                        mTvScanAssetManager.setText(mAssetInfo.getOldManager().getUsername());
                        mTvScanAssetLocation.setText(mAssetInfo.getLocationNum());
                        mTvScanAssetDepartment.setText(mAssetInfo.getDeptNum());
                        mTvScanAssetRegisterDate.setText(mAssetInfo.getRegisterDate());
                        switch (mAssetInfo.getStatus()) {
                            case 0:
                                mTvScanAssetState.setText("正常");
                                break;
                            case 1:
                                mTvScanAssetState.setText("损坏");
                                break;
                            case 2:
                                mTvScanAssetState.setText("丢失");
                                break;
                            case 3:
                                mTvScanAssetState.setText("待报废");
                                break;
                            case 4:
                                mTvScanAssetState.setText("待移交");
                                break;
                            case 5:
                                mTvScanAssetState.setText("送修");
                        }

                        title = mAssetInfo.getAssetName();
                        initNaviView();

                    }
                    break;
            }
        }
    }

}
