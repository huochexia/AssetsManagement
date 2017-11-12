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
import com.example.administrator.assetsmanagement.bean.Department;
import com.example.administrator.assetsmanagement.bean.Person;
import com.example.administrator.assetsmanagement.treeUtil.BaseNode;
import com.example.administrator.assetsmanagement.treeUtil.CheckboxTreeNodeAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 人员管理：增加，修改信息，删除等操作。删除时要判断是否有资产，无则可以删除。人员调动操作过程 是
 * 将其名下所有资产先进行移交，然后在旧部门中删除，在新部门中增加后，在接受资产。
 * Created by Administrator on 2017/11/10.
 */

public class PersonSettingActivity extends ParentWithNaviActivity {
    @BindView(R.id.tv_tree_structure_current_node_title)
    TextView tvTreeStructureCurrentNodeTitle;
    @BindView(R.id.tv_tree_structure_current_node)
    TextView tvTreeStructureCurrentNode;
    @BindView(R.id.ed_tree_structure_new)
    EditText edTreeStructureNew;
    @BindView(R.id.lv_tree_structure)
    RecyclerView mLvTreeStructure;

    protected List<Object> personNodeList = new ArrayList<>();
    private BaseNode mBaseNode;
    private int mPosition;
    private CheckboxTreeNodeAdapter adapter;

    @Override
    public String title() {
        return "人员";
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

    Department l1 = new Department("1", "0", "信息技术部");
    Department l2 = new Department("A", "0", "后勤部");
    Department l3 = new Department("01", "A", "餐厅");
    Department l4 = new Department("02", "A", "公寓");
    Person l5 = new Person("101", "01", "王卫客");
    Person l6 = new Person("201", "02", "徐国华");
    Department l7 = new Department("B", "0", "教务部");
    Department l8 = new Department("C", "0", "教学部");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree_node_setting);
        ButterKnife.bind(this);
        initNaviView();
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/隶书.ttf");
        tvTreeStructureCurrentNodeTitle.setTypeface(typeface);
        tvTreeStructureCurrentNodeTitle.setText("选择人员");
        tvTreeStructureCurrentNode.setTypeface(typeface);
        edTreeStructureNew.setTypeface(typeface);
        edTreeStructureNew.setHint("请输入人员姓名");
        //模拟数据
        personNodeList.add(l1);
        personNodeList.add(l2);
        personNodeList.add(l3);
        personNodeList.add(l4);
        personNodeList.add(l5);
        personNodeList.add(l6);
        personNodeList.add(l7);
        personNodeList.add(l8);

        adapter = new CheckboxTreeNodeAdapter(this, personNodeList,
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
                if (mBaseNode != null && !mBaseNode.isLast) {
                    if (!TextUtils.isEmpty(edTreeStructureNew.getText())) {
                        BaseNode newNode = new BaseNode();
                        newNode.setName(edTreeStructureNew.getText().toString());
                        newNode.setId(System.currentTimeMillis() + "");
                        newNode.setpId(mBaseNode.getId());
                        newNode.isLast = true;
                        if (addToBmob(newNode)) {
                            addNode(newNode, mBaseNode.getLevel() + 1);
                        }
                    } else {
                        toast("请输入姓名！");
                    }
                } else {
                    if (mBaseNode == null) {
                        toast("请选择部门");
                    } else {
                        toast("人员没有子类");
                    }

                }
                break;
            case R.id.btn_tree_replace_node:
                //只能修改人员信息
                if (mBaseNode != null && mBaseNode.isLast) {
                    if (!TextUtils.isEmpty(edTreeStructureNew.getText())) {
                        if (updateToBmob(mBaseNode)) {
                            updateNode(mBaseNode);
                        }
                    } else {
                        toast("请输入姓名！");
                    }
                } else {
                    if (mBaseNode == null) {
                        toast("请选择要修改人员!");
                    } else {
                        toast("不能修改部门信息！");
                    }
                }
                break;
            case R.id.btn_tree_delete_node:
                if (mBaseNode != null && mBaseNode.isLast) {
                    if (removeToBmob(mBaseNode)) {
                        tvTreeStructureCurrentNode.setText("");
                        adapter.deleteNode(mBaseNode);
                        adapter.notifyDataSetChanged();
                    }

                } else {
                    if (mBaseNode == null) {
                        toast("请选择要删除人员！");
                    } else {
                        toast("不能删除部门信息！");
                    }
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
            tvTreeStructureCurrentNode.setText("--" + node.getName());
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

    public boolean addToBmob(BaseNode node) {
        return true;
    }

    public boolean updateToBmob(BaseNode node) {
        return true;
    }

    public boolean removeToBmob(BaseNode node) {
        return true;
    }


}
