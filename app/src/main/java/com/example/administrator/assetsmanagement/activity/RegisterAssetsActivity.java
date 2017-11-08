package com.example.administrator.assetsmanagement.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.assetsmanagement.Interface.ToolbarClickListener;
import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.base.ParentWithNaviActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * 登记资产
 * Created by Administrator on 2017/11/4 0004.
 */

public class RegisterAssetsActivity extends ParentWithNaviActivity {

    @BindView(R.id.tv_register_place)
    TextView mTvRegisterPlace;
    @BindView(R.id.tv_register_category)
    TextView mTvRegisterCategory;
    @BindView(R.id.tv_assets_register_name)
    TextView mTvAssetsRegisterName;
    @BindView(R.id.et_register_assets_name)
    EditText mEtRegisterAssetsName;
    @BindView(R.id.btn_register_location)
    FancyButton mBtnRegisterLocation;
    @BindView(R.id.iv_register_picture)
    ImageView mIvRegisterPicture;
    @BindView(R.id.tv_assets_item_quantity)
    TextView mTvAssetsItemQuantity;
    @BindView(R.id.et_register_assets_quantity)
    EditText mEtRegisterAssetsQuantity;
    @BindView(R.id.tv_assets_item_picture_lib)
    TextView mTvAssetsItemPictureLib;
    @BindView(R.id.tv_assets_item_camera)
    TextView mTvAssetsItemCamera;
    @BindView(R.id.rv_assets_register_list)
    RecyclerView rvAssetsRegisterList;
    @BindView(R.id.btn_register_add_ok)
    FancyButton btnRegisterAddOk;
    @BindView(R.id.btn_register_add_next)
    FancyButton btnRegisterAddNext;

    @Override
    public String title() {
        return "登记资产";
    }

    @Override
    public Object left() {
        return R.drawable.ic_left_navi;
    }

    /**
     * 实现点击事件处理方法
     *
     * @return
     */
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
        setContentView(R.layout.activity_assets_register);
        ButterKnife.bind(this);
        initNaviView();
        btnRegisterAddNext.setEnabled(false);
        setTextFonts();
    }

    private void setTextFonts() {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/隶书.ttf");
        mTvAssetsItemQuantity.setTypeface(typeface);
        mTvAssetsRegisterName.setTypeface(typeface);
        mTvRegisterCategory.setTypeface(typeface);
        mEtRegisterAssetsName.setTypeface(typeface);
        mEtRegisterAssetsQuantity.setTypeface(typeface);
        mTvAssetsItemPictureLib.setTypeface(typeface);
        mTvAssetsItemCamera.setTypeface(typeface);
    }

    @OnClick({R.id.btn_register_location, R.id.btn_register_category, R.id.btn_register_add_ok,
            R.id.btn_register_add_next, R.id.tv_assets_item_picture_lib, R.id.tv_assets_item_camera})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_register_location:
                break;
            case R.id.btn_register_category:
                break;
            case R.id.btn_register_add_ok:
                btnRegisterAddOk.setEnabled(false);
                btnRegisterAddNext.setEnabled(true);

                break;
            case R.id.btn_register_add_next:
                btnRegisterAddOk.setEnabled(true);
                btnRegisterAddNext.setEnabled(false);
                mTvRegisterPlace.setText("");
                mTvRegisterCategory.setText("");
                mEtRegisterAssetsName.setText("");
                mEtRegisterAssetsQuantity.setText("");
                mIvRegisterPicture.setImageResource(R.drawable.assets_image_default);
                break;
            case R.id.tv_assets_item_picture_lib:
                break;
            case R.id.tv_assets_item_camera:
                break;
        }
    }


}
