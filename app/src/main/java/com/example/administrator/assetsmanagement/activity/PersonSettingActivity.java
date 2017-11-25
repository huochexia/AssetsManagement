package com.example.administrator.assetsmanagement.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
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
import cn.bmob.v3.listener.UpdateListener;

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
    Person person;

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
        BmobQuery<Person> query = new BmobQuery<>();
        query.setLimit(500);
        //执行查询方法
        query.findObjects(this, new FindListener<Person>() {
            @Override
            public void onSuccess(final List<Person> object) {
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
    public static final int REPAIR_PERSON_INFO = 12;
    public static final int DELETE_PERSON = 13;
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
                    List<Person> usersList = (List<Person>) msg.getData().getSerializable("person");
                    if (usersList != null && usersList.size() > 0) {
                        personNodeList.addAll(usersList);
                    }
                    initAdapter();
                    break;
                case REPAIR_PERSON_INFO:
                    person = (Person) msg.getData().getSerializable("one");
                    setPersonInfo(person);
                    break;
                case DELETE_PERSON:
                    break;
            }
        }
    }

    /**
     * 根据人员信息填写对话框内容
     *
     * @param one
     */
    private void setPersonInfo(Person one) {
        EditText name = (EditText) mDialogView.findViewById(R.id.et_person_name);
        EditText phone = (EditText) mDialogView.findViewById(R.id.et_person_telephone);
        CheckBox register = (CheckBox) mDialogView.findViewById(R.id.cb_person_role_register);
        CheckBox approve = (CheckBox) mDialogView.findViewById(R.id.cb_person_role_approve);
        CheckBox location = (CheckBox) mDialogView.findViewById(R.id.cb_person_role_location);
        CheckBox department = (CheckBox) mDialogView.findViewById(R.id.cb_person_role_department);
        CheckBox persons = (CheckBox) mDialogView.findViewById(R.id.cb_person_role_person);
        CheckBox category = (CheckBox) mDialogView.findViewById(R.id.cb_person_role_category);
        register.setChecked(one.getRole().contains("register"));
        approve.setChecked(one.getRole().contains("approve"));
        location.setChecked(one.getRole().contains("location"));
        department.setChecked(one.getRole().contains("department"));
        persons.setChecked(one.getRole().contains("persons"));
        category.setChecked(one.getRole().contains("category"));
        name.setText(one.getNodename());
        phone.setText(one.getMobilePhoneNumber());
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
                mDialogView = (LinearLayout) this.getLayoutInflater().inflate(R.layout.person_dialog_view, null);
                if (mBaseNode != null && !mBaseNode.isLast) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("添加人员信息").setView(mDialogView);
                    setPositiveButton(builder);
                    setNegativeButton(builder).create().show();
                } else {
                    toast("请选择部门！");
                }

                break;
            case R.id.btn_person_setting_repair:
                if (mBaseNode != null && mBaseNode.isLast) {
                    mDialogView = (LinearLayout) this.getLayoutInflater().inflate(R.layout.person_dialog_view, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("人员信息").setView(mDialogView);
                    setNegativeButton(builder).create().show();
                    BmobQuery<Person> query = new BmobQuery<>();
                    query.addWhereEqualTo("id", mBaseNode.getId());
                    query.findObjects(this, new FindListener<Person>() {
                        @Override
                        public void onSuccess(final List<Person> list) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Message msg = new Message();
                                    msg.what = REPAIR_PERSON_INFO;
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("one", list.get(0));
                                    msg.setData(bundle);
                                    handler.sendMessage(msg);

                                }
                            }).start();
                        }

                        @Override
                        public void onError(int i, String s) {

                        }
                    });
                } else {
                    toast("请选择要查看的人员！");
                }
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
        adapter.addData(mPosition, newNode, level);
        adapter.notifyDataSetChanged();
    }

    public void addToBmob(Person person) {
        BmobUser user = person;
        user.signUp(this, new SaveListener() {
            @Override
            public void onSuccess() {
                toast("添加人员成功！");
            }

            @Override
            public void onFailure(int i, String s) {
                toast("添加人员失败！" + s);
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
     * 添加对话框确认事件方法
     */
    private AlertDialog.Builder setPositiveButton(AlertDialog.Builder builder) {

        return builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                savePersonAndCreateNode();
            }
        });

    }


    /**
     * 保存用户到Bmob云数据库，同时生成节点
     */
    private void savePersonAndCreateNode() {
        Person person = new Person();
        BaseNode<Person> newNode = new BaseNode<>();
        EditText name = (EditText) mDialogView.findViewById(R.id.et_person_name);
        EditText phone = (EditText) mDialogView.findViewById(R.id.et_person_telephone);
        List<String> roles = getRoles();
        if (!TextUtils.isEmpty(name.getText()) && !TextUtils.isEmpty(phone.getText())) {
            person.setUsername(name.getText().toString());
            person.setNodename(name.getText().toString());
            newNode.setName(name.getText().toString());
            person.setMobilePhoneNumber(phone.getText().toString());
            person.setRole(roles);
            String num = System.currentTimeMillis() + "";
            person.setId(num);
            newNode.setId(num);
            person.setParentId(mBaseNode.getId());
            newNode.setpId(mBaseNode.getId());
            person.setPassword("123456");
            addToBmob(person);
            addNode(newNode, mBaseNode.getLevel() + 1);
        } else {
            toast("输入人员姓名和电话号码！");
        }
    }

    /**
     * 获得用户拥有的角色
     *
     * @return
     */
    @NonNull
    private List<String> getRoles() {
        CheckBox register = (CheckBox) mDialogView.findViewById(R.id.cb_person_role_register);
        CheckBox approve = (CheckBox) mDialogView.findViewById(R.id.cb_person_role_approve);
        CheckBox location = (CheckBox) mDialogView.findViewById(R.id.cb_person_role_location);
        CheckBox department = (CheckBox) mDialogView.findViewById(R.id.cb_person_role_department);
        CheckBox persons = (CheckBox) mDialogView.findViewById(R.id.cb_person_role_person);
        CheckBox category = (CheckBox) mDialogView.findViewById(R.id.cb_person_role_category);
        List<String> roles = new ArrayList<>();
        if (register.isChecked())
            roles.add("register");
        if (approve.isChecked())
            roles.add("approve");
        if (location.isChecked())
            roles.add("location");
        if (department.isChecked())
            roles.add("department");
        if (persons.isChecked())
            roles.add("persons");
        if (category.isChecked())
            roles.add("category");
        return roles;
    }

    /**
     * 对话框取消事件
     */
    private AlertDialog.Builder setNegativeButton(AlertDialog.Builder builder) {
        return builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
    }


}
