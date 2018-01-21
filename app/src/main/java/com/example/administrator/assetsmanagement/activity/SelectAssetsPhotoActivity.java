package com.example.administrator.assetsmanagement.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.example.administrator.assetsmanagement.Interface.PhotoSelectedListener;
import com.example.administrator.assetsmanagement.Interface.ToolbarClickListener;
import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.adapter.PhotoRecyclerViewAdapter;
import com.example.administrator.assetsmanagement.base.ParentWithNaviActivity;
import com.example.administrator.assetsmanagement.bean.AssetInfo;
import com.example.administrator.assetsmanagement.bean.AssetPicture;
import com.example.administrator.assetsmanagement.bean.CategoryTree.AssetCategory;
import com.example.administrator.assetsmanagement.bean.Manager.Person;
import com.example.administrator.assetsmanagement.utils.AssetsUtil;
import com.example.administrator.assetsmanagement.utils.PictureReceiveEvent;

import org.greenrobot.eventbus.EventBus;

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
    @BindView(R.id.loading_picture_progress)
    ProgressBar loadingPictureProgress;

    private String title;
    private AssetCategory category;
    private AssetPicture imageFile;

    private PhotoRecyclerViewAdapter mAdapter;
    private List<AssetPicture> photoLists = new ArrayList<>();

    private int page;//分页获取数据的当前页数
    /**
     * 最后一个可见的item的位置
     */
    private int lastVisibleItemPosition;

    /**
     * 当前滑动的状态
     */
    private int currentScrollState = 0;

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
                    //发送EventBus事件
                    EventBus.getDefault().post(new PictureReceiveEvent(imageFile));
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

        final GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        mRcPicturesList.setLayoutManager(layoutManager);
        mRcPicturesList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                currentScrollState = newState;

//                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
//                int visibleItemCount = layoutManager.getChildCount();
//                int totalItemCount = layoutManager.getItemCount();
//                if ((visibleItemCount > 0 && currentScrollState== RecyclerView.SCROLL_STATE_IDLE &&
//                        (lastVisibleItemPosition) >= totalItemCount - 1) ) {
//                    mAdapter.changeState(1);
//                    getPictureList("category", category, handler);
//                }
                if (currentScrollState == RecyclerView.SCROLL_STATE_IDLE && isSlideToBottom(recyclerView)) {
                    mAdapter.changeState(1);
                    getPictureList("category", category, handler);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

            }
        });
        //让Footer占据整行
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int type = mRcPicturesList.getAdapter().getItemViewType(position);
                if (type == PhotoRecyclerViewAdapter.TYPE_FOOTER) {
                    return layoutManager.getSpanCount();
                } else {
                    return 1;
                }
            }
        });

        Intent intent = getIntent();
        //有两种情况启动这个Activity，一是登记；二是个人资产查询。如果是登记，根据类别显示图片；如果是
        // 个人资产查询，则根据个人所拥有的资产，显示个人拥有资产的图片。
        boolean isRegister = intent.getBooleanExtra("isRegister", true);
        if (isRegister) {
            title = intent.getStringExtra("category_name");
            category = (AssetCategory) intent.getSerializableExtra("category");
            photoLists.clear();
            page = 0;
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
     */
    private void getPictureList(final String para, final Object value, final Handler handler) {
        BmobQuery<AssetPicture> query = new BmobQuery<>();
        query.addWhereEqualTo(para, value);
        query.order("-createdAt");

        query.setSkip(page * 15);
        query.setLimit(15);
        query.findObjects(new FindListener<AssetPicture>() {
            @Override
            public void done(final List<AssetPicture> list, BmobException e) {
                if (e == null) {
                    if (list.size() > 0) {
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
                        mAdapter.changeState(2);
                    }

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
                    //增加新查询的数据
                    List<AssetPicture> pictureList = (List<AssetPicture>) msg.getData().getSerializable("photo");
                    if (pictureList.size() > 0) {
                        photoLists.addAll(pictureList);
                        //图片定位，将刚下载的图片定位到屏幕定部。（原理：刚下载下来的第一个图片位于全部图片
                        // 的页数乘15减1的位置。
                        mRcPicturesList.scrollToPosition(page * 15 - 1);
                        page++;
                    }

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
            loadingPictureProgress.setVisibility(View.GONE);
            mAdapter.getSelectedListener(new PhotoSelectedListener() {
                @Override
                public void selectPhoto(AssetPicture picture) {
                    imageFile = picture;
                }

            });

        }
    }

    /**
     * 判断列表是否达到底部
     *
     * @param recyclerView
     * @return
     */

    public boolean isSlideToBottom(RecyclerView recyclerView) {
        if (recyclerView == null) return false;
        if (recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset()
                >= recyclerView.computeVerticalScrollRange())
            return true;
        return false;
    }

}
