package com.example.administrator.assetsmanagement.activity;

import android.content.DialogInterface;
import android.graphics.Typeface;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.assetsmanagement.Interface.ToolbarClickListener;
import com.example.administrator.assetsmanagement.Interface.TreeNodeSelected;
import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.base.ParentWithNaviActivity;
import com.example.administrator.assetsmanagement.bean.AssetInfo;
import com.example.administrator.assetsmanagement.bean.CategoryTree.AssetCategory;
import com.example.administrator.assetsmanagement.bean.CategoryTree.CategoryCheckboxNodeAdapter;
import com.example.administrator.assetsmanagement.bean.CategoryTree.CategoryNodeSelected;
import com.example.administrator.assetsmanagement.bean.LocationTree.Location;
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
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 资产类别管理类
 * Created by Administrator on 2017/11/10.
 */

public class CategorySettingActivity extends ParentWithNaviActivity {

    @BindView(R.id.lv_tree_structure)
    RecyclerView mLvTreeStructure;

    public myHandler handler = new myHandler();
    protected List<AssetCategory> treeNodeList = new ArrayList<>();
    private AssetCategory mBaseNode;
    private int mPosition;
    protected CategoryCheckboxNodeAdapter adapter;

    @Override
    public String title() {
        return "资产类别";
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



    @OnClick({R.id.btn_tree_add_node, R.id.btn_tree_replace_node, R.id.btn_tree_delete_node})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_tree_add_node:
                if (mBaseNode == null) {
                    toast("请选择要增加的大类别！");
                    return;
                }
                addNodeDialog();

                break;
            case R.id.btn_tree_replace_node:
                if (mBaseNode == null) {
                    toast("请选择要修改的类别！");
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
                    toast("请选择要删除的类别！");
                    return;
                }
                if (mBaseNode.getChildren().size()>0) {
                    toast("它有子类别，不能删除！");
                    return;
                }
                //判断类别下是否有资产，如果有则不能删除
                BmobQuery<AssetInfo> query = new BmobQuery<>();
                query.addWhereEqualTo("mCategory", mBaseNode);
                query.count(AssetInfo.class, new CountListener() {
                    @Override
                    public void done(final Integer integer, BmobException e) {
                        runOnMain(new Runnable() {
                            @Override
                            public void run() {
                                if (integer > 0) {
                                    toast("该类别下有资产，不能删除！");
                                }else {
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
     * 删除节点对话框
     */
    private void deleteNodeDialog() {
        AlertDialog.Builder builder3 = new AlertDialog.Builder(this);
        String text  = mBaseNode.getCategoryName();
        String message = "确定要删除" + "\"" + text + "\""+"吗？";
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
        editText.setText(mBaseNode.getCategoryName());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_title_edit).setView(mDialogView);
        builder.setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!TextUtils.isEmpty(editText.getText())) {
                    mBaseNode.setCategoryName(editText.getText() + "");
                    updateToBmob(mBaseNode);
                    updateNode();
                    dialog.dismiss();
                } else {
                    toast("请输入新类别！");
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
                    AssetCategory newNode = new AssetCategory();
                    newNode.setCategoryName(editText.getText().toString());
                    newNode.setId(System.currentTimeMillis() + "");
                    newNode.setParentId(mBaseNode.getId());
                    addToBmob(newNode);
                    addNode(newNode, mBaseNode.getLevel() + 1);
                    dialog.dismiss();
                } else {
                    toast("请输入新类别！");
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
     * 从服务器获取数据，并以此设置适配器
     */
    private void getDataFromBmob() {
        String sql = "select * from AssetCategory";
        new BmobQuery<AssetCategory>().doSQLQuery(sql, new SQLQueryListener<AssetCategory>() {
            @Override
            public void done(BmobQueryResult<AssetCategory> bmobQueryResult, BmobException e) {
                List<AssetCategory> assetCategories = bmobQueryResult.getResults();
                if (e==null) {
                    setListAdapter(assetCategories);
                }

            }
        });
    }

    /**
     * 设置列表适配器
     * @param assetCategories
     */
    private void setListAdapter(List<AssetCategory> assetCategories) {
        AssetCategory root_category = new AssetCategory("0","-1","固定资产");
        List<AssetCategory> categoryArrayList = new ArrayList<>();
        categoryArrayList.add(root_category);
        treeNodeList.clear();
        treeNodeList.addAll(assetCategories);
        categoryArrayList.addAll(treeNodeList);
        adapter = new CategoryCheckboxNodeAdapter(CategorySettingActivity.this, categoryArrayList,
                1, R.mipmap.expand, R.mipmap.collapse);
        mLvTreeStructure.setAdapter(adapter);
        adapter.setCheckBoxSelectedListener(new CategoryNodeSelected() {
            @Override
            public void checked(AssetCategory node, int postion) {
                mBaseNode = node;
                mPosition = postion;
                String parent = "";
                if (node.getParent() != null) {
                    parent = node.getParent().getCategoryName();
                }
            }

            @Override
            public void cancelCheck(AssetCategory node, int position) {
                mBaseNode = null;
                mPosition = 0;
            }
        });
    }

    /**
     * 删除节点
     * @param node
     */
    private void deleteNode(AssetCategory node) {
        adapter.deleteNode(node);
        adapter.notifyDataSetChanged();

    }

    /**
     * 修改节点
     *
     *
     */
    private void updateNode() {
        adapter.notifyDataSetChanged();
    }

    /**
     * 增加节点
     *
     * @param newNode
     */
    private void addNode(AssetCategory newNode, int level) {
        adapter.addData(mPosition, newNode, level);
        adapter.notifyDataSetChanged();

    }

    /**
     * 将新增加数据保存到服务器上
     * @param node
     */
    public void addToBmob(AssetCategory node) {
        AssetCategory category = new AssetCategory();
        category.setId(node.getId());
        category.setParentId(node.getParentId());
        category.setCategoryName(node.getCategoryName());
        category.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    toast("添加成功！");
                } else {
                    toast("添加失败！");
                }
            }
        });
    }

    /**
     * 将修改服务器上的相应内容
     * @param node
     */
    public void updateToBmob(final AssetCategory node) {

        BmobQuery<AssetCategory> query = new BmobQuery<>();
        query.addWhereEqualTo("id", node.getId());
        query.findObjects(new FindListener<AssetCategory>() {
            @Override
            public void done(final List<AssetCategory> list, BmobException e) {
                if (e == null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message msg = new Message();
                            msg.what = UPDATE_FLAG;
                            Bundle bundle = new Bundle();
                            bundle.putString("objectId", list.get(0).getObjectId());
                            bundle.putString("name", node.getCategoryName());
                            msg.setData(bundle);
                            handler.sendMessage(msg);

                        }
                    }).start();
                }
            }

        });
    }

    public void removeFromBmob(AssetCategory node) {
        node.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                toast("删除成功！");
            }
        });
    }

    public static final int UPDATE_FLAG = 0;
    public static final int DELETE_FLAG = 1;

    /**
     * 异步处理类
     */

    class myHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            AssetCategory category = new AssetCategory();
            switch (msg.what) {
                case UPDATE_FLAG:
                    String objectId = msg.getData().getString("objectId");
                    String name = msg.getData().getString("name");
                    category.setCategoryName(name);
                    category.update(objectId, new UpdateListener() {
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
                case DELETE_FLAG:
                    String objectId1 = msg.getData().getString("objectId");
                    category.setObjectId(objectId1);
                    category.delete(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                    toast("删除成功");
                            }else{
                                toast("删除失败"+e.toString());
                            }

                        }
                    });
                    break;
            }
        }
    }

}
