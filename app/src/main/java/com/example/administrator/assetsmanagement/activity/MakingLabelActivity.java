package com.example.administrator.assetsmanagement.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.assetsmanagement.Interface.AssetSelectedListener;
import com.example.administrator.assetsmanagement.Interface.ToolbarClickListener;
import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.adapter.MakingLabelsListAdapter;
import com.example.administrator.assetsmanagement.base.ParentWithNaviActivity;
import com.example.administrator.assetsmanagement.bean.AssetInfo;
import com.example.administrator.assetsmanagement.bean.AssetPicture;
import com.example.administrator.assetsmanagement.utils.AssetsUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 制作资产标签，仅打印标签，或者打印后进行移交。该活动适用于新登记的资产(尚未保存）和从数据库中获取
 * 的资产。通过flag来进行判断。
 * Created by Administrator on 2018/1/3 0003.
 */

public class MakingLabelActivity extends ParentWithNaviActivity {
    @BindView(R.id.tv_item_header_name)
    TextView mTvItemHeaderName;
    @BindView(R.id.tv_item_header_quantity)
    TextView mTvItemHeaderQuantity;
    @BindView(R.id.ll_item_header_quantity)
    LinearLayout mLlItemHeaderQuantity;
    @BindView(R.id.ll_item_header_status)
    LinearLayout mLlItemHeaderStatus;
    @BindView(R.id.rv_making_label)
    RecyclerView mRvMakingLabel;

    MakingLabelsListAdapter madapter;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvToolbarTitle;
    @BindView(R.id.iv_left_navi)
    ImageView mIvLeftNavi;
    @BindView(R.id.iv_right_navi)
    ImageView mIvRightNavi;
    @BindView(R.id.btn_print_label_and_move_asset)
    Button mBtnPrintLabelAndMoveAsset;
    private List<AssetInfo> mInfoList;
    private List<AssetInfo> mSelectedList;
    private AssetPicture mAssetPicture;
    private int flag;//标志，1为新，否则为旧数据

    @Override
    public String title() {
        return "制作标签";
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
        setContentView(R.layout.activity_making_label_and_move);
        initNaviView();
        ButterKnife.bind(this);

        mLlItemHeaderStatus.setVisibility(View.INVISIBLE);
        mTvItemHeaderName.setText("资产编号");
        mTvItemHeaderQuantity.setText("名称");

        LinearLayoutManager ll = new LinearLayoutManager(this);
        mRvMakingLabel.setLayoutManager(ll);

        Bundle bundle = getBundle();
        flag = bundle.getInt("flag");//标志，为1则是新登记尚未保存的。否则需要从数据库中查询得到
        if (flag == 1) {
            mInfoList = (List<AssetInfo>) bundle.getSerializable("newasset");
            setListAdapter();

        } else {
            mBtnPrintLabelAndMoveAsset.setEnabled(false);//旧资产在这里只打印，不做移交。
            mAssetPicture = (AssetPicture) bundle.getSerializable("picture");
            AssetsUtil.AndQueryAssets(this, "mPicture", mAssetPicture, handler);
        }


    }

    /**
     *设置列表适配器
     */
    private void setListAdapter() {
        madapter = new MakingLabelsListAdapter(MakingLabelActivity.this, mInfoList);
        mRvMakingLabel.setAdapter(madapter);
        madapter.setSelectedListener(new AssetSelectedListener() {
            @Override
            public void selectAsset(AssetInfo assetInfo) {
                mSelectedList.add(assetInfo);
            }

            @Override
            public void cancelAsset(AssetInfo assetInfo) {
                mSelectedList.remove(assetInfo);
            }
        });
    }

    @OnClick({R.id.btn_print_asset_label, R.id.btn_print_label_and_move_asset})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_print_asset_label:
                if (flag == 1) {//打印完后，保存资产信息
                    //TODO:打印功能
                } else {//否则只打印

                }
                break;
            case R.id.btn_print_label_and_move_asset:
                //如果是新登记的资产打印后要做移交时，要直接传递资产信息，移交后再保存。因为如果先保存
                //再从数据库中取出进行移交，因为网络时差的原因，会产生取出的数据不全的现象。所以要移交
                // 后再做保存。

                break;
        }
    }

    MakingHander handler = new MakingHander();

    class MakingHander extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AssetsUtil.SEARCH_ONE_ASSET:
                    mInfoList = (List<AssetInfo>) msg.getData().getSerializable("assets");
                    setListAdapter();
                    break;
            }
        }
    }

}
