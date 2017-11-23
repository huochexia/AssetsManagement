package com.example.administrator.assetsmanagement.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.administrator.assetsmanagement.Interface.ToolbarClickListener;
import com.example.administrator.assetsmanagement.Interface.TreeNodeSelected;
import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.base.ParentWithNaviActivity;
import com.example.administrator.assetsmanagement.bean.Department;
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
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * 人员管理：增加，修改信息，删除以及角色的设定等操作。删除时要判断是否有资产，无则可以删除。人员调动操作过程 是
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

    LinearLayout mDialogView;

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


        LinearLayoutManager ll = new LinearLayoutManager(this);
        mLvTreeStructure.setLayoutManager(ll);

        mDialogView = (LinearLayout) this.getLayoutInflater().inflate(R.layout.person_dialog_view, null);
        //
        queryDepa();
    }

    /**
     * 查询部门
     */
    private void queryDepa() {
        BmobQuery<Department> query = new BmobQuery<>();
        query.setLimit(500);
        //执行查询方法
        query.findObjects(this, new FindListener<Department>() {
            @Override
            public void onSuccess(final List<Department> object) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message msg = new Message();
                        msg.what = REQUEST_DEPARTMENT;
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
                toast("查询部门失败：" + msg);
            }
        });
    }

    /**
     * 查询人员
     */
    private void queryPerson() {
        BmobQuery<BmobUser> query = new BmobQuery<>();
        query.setLimit(500);
        //执行查询方法
        query.findObjects(this, new FindListener<BmobUser>() {
            @Override
            public void onSuccess(final List<BmobUser> object) {
                // TODO Auto-generated method stub

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message msg = new Message();
                        msg.what = REQUEST_USER;
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("person", (Serializable) object);
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }
                }).start();

            }

            @Override
            public void onError(int code, String msg) {
                // TODO Auto-generated method stub
                toast("查询人员失败：" + msg);
            }
        });
    }

    public static final int REQUEST_DEPARTMENT = 10;
    public static final int REQUEST_USER = 11;
    public static final int ADD_SUCCESS = 12;

    MyHandler handler = new MyHandler();
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REQUEST_DEPARTMENT:
                    List<Department> departments = (List<Department>) msg.getData().getSerializable("department");
                    if (departments != null && departments.size() > 0) {
                        personNodeList.clear();
                        personNodeList.addAll(departments);
                    }
                    queryPerson();
                    break;
                case REQUEST_USER:
                    List<Person> personList = (List<Person>) msg.getData().getSerializable("person");
                    if (personList != null && personList.size() > 0) {

                        personNodeList.addAll(personList);
                    }
                    initAdapter();
                    break;

            }
        }
    }

    private void initAdapter() {
        adapter = new CheckboxTreeNodeAdapter(PersonSettingActivity.this, personNodeList,
                0, R.mipmap.expand, R.mipmap.collapse);
        mLvTreeStructure.setAdapter(adapter);
        adapter.setCheckBoxSelectedListener(new TreeNodeSelected() {
            @Override
            public void checked(BaseNode node, int postion) {
                mBaseNode = node;
                mPosition = postion;
            }

            @Override
            public void cancelCheck(BaseNode node, int position) {
                mBaseNode = null;
                mPosition = 0;

            }
        });
        adapter.notifyDataSetChanged();
    }

    @OnClick({R.id.btn_person_setting_add, R.id.btn_person_setting_repair, R.id.btn_person_setting_delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_person_setting_add:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("添加人员").setView(mDialogView);
                setPositiveButton(builder);
                setNegativeButton(builder).create().show();
                break;
            case R.id.btn_person_setting_repair:
                break;
            case R.id.btn_person_setting_delete:
                break;

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
    /**
     * 对话框确认事件方法
     */
    private AlertDialog.Builder setPositiveButton(AlertDialog.Builder builder) {
        return builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {

            }
        });

    }

    /**
     * 对话框取消按事件
     */
    private AlertDialog.Builder setNegativeButton(AlertDialog.Builder builder) {
        return builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
    }

}
