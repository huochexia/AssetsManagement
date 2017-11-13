package com.example.administrator.assetsmanagement.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.administrator.assetsmanagement.Interface.ToolbarClickListener;
import com.example.administrator.assetsmanagement.Interface.TreeNodeSelected;
import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.base.ParentWithNaviActivity;
import com.example.administrator.assetsmanagement.treeUtil.BaseNode;
import com.example.administrator.assetsmanagement.treeUtil.CheckboxTreeNodeAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobObject;

/**
 * 抽象类：将从服务器中获取的数据转换成节点，以树形结构显示。实现对树形节点的增加，修改和删除功能。
 * 抽象方法是对节点对应数据的处理。
 * Created by Administrator on 2017/11/10.
 */

public class SelectedTreeNodeActivity extends ParentWithNaviActivity {
    public static final int  SEARCH_LOCATION = 1;
    public static final int  SEARCH_DEPARTMENT = 2;
    public static final int  SEARCH_MANAGER= 3;
    public static final int  SEARCH_RESULT_OK =100;
    protected List<BmobObject> treeNodeList = new ArrayList<>();
    @BindView(R.id.lv_tree_structure)
    RecyclerView mLvTreeStructure;
    private BaseNode mBaseNode;
    private int mPosition;
    private CheckboxTreeNodeAdapter adapter;
    private int type;
    private boolean isPerson;

    @Override
    public String title() {
        switch (type) {
            case SEARCH_LOCATION:
                return "位置";
            case SEARCH_DEPARTMENT:
                return "部门";
            case SEARCH_MANAGER:
                return "管理员";
            default:
                break;
        }
        return null;
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
        setContentView(R.layout.tree_structrue_search);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        type = intent.getIntExtra("type", 0);
        isPerson = intent.getBooleanExtra("person", false);
        initNaviView();
        adapter = new CheckboxTreeNodeAdapter(this, treeNodeList,
                0, R.mipmap.expand, R.mipmap.collapse);
        adapter.setCheckBoxSelectedListener(new TreeNodeSelected() {
            @Override
            public void checked(BaseNode node, int postion) {
                 mBaseNode = node;
            }

            @Override
            public void cancelCheck(BaseNode node, int position) {
                 mBaseNode =null;
            }
        });
        LinearLayoutManager ll = new LinearLayoutManager(this);
        mLvTreeStructure.setLayoutManager(ll);
        mLvTreeStructure.setAdapter(adapter);
    }


    @OnClick(R.id.btn_tree_search_node_ok)
    public void onViewClicked() {
        //模拟数据
        mBaseNode = new BaseNode();
        mBaseNode.isLast = true;
        mBaseNode.setName("联系");
        if (mBaseNode != null) {
            if (!isPerson) {
                //如果是查找位置或部门，则直接返回选择的节点

                sendNodeInfo(mBaseNode);
                finish();
            } else {
                //如果是查找人员，则必须选择人员，而不能选择部门
                if (mBaseNode.isLast) {
                    sendNodeInfo(mBaseNode);
                    finish();
                } else {
                    toast("请选择人员！");
                }
            }
        } else {
            toast("请选择要查询的内容！");
        }

    }

    /**
     * 返回选择节点
     * @param node
     */
    private void sendNodeInfo(BaseNode node) {
        Intent intent = new Intent();
        intent.putExtra("node", node);
        setResult(SEARCH_RESULT_OK, intent);
    }

}
