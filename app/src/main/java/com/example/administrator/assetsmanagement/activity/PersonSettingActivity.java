package com.example.administrator.assetsmanagement.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.administrator.assetsmanagement.Interface.ToolbarClickListener;
import com.example.administrator.assetsmanagement.Interface.TreeNodeSelected;
import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.base.ParentWithNaviActivity;
import com.example.administrator.assetsmanagement.bean.Department;
import com.example.administrator.assetsmanagement.bean.Location;
import com.example.administrator.assetsmanagement.bean.Person;
import com.example.administrator.assetsmanagement.treeUtil.BaseNode;
import com.example.administrator.assetsmanagement.treeUtil.CheckboxTreeNodeAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * 人员管理：增加，修改信息，删除等操作。删除时要判断是否有资产，无则可以删除。人员调动操作过程 是
 * 将其名下所有资产先进行移交，然后在旧部门中删除，在新部门中增加后，在接受资产。
 * Created by Administrator on 2017/11/10.
 */

public class PersonSettingActivity extends ParentWithNaviActivity {

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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_setting);
        ButterKnife.bind(this);
        initNaviView();

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

            }

            @Override
            public void cancelCheck(BaseNode node, int position) {
                mBaseNode = null;
                mPosition = 0;

            }
        });
        LinearLayoutManager ll = new LinearLayoutManager(this);
        mLvTreeStructure.setLayoutManager(ll);
        mLvTreeStructure.setAdapter(adapter);

        //
        BmobQuery<Department> query = new BmobQuery<>();
        query.setLimit(500);
        //执行查询方法
        query.findObjects(this, new FindListener<Department>() {
            @Override
            public void onSuccess(final List<Department> object) {
                // TODO Auto-generated method stub

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message msg = new Message();
                        msg.what = USER_REQUEST;
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("department", (Serializable) object);
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }
                }).start();

            }

            @Override
            public void onError(int code, String msg) {
                // TODO Auto-generated method stub
                toast("查询失败：" + msg);
            }
        });
    }

    public static final int USER_REQUEST = 10;
    public static final int ADD_SUCCESS = 11;

    MyHandler handler = new MyHandler();

    @OnClick({R.id.btn_person_setting_add, R.id.btn_person_setting_repair, R.id.btn_person_setting_delete, R.id.btn_person_setting_role})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_person_setting_add:
                break;
            case R.id.btn_person_setting_repair:
                break;
            case R.id.btn_person_setting_delete:
                break;
            case R.id.btn_person_setting_role:
                break;
        }
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case USER_REQUEST:
                    List<Department> departments = (List<Department>) msg.getData().getSerializable("location");
                    personNodeList.clear();
                    personNodeList.addAll(departments);
                    adapter.notifyDataSetChanged();
                    break;

            }
        }
    }


    /**
     * 修改节点
     *
     * @param node
     */
    private void updateNode(BaseNode node) {

    }

    /**
     * 增加节点
     *
     * @param newNode
     */
    private void addNode(BaseNode newNode, int level) {

    }

    public void addToBmob(BaseNode node) {
        Person person = new Person();
        person.setId(node.getId());
        person.setParentId(node.getpId());
        person.setUsername(node.getName());
        person.login(this, new SaveListener() {
            @Override
            public void onSuccess() {
                toast("ok");
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });


    }

    public boolean updateToBmob(BaseNode node) {
        return true;
    }

    public boolean removeFromBmob(BaseNode node) {
        return true;
    }


}
