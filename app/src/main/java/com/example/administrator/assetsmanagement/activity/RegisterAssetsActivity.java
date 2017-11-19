package com.example.administrator.assetsmanagement.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.assetsmanagement.Interface.ToolbarClickListener;
import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.base.ParentWithNaviActivity;
import com.example.administrator.assetsmanagement.bean.AssetInfo;
import com.example.administrator.assetsmanagement.bean.AssetPicture;
import com.example.administrator.assetsmanagement.treeUtil.BaseNode;
import com.example.administrator.assetsmanagement.treeUtil.NodeHelper;
import com.example.administrator.assetsmanagement.utils.ImageFactory;
import com.example.administrator.assetsmanagement.utils.LineEditText;
import com.example.administrator.assetsmanagement.utils.TimeUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * 登记资产
 * Created by Administrator on 2017/11/4 0004.
 */

public class RegisterAssetsActivity extends ParentWithNaviActivity {
    public static final int REGISTER_LOCATION = 2;
    public static final int REGISTER_CATEGORY = 3;
    public static final int REGISTER_DEPARTMENT = 4;
    public static final int CHOOSET_PHOTO = 5;
    public static final int TAKE_PHOTO = 6;
    public static final String IMAGE_UNSPECIFIED = "image/*";
    public static final int REQUEST_CROP = 1;

    @BindView(R.id.tv_register_place)
    TextView mTvRegisterLocation;
    @BindView(R.id.tv_register_category)
    TextView mTvRegisterCategory;
    @BindView(R.id.tv_assets_register_name)
    TextView mTvAssetsRegisterName;
    @BindView(R.id.et_register_assets_name)
    EditText mEtRegisterAssetsName;
    @BindView(R.id.btn_register_location)
    FancyButton mBtnRegisterLocation;
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
    FancyButton btnRegisterAddOk;
    @BindView(R.id.btn_register_add_next)
    FancyButton btnRegisterAddNext;
    @BindView(R.id.btn_register_category)
    FancyButton btnRegisterCategory;
    @BindView(R.id.btn_register_department)
    FancyButton mBtnRegisterDepartment;
    @BindView(R.id.tv_register_department)
    TextView mTvRegisterDepartment;
    @BindView(R.id.et_register_assets_date)
    LineEditText mEtRegisterAssetsDate;

