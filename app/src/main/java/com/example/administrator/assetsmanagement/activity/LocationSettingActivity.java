package com.example.administrator.assetsmanagement.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.administrator.assetsmanagement.Interface.ToolbarClickListener;
import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.base.ParentWithNaviActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/11/10.
 */

public class LocationSettingActivity extends ParentWithNaviActivity {
    @BindView(R.id.tv_tree_structure_current_node_title)
    TextView tvTreeStructureCurrentNodeTitle;
    @BindView(R.id.tv_tree_structure_current_node)
    TextView tvTreeStructureCurrentNode;
    @BindView(R.id.ed_tree_structure_new)
    EditText edTreeStructureNew;

    @Override
    public String title() {
        return "位置";
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
        setContentView(R.layout.activity_location_setting);
        ButterKnife.bind(this);
        initNaviView();
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/隶书.ttf");
        tvTreeStructureCurrentNodeTitle.setTypeface(typeface);
        tvTreeStructureCurrentNode.setTypeface(typeface);
        edTreeStructureNew.setTypeface(typeface);

    }

    @OnClick({R.id.btn_tree_add_node, R.id.btn_tree_replace_node, R.id.btn_tree_delete_node})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_tree_add_node:
                break;
            case R.id.btn_tree_replace_node:
                break;
            case R.id.btn_tree_delete_node:
                break;
        }
    }
}
