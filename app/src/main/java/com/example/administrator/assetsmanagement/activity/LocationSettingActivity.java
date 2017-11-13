package com.example.administrator.assetsmanagement.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;

import com.example.administrator.assetsmanagement.bean.Location;
import com.example.administrator.assetsmanagement.treeUtil.BaseNode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Administrator on 2017/11/12 0012.
 */

public class LocationSettingActivity extends TreeNodeSettingActivity {
    //    Location l1 = new Location("1", "0", "河北省税务干部学校");
//    Location l2 = new Location("A", "0", "A座");
//    Location l3 = new Location("01", "A", "一楼");
//    Location l4 = new Location("02", "A", "二楼");
//    Location l5 = new Location("101", "01", "101");
//    Location l6 = new Location("201", "02", "学校");
//    Location l7 = new Location("B", "0", "B座");
//    Location l8 = new Location("C", "0", "C座");
    private List<Object> locationList = new ArrayList<>();
    private boolean isSuccess;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        locationList.add(l1);
//        locationList.add(l2);
//        locationList.add(l3);
//        locationList.add(l4);
//        locationList.add(l5);
//        locationList.add(l6);
//        locationList.add(l7);
//        locationList.add(l8);
//        treeNodeList = locationList;

        BmobQuery<Location> query = new BmobQuery<>();
        query.setLimit(500);
        //执行查询方法
        query.findObjects(this, new FindListener<Location>() {
            @Override
            public void onSuccess(final List<Location> object) {
                // TODO Auto-generated method stub

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message msg = new Message();
                        msg.what = USER_REQUEST;
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("location", (Serializable) object);
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
        super.onCreate(savedInstanceState);
    }

    public static final int USER_REQUEST = 10;
    public static final int ADD_SUCCESS = 11;

    MyHandler handler = new MyHandler();

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case USER_REQUEST:
                    List<Location> locations = (List<Location>) msg.getData().getSerializable("location");
                    locationList.clear();
                    locationList.addAll(locations);
                    break;

            }
        }
    }


    @Override
    public void addToBmob(BaseNode node) {

        Location location = new Location();
        location.setId(node.getId());
        location.setParentId(node.getpId());
        location.setLocationName(node.getName());
        location.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                toast("添加位置成功！");
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });

    }

    @Override
    public boolean updateToBmob(BaseNode node) {
        return true;
    }

    @Override
    public boolean removeFromBmob(BaseNode node) {
        return true;
    }

    @Override
    public String title() {
        return "位置";
    }
}
