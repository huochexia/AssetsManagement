package com.example.administrator.assetsmanagement.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.example.administrator.assetsmanagement.bean.DepartmentTree.Department;
import com.example.administrator.assetsmanagement.bean.DepartmentTree.DepartmentCheckboxNodeAdapter;
import com.example.administrator.assetsmanagement.bean.DepartmentTree.DepartmentNodeSelected;
import com.example.administrator.assetsmanagement.treeUtil.BaseNode;
import com.example.administrator.assetsmanagement.treeUtil.CheckboxTreeNodeAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 部门管理类
 * Created by Administrator on 2017/11/10.
 */

public class DepartmentSettingActivity extends ParentWithNaviActivity {
    @BindView(R.id.tv_tree_structure_current_node_title)
    TextView tvTreeStructureCurrentNodeTitle;
    @BindView(R.id.tv_tree_structure_current_node)
    TextView tvTreeStructureCurrentNode;
    @BindView(R.id.ed_tree_structure_new)
    EditText edTreeStructureNew;
    @BindView(R.id.lv_tree_structure)
    RecyclerView mLvTreeStructure;

    public myHandler handler = new myHandler();
    protected List<Department> treeNodeList = new ArrayList<>();
    private Department mBaseNode;
    private int mPosition;
    protected DepartmentCheckboxNodeAdapter adapter;

    @Override
    public String title() {
        return "部门";
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
        setContentView(R.layout.activity_tree_node_setting);
        ButterKnife.bind(this);
        initNaviView();
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/隶书.ttf");
        tvTreeStructureCurrentNodeTitle.setTypeface(typeface);
        tvTreeStructureCurrentNode.setTypeface(typeface);
        edTreeStructureNew.setTypeface(typeface);

        getDataFromBmob();

        LinearLayoutManager ll = new LinearLayoutManager(this);
        mLvTreeStructure.setLayoutManager(ll);
        mLvTreeStructure.setAdapter(adapter);
    }

    /**
     * 从服务器获取数据，并以此设置适配器
     */
    private void getDataFromBmob() {
        String sql = "select * from Department";
        new BmobQuery<Department>().doSQLQuery(sql, new SQLQueryListener<Department>() {
            @Override
            public void done(BmobQueryResult<Department> bmobQueryResult, BmobException e) {
                List<Department> departments = bmobQueryResult.getResults();
                if (departments != null && departments.size() > 0) {
                    treeNodeList.clear();
                    treeNodeList.addAll(departments);
                    adapter = new DepartmentCheckboxNodeAdapter(DepartmentSettingActivity.this, treeNodeList,
                            0, R.mipmap.expand, R.mipmap.collapse);
                    mLvTreeStructure.setAdapter(adapter);
                    adapter.setCheckBoxSelectedListener(new DepartmentNodeSelected() {
                        @Override
                        public void checked(Department node, int postion) {
                            mBaseNode = node;
                            mPosition = postion;
                            String parent = "";
                            if (node.getParent() != null) {
                                parent = node.getParent().getDepartmentName();
                            }
                            tvTreeStructureCurrentNode.setText(parent + "--" + node.getDepartmentName());
                        }

                        @Override
                        public void cancelCheck(Department node, int position) {
                            mBaseNode = null;
                            mPosition = 0;
                            tvTreeStructureCurrentNode.setText("");
                        }
                    });
                }

            }
        });
    }

