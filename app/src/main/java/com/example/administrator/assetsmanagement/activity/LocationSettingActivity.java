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
import com.example.administrator.assetsmanagement.bean.Location;
import com.example.administrator.assetsmanagement.treeUtil.BaseNode;
import com.example.administrator.assetsmanagement.treeUtil.CheckboxTreeNodeAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 位置管理类：涉及从服务器数据库中获取所有数据，然后增加，修改名称和删除等三个主要功能。
 * 这三个功能都涉及到了同时对节点和数据库的两个操作。其中对数据库的操作使用到了异步操作，
 * 特别是修改和删除功能。因为Bmob数据库操作的局限性（即对BmobObject类的修改和删除只能使用
 * ObjectId属性，所有要通过从节点中获取的对象id，到数据库中查找对象ObjectId，然后再利用这
 * 个ObjectId进行修改和删除。所以它们都使用了异步操作。即从数据库中取得结果后，开启子线程
 * 在子线程中进行对数据库内容的修改和删除。
 * Created by Administrator on 2017/11/10.
 */

public class LocationSettingActivity extends ParentWithNaviActivity {
    @BindView(R.id.tv_tree_structure_current_node_title)
    TextView tvTreeStructureCurrentNodeTitle;
    @BindView(R.id.tv_tree_structure_current_node)
    TextView tvTreeStructureCurrentNode;
    @BindView(R.id.ed_tree_structure_new)
    EditText edTreeStructureNew;
    @BindView(R.id.lv_tree_structure)
    RecyclerView mLvTreeStructure;

    public myHandler handler = new myHandler();
    protected List<Object> treeNodeList = new ArrayList<>();
    private BaseNode mBaseNode;
    private int mPosition;
    protected CheckboxTreeNodeAdapter adapter;

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

        BmobQuery<Location> query = new BmobQuery<>();
        query.setLimit(500);
        query.findObjects(this, new FindListener<Location>() {
            @Override
            public void onSuccess(List<Location> list) {
                Message msg = new Message();
                msg.what = FIND_ALL;
                Bundle bundle = new Bundle();
                bundle.putSerializable("list", (Serializable) list);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }

            @Override
            public void onError(int i, String s) {

            }
        });

    }

    @OnClick({R.id.btn_tree_add_node, R.id.btn_tree_replace_node, R.id.btn_tree_delete_node})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_tree_add_node:
                if (mBaseNode != null) {
                    if (!TextUtils.isEmpty(edTreeStructureNew.getText())) {
                        BaseNode newNode = new BaseNode();
                        newNode.setName(edTreeStructureNew.getText().toString());
                        newNode.setId(System.currentTimeMillis() + "");
                        newNode.setpId(mBaseNode.getId());
                        addToBmob(newNode);
                        addNode(newNode, mBaseNode.getLevel() + 1);
                        mBaseNode = null;
                    } else {
                        toast("请输入新名称！");
                    }
                } else {
                    if (!TextUtils.isEmpty(edTreeStructureNew.getText())) {
                        //如果没有选择节点，则列表最后创建根节点
                        mBaseNode = new BaseNode(System.currentTimeMillis() + "",
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
                    toast("请选择要修改的位置！");
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

    }

    /**
     * 删除节点
     * @param node
     */
    private void deleteNode(BaseNode node) {
        tvTreeStructureCurrentNode.setText("");
        adapter.deleteNode(node);
        adapter.notifyDataSetChanged();

    }

    /**
     * 将新增加数据保存到服务器上
     * @param node
     */
    public void addToBmob(BaseNode node) {
        Location location = new Location();
        location.setId(node.getId());
        location.setParentId(node.getpId());
        location.setLocationName(node.getName());
        location.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                toast("添加成功！");
            }

            @Override
            public void onFailure(int i, String s) {
                toast("添加失败！！");
            }
        });
    }

    /**
     * 将修改服务器上的相应内容
     * @param node
     */
    public void updateToBmob(final BaseNode node) {
        BmobQuery<Location> query = new BmobQuery<>();
        query.addWhereEqualTo("id", node.getId());
        query.findObjects(this, new FindListener<Location>() {
            @Override
            public void onSuccess(final List<Location> list) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message msg = new Message();
                        msg.what = UPDATE_FLAG;
                        Bundle bundle = new Bundle();
                        bundle.putString("objectId", list.get(0).getObjectId());
                        bundle.putString("name", node.getName());
                        msg.setData(bundle);
                        handler.sendMessage(msg);

                    }
                }).start();
            }
            @Override
            public void onError(int i, String s) {

            }
        });
    }

    public void removeFromBmob(BaseNode node) {
        BmobQuery<Location> query = new BmobQuery<>();
        query.addWhereEqualTo("id", node.getId());
        query.findObjects(this, new FindListener<Location>() {
            @Override
            public void onSuccess(final List<Location> list) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message msg = new Message();
                        msg.what = DELETE_FLAG;
                        Bundle bundle = new Bundle();
                        bundle.putString("objectId", list.get(0).getObjectId());
                        msg.setData(bundle);
                        handler.sendMessage(msg);

                    }
                }).start();
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    public static final int FIND_ALL =0;
    public static final int UPDATE_FLAG = 1;
    public static final int DELETE_FLAG = 2;


    /**
     * 异步处理类
     */

    class myHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Location location = new Location();
            switch (msg.what) {
                case FIND_ALL:
                    List<Location> list = (List<Location>) msg.getData().getSerializable("list");
                    initAdapter(list);
                    break;
                case UPDATE_FLAG:
                    String objectId = msg.getData().getString("objectId");
                    String name = msg.getData().getString("name");
                    location.setLocationName(name);
                    location.update(LocationSettingActivity.this,objectId, new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            toast("修改成功！");
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            toast("修改失败！"+s);
                        }
                    });
                    break;
                case DELETE_FLAG:
                    String objectId1 = msg.getData().getString("objectId");
                    location.setObjectId(objectId1);
                    location.delete(LocationSettingActivity.this, new DeleteListener() {
                        @Override
                        public void onSuccess() {
                            toast("删除成功");
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            toast("删除失败"+s);
                        }
                    });
                    break;
            }
        }
    }

    /**
     * 初始化创建适配器
     * @param list
     */
    private void initAdapter(List<Location> list) {
        treeNodeList.clear();
        treeNodeList.addAll(list);
        adapter = new CheckboxTreeNodeAdapter(LocationSettingActivity.this, treeNodeList,
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
                tvTreeStructureCurrentNode.setText(parent + "--" + node.getName());
            }

            @Override
            public void cancelCheck(BaseNode node, int position) {
                mBaseNode = null;
                mPosition = 0;
                tvTreeStructureCurrentNode.setText("");
            }
        });
    }

}
