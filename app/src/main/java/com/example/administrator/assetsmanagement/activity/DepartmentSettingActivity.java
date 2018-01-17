package com.example.administrator.assetsmanagement.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.administrator.assetsmanagement.AssetManagerApplication;
import com.example.administrator.assetsmanagement.Interface.ToolbarClickListener;
import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.base.ParentWithNaviActivity;
import com.example.administrator.assetsmanagement.bean.AssetInfo;
import com.example.administrator.assetsmanagement.bean.DepartmentTree.Department;
import com.example.administrator.assetsmanagement.bean.DepartmentTree.DepartmentCheckboxNodeAdapter;
import com.example.administrator.assetsmanagement.bean.DepartmentTree.DepartmentNodeSelected;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 部门管理类
 * Created by Administrator on 2017/11/10.
 */

public class DepartmentSettingActivity extends ParentWithNaviActivity {

    @BindView(R.id.lv_tree_structure)
    RecyclerView mLvTreeStructure;

    public myHandler handler = new myHandler();
    protected List<Department> treeNodeList = new ArrayList<>();
    @BindView(R.id.download_progress_location)
    ProgressBar downloadProgressLocation;
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

        getDataFromBmob();

        LinearLayoutManager ll = new LinearLayoutManager(this);
        mLvTreeStructure.setLayoutManager(ll);
        mLvTreeStructure.setAdapter(adapter);
    }

    /**
     * 删除节点对话框
     */
    private void deleteNodeDialog() {
        AlertDialog.Builder builder3 = new AlertDialog.Builder(this);
        String text = mBaseNode.getDepartmentName();
        String message = "确定要删除" + "\"" + text + "\"" + "吗？";
        builder3.setMessage(message);
        builder3.setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeFromBmob(mBaseNode);
                deleteNode(mBaseNode);
                mBaseNode = null;
                dialog.dismiss();
            }
        });
        builder3.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder3.show();
    }

    /**
     * 修改节点对话框
     */
    private void editNodeDialog() {
        RelativeLayout mDialogView = (RelativeLayout) getLayoutInflater()
                .inflate(R.layout.dialog_add_commodity, null);
        final EditText editText = (EditText) mDialogView.findViewById(R.id.et_add_or_edit_content);
        editText.setText(mBaseNode.getDepartmentName());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_title_edit).setView(mDialogView);
        builder.setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!TextUtils.isEmpty(editText.getText())) {
                    mBaseNode.setDepartmentName(editText.getText() + "");
                    updateToBmob(mBaseNode);
                    updateNode();
                    dialog.dismiss();
                } else {
                    toast("请输入新位置！");
                }
            }
        });
        builder.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    /**
     * 生成增加节点对话框
     */
    private void addNodeDialog() {
        //获得对话框自定义视图对象
        RelativeLayout mDialogView = (RelativeLayout) getLayoutInflater()
                .inflate(R.layout.dialog_add_commodity, null);
        final EditText editText = (EditText) mDialogView.findViewById(R.id.et_add_or_edit_content);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_title_add).setView(mDialogView);
        builder.setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!TextUtils.isEmpty(editText.getText())) {
                    Department newNode = new Department();
                    newNode.setDepartmentName(editText.getText().toString());
                    newNode.setId(System.currentTimeMillis() + "");
                    newNode.setParentId(mBaseNode.getId());
                    addToBmob(newNode);
                    addNode(newNode, mBaseNode.getLevel() + 1);
//                    mBaseNode = null;
                    dialog.dismiss();
                } else {
                    toast("请输入新位置！");
                }
            }
        });
        builder.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * 从服务器获取数据，并以此设置适配器。这里用的是基本查询语句，所以没有使用异步处理。与LocationSettingActivity
     * 使用的查询方法不一样。
     */
    private void getDataFromBmob() {
        String sql = "select * from Department";
        new BmobQuery<Department>().doSQLQuery(sql, new SQLQueryListener<Department>() {
            @Override
            public void done(BmobQueryResult<Department> bmobQueryResult, BmobException e) {
                List<Department> departments = bmobQueryResult.getResults();
                if (e == null) {

                    setListAdapter(departments);
                }

            }
        });
    }

    /**
     * 设置列表适配器
     *
     * @param departments 列表内容
     */
    private void setListAdapter(List<Department> departments) {
        Department department = new Department("0", "-1", AssetManagerApplication.COMPANY);
        List<Department> departmentList = new ArrayList<>();
        departmentList.add(department);
        treeNodeList.clear();
        treeNodeList.addAll(departments);
        departmentList.addAll(treeNodeList);
        downloadProgressLocation.setVisibility(View.GONE);
        adapter = new DepartmentCheckboxNodeAdapter(DepartmentSettingActivity.this, departmentList,
                1, R.mipmap.expand, R.mipmap.collapse);
        mLvTreeStructure.setAdapter(adapter);
        adapter.setCheckBoxSelectedListener(new DepartmentNodeSelected() {
            @Override
            public void checked(Department node, int postion) {
                mBaseNode = node;
                mPosition = postion;
            }

            @Override
            public void cancelCheck(Department node, int position) {
                mBaseNode = null;
                mPosition = 0;
            }
        });
    }

    @OnClick({R.id.btn_tree_add_node, R.id.btn_tree_replace_node, R.id.btn_tree_delete_node})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_tree_add_node:
                if (mBaseNode == null) {
                    toast("请选择要增加的部门！");
                    return;
                }
                addNodeDialog();
                break;
            case R.id.btn_tree_replace_node:
                if (mBaseNode == null) {
                    toast("请选择要修改的部门！");
                    return;
                }
                if (mBaseNode.getParentId().equals("-1")) {
                    toast("不能修改单位名称");
                    return;
                }
                editNodeDialog();
                break;
            case R.id.btn_tree_delete_node:
                if (mBaseNode == null) {
                    toast("请选择要删除的部门！");
                    return;
                }
                if (mBaseNode.getChildren().size() > 0) {
                    toast("它有子部门，不能删除！");
                    return;
                }
                BmobQuery<AssetInfo> query = new BmobQuery<>();
                query.addWhereEqualTo("mDepartment", mBaseNode);
                query.count(AssetInfo.class, new CountListener() {
                    @Override
                    public void done(final Integer integer, BmobException e) {
                        runOnMain(new Runnable() {
                            @Override
                            public void run() {
                                if (integer > 0) {
                                    toast("该部门拥有资产，不能删除！");
                                } else {
                                    deleteNodeDialog();
                                }
                            }
                        });
                    }
                });
                break;
        }
    }

    /**
     * 删除节点
     *
     * @param node
     */
    private void deleteNode(Department node) {
        adapter.deleteNode(node);
        adapter.notifyDataSetChanged();

    }

    /**
     * 修改节点
     *
     * @param
     */
    private void updateNode() {
        adapter.notifyDataSetChanged();
    }

    /**
     * 增加节点
     *
     * @param newNode
     */
    private void addNode(Department newNode, int level) {

        adapter.addData(mPosition, newNode, level);
        adapter.notifyDataSetChanged();

    }

    /**
     * 将新增加数据保存到服务器上
     *
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
     *
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
                                toast("修改失败！" + e.toString());
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
