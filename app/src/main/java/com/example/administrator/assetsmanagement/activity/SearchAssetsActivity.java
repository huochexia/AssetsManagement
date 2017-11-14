package com.example.administrator.assetsmanagement.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RadioGroup;
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
 * Created by Administrator on 2017/11/4 0004.
 */

public class SearchAssetsActivity extends ParentWithNaviActivity {

    public static final int SEARCHASSETS_REQUEST = 1;

    @BindView(R.id.rg_assets_search)
    RadioGroup mRgAssetsSearch;
    @BindView(R.id.btn_search_location)
    FancyButton mBtnSearchLocation;
    @BindView(R.id.btn_register_category)
    FancyButton mBtnRegisterCategory;
    @BindView(R.id.btn_search_name)
    FancyButton mBtnSearchName;
    @BindView(R.id.btn_search_manager)
    FancyButton mBtnSearchManager;
    @BindView(R.id.tv_search_content)
    TextView mTvSearchContent;
    @BindView(R.id.btn_register_add_ok)
    FancyButton mBtnRegisterAddOk;
    @BindView(R.id.btn_search_dept)
    FancyButton mBtnSearchDept;
    @BindView(R.id.rc_search_list)
    RecyclerView mRcSearchList;

    private BaseNode mNode;//接收传入的节点信息

    @Override
    public String title() {
        return "查找资产";
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
        setContentView(R.layout.activity_assets_search);
        ButterKnife.bind(this);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/隶书.ttf");
        mTvSearchContent.setTypeface(typeface);
        initNaviView();
        mRgAssetsSearch.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_assets_search_location:
                        allSetGone();
                        mBtnSearchLocation.setVisibility(View.VISIBLE);
                        break;
                    case R.id.rb_assets_search_category:
                        allSetGone();
                        mBtnRegisterCategory.setVisibility(View.VISIBLE);
                        break;
                    case R.id.rb_assets_search_dept:
                        allSetGone();
                        mBtnSearchDept.setVisibility(View.VISIBLE);
                        break;
                    case R.id.rb_assets_search_manager:
                        allSetGone();
                        mBtnSearchManager.setVisibility(View.VISIBLE);
                        break;
                    case R.id.rb_assets_search_name:
                        allSetGone();
                        mBtnSearchName.setVisibility(View.VISIBLE);
                        break;
                    case R.id.rb_assets_search_scrap:
                        allSetGone();
                        mTvSearchContent.setText("所有报废资产");
                        mTvSearchContent.setTextSize(25);
                        break;
                }
            }
        });
    }

    private void allSetGone() {
        mBtnSearchLocation.setVisibility(View.GONE);
        mBtnRegisterCategory.setVisibility(View.GONE);
        mBtnSearchManager.setVisibility(View.GONE);
        mBtnSearchName.setVisibility(View.GONE);
        mBtnSearchDept.setVisibility(View.GONE);
        mTvSearchContent.setText("");
    }

    @OnClick({R.id.btn_search_location, R.id.btn_register_category, R.id.btn_search_name, R.id.btn_search_manager, R.id.btn_search_dept})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_search_location:
                startActivity(SelectedTreeNodeActivity.SEARCH_LOCATION, false);
                break;
            case R.id.btn_register_category:
                startActivity(SelectedTreeNodeActivity.SEARCH_CATEGORY, false);
                break;
            case R.id.btn_search_name:

                break;
            case R.id.btn_search_dept:
                startActivity(SelectedTreeNodeActivity.SEARCH_DEPARTMENT, false);
                break;
            case R.id.btn_search_manager:
                startActivity(SelectedTreeNodeActivity.SEARCH_MANAGER, true);
                break;
        }
    }

    private void startActivity(int type, boolean isPerson) {
        Intent intent = new Intent(SearchAssetsActivity.this, SelectedTreeNodeActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("person", isPerson);
        startActivityForResult(intent, SEARCHASSETS_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == SelectedTreeNodeActivity.SEARCH_RESULT_OK) {
                    mNode = (BaseNode) data.getSerializableExtra("node");
                    setSearchContent(mNode);
                }
                break;
            default:
        }
    }

    /**
     * 显示要查找的内容，传入的节点是201室，得到它的完整链内容。比如 A座-2楼-201室。
     * @param baseNode
     */
    private void setSearchContent(BaseNode baseNode) {
        StringBuffer buffer = new StringBuffer();
        List<BaseNode> nodes = new ArrayList<>();
        NodeHelper.getAllParents(nodes, baseNode);
        int i = nodes.size();
        while (i > 0) {
            i--;
            buffer.append(nodes.get(i).getName());
            if (i != 0)
                buffer.append("-");
        }
        mTvSearchContent.setText(buffer.toString());
    }

}
