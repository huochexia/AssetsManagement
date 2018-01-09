package com.example.administrator.assetsmanagement.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.administrator.assetsmanagement.Interface.ToolbarClickListener;
import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.base.ParentWithNaviActivity;
import com.example.administrator.assetsmanagement.bean.AssetInfo;
import com.example.administrator.assetsmanagement.bean.AssetPicture;
import com.example.administrator.assetsmanagement.bean.CategoryTree.AssetCategory;
import com.example.administrator.assetsmanagement.bean.CategoryTree.CategoryNodeHelper;
import com.example.administrator.assetsmanagement.bean.Person;
import com.example.administrator.assetsmanagement.utils.ImageFactory;
import com.example.administrator.assetsmanagement.utils.LineEditText;
import com.example.administrator.assetsmanagement.utils.TimeUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * 登记资产：对原有资产和新购资产进行基本信息登记，资产图片的分两种方式，一是从现有图库中进行选择，
 * 二是现场拍照。资产编号根据登记时的系统时间自动产生，同样资产的编号，在自动产生的编号基础上依据
 * 其数量增加序号，做到一资产一编号。登记确定后，需要进行两种选择，一种是制作标签并移交，一种是只移
 * 交，将来再制作标签。
 * <p>
 * Created by Administrator on 2017/11/4 0004.
 */

public class RegisterAssetsActivity extends ParentWithNaviActivity {
    public static final int REGISTER_CATEGORY = 3;
    public static final int CHOOSET_PHOTO = 5;
    public static final int TAKE_PHOTO = 6;

    @BindView(R.id.tv_register_category)
    TextView mTvRegisterCategory;
    @BindView(R.id.tv_assets_register_name)
    TextView mTvAssetsRegisterName;
    @BindView(R.id.et_register_assets_name)
    EditText mEtRegisterAssetsName;

    @BindView(R.id.iv_register_picture)
    ImageView mIvRegisterPicture;
    @BindView(R.id.tv_assets_item_quantity)
    TextView mTvAssetsItemQuantity;
    @BindView(R.id.et_register_assets_quantity)
    EditText mEtRegisterAssetsQuantity;
    @BindView(R.id.tv_assets_item_picture_lib)
    TextView mTvAssetsItemPictureLib;
    @BindView(R.id.tv_assets_item_camera)
    TextView mTvAssetsItemCamera;

    @BindView(R.id.btn_register_add_ok)
    Button btnRegisterAddOk;
    @BindView(R.id.btn_register_add_next)
    Button btnRegisterAddNext;
    @BindView(R.id.btn_register_category)
    FancyButton btnRegisterCategory;
    @BindView(R.id.et_register_assets_date)
    LineEditText mEtRegisterAssetsDate;
    @BindView(R.id.tv_assets_register_date)
    TextView tvAssetsRegisterDate;
    @BindView(R.id.tv_assets_register_comment)
    TextView tvAssetsRegisterComment;
    @BindView(R.id.et_register_assets_comment)
    LineEditText etRegisterAssetsComment;
    @BindView(R.id.tv_assets_price)
    TextView mTvAssetsPrice;
    @BindView(R.id.et_register_asset_price)
    LineEditText mEtRegisterAssetPrice;

    private AssetCategory mCategory;//临时节点
    private AssetInfo asset;
    private File Imagefile;
    private Uri imageUri;
    private String assetNumber;
    private List<AssetInfo> mAssetInfos = new ArrayList<>();
    private boolean hasPhoto = false;//判断是否添加图片

    @Override
    public String title() {
        return "登记资产";
    }

    @Override
    public Object left() {
        return R.drawable.ic_left_navi;
    }

