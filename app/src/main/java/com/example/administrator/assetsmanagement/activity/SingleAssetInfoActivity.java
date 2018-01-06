package com.example.administrator.assetsmanagement.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.assetsmanagement.Interface.ToolbarClickListener;
import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.base.ParentWithNaviActivity;
import com.example.administrator.assetsmanagement.bean.AssetInfo;
import com.example.administrator.assetsmanagement.bean.DepartmentTree.Department;
import com.example.administrator.assetsmanagement.bean.LocationTree.Location;
import com.example.administrator.assetsmanagement.bean.Person;
import com.example.administrator.assetsmanagement.utils.AssetsUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 通过扫描二维码，获取单个资产的信息，并可以进行相应的操作
 * Created by Administrator on 2017/12/20 0020.
 */

public class SingleAssetInfoActivity extends ParentWithNaviActivity {
    String title;
    @BindView(R.id.iv_single_asset_image)
    ImageView mIvSingleAssetImage;
    @BindView(R.id.tv_scan_asset_manager)
    TextView mTvScanAssetManager;
    @BindView(R.id.tv_scan_asset_location)
    TextView mTvScanAssetLocation;
    @BindView(R.id.tv_scan_asset_department)
    TextView mTvScanAssetDepartment;
    @BindView(R.id.tv_scan_asset_register_date)
    TextView mTvScanAssetRegisterDate;
    @BindView(R.id.tv_scan_asset_state)
    TextView mTvScanAssetState;


    String assetNum;
    AssetInfo mAssetInfo;
    @BindView(R.id.ll_asset_single_management)
    LinearLayout mLlAssetSingleManagement;
    @BindView(R.id.btn_single_asset_change)
    Button mBtnSingleAssetChange;

    @BindView(R.id.btn_single_asset_maintain)
    Button mBtnSingleAssetMaintain;
    @BindView(R.id.btn_single_asset_cancel_maintain)
    Button mBtnSingleAssetCancelMaintain;
    @BindView(R.id.btn_single_asset_lose)
    Button mBtnSingleAssetLose;
    @BindView(R.id.btn_single_asset_cancel_lose)
    Button mBtnSingleAssetCancelLose;
    @BindView(R.id.btn_single_asset_scrap)
    Button mBtnSingleAssetScrap;
    @BindView(R.id.btn_single_asset_cancel_scrap)
    Button mBtnSingleAssetCancelScrap;
    @BindView(R.id.btn_single_asset_receive)
    Button mBtnSingleAssetReceive;
    @BindView(R.id.tv_scan_asset_price)
    TextView mTvScanAssetPrice;
    @BindView(R.id.tv_scan_asset_comment)
    TextView mTvScanAssetComment;



    @Override
    public String title() {
        return title;
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
        setContentView(R.layout.activity_scan_asset_info);
        ButterKnife.bind(this);
        Bundle bundle = getBundle();
        assetNum = bundle.getString("assetNum");
        AssetsUtil.AndQueryAssets(this, "mAssetsNum", assetNum, handler);

    }

    SingleAssetHandler handler = new SingleAssetHandler();

