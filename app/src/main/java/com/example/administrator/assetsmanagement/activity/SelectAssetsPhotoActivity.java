package com.example.administrator.assetsmanagement.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.administrator.assetsmanagement.Interface.PhotoSelectedListener;
import com.example.administrator.assetsmanagement.Interface.ToolbarClickListener;
import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.adapter.PhotoRecyclerViewAdapter;
import com.example.administrator.assetsmanagement.base.ParentWithNaviActivity;
import com.example.administrator.assetsmanagement.bean.AssetPicture;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * 依据资产类别编号下载图片列表，选择图片后，返回图片信息对象
 * Created by Administrator on 2017/11/19 0019.
 */

public class SelectAssetsPhotoActivity extends ParentWithNaviActivity {
    public static final int RESULT_OK = 0;
    @BindView(R.id.rc_pictures_list)
    RecyclerView mRcPicturesList;

    private String title;
    private String categoryNum;

    private String imageNum;
    private AssetPicture imageFile;

    private PhotoRecyclerViewAdapter mAdapter;
    private List<AssetPicture> photoLists;
    private AssetPicture selectedPicture;
    @Override
    public String title() {
        return title;
    }

    @Override
    public Object left() {
        return R.drawable.ic_left_navi;
    }

    @Override
    public Object right() {
        return R.drawable.ic_right_check;
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
                if (imageNum!=null && imageFile!=null) {
                    Intent returnPhoto = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("imageNum",imageNum);
                    bundle.putSerializable("imageFile",imageFile);
                    returnPhoto.putExtra("assetpicture", bundle);
                    setResult(RESULT_OK, returnPhoto);
                    finish();
                } else {
                    toast("请选择图片！");
                }
            }
        };
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_photo);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        title = intent.getStringExtra("category_name");
        categoryNum = intent.getStringExtra("category_num");
        initNaviView();
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        mRcPicturesList.setLayoutManager(layoutManager);
        BmobQuery<AssetPicture> query = new BmobQuery<>();
        query.addWhereEqualTo("categoryNum", categoryNum);
        query.setLimit(500);
        query.findObjects(new FindListener<AssetPicture>() {
            @Override
            public void done(final List<AssetPicture> list, BmobException e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        message.what= TAKE_PHOTO;
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("photo", (Serializable) list);
                        message.setData(bundle);
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });
    }


    public static final int TAKE_PHOTO=0;
    public MyHandler handler= new MyHandler();
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TAKE_PHOTO:

                    photoLists = (List<AssetPicture>) msg.getData().getSerializable("photo");
                    mAdapter = new PhotoRecyclerViewAdapter(SelectAssetsPhotoActivity.this, photoLists);
                    mRcPicturesList.setAdapter(mAdapter);
                    mAdapter.getSelectedListener(new PhotoSelectedListener() {
                        @Override
                        public void selected(String Num, AssetPicture picture) {
                            imageNum =  Num;
                            imageFile = picture;
                        }
                    });
                    break;
            }
        }
    }
}
