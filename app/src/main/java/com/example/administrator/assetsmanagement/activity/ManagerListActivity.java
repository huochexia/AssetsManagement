package com.example.administrator.assetsmanagement.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.administrator.assetsmanagement.Interface.SelectManagerClickListener;
import com.example.administrator.assetsmanagement.Interface.ToolbarClickListener;
import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.adapter.ManagerRecyclerViewAdapter;
import com.example.administrator.assetsmanagement.base.ParentWithNaviActivity;
import com.example.administrator.assetsmanagement.bean.Person;

import java.io.Serializable;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;



/**
 * Created by Administrator on 2017/12/14 0014.
 */

public class ManagerListActivity extends ParentWithNaviActivity {
    public static final int SEARCH_OK =100;
    List<Person> mPersonList;
    ManagerRecyclerViewAdapter adapter;
    RecyclerView mRecyclerView;
    private Person manager;

    @Override
    public String title() {
        return "管理员";
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
        setContentView(R.layout.activity_manager_list);
        ButterKnife.bind(this);
        initNaviView();

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_manager_list_view);
        LinearLayoutManager ll = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(ll);
        getManager();

    }

    private void getManager() {
        BmobQuery<Person> query = new BmobQuery<>();
        query.setLimit(500);
        query.include("department");
        query.findObjects(new FindListener<Person>() {
            @Override
            public void done(final List<Person> list, BmobException e) {
                if (e == null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message msg = new Message();
                            msg.what = 1;
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("manager", (Serializable) list);
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }
                    }).start();
                }
            }
        });
    }

    ManagerHandler handler = new ManagerHandler();

    @OnClick(R.id.btn_manager_select_ok)
    public void onViewClicked() {
        if (manager != null) {
            Intent intent = new Intent();
            intent.putExtra("manager", manager);
            setResult(SEARCH_OK, intent);
            finish();
        }else{
            toast("请选择管理员！");
        }

    }


    class ManagerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mPersonList = (List<Person>) msg.getData().getSerializable("manager");
                    adapter = new ManagerRecyclerViewAdapter(ManagerListActivity.this, mPersonList);
                    mRecyclerView.setAdapter(adapter);
                    adapter.setOnClickListener(new SelectManagerClickListener() {
                        @Override
                        public void select(Person person) {
                            manager = person;
                        }

                        @Override
                        public void cancelSelect() {
                            manager = null;
                        }


                    });
                    break;
            }
        }
    }
}