    @OnClick({R.id.btn_single_asset_change, R.id.btn_single_asset_maintain, R.id.btn_single_asset_cancel_maintain,
            R.id.btn_single_asset_lose, R.id.btn_single_asset_cancel_lose, R.id.btn_single_asset_scrap,
            R.id.btn_single_asset_cancel_scrap, R.id.btn_single_asset_receive})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_single_asset_change:
                Bundle bundle = new Bundle();
                bundle.putSerializable("asset", mAssetInfo);
                startActivity(SingleAssetTransferActivity.class, bundle, false);
                break;
            case R.id.btn_single_asset_maintain:
                mAssetInfo.setStatus(1);
                setButtonStatus();
                break;
            case R.id.btn_single_asset_cancel_maintain:
                mAssetInfo.setStatus(0);
                setButtonStatus();
                break;
            case R.id.btn_single_asset_lose:
                mAssetInfo.setStatus(2);
                setButtonStatus();
                break;
            case R.id.btn_single_asset_cancel_lose:
                mAssetInfo.setStatus(0);
                setButtonStatus();
                break;
            case R.id.btn_single_asset_scrap:
                mAssetInfo.setStatus(3);
                setButtonStatus();
                break;
            case R.id.btn_single_asset_cancel_scrap:
                mAssetInfo.setStatus(0);
                setButtonStatus();
                break;
            case R.id.btn_single_asset_receive:
                mAssetInfo.setValue("mOldManager", BmobUser.getCurrentUser(Person.class));
                mAssetInfo.setValue("mNewManager", null);
                break;

        }
        mAssetInfo.update(mAssetInfo.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {

            }
        });
    }

    class SingleAssetHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AssetsUtil.SEARCH_ONE_ASSET:
                    final List<AssetInfo> asset = (List<AssetInfo>) msg.getData().getSerializable("assets");
                    if (asset.size() > 0) {
                        mAssetInfo = asset.get(0);
                        Glide.with(SingleAssetInfoActivity.this)
                                .load(mAssetInfo.getPicture().getImageUrl()).into(mIvSingleAssetImage);
                        mTvScanAssetManager.setText(mAssetInfo.getOldManager().getUsername());
                        getLocationAndDepartment(asset);
                        mTvScanAssetRegisterDate.setText(mAssetInfo.getRegisterDate());
                        mTvScanAssetPrice.setText(mAssetInfo.getPrice()+"");
                        mTvScanAssetComment.setText(mAssetInfo.getComment());
                        setButtonStatus();
//                        Person person = BmobUser.getCurrentUser(Person.class);
                        String id = mAssetInfo.getOldManager().getObjectId();
                        // 自V3.4.5版本开始，SDK新增了getObjectByKey(key)方法从本地缓存中获取
                        // 当前登陆用户某一列的值。其中key为用户表的指定列名
                        if (!id.equals(BmobUser.getObjectByKey("objectId"))) {
                            mLlAssetSingleManagement.setVisibility(View.GONE);
                        } else {
                            mLlAssetSingleManagement.setVisibility(View.VISIBLE);
                        }
                        title = mAssetInfo.getAssetName();
                        initNaviView();

                    }
                    break;
            }
        }
    }

    private void setButtonStatus() {

        switch (mAssetInfo.getStatus()) {
            case 0:
                mTvScanAssetState.setText("正常");
                mBtnSingleAssetChange.setVisibility(View.VISIBLE);
                mBtnSingleAssetScrap.setVisibility(View.VISIBLE);
                mBtnSingleAssetCancelScrap.setVisibility(View.GONE);
                mBtnSingleAssetMaintain.setVisibility(View.VISIBLE);
                mBtnSingleAssetCancelMaintain.setVisibility(View.GONE);
                mBtnSingleAssetLose.setVisibility(View.VISIBLE);
                mBtnSingleAssetCancelLose.setVisibility(View.GONE);
                break;
            case 1:
                mTvScanAssetState.setText("损坏");
                mBtnSingleAssetCancelMaintain.setVisibility(View.VISIBLE);
                mBtnSingleAssetMaintain.setVisibility(View.GONE);
                break;
            case 2:
                mTvScanAssetState.setText("丢失");
                mBtnSingleAssetCancelLose.setVisibility(View.VISIBLE);
                mBtnSingleAssetLose.setVisibility(View.GONE);
                break;
            case 3:
                mTvScanAssetState.setText("待报废");
                mBtnSingleAssetScrap.setVisibility(View.GONE);
                mBtnSingleAssetCancelScrap.setVisibility(View.VISIBLE);
                break;
            case 4:
                mTvScanAssetState.setText("待移交");
                break;
            case 5:
                mTvScanAssetState.setText("送修");
                break;
            case 9:
                mTvScanAssetState.setText("新登记未移交");
        }
    }

    /**
     * 利用编号查找位置名称，是因为Location转化为BaseNode时是不能利用注释的方法保留objectId，
     * 这样当选择节点后返回的节点没有objectId也就无法转换成Location类，也就无法在AssetInfo中
     * 保存Location类型属性值，只能保存Location的id。（部门类同样有这个问题）
     */

    private void getLocationAndDepartment(List<AssetInfo> asset) {
        if (asset.get(0).getLocationNum() != null && asset.get(0).getLocationNum()!="") {
            BmobQuery<Location> queryl = new BmobQuery<>();
            queryl.addWhereEqualTo("id", asset.get(0).getLocationNum());
            queryl.findObjects(new FindListener<Location>() {
                @Override
                public void done(final List<Location> list, BmobException e) {
                    if (list.size() > 0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mTvScanAssetLocation.setText(list.get(0).getLocationName());
                            }
                        });
                    }

                }
            });
        }
        if (asset.get(0).getDeptNum() != null && asset.get(0).getDeptNum()!="") {
            BmobQuery<Department> query2 = new BmobQuery<>();
            query2.addWhereEqualTo("id", asset.get(0).getDeptNum());
            query2.findObjects(new FindListener<Department>() {
                @Override
                public void done(final List<Department> list, BmobException e) {
                    if (list.size() > 0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                mTvScanAssetDepartment.setText(list.get(0).getDepartmentName());
                            }
                        });
                    }

                }
            });
        }

    }

}