    /**
     * 实现点击事件处理方法
     *
     * @return
     */
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
        setContentView(R.layout.activity_assets_register);
        ButterKnife.bind(this);
        initNaviView();
        btnRegisterAddNext.setEnabled(false);
        setTextFonts();
        asset = new AssetInfo();
        asset.setStatus(9);//初始状态，9新登记
        asset.setOldManager(BmobUser.getCurrentUser(Person.class));//初始管理者，为空或登记人即当前用户
        initEvent();
        mEtRegisterAssetsQuantity.setText("");
        mEtRegisterAssetsDate.setText(TimeUtils.getFormatToday(TimeUtils.FORMAT_DATE));
    }


    public void initEvent() {
        //赋值资产名称
        mEtRegisterAssetsName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                asset.setAssetName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mEtRegisterAssetPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                asset.setPrice(Float.valueOf(s.toString().trim()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //赋值登记日期
        mEtRegisterAssetsDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                asset.setRegisterDate(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //备注
        etRegisterAssetsComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                asset.setComment(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * 设置所有文本的字体
     */
    private void setTextFonts() {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/隶书.ttf");
        mTvAssetsItemQuantity.setTypeface(typeface);
        mTvAssetsRegisterName.setTypeface(typeface);
        mTvRegisterCategory.setTypeface(typeface);
        mEtRegisterAssetsName.setTypeface(typeface);
        mEtRegisterAssetsQuantity.setTypeface(typeface);
        mTvAssetsItemPictureLib.setTypeface(typeface);
        mTvAssetsItemCamera.setTypeface(typeface);
        tvAssetsRegisterDate.setTypeface(typeface);
        tvAssetsRegisterComment.setTypeface(typeface);
        mTvAssetsPrice.setTypeface(typeface);
        etRegisterAssetsComment.setTypeface(typeface);
    }

    @OnClick({R.id.btn_register_category, R.id.btn_register_add_ok,
            R.id.btn_register_add_next, R.id.tv_assets_item_picture_lib, R.id.tv_assets_item_camera})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_register_category:
                Intent intent1 = new Intent(RegisterAssetsActivity.this, SelectedTreeNodeActivity.class);
                intent1.putExtra("type", SelectedTreeNodeActivity.SEARCH_CATEGORY);
                startActivityForResult(intent1, REGISTER_CATEGORY);
                break;
            case R.id.btn_register_add_ok:
                if (checkAlltext()) {
                    int quantity = Integer.parseInt(mEtRegisterAssetsQuantity.getText().toString());
                    createAssetNumber(quantity, asset);
                    mIvRegisterPicture.setImageResource(R.drawable.pictures_no);
                    hasPhoto = false;
                    setAllWidget(true);
                    startSelectDialog(mAssetInfos);
                    asset.setPicture(null);
                }
                break;
            case R.id.btn_register_add_next:
                setAllWidget(false);
                mAssetInfos.clear();
                break;
            case R.id.tv_assets_item_picture_lib:
                if (asset.getCategory() != null) {
                    Intent intentPhoto = new Intent(this, SelectAssetsPhotoActivity.class);
                    intentPhoto.putExtra("category", asset.getCategory());
                    intentPhoto.putExtra("category_name", mTvRegisterCategory.getText());
                    intentPhoto.putExtra("isRegister", true);
                    startActivityForResult(intentPhoto, CHOOSET_PHOTO);
                } else {
                    toast("请先选择资产类别！");
                }

                break;
            case R.id.tv_assets_item_camera:
                if (asset.getCategory() != null) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
                        if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 222);
                            return;
                        } else {
                            Imagefile = startCamera();
                        }
                    } else {
                        Imagefile = startCamera();
                    }

                } else {
                    toast("请先选择资产类别！");
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 222:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Imagefile = startCamera();
                } else {
                    Toast.makeText(this, "很遗憾你把相机权限禁用了。", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case REGISTER_CATEGORY:
                if (resultCode == SelectedTreeNodeActivity.SEARCH_RESULT_OK) {
                    mCategory = (AssetCategory) data.getSerializableExtra("node");
                    mTvRegisterCategory.setText(getNodeAllPathName(mCategory));
                    AssetCategory ac = new AssetCategory();
                    ac.setObjectId(mCategory.getObjectId());
                    asset.setCategory(ac);
                }
                break;

            case CHOOSET_PHOTO:
                if (data != null) {
                    Bundle bundle = data.getBundleExtra("assetpicture");
                    AssetPicture image1 = (AssetPicture) bundle.getSerializable("imageFile");
                    asset.setPicture(image1);
                    hasPhoto = true;
                    Glide.with(this).load(image1.getImageUrl()).into(mIvRegisterPicture);
                }
                break;
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        //第一步：将拍照得到原始图片存入文件
                        BitmapFactory.decodeStream(getContentResolver().
                                openInputStream(imageUri));
                        //第二步：将文件进行压缩处理后得到新的Bitmap
                        Bitmap bm = ImageFactory.getSmallBitmap(Imagefile.getPath());
                        //第三步：创建文件输入流
                        FileOutputStream baos = new FileOutputStream(Imagefile);
                        //第四步：将位图以JPG格式，按100比例，再次压缩形成新文件
                        bm.compress(Bitmap.CompressFormat.JPEG, 90, baos);
                        baos.flush();
                        baos.close();
                        mIvRegisterPicture.setImageBitmap(bm);
                        uploadPhotoFile(Imagefile);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                break;

        }
    }

    /**
     * 列表对话框，选择是制作标签还是直接进行移交
     */
    private void startSelectDialog(final List<AssetInfo> list) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] items = new String[]{"制作标签", "直接移交"};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        dialog.dismiss();
                        startMakingLabel(list);
                        break;
                    case 1:
                        dialog.dismiss();
                        startTurnOverAsset(list);
                        break;
                }

            }
        });
        builder.setTitle("请选择下一步操作");
        builder.setNegativeButton("取消登记", null);
        builder.show();

    }

    /**
     * 直接进行移交，将来再制作标签。
     *
     * @param list
     */
    private void startTurnOverAsset(List<AssetInfo> list) {
        Bundle bundle = new Bundle();
        bundle.putInt("flag", 1);
        bundle.putSerializable("newasset", (Serializable) list);
        startActivity(AssetsTurnOverActivity.class, bundle, false);
    }

    /**
     * 仅制作标签或者制作标签和进行移交
     *
     * @param list
     */
    private void startMakingLabel(List<AssetInfo> list) {
        Bundle bundle = new Bundle();
        bundle.putInt("flag", 1);
        bundle.putSerializable("newasset", (Serializable) list);
        startActivity(MakingLabelActivity.class, bundle, false);
    }

    /**
     * 启动照相
     */
    private File startCamera() {
        File outputImage = new File(getExternalCacheDir(), System.currentTimeMillis() + ".jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(RegisterAssetsActivity.this,
                    "com.example.administrator.assetsmanagement", outputImage);
        } else {
            imageUri = Uri.fromFile(outputImage);
        }
        //启动相机
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, TAKE_PHOTO);
        return outputImage;

    }

    /**
     * 生成资产编号并保存资产列表，一种资产可能有许多个，每一个都有自己的编号。
     */
    private void createAssetNumber(int quantity, AssetInfo asset) {
        assetNumber = String.valueOf(System.currentTimeMillis());
        int m = quantity / 50; //求模
        int s = quantity % 50;//求余数
        int n = 0;
        if (m >= 1) {
            for (int i = 0; i < m; i++) {
                n++;
                for (int j = 0; j < 50; j++) {
                    try {
                        //克隆一份资产，然后赋值编号成为一个资产。
                        AssetInfo asi = (AssetInfo) asset.clone();
                        asi.setAssetsNum(assetNumber + "-" + n);
                        mAssetInfos.add(asi);
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                    n++;
                }
            }
        } else {
            n++;
        }
        for (int k = 0; k < s; k++) {
            try {
                AssetInfo asi = (AssetInfo) asset.clone();
                asi.setAssetsNum(assetNumber + "-" + n);
                mAssetInfos.add(asi);
            } catch (CloneNotSupportedException e) {

            }
            n++;
        }

    }

    /**
     * 检查资产信息填写情况
     *
     * @return
     */
    private boolean checkAlltext() {

        if (!TextUtils.isEmpty(mEtRegisterAssetsQuantity.getText())) {
            int quantity = Integer.parseInt(mEtRegisterAssetsQuantity.getText().toString());
            if (quantity == 0) {
                toast("请填写资产数量！");
                return false;
            }
        }
        if (TextUtils.isEmpty(mEtRegisterAssetsQuantity.getText())) {
            toast("请填写资产数量！");
            return false;
        }

        if (TextUtils.isEmpty(mTvRegisterCategory.getText())) {
            toast("请选择资产类别！");
            return false;
        }
        if (TextUtils.isEmpty(mEtRegisterAssetsName.getText())) {
            toast("请填入资产名称！");
            return false;
        }
        if (!hasPhoto) {
            toast("请添加图片");
            return false;
        }
        return true;
    }

    /**
     * 设置所有控件状态
     *
     * @param i
     */
    private void setAllWidget(boolean i) {
        if (i) {
            btnRegisterAddOk.setEnabled(false);
            btnRegisterAddNext.setEnabled(true);
            btnRegisterCategory.setEnabled(false);
            mEtRegisterAssetsName.setEnabled(false);
            mEtRegisterAssetsQuantity.setEnabled(false);
            mTvAssetsItemPictureLib.setEnabled(false);
            mTvAssetsItemCamera.setEnabled(false);
            mEtRegisterAssetPrice.setText("0.0");
        } else {
            btnRegisterAddOk.setEnabled(true);
            btnRegisterAddNext.setEnabled(false);
            btnRegisterCategory.setEnabled(true);
            mEtRegisterAssetsName.setEnabled(true);
            mEtRegisterAssetsQuantity.setEnabled(true);
            mTvAssetsItemPictureLib.setEnabled(true);
            mTvAssetsItemCamera.setEnabled(true);
            mEtRegisterAssetsName.setText("");
            mEtRegisterAssetsQuantity.setText("");
            etRegisterAssetsComment.setText("");
            mIvRegisterPicture.setImageResource(R.drawable.pictures_no);
        }
    }


    /**
     * 获得节点的全路径名称
     *
     * @param node
     * @return
     */
    private String getNodeAllPathName(AssetCategory node) {
        StringBuffer buffer = new StringBuffer();
        List<AssetCategory> nodes = new ArrayList<>();
        CategoryNodeHelper.getAllParents(nodes, node);
        int i = nodes.size();
        while (i > 0) {
            i--;
            buffer.append(nodes.get(i).getCategoryName());
            if (i != 0)
                buffer.append("-");
        }
        return buffer.toString();
    }


    /**
     * 上传图片信息对象操作
     * insertObject
     *
     * @return void
     * @throws
     */
    private void insertObject(final BmobObject obj) {
        obj.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    toast("-->上传图片信息成功：");
                } else {
                    toast("-->上传图片失败：");
                }
            }
        });
    }


    /**
     * 上传指定路径下的图片文件
     */
    private void uploadPhotoFile(File file) {
        final BmobFile bmobFile = new BmobFile(file);
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void onProgress(Integer arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void done(BmobException e) {
                if (e == null) {
                    AssetPicture picture = new AssetPicture();
                    picture.setCategory(asset.getCategory());
                    String imangNum = System.currentTimeMillis() + "";
                    picture.setImageNum(imangNum);
                    picture.setImageUrl(bmobFile.getFileUrl());
                    asset.setPicture(picture);
                    hasPhoto = true;
                    insertObject(picture);
                } else {
                    toast("加载图片失败！");
                }

            }

        });

    }


}
