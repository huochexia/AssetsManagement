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
import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.base.ParentWithNaviActivity;
import com.example.administrator.assetsmanagement.bean.LocationTree.LocationCheckboxNodeAdapter;
import com.example.administrator.assetsmanagement.bean.LocationTree.Location;
import com.example.administrator.assetsmanagement.bean.LocationTree.LocationNodeSelected;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
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
    protected List<Location> treeNodeList = new ArrayList<>();
    private Location mBaseNode;
    private int mPosition;
    protected LocationCheckboxNodeAdapter adapter;

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
        query.findObjects(new FindListener<Location>() {
            @Override
            public void done(final List<Location> list, BmobException e) {
                if (e == null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message msg = new Message();
                            msg.what = FIND_ALL;
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("list", (Serializable) list);
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }
                    }).start();

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
                        Location newNode = new Location();
                        newNode.setLocationName(edTreeStructureNew.getText().toString());
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
                        mBaseNode = new Location(System.currentTimeMillis() + "",
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
                    String objectId = mBaseNode.getObjectId();
                    if (!TextUtils.isEmpty(edTreeStructureNew.getText())) {
                        mBaseNode.setLocationName(edTreeStructureNew.getText()+"");
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
    private void updateNode(Location node) {
        node.setLocationName(edTreeStructureNew.getText().toString());
        if (node.getParent() != null) {
            tvTreeStructureCurrentNode.setText(node.getParent().getLocationName() + "--" + node.getLocationName());
        } else {
            tvTreeStructureCurrentNode.setText("--" + node.getLocationName());
        }
        adapter.notifyDataSetChanged();
        edTreeStructureNew.setText("");
    }

    /**
     * 增加节点
     *
     * @param newNode
     */
    private void addNode(Location newNode, int level) {
        edTreeStructureNew.setText("");
        tvTreeStructureCurrentNode.setText("");
        adapter.addData(mPosition, newNode, level);
        adapter.notifyDataSetChanged();

    }

    /**
     * 删除节点
     * @param node
     */
    private void deleteNode(Location node) {
        tvTreeStructureCurrentNode.setText("");
        adapter.deleteNode(node);
        adapter.notifyDataSetChanged();

    }

    /**
     * 将新增加数据保存到服务器上
     * @param node
     */
    public void addToBmob(Location node) {
        Location location = new Location();
        location.setId(node.getId());
        location.setParentId(node.getParentId());
        location.setLocationName(node.getLocationName());
        location.save(new SaveListener<String>() {
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
    public void updateToBmob(final Location node) {
        BmobQuery<Location> query = new BmobQuery<>();
        query.addWhereEqualTo("id", node.getId());
        query.findObjects(new FindListener<Location>() {
            @Override
            public void done(final List<Location> list, BmobException e) {
                if (e == null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message msg = new Message();
                            msg.what = UPDATE_FLAG;
                            Bundle bundle = new Bundle();
                            bundle.putString("objectId", list.get(0).getObjectId());
                            bundle.putString("name", node.getLocationName());
                            msg.setData(bundle);
                            handler.sendMessage(msg);

                        }
                    }).start();
                }
            }

        });
    }

    public void removeFromBmob(Location node) {
        node.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                toast("删除成功！");
            }
        });

    }

    public static final int FIND_ALL =0;
    public static final int UPDATE_FLAG = 1;


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
                    location.update(objectId, new UpdateListener() {
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
        adapter = new LocationCheckboxNodeAdapter(LocationSettingActivity.this, treeNodeList,
                0, R.mipmap.expand, R.mipmap.collapse);
        mLvTreeStructure.setAdapter(adapter);
        adapter.setCheckBoxSelectedListener(new LocationNodeSelected() {
            @Override
            public void checked(Location node, int postion) {
                mBaseNode = node;
                mPosition = postion;
                String parent = "";
                if (node.getParent() != null) {
                    parent = node.getParent().getLocationName();
                }
                tvTreeStructureCurrentNode.setText(parent + "--" + node.getLocationName());
            }

            @Override
            public void cancelCheck(Location node, int position) {
                mBaseNode = null;
                mPosition = 0;
                tvTreeStructureCurrentNode.setText("");
            }
        });
    }

}