    private BaseNode mBaseNode;//临时节点
    private AssetInfo asset;
    private File Imagefile;
    private Uri imageUri;

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
        asset.setmStatus(0);//初始状态，0正常
        asset.setmManagerNum("");//初始管理者，为空
        initEvent();
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
                asset.setmAssetName(s.toString());
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
                asset.setmRegisterDate(s.toString());
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
        mTvRegisterLocation.setTypeface(typeface);
        mTvRegisterDepartment.setTypeface(typeface);
    }

    @OnClick({R.id.btn_register_location, R.id.btn_register_category, R.id.btn_register_add_ok,
            R.id.btn_register_department, R.id.btn_register_add_next, R.id.tv_assets_item_picture_lib,
            R.id.tv_assets_item_camera})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_register_location:
                Intent intent = new Intent(RegisterAssetsActivity.this, SelectedTreeNodeActivity.class);
                intent.putExtra("type", SelectedTreeNodeActivity.SEARCH_LOCATION);
                startActivityForResult(intent, REGISTER_LOCATION);
                break;
            case R.id.btn_register_department:
                Intent intentd = new Intent(RegisterAssetsActivity.this, SelectedTreeNodeActivity.class);
                intentd.putExtra("type", SelectedTreeNodeActivity.SEARCH_DEPARTMENT);
                startActivityForResult(intentd, REGISTER_DEPARTMENT);
                break;
            case R.id.btn_register_category:
                Intent intent1 = new Intent(RegisterAssetsActivity.this, SelectedTreeNodeActivity.class);
                intent1.putExtra("type", SelectedTreeNodeActivity.SEARCH_CATEGORY);
                startActivityForResult(intent1, REGISTER_CATEGORY);
                break;
            case R.id.btn_register_add_ok:
                if (checkAlltext()) {
                    int quantity = Integer.parseInt(mEtRegisterAssetsQuantity.getText().toString());
                    createAssetNumber(quantity, asset);
                    setAllWidget(true);
                }

                break;
            case R.id.btn_register_add_next:
                setAllWidget(false);
                break;
            case R.id.tv_assets_item_picture_lib:
                if (asset.getmCategoryNum() != null) {
                    Intent intentPhoto = new Intent(this, SelectAssetsPhotoActivity.class);
                    intentPhoto.putExtra("category_num", asset.getmCategoryNum());
                    intentPhoto.putExtra("category_name", mTvRegisterCategory.getText());
                    startActivityForResult(intentPhoto, CHOOSET_PHOTO);
                } else {
                    toast("请先选择资产类别！");
                }

                break;
            case R.id.tv_assets_item_camera:
                if (asset.getmCategoryNum() != null) {
                    Imagefile = startCamera();
                } else {
                    toast("请先选择资产类别！");
                }
                break;
        }
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
     * 生成资产编号，一种资产可能有许多个，每一个都有自己的编号。
     */
    private void createAssetNumber(int quantity, AssetInfo asset) {
        String number = String.valueOf(System.currentTimeMillis());
        int m = quantity / 50; //求模
        int s = quantity % 50;//求余数
        int n = 0;
        List<BmobObject> list = new ArrayList<>();
        if (m >= 1) {
            for (int i = 0; i < m; i++) {
                list.clear();
                n++;
                for (int j = 0; j < 50; j++) {
                    try {
                        AssetInfo asi = (AssetInfo) asset.clone();
                        asi.setmAssetsNum(number + "-" + n);
                        list.add(asi);
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                    n++;
                }
                new BmobObject().insertBatch(this, list, new SaveListener() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        toast("批量添加失败:" + msg);
                    }
                });
            }
        } else {
            n++;
        }
        list.clear();
        for (int k = 0; k < s; k++) {
            try {
                AssetInfo asi = (AssetInfo) asset.clone();
                asi.setmAssetsNum(number + "-" + n);
                list.add(asi);
            } catch (CloneNotSupportedException e) {

            }
            n++;
        }
        new BmobObject().insertBatch(this, list, new SaveListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    /**
     * 检查资产信息填写情况
     *
     * @return
     */
    private boolean checkAlltext() {
        if (TextUtils.isEmpty(mTvRegisterLocation.getText())) {
            toast("请选择位置！");
            return false;
        } else if (TextUtils.isEmpty(mTvRegisterDepartment.getText())) {
            toast("请选择部门！");
            return false;
        } else if (TextUtils.isEmpty(mTvRegisterCategory.getText())) {
            toast("请选择资产类别！");
            return false;
        } else if (TextUtils.isEmpty(mEtRegisterAssetsName.getText())) {
            toast("请填入资产名称！");
            return false;
        } else if (TextUtils.isEmpty(mEtRegisterAssetsQuantity.getText())) {
            toast("请填写资产数量！");
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
            mBtnRegisterDepartment.setEnabled(false);
            mBtnRegisterLocation.setEnabled(false);
            mEtRegisterAssetsName.setEnabled(false);
            mEtRegisterAssetsQuantity.setEnabled(false);
            mTvAssetsItemPictureLib.setEnabled(false);
            mTvAssetsItemCamera.setEnabled(false);
        } else {
            btnRegisterAddOk.setEnabled(true);
            btnRegisterAddNext.setEnabled(false);
            btnRegisterCategory.setEnabled(true);
            mBtnRegisterLocation.setEnabled(true);
            mBtnRegisterDepartment.setEnabled(true);
            mEtRegisterAssetsName.setEnabled(true);
            mEtRegisterAssetsQuantity.setEnabled(true);
            mTvAssetsItemPictureLib.setEnabled(true);
            mTvAssetsItemCamera.setEnabled(true);
            mEtRegisterAssetsName.setText("");
            mEtRegisterAssetsQuantity.setText("");
            mIvRegisterPicture.setImageResource(R.drawable.assets_image_default);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REGISTER_LOCATION:
                if (resultCode == SelectedTreeNodeActivity.SEARCH_RESULT_OK) {
                    mBaseNode = (BaseNode) data.getSerializableExtra("node");
                    mTvRegisterLocation.setText(getNodeAllPathName(mBaseNode));
                    //TODO:位置编号赋值给资产对象实例
                    asset.setmLocationNum(mBaseNode.getId());
                }
                break;
            case REGISTER_CATEGORY:
                if (resultCode == SelectedTreeNodeActivity.SEARCH_RESULT_OK) {
                    mBaseNode = (BaseNode) data.getSerializableExtra("node");
                    mTvRegisterCategory.setText(getNodeAllPathName(mBaseNode));
                    //TODO:类别编号赋值给资产对象实例
                    asset.setmCategoryNum(mBaseNode.getId());
                }
                break;
            case REGISTER_DEPARTMENT:
                if (resultCode == SelectedTreeNodeActivity.SEARCH_RESULT_OK) {
                    mBaseNode = (BaseNode) data.getSerializableExtra("node");
                    mTvRegisterDepartment.setText(getNodeAllPathName(mBaseNode));
                    //TODO:部门编号赋值给资产对象实例
                    asset.setmDeptNum(mBaseNode.getId());
                }
                break;
            case CHOOSET_PHOTO:
                if (data != null) {
                    Bundle bundle = data.getBundleExtra("assetpicture");
                    asset.setmPictureNum(bundle.getString("imageNum"));
                    Glide.with(this).load(bundle.getSerializable("imageFile")).centerCrop().into(mIvRegisterPicture);
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
            case REQUEST_CROP:
                break;
        }
    }

    /**
     * 获得节点的全路径名称
     *
     * @param node
     * @return
     */
    private String getNodeAllPathName(BaseNode node) {
        StringBuffer buffer = new StringBuffer();
        List<BaseNode> nodes = new ArrayList<>();
        NodeHelper.getAllParents(nodes, node);
        int i = nodes.size();
        while (i > 0) {
            i--;
            buffer.append(nodes.get(i).getName());
            if (i != 0)
                buffer.append("-");
        }
        return buffer.toString();
    }

    /**
     * 获得节点全路径Id
     *
     * @param node
     * @return
     */
    private String getNodeAllPathId(BaseNode node) {
        StringBuffer buffer = new StringBuffer();
        List<BaseNode> nodes = new ArrayList<>();
        NodeHelper.getAllParents(nodes, node);
        int i = nodes.size();
        while (i > 0) {
            i--;
            buffer.append(nodes.get(i).getId());
            if (i != 0) {
                buffer.append("-");
            }
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
        obj.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                toast("-->上传图片信息成功：");

            }

            @Override
            public void onFailure(int arg0, String arg1) {
                // TODO Auto-generated method stub
                toast("-->上传图片失败：" + arg0 + ",msg = " + arg1);
            }
        });
    }

    /**
     * 裁剪
     */
    private void cropImage(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, REQUEST_CROP);
    }

    /**
     * 上传指定路径下的图片文件
     *
     * @param @param type
     * @param @param i
     * @param @param file
     * @return void
     * @throws
     * @Title: uploadMovoieFile
     * @Description: TODO
     */
    private void uploadPhotoFile(File file) {
        final BmobFile bmobFile = new BmobFile(file);
        bmobFile.uploadblock(this, new UploadFileListener() {
            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                AssetPicture picture = new AssetPicture();
                picture.setCategoryNum(asset.getmCategoryNum());
                picture.setImageNum(System.currentTimeMillis() + "");
                picture.setImageFile(bmobFile);
                asset.setmPictureNum(picture.getImageNum());
                insertObject(picture);

            }

            @Override
            public void onProgress(Integer arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                // TODO Auto-generated method stub

            }

        });

    }
}
