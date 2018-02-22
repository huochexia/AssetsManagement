package com.example.administrator.assetsmanagement.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.administrator.assetsmanagement.Interface.SelectManagerClickListener;
import com.example.administrator.assetsmanagement.Interface.ToolbarClickListener;
import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.base.ParentWithNaviActivity;
import com.example.administrator.assetsmanagement.bean.Manager.AcronymItem;
import com.example.administrator.assetsmanagement.bean.Manager.CharIndexBar;
import com.example.administrator.assetsmanagement.bean.Manager.Person;
import com.example.administrator.assetsmanagement.bean.Manager.Role;
import com.example.administrator.assetsmanagement.bean.Manager.SetManagerRightAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 人员管理：主要是对人员权限的查看、设置和修改。"登记","查询","设置","审批"四种权限
 * Created by Administrator on 2017/11/10.
 */

public class PersonSettingActivity extends ParentWithNaviActivity {

    @BindView(R.id.lv_tree_structure)
    RecyclerView mLvTreeStructure;
    AcronymItem mItemDecoration;

    protected List<Person> userlist = new ArrayList<>();
    Person person;
    SetManagerRightAdapter adapter;
    Role role;
    int count = 0;//计数器，每500条加1
    @BindView(R.id.loading_person_progress)
    ProgressBar mLoadingPersonProgress;
    @BindView(R.id.char_index_bar)
    CharIndexBar mCharIndexBar;
    @BindView(R.id.tv_show_hint)
    TextView mTvShowHint;


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
        List<Person> allPerson = new ArrayList<>();
        count = 0;
        queryPerson(allPerson);
        mCharIndexBar.setShowHintText(mTvShowHint);
        mCharIndexBar.setmLayoutManager(ll);
    }

    /**
     * 选择权限对话框
     */
    private void startSelectDialog(final Role role, final Person person) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(person.getUsername()+"的权限");
        final boolean[] items = new boolean[]{false, false, false, false, false,false};
        //确定权限初始值
        if (role.getRights() != null) {
            if (role.getRights().contains("登记")) {
                items[0] = true;
            }
            if (role.getRights().contains("查询")) {
                items[1] = true;
            }

            if (role.getRights().contains("设置")) {
                items[2] = true;
            }
            if (role.getRights().contains("审批报废")) {
                items[3] = true;
            }
            if (role.getRights().contains("处置资产")) {
                items[4] = true;
            }
            if (role.getRights().contains("修改数据")) {
                items[5] = true;
            }

        }
        final String[] rights = new String[]{"登记", "查询", "设置", "审批报废", "处置资产","修改数据"};
        builder.setMultiChoiceItems(rights, items, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    try {
                        role.addUnique("rights", rights[which]);
                    } catch (NullPointerException e) {
                    }
                    role.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e != null) {
                                toast("设置失败：" + e.getMessage());
                            }
                        }
                    });

                } else {
                    role.removeAll("rights", Arrays.asList(rights[which]));
                    role.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e != null) {
                                toast("设置失败：" + e.getMessage());
                            }
                        }
                    });

                }
            }
        });

        builder.setNegativeButton("返回", null);
        builder.show();
    }

    /**
     * 查询人员,有可能超过500人
     */

    private void queryPerson(final List<Person> allPerson) {
        final BmobQuery<Person> query = new BmobQuery<>();
        query.order("acronym");
        query.setSkip(count * 500);
        query.setLimit(500);
        //执行查询方法
        query.findObjects(new FindListener<Person>() {
            @Override
            public void done(final List<Person> list, BmobException e) {
                if (e == null) {
                    allPerson.addAll(list);
                    if (list.size() > 500) {
                        count++;
                        queryPerson(allPerson);
                    } else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Message msg = new Message();
                                msg.what = REQUEST_USER;
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("person", (Serializable) allPerson);
                                msg.setData(bundle);
                                handler.sendMessage(msg);
                            }
                        }).start();
                    }

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
                    if (userlist != null) {
                        adapter = new SetManagerRightAdapter(PersonSettingActivity.this, userlist);
                        mLvTreeStructure.setAdapter(adapter);
                        mLvTreeStructure.addItemDecoration(new AcronymItem(PersonSettingActivity.this, userlist));
                        mCharIndexBar.setPersonList(userlist);
                        mLoadingPersonProgress.setVisibility(View.GONE);
                        adapter.setOnClickListener(new SelectManagerClickListener() {
                            @Override
                            public void select(final Person person) {
                                runOnMain(new Runnable() {
                                    @Override
                                    public void run() {
                                        BmobQuery<Role> query = new BmobQuery<>();
                                        query.addWhereEqualTo("user", person);
                                        query.findObjects(new FindListener<Role>() {
                                            @Override
                                            public void done(List<Role> list, BmobException e) {
                                                if (list != null) {
                                                    role = list.get(0);
                                                } else {
                                                    role = new Role();
                                                }
                                                startSelectDialog(role, person);
                                            }
                                        });
                                    }
                                });
                            }

                            @Override
                            public void cancelSelect() {

                            }
                        });
                    }
                    break;

            }
        }
    }


}