    @OnClick({R.id.btn_tree_add_node, R.id.btn_tree_replace_node, R.id.btn_tree_delete_node})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_tree_add_node:
                if (mBaseNode != null) {
                    if (!TextUtils.isEmpty(edTreeStructureNew.getText())) {
                        Department newNode = new Department();
                        newNode.setDepartmentName(edTreeStructureNew.getText().toString());
                        newNode.setId(System.currentTimeMillis() + "");
                        newNode.setParentId(mBaseNode.getId());
                        addToBmob(newNode);
                        addNode(newNode, mBaseNode.getLevel() + 1);
                        mBaseNode = null;
                    } else {
                        toast("请输入新名称！");
                    }
                } else {
                    if (!TextUtils.isEmpty(edTreeStructureNew.getText())) {
                        //如果没有选择节点，则列表最后创建根节点
                        mBaseNode = new Department(System.currentTimeMillis() + "",
                                "0", edTreeStructureNew.getText().toString());
                        addToBmob(mBaseNode);
                        addNode(mBaseNode, 0);
                        mBaseNode = null;
                    } else {
                        toast("请输入新名称！");
                    }
                }
                break;
            case R.id.btn_tree_replace_node:
                if (mBaseNode != null) {
                    if (!TextUtils.isEmpty(edTreeStructureNew.getText())) {
                        updateToBmob(mBaseNode);
                        updateNode(mBaseNode);

                    } else {
                        toast("请输入新名称！");
                    }
                } else {
                    toast("请选择要修改的部门！");
                }
                break;
            case R.id.btn_tree_delete_node:
                if (mBaseNode != null && mBaseNode.getChildren().size()<=0) {
                    removeFromBmob(mBaseNode);
                    deleteNode(mBaseNode);
                    mBaseNode = null;
                } else {
                    if (mBaseNode == null) {

                        toast("请选择要删除的内容！");
                    } else {
                        toast("不能删除父节点！");
                    }
                }
                break;
        }
    }

    /**
     * 删除节点
     * @param node
     */
    private void deleteNode(Department node) {
        tvTreeStructureCurrentNode.setText("");
        adapter.deleteNode(node);
        adapter.notifyDataSetChanged();

    }

    /**
     * 修改节点
     *
     * @param node
     */
    private void updateNode(Department node) {
        node.setDepartmentName(edTreeStructureNew.getText().toString());
        if (node.getParent() != null) {
            tvTreeStructureCurrentNode.setText(node.getParent().getDepartmentName() + "--" + node.getDepartmentName());
        } else {
            tvTreeStructureCurrentNode.setText("--" + node.getDepartmentName());
        }
        adapter.notifyDataSetChanged();
        edTreeStructureNew.setText("");
    }

    /**
     * 增加节点
     *
     * @param newNode
     */
    private void addNode(Department newNode, int level) {
        edTreeStructureNew.setText("");
        tvTreeStructureCurrentNode.setText("");
        adapter.addData(mPosition, newNode, level);
        adapter.notifyDataSetChanged();

    }

    /**
     * 将新增加数据保存到服务器上
     * @param node
     */
    public void addToBmob(Department node) {
        Department depa = new Department();
        depa.setId(node.getId());
        depa.setParentId(node.getParentId());
        depa.setDepartmentName(node.getDepartmentName());
        depa.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    toast("添加成功！");
                } else {
                    toast("添加失败！！");
                }
            }

        });
    }

    /**
     * 将修改服务器上的相应内容
     * @param node
     */
    public void updateToBmob(final Department node) {
        BmobQuery<Department> query = new BmobQuery<>();
        query.addWhereEqualTo("id", node.getId());
        query.findObjects(new FindListener<Department>() {
            @Override
            public void done(final List<Department> list, BmobException e) {
                if (e == null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message msg = new Message();
                            msg.what = UPDATE_FLAG;
                            Bundle bundle = new Bundle();
                            bundle.putString("objectId", list.get(0).getObjectId());
                            bundle.putString("name", node.getDepartmentName());
                            msg.setData(bundle);
                            handler.sendMessage(msg);

                        }
                    }).start();
                }
            }
        });
    }

    public void removeFromBmob(Department node) {
        node.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                toast("删除成功！");
            }
        });
//        BmobQuery<Department> query = new BmobQuery<>();
//        query.addWhereEqualTo("id", node.getId());
//        query.findObjects(new FindListener<Department>() {
//            @Override
//            public void done(final List<Department> list, BmobException e) {
//                if (e == null) {
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Message msg = new Message();
//                            msg.what = DELETE_FLAG;
//                            Bundle bundle = new Bundle();
//                            bundle.putString("objectId", list.get(0).getObjectId());
//                            msg.setData(bundle);
//                            handler.sendMessage(msg);
//
//                        }
//                    }).start();
//                }
//            }
//
//        });
    }

    public static final int UPDATE_FLAG = 0;
    public static final int DELETE_FLAG = 1;

    /**
     * 异步处理类
     */
    
    class myHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Department depa = new Department();
            switch (msg.what) {
                case UPDATE_FLAG:
                    String objectId = msg.getData().getString("objectId");
                    String name = msg.getData().getString("name");
                    depa.setDepartmentName(name);
                    depa.update(objectId, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                toast("修改成功！");
                            } else {
                                toast("修改失败！"+e.toString());
                            }
                        }

                    });
                    break;
//                case DELETE_FLAG:
//                    String objectId1 = msg.getData().getString("objectId");
//                    depa.setObjectId(objectId1);
//                    depa.delete( new UpdateListener() {
//                        @Override
//                        public void done(BmobException e) {
//                            if (e == null) {
//                                toast("删除成功");
//                            } else {
//                                toast("删除失败"+e.toString());
//                            }
//                        }
//                    });
//                    break;
            }
        }
    }

}
