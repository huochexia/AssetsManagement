package com.example.administrator.assetsmanagement.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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

/**
 * 抽象类：将从服务器中获取的数据转换成节点，以树形结构显示。实现对树形节点的增加，修改和删除功能。
 * 抽象方法是对节点对应数据的处理。
 * Created by Administrator on 2017/11/10.
 */

public abstract class TreeNodeSettingActivity extends ParentWithNaviActivity {
    @BindView(R.id.tv_tree_structure_current_node_title)
    TextView tvTreeStructureCurrentNodeTitle;
    @BindView(R.id.tv_tree_structure_current_node)
    TextView tvTreeStructureCurrentNode;
    @BindView(R.id.ed_tree_structure_new)
    EditText edTreeStructureNew;
    @BindView(R.id.lv_tree_structure)
    RecyclerView mLvTreeStructure;


    protected List<Object> treeNodeList = new ArrayList<>();
    private BaseNode mBaseNode;
    private int mPosition;
    private CheckboxTreeNodeAdapter adapter;


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
        setContentView(R.layout.activity_tree_node_setting);
        ButterKnife.bind(this);
        initNaviView();
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/隶书.ttf");
        tvTreeStructureCurrentNodeTitle.setTypeface(typeface);
        tvTreeStructureCurrentNode.setTypeface(typeface);
        edTreeStructureNew.setTypeface(typeface);

        adapter = new CheckboxTreeNodeAdapter(this, treeNodeList,
                0, R.mipmap.expand, R.mipmap.collapse);
        adapter.setCheckBoxSelectedListener(new TreeNodeSelected() {
            @Override
            public void checked(BaseNode node, int postion) {
                mBaseNode = node;
                mPosition = postion;
                String parent = "";
                if (node.getParent() != null) {
                    parent = node.getParent().getName();
                }
                tvTreeStructureCurrentNode.setText(parent + "--" + node.getName());
            }

            @Override
            public void cancelCheck(BaseNode node, int position) {
                mBaseNode = null;
                mPosition = 0;
                tvTreeStructureCurrentNode.setText("");
            }
        });
        LinearLayoutManager ll = new LinearLayoutManager(this);
        mLvTreeStructure.setLayoutManager(ll);
        mLvTreeStructure.setAdapter(adapter);
    }

    @OnClick({R.id.btn_tree_add_node, R.id.btn_tree_replace_node, R.id.btn_tree_delete_node})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_tree_add_node:
                if (mBaseNode != null ) {
                    if (!TextUtils.isEmpty(edTreeStructureNew.getText())) {
                        BaseNode newNode = new BaseNode();
                        newNode.setName(edTreeStructureNew.getText().toString());
                        newNode.setId(System.currentTimeMillis() + "");
                        newNode.setpId(mBaseNode.getId());
                        if (addToBmob(newNode)) {
                            addNode(newNode, mBaseNode.getLevel() + 1);
                        }

                    } else {
                        toast("请输入新名称！");
                    }
                } else {
                       if (!TextUtils.isEmpty(edTreeStructureNew.getText())) {
                            //如果没有选择节点，则列表最后创建根节点
                            mBaseNode = new BaseNode(System.currentTimeMillis() + "",
                                    "0", edTreeStructureNew.getText().toString());
                            if (addToBmob(mBaseNode)) {
                                addNode(mBaseNode, 0);
                            }
                        } else {
                            toast("请输入新名称！");
                        }
                }
                break;
            case R.id.btn_tree_replace_node:
                if (mBaseNode != null) {
                    if (!TextUtils.isEmpty(edTreeStructureNew.getText())) {
                        if (updateToBmob(mBaseNode)) {
                            updateNode(mBaseNode);
                        }
                    } else {
                        toast("请输入新名称！");
                    }
                } else {
                    toast("请选择要修改的位置！");
                }
                break;
            case R.id.btn_tree_delete_node:
                if (mBaseNode != null) {
                    if (removeFromBmob(mBaseNode)) {
                        tvTreeStructureCurrentNode.setText("");
                        adapter.deleteNode(mBaseNode);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    toast("请选择要删除的内容！");
                }
                break;
        }
    }

    /**
     * 修改节点
     *
     * @param node
     */
    private void updateNode(BaseNode node) {
        node.setName(edTreeStructureNew.getText().toString());
        if (node.getParent() != null) {
            tvTreeStructureCurrentNode.setText(node.getParent().getName() + "--" + node.getName());
        } else {
            tvTreeStructureCurrentNode.setText("--"+node.getName());
        }
        adapter.notifyDataSetChanged();
        edTreeStructureNew.setText("");
    }

    /**
     * 增加节点
     *
     * @param newNode
     */
    private void addNode(BaseNode newNode, int level) {
        edTreeStructureNew.setText("");
        tvTreeStructureCurrentNode.setText("");
        adapter.addData(mPosition, newNode, level);
        adapter.notifyDataSetChanged();
        mBaseNode = null;
    }

    public abstract boolean addToBmob(BaseNode node);

    public abstract boolean updateToBmob(BaseNode node);

    public abstract boolean removeFromBmob(BaseNode node);


}
