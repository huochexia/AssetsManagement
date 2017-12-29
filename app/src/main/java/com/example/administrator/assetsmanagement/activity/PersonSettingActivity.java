package com.example.administrator.assetsmanagement.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.administrator.assetsmanagement.Interface.SelectManagerClickListener;
import com.example.administrator.assetsmanagement.Interface.ToolbarClickListener;
import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.adapter.SetManagerRightAdapter;
import com.example.administrator.assetsmanagement.base.ParentWithNaviActivity;
import com.example.administrator.assetsmanagement.bean.Person;
import com.example.administrator.assetsmanagement.bean.Role;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 人员管理：主要是对人员权限的查看、设置和修改。"登记","查询","设置","审批"四种权限
 * Created by Administrator on 2017/11/10.
 */

public class PersonSettingActivity extends ParentWithNaviActivity {

    @BindView(R.id.lv_tree_structure)
    RecyclerView mLvTreeStructure;

    protected List<Person> userlist = new ArrayList<>();
    Person person;
    SetManagerRightAdapter adapter;
    Role role;

    @Override
    public String title() {
        return "权限设置";
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
        queryPerson();

    }

    /**
     * 选择权限对话框
     */
    private void startSelectDialog(final Role role , final Person person) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择人员权限");
        final boolean[] items = new boolean[]{false,false,false,false,false};
        //确定权限初始值
        if (role.getRights() != null) {
            if( role.getRights().contains("登记")){
                items[0]=true;
            }
            if( role.getRights().contains("查询")){
                items[1]=true;
            }

            if( role.getRights().contains("设置")){
               items[2]=true;
            }
            if( role.getRights().contains("审批")){
               items[3]=true;
            }
            if( role.getRights().contains("处置")){
               items[4]=true;
            }

        }
        final String[] rights =new String[]{"登记","查询","设置","审批","处置"};
        builder.setMultiChoiceItems(rights, items, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    try {
                        role.addUnique("rights", rights[which]);
                    } catch (NullPointerException e) {
                    }
                    if (role.getUser() == null) {//如果用户为空，说明这个用户是第一次设置权限，则要插入
                        role.setUser(person);
                        role.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e != null) {
                                    toast("设置失败："+e.getMessage());
                                }
                            }
                        });
                    } else {//如果用户不为空，则修改它的权限
                        role.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e != null) {
                                    toast("设置失败："+e.getMessage());
                                }
                            }
                        });

                    }
                } else {
                   role.removeAll("rights", Arrays.asList(rights[which]));
                   role.update(new UpdateListener() {
                       @Override
                       public void done(BmobException e) {
                           if (e != null) {
                               toast("设置失败："+e.getMessage());
                           }
                       }
                   });

                }
            }
        });

        builder.setNegativeButton("返回",null );
        builder.show();
    }
    /**
     * 查询人员
     */
    private void queryPerson() {
        BmobQuery<Person> query = new BmobQuery<>();
        query.setLimit(500);
        //执行查询方法
        query.findObjects(new FindListener<Person>() {
            @Override
            public void done(final List<Person> list, BmobException e) {
                if (e == null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message msg = new Message();
                            msg.what = REQUEST_USER;
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("person", (Serializable) list);
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }
                    }).start();
                } else {
                    toast("查询人员失败：" + e.toString());
                }
            }


        });
    }

    public static final int REQUEST_USER = 11;
    MyHandler handler = new MyHandler();

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REQUEST_USER:
                    userlist = (List<Person>) msg.getData().getSerializable("person");
                    adapter = new SetManagerRightAdapter(PersonSettingActivity.this, userlist);
                    mLvTreeStructure.setAdapter(adapter);
                    adapter.setOnClickListener(new SelectManagerClickListener() {
                        @Override
                        public void select(final Person person) {
                            runOnMain(new Runnable() {
                                @Override
                                public void run() {
                                    BmobQuery<Role> query = new BmobQuery<>();
                                    query.addWhereEqualTo("user",person);
                                    query.findObjects(new FindListener<Role>() {
                                        @Override
                                        public void done(List<Role> list, BmobException e) {
                                            if (list != null) {
                                                role = list.get(0);
                                            } else {
                                                role = new Role();
                                            }
                                            startSelectDialog(role,person);
                                        }
                                    });
                                }
                            });
                        }

                        @Override
                        public void cancelSelect() {

                        }
                    });
                    break;

            }
        }
    }


}
