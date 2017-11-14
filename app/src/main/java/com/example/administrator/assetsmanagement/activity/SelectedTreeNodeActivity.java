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
import com.example.administrator.assetsmanagement.bean.Location;
import com.example.administrator.assetsmanagement.treeUtil.BaseNode;
import com.example.administrator.assetsmanagement.treeUtil.CheckboxTreeNodeAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SQLQueryListener;

/**
 * 根据要求从不同的表中获取数据集
 * Created by Administrator on 2017/11/10.
 */

public class SelectedTreeNodeActivity extends ParentWithNaviActivity {
    public static final int SEARCH_LOCATION = 1;
    public static final int SEARCH_DEPARTMENT = 2;
    public static final int SEARCH_MANAGER = 3;
    public static final int SEARCH_CATEGORY = 4;
    public static final int SEARCH_RESULT_OK = 100;
    protected List<BmobObject> treeNodeList = new ArrayList<>();
    @BindView(R.id.lv_tree_structure)
    RecyclerView mLvTreeStructure;
    private BaseNode mBaseNode;
    private int mPosition;
    private CheckboxTreeNodeAdapter adapter;
    private int type;
    private boolean isPerson;
    private String sql;

    @Override
    public String title() {
        switch (type) {
            case SEARCH_LOCATION:
                return "位置";
            case SEARCH_DEPARTMENT:
                return "部门";
            case SEARCH_MANAGER:
                return "管理员";
            case SEARCH_CATEGORY:
                return "资产类别";
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
        LinearLayoutManager ll = new LinearLayoutManager(this);
        mLvTreeStructure.setLayoutManager(ll);
        switch (type) {
            case SEARCH_LOCATION:
                sql ="select * from Location";
                break;
            case SEARCH_DEPARTMENT:
                sql="select * from Department";
                break;
            case SEARCH_MANAGER:
                break;
            case SEARCH_CATEGORY:
                sql="select * from Category";
                break;
            default:
                break;
        }
        getDataFromBmob(sql);


    }

    /**
     * 从服务器获取数据，并以此设置适配器
     */
    private void getDataFromBmob(String sql) {

        new BmobQuery<Location>().doSQLQuery(this, sql, new SQLQueryListener<Location>() {
            @Override
            public void done(BmobQueryResult<Location> bmobQueryResult, BmobException e) {
                List<Location> locations = bmobQueryResult.getResults();
                if (locations != null && locations.size() > 0) {
                    treeNodeList.clear();
                    treeNodeList.addAll(locations);
                    adapter = new CheckboxTreeNodeAdapter(SelectedTreeNodeActivity.this, treeNodeList,
                            0, R.mipmap.expand, R.mipmap.collapse);
                    mLvTreeStructure.setAdapter(adapter);
                    adapter.setCheckBoxSelectedListener(new TreeNodeSelected() {
                        @Override
                        public void checked(BaseNode node, int postion) {
                            mBaseNode = node;
                            mPosition = postion;
                            String parent = "";
                            if (node.getParent() != null) {
                                parent = node.getParent().getName();
                            }
                                                   }

                        @Override
                        public void cancelCheck(BaseNode node, int position) {
                            mBaseNode = null;
                            mPosition = 0;

                        }
                    });
                }

            }
        });
    }

    @OnClick(R.id.btn_tree_search_node_ok)
    public void onViewClicked() {

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
     *
     * @param node
     */
    private void sendNodeInfo(BaseNode node) {
        Intent intent = new Intent();
        intent.putExtra("node", node);
        setResult(SEARCH_RESULT_OK, intent);
    }

}
