package com.example.administrator.assetsmanagement.activity;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dothantech.lpapi.LPAPI;
import com.dothantech.printer.IDzPrinter;
import com.example.administrator.assetsmanagement.Interface.AssetSelectedListener;
import com.example.administrator.assetsmanagement.Interface.ToolbarClickListener;
import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.adapter.MakingLabelsListAdapter;
import com.example.administrator.assetsmanagement.adapter.PrintDeviceListAdapter;
import com.example.administrator.assetsmanagement.base.ParentWithNaviActivity;
import com.example.administrator.assetsmanagement.bean.AssetInfo;
import com.example.administrator.assetsmanagement.bean.AssetPicture;
import com.example.administrator.assetsmanagement.utils.AssetsUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 制作资产标签，仅打印标签，或者打印后进行移交。该活动适用于新登记的资产(尚未保存）和从数据库中获取
 * 的资产。通过flag来进行判断。
 * Created by Administrator on 2018/1/3 0003.
 */

public class MakingLabelActivity extends ParentWithNaviActivity {
    @BindView(R.id.tv_item_header_name)
    TextView mTvItemHeaderName;
    @BindView(R.id.tv_item_header_quantity)
    TextView mTvItemHeaderQuantity;
    @BindView(R.id.ll_item_header_quantity)
    LinearLayout mLlItemHeaderQuantity;
    @BindView(R.id.ll_item_header_status)
    LinearLayout mLlItemHeaderStatus;
    @BindView(R.id.rv_making_label)
    RecyclerView mRvMakingLabel;

    MakingLabelsListAdapter madapter;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvToolbarTitle;
    @BindView(R.id.iv_left_navi)
    ImageView mIvLeftNavi;
    @BindView(R.id.iv_right_navi)
    ImageView mIvRightNavi;
    @BindView(R.id.btn_print_label_and_move_asset)
    Button mBtnPrintLabelAndMoveAsset;
    @BindView(R.id.tv_printer_state)
    TextView tvPrinterState;
    private List<AssetInfo> mInfoList;
    private List<AssetInfo> mSelectedList;
    private AssetPicture mAssetPicture;
    private int flag;//标志，1为新，否则为旧数据

    /**
     *打印机部分
     */
    private LPAPI api; //打印机api
    private Handler mHandler = new Handler();//异步处理线程，更新界面
    private IDzPrinter.PrinterAddress mPrinterAddress;
    private AlertDialog stateAlertDialog = null;// 状态提示框
    private List<IDzPrinter.PrinterAddress> pairedPrinters = new ArrayList<>();

    // LPAPI 打印机操作相关的回调函数。
    private final LPAPI.Callback mCallback = new LPAPI.Callback() {
        // 蓝牙适配器状态发生变化时被调用
        @Override
        public void onProgressInfo(IDzPrinter.ProgressInfo progressInfo, Object o) {

        }

        // 打印机连接状态发生变化时被调用
        @Override
        public void onStateChange(IDzPrinter.PrinterAddress printerAddress,
                                  IDzPrinter.PrinterState printerState) {
            final IDzPrinter.PrinterAddress printer = printerAddress;
            switch (printerState) {
                //连接打印机成功，发送通知，刷新界面提示
                case Connected:
                case Connected2:
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            onPrinterConnected(printer);
                        }
                    });
                    break;
                //连接打印机失败、断开连接，发送通知，刷新界面提示
                case Disconnected:
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            onPrinterDisconnected();
                        }
                    });
                    break;
                default:
                    break;
            }

        }

        // 打印标签的进度发生变化是被调用
        @Override
        public void onPrintProgress(IDzPrinter.PrinterAddress printerAddress, Object o,
                                    IDzPrinter.PrintProgress printProgress, Object o1) {
            switch (printProgress) {
                case Success:
                    // 打印标签成功，发送通知，刷新界面提示
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            onPrintSuccess();
                        }
                    });
                    break;

                case Failed:
                    // 打印标签失败，发送通知，刷新界面提示
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            onPrintFailed();
                        }
                    });
                    break;

                default:
                    break;
            }
        }

        @Override
        public void onPrinterDiscovery(IDzPrinter.PrinterAddress printerAddress,
                                       IDzPrinter.PrinterInfo printerInfo) {

        }
    };


    // 连接打印机成功时操作
    private void onPrinterConnected(IDzPrinter.PrinterAddress printer) {
        clearAlertDialog();
        // 连接打印机成功时，刷新界面提示，保存相关信息
        Toast.makeText(this, this.getResources().getString(R.string.connectprintersuccess),
                Toast.LENGTH_SHORT).show();
        mPrinterAddress = printer;
        // 调用LPAPI对象的getPrinterInfo方法获得当前连接的打印机信息
        String txt = api.getPrinterInfo().deviceName;
        tvPrinterState.setCompoundDrawables(this.getResources().getDrawable(R.drawable.ic_bluetooth_connect,
                null),null,null,null);
        tvPrinterState.setText(txt);
    }
    // 连接打印机操作提交失败、打印机连接失败或连接断开时操作
    private void onPrinterDisconnected() {
        clearAlertDialog();
        // 连接打印机操作提交失败、打印机连接失败或连接断开时，刷新界面提示
        Toast.makeText(this, this.getResources().getString(R.string.connectprinterfailed),
                Toast.LENGTH_SHORT).show();
        tvPrinterState.setCompoundDrawables(this.getResources().getDrawable(R.drawable.ic_bluetooth,
                null),null,null,null);
        tvPrinterState.setText("未连接打印机");
    }
    // 标签打印成功时操作
    private void onPrintSuccess() {
        clearAlertDialog();
        // 标签打印成功时，刷新界面提示
        Toast.makeText(this, this.getResources().getString(R.string.printsuccess), Toast.LENGTH_SHORT).show();
    }
    // 打印请求失败或标签打印失败时操作
    private void onPrintFailed() {
        clearAlertDialog();
        // 打印请求失败或标签打印失败时，刷新界面提示
        Toast.makeText(this, this.getResources().getString(R.string.printfailed), Toast.LENGTH_SHORT).show();
    }
    // 连接打印机请求成功提交时操作
    private void onPrinterConnecting(IDzPrinter.PrinterAddress printer) {
        clearAlertDialog();
        // 连接打印机请求成功提交，刷新界面提示
        String txt = printer.shownName;
        if (TextUtils.isEmpty(txt))
            txt = printer.macAddress;
        txt = getResources().getString(R.string.nowisconnectingprinter) + '[' + txt + ']';
        tvPrinterState.setCompoundDrawables(this.getResources().getDrawable(R.drawable.ic_bluetooth_search,
                null),null,null,null);
        tvPrinterState.setText(txt);
    }
    // 显示连接、打印的状态提示框。使用资源设置提示内容
    private void showStateAlertDialog(int resId) {
        showStateAlertDialog(getResources().getString(resId));
    }
    // 显示连接、打印的状态提示框
    private void showStateAlertDialog(String str) {
        if (stateAlertDialog != null && stateAlertDialog.isShowing()) {
            stateAlertDialog.setTitle(str);
        } else {
            stateAlertDialog = new AlertDialog.Builder(this).setCancelable(false).setTitle(str).show();
        }
    }
    // 清除连接、打印的状态提示框
    private void clearAlertDialog() {
        if (stateAlertDialog != null && stateAlertDialog.isShowing()) {
            stateAlertDialog.dismiss();
        }
        stateAlertDialog = null;
    }
    // 选择打印机的按钮事件
    public void selectPrinterOnClick() {
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            Toast.makeText(this, this.getResources().getString(R.string.unsupportedbluetooth), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!btAdapter.isEnabled()) {
            Toast.makeText(this, this.getResources().getString(R.string.unenablebluetooth), Toast.LENGTH_SHORT).show();
            return;
        }

        pairedPrinters = api.getAllPrinterAddresses(null);
        new AlertDialog.Builder(this).setTitle(R.string.selectbondeddevice).
                setAdapter(new PrintDeviceListAdapter(this,pairedPrinters), new DeviceListItemClicker()).show();
    }
    // 打印机列表的每项点击事件
    private class DeviceListItemClicker implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            IDzPrinter.PrinterAddress printer = pairedPrinters.get(which);
            if (printer != null) {
                // 连接选择的打印机
                if (api.openPrinterByAddress(printer)) {
                    // 连接打印机的请求提交成功，刷新界面提示
                    onPrinterConnecting(printer);
                    return;
                }
            }

            // 连接打印机失败，刷新界面提示
            onPrinterDisconnected();
        }
    }
    /**
     *
     * 界面部分
     */
    @Override
    public String title() {
        return "制作标签";
    }

    @Override
    public Object left() {
        return R.drawable.ic_left_navi;
    }

    @Override
    public Object right() {
        return R.drawable.print_d;
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
                toast("近期推出，敬请期待");
            }
        };
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_making_label_and_move);
        initNaviView();
        ButterKnife.bind(this);
        //初始化打印机
        this.api = LPAPI.Factory.createInstance(mCallback);
        // 尝试连接上次成功连接的打印机
        if (mPrinterAddress != null) {
            if (api.openPrinterByAddress(mPrinterAddress)) {
                // 连接打印机的请求提交成功，刷新界面提示
                onPrinterConnecting(mPrinterAddress);
                return;
            }
        }
        //点击事件：出现打印机列表
        tvPrinterState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPrinterOnClick();
            }
        });
        //初始化适配器
        LinearLayoutManager ll = new LinearLayoutManager(this);
        mRvMakingLabel.setLayoutManager(ll);

        //初始化界面列表头
        mLlItemHeaderStatus.setVisibility(View.INVISIBLE);
        mTvItemHeaderName.setText("资产编号");
        mTvItemHeaderQuantity.setText("名称");

        //初始化列表内容
        Bundle bundle = getBundle();
        flag = bundle.getInt("flag");//标志，为1则是新登记尚未保存的。否则需要从数据库中查询得到
        if (flag == 1) {
            mInfoList = (List<AssetInfo>) bundle.getSerializable("newasset");
            setListAdapter();

        } else {
            mBtnPrintLabelAndMoveAsset.setEnabled(false);//旧资产在这里只打印，不做移交。
            mAssetPicture = (AssetPicture) bundle.getSerializable("picture");
            AssetsUtil.AndQueryAssets(this, "mPicture", mAssetPicture, handler);
        }


    }

    /**
     * 设置列表适配器
     */
    private void setListAdapter() {
        madapter = new MakingLabelsListAdapter(MakingLabelActivity.this, mInfoList);
        mRvMakingLabel.setAdapter(madapter);
        madapter.setSelectedListener(new AssetSelectedListener() {
            @Override
            public void selectAsset(AssetInfo assetInfo) {
                mSelectedList.add(assetInfo);
            }

            @Override
            public void cancelAsset(AssetInfo assetInfo) {
                mSelectedList.remove(assetInfo);
            }
        });
    }

    @OnClick({R.id.btn_print_asset_label, R.id.btn_print_label_and_move_asset})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_print_asset_label:
                if (flag == 1) {//打印完后，保存资产信息
                    //TODO:打印功能
                } else {//否则只打印

                }
                break;
            case R.id.btn_print_label_and_move_asset:
                //如果是新登记的资产打印后要做移交时，要直接传递资产信息，移交后再保存。因为如果先保存
                //再从数据库中取出进行移交，因为网络时差的原因，会产生取出的数据不全的现象。所以要移交
                // 后再做保存。

                break;
        }
    }

    MakingHander handler = new MakingHander();

    class MakingHander extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AssetsUtil.SEARCH_ONE_ASSET:
                    mInfoList = (List<AssetInfo>) msg.getData().getSerializable("assets");
                    setListAdapter();
                    break;
            }
        }
    }

}
