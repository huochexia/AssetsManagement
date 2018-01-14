package com.example.administrator.assetsmanagement.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.administrator.assetsmanagement.Interface.PhotoSelectedListener;
import com.example.administrator.assetsmanagement.Interface.ToolbarClickListener;
import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.adapter.PhotoRecyclerViewAdapter;
import com.example.administrator.assetsmanagement.base.ParentWithNaviActivity;
import com.example.administrator.assetsmanagement.bean.AssetInfo;
import com.example.administrator.assetsmanagement.bean.AssetPicture;
import com.example.administrator.assetsmanagement.bean.CategoryTree.AssetCategory;
import com.example.administrator.assetsmanagement.bean.Person;
import com.example.administrator.assetsmanagement.utils.AssetsUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;


/**
 * 依据资产类别编号下载图片列表，选择图片后，返回图片信息对象,图片下载要实现分页下载下拉刷新，上拉加载
 * Created by Administrator on 2017/11/19 0019.
 */

public class SelectAssetsPhotoActivity extends ParentWithNaviActivity {
    public static final int RESULT_OK = 0;
    @BindView(R.id.rc_pictures_list)
    RecyclerView mRcPicturesList;


    private String title;
    private AssetCategory category;
    private AssetPicture imageFile;

    private PhotoRecyclerViewAdapter mAdapter;
    private List<AssetPicture> photoLists = new ArrayList<>();
    private boolean isRegister;
    private String para;
    private Object value;
    private int count;

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
                if (imageFile != null) {
                    Intent returnPhoto = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("imageFile", imageFile);
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
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        mRcPicturesList.setLayoutManager(layoutManager);
//        mRcPicturesList.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                GridLayoutManager gl = (GridLayoutManager) mRcPicturesList.getLayoutManager();
//                int lastVisibleItemPosition=gl.findLastVisibleItemPosition();
//                if (lastVisibleItemPosition >= gl.getItemCount() - 1) {//到达页末
//                    //TODO:加载数据
//                    count = photoLists.size()/18;
//                    getPictureList2("category", category, handler);
//                }
//            }
//        });
        Intent intent = getIntent();
        isRegister = intent.getBooleanExtra("isRegister", true);
        if (isRegister) {
            title = intent.getStringExtra("category_name");
            category = (AssetCategory) intent.getSerializableExtra("category");
            getPictureList("category", category, handler);
        } else {
            title = "我的资产图片";
            List<AssetInfo> allList = new ArrayList<>();
            AssetsUtil.count = 0;
            AssetsUtil.AndQueryAssets(this, "mOldManager", BmobUser.getCurrentUser(Person.class), handler, allList);
        }
        initNaviView();
    }

    /**
     * 根据类别查询图片
     *
     * @param para
     * @param value
     * @param handler
     *
     */
    private void getPictureList(final String para, final Object value, final Handler handler) {
        BmobQuery<AssetPicture> query = new BmobQuery<>();
        query.addWhereEqualTo(para, value);
        query.order("-createdAt");
        query.setLimit(500);
        query.findObjects(new FindListener<AssetPicture>() {
            @Override
            public void done(final List<AssetPicture> list, BmobException e) {
                if (e == null) {
                           new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Message message = new Message();
                                message.what = TAKE_PHOTO;
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("photo", (Serializable) list);
                                message.setData(bundle);
                                handler.sendMessage(message);
                            }
                        }).start();
                } else {
                    toast("查询出现异常，请稍后再试！");
                }

            }
        });
    }
    private void getPictureList2(final String para, final Object value, final Handler handler) {
        BmobQuery<AssetPicture> query = new BmobQuery<>();
        query.addWhereEqualTo(para, value);
        query.order("-createdAt");
        query.setSkip(count * 18);
        query.setLimit(18);
        query.findObjects(new FindListener<AssetPicture>() {
            @Override
            public void done(final List<AssetPicture> list, BmobException e) {
                if (e == null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message message = new Message();
                            message.what = TAKE_PHOTO;
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("photo", (Serializable)list);
                            message.setData(bundle);
                            handler.sendMessage(message);
                        }
                    }).start();
                } else {
                    toast("查询出现异常，请稍后再试！");
                }

            }
        });
    }


    public static final int TAKE_PHOTO = 0;
    public MyHandler handler = new MyHandler();

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TAKE_PHOTO:
//                    List<AssetPicture> templist = (List<AssetPicture>) msg.getData().getSerializable("photo");
//                    photoLists.addAll(templist);
                    photoLists.clear();
                    photoLists = (List<AssetPicture>) msg.getData().getSerializable("photo");
                    break;
                case AssetsUtil.SEARCH_ONE_ASSET:
                    List<AssetInfo> list = (List<AssetInfo>) msg.getData().getSerializable("assets");
                    List<AssetInfo> mergeList = AssetsUtil.mergeAsset(list);
                    photoLists.clear();
                    for (AssetInfo asset : mergeList) {
                        photoLists.add(asset.getPicture());
                    }
                    break;
            }
            mAdapter = new PhotoRecyclerViewAdapter(SelectAssetsPhotoActivity.this, photoLists);
            mRcPicturesList.setAdapter(mAdapter);
            mAdapter.getSelectedListener(new PhotoSelectedListener() {
                @Override
                public void selectPhoto(AssetPicture picture) {
                    imageFile = picture;
                }

            });
        }
    }

    /**
     * 上拉加载更多的图片
     */
    class MoreArticleTask extends AsyncTask<Void,Void, List<AssetPicture>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (photoLists != null && photoLists.size() > 0) {
                photoLists.add(null);//增加了一个null标记Footer
                // notifyItemInserted(int position)，这个方法是在第position位置
                // 被插入了一条数据的时候可以使用这个方法刷新，
                // 注意这个方法调用后会有插入的动画，这个动画可以使用默认的，也可以自己定义。
                mAdapter.notifyItemInserted(photoLists.size() - 1);
            }
        }

        @Override
        protected List<AssetPicture> doInBackground(Void... params) {
            List<AssetPicture> datas = new ArrayList<>();
            if (photoLists.size() == 0) {
                count = 0;
            } else {
            }
            //TODO:利用分页显示原理从Bmob数据库中获取,
            return datas;
        }

        @Override
        protected void onPostExecute(List<AssetPicture> datas) {
            super.onPostExecute(datas);
            if (photoLists.size() == 0) {
                photoLists.addAll(datas);
                mAdapter.notifyDataSetChanged();
            } else {
                //删除footer
                photoLists.remove(photoLists.size() - 1);
                //只有到达最底部才加载
                //防止上拉到了倒数两三个也加载
//                if (!bottom && lastVisibleItem == totalItemCount - 1 && moreArticles.size() == 0) {
//                    Snackbar.with(mActivity.getApplicationContext()) // context
//                            .text(mActivity.getResources().getString(R.string.list_no_data)) // text to display
//                            .duration(Snackbar.SnackbarDuration.LENGTH_SHORT) // make it shorter
//                            .show(mActivity); // activity where it is displayed
//                    bottom = true;
//                }
                photoLists.addAll(datas);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

}
