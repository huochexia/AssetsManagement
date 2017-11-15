package com.example.administrator.assetsmanagement.activity;

import android.content.Intent;
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
import com.example.administrator.assetsmanagement.treeUtil.BaseNode;
import com.example.administrator.assetsmanagement.treeUtil.NodeHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * 登记资产
 * Created by Administrator on 2017/11/4 0004.
 */

public class RegisterAssetsActivity extends ParentWithNaviActivity {
    public static final int REGISTER_LOCATION = 2;
    public static final int REGISTER_CATEGORY = 3;

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
    @BindView(R.id.btn_register_category)
    FancyButton btnRegisterCategory;

    private BaseNode mBaseNode;

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
                Intent intent = new Intent(RegisterAssetsActivity.this, SelectedTreeNodeActivity.class);
                intent.putExtra("type", SelectedTreeNodeActivity.SEARCH_LOCATION);
                startActivityForResult(intent, REGISTER_LOCATION);
                break;
            case R.id.btn_register_category:
                Intent intent1 = new Intent(RegisterAssetsActivity.this, SelectedTreeNodeActivity.class);
                intent1.putExtra("type", SelectedTreeNodeActivity.SEARCH_CATEGORY);
                startActivityForResult(intent1, REGISTER_CATEGORY);
                break;
            case R.id.btn_register_add_ok:
                setAllWidget(true);
                break;
            case R.id.btn_register_add_next:
                setAllWidget(false);
                break;
            case R.id.tv_assets_item_picture_lib:
                break;
            case R.id.tv_assets_item_camera:
                break;
        }
    }

    /**
     * 设置所有控件状态
     * @param i
     */
    private void setAllWidget(boolean i) {
        if (i) {
            btnRegisterAddOk.setEnabled(false);
            btnRegisterAddNext.setEnabled(true);
            btnRegisterCategory.setEnabled(false);
            mBtnRegisterLocation.setEnabled(false);
            mEtRegisterAssetsName.setEnabled(false);
            mEtRegisterAssetsQuantity.setEnabled(false);
            mTvAssetsItemPictureLib.setEnabled(false);
            mTvAssetsItemCamera.setEnabled(false);
        } else {
            btnRegisterAddOk.setEnabled(true);
            btnRegisterAddNext.setEnabled(false);
            btnRegisterCategory.setEnabled(true);
            mBtnRegisterLocation.setEnabled(true);
            mEtRegisterAssetsName.setEnabled(true);
            mEtRegisterAssetsQuantity.setEnabled(true);
            mTvAssetsItemPictureLib.setEnabled(true);
            mTvAssetsItemCamera.setEnabled(true);
            mTvRegisterPlace.setText("");
            mTvRegisterCategory.setText("");
            mEtRegisterAssetsName.setText("");
            mEtRegisterAssetsQuantity.setText("");
            mIvRegisterPicture.setImageResource(R.drawable.assets_image_default);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 2:
                if (resultCode == SelectedTreeNodeActivity.SEARCH_RESULT_OK) {
                    mBaseNode = (BaseNode) data.getSerializableExtra("node");
                    mTvRegisterPlace.setText(getNodeAllPathName(mBaseNode));
                    //TODO:位置编号赋值给资产对象实例
                }
                break;
            case 3:
                if (resultCode == SelectedTreeNodeActivity.SEARCH_RESULT_OK) {
                    mBaseNode = (BaseNode) data.getSerializableExtra("node");
                    mTvRegisterCategory.setText(getNodeAllPathName(mBaseNode));
                    //TODO:类别编号赋值给资产对象实例
                }
        }
    }

    /**
     * 获得节点的全路径名称
     *
     * @param node
     * @return
     */
    private String getNodeAllPathName(BaseNode node) {
        StringBuffer buffer = new StringBuffer();
        List<BaseNode> nodes = new ArrayList<>();
        NodeHelper.getAllParents(nodes, node);
        int i = nodes.size();
        while (i > 0) {
            i--;
            buffer.append(nodes.get(i).getName());
            if (i != 0)
                buffer.append("-");
        }
        return buffer.toString();
    }

    /**
     * 获得节点全路径Id
     *
     * @param node
     * @return
     */
    private String getNodeAllPathId(BaseNode node) {
        StringBuffer buffer = new StringBuffer();
        List<BaseNode> nodes = new ArrayList<>();
        NodeHelper.getAllParents(nodes, node);
        int i = nodes.size();
        while (i > 0) {
            i--;
            buffer.append(nodes.get(i).getId());
            if (i != 0) {
                buffer.append("-");
            }
        }
        return buffer.toString();
    }
}
