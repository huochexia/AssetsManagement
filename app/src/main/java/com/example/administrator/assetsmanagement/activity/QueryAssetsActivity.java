package com.example.administrator.assetsmanagement.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.assetsmanagement.Interface.ToolbarClickListener;
import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.base.ParentWithNaviActivity;
import com.example.administrator.assetsmanagement.bean.CategoryTree.AssetCategory;
import com.example.administrator.assetsmanagement.bean.CategoryTree.CategoryNodeHelper;
import com.example.administrator.assetsmanagement.bean.DepartmentTree.Department;
import com.example.administrator.assetsmanagement.bean.DepartmentTree.DepartmentNodeHelper;
import com.example.administrator.assetsmanagement.bean.LocationTree.Location;
import com.example.administrator.assetsmanagement.bean.LocationTree.LocationNodeHelper;
import com.example.administrator.assetsmanagement.bean.Person;
import com.example.administrator.assetsmanagement.utils.FlowRadioGroup;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobObject;

/**
 * 查询资产：用户通过指定查询条件，如位置，属性，类别，管理人员，图片等属性对资产进行查询
 * Created by Administrator on 2018/1/12 0012.
 */

public class QueryAssetsActivity extends ParentWithNaviActivity {
    public static final int ASSET_LOCATION = 1;
    public static final int ASSET_DEPARTMENT = 2;
    public static final int ASSET_CATEGORY = 3;
    public static final int ASSET_MANAGER = 4;
    public static final int ASSET_PICTURE = 5;
    public static final int ASSET_NOT_NORMAL = 6;
    public static final int REQUEST_SELECTE_MANAGER = 111;

    @BindView(R.id.rg_query_condition)
    FlowRadioGroup mRgQueryCondition;
    @BindView(R.id.tv_query_content)
    TextView mTvQueryContent;

    private int mCondition = 0;
    private BmobObject mBmobObject;
    private Location mLocation;
    private AssetCategory mCategory;
    private Department mDepartment;
    private Person person;


    @Override
    public String title() {
        return "查询资产";
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
        setContentView(R.layout.activity_query_assets);
        ButterKnife.bind(this);
        initNaviView();
        initRGEvent();
    }

    /**
     * 单选按钮事件
     */
    private void initRGEvent() {
        mRgQueryCondition.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_location_s:
                        mCondition = ASSET_LOCATION;
                        mTvQueryContent.setText("");
                        break;
                    case R.id.rb_department_s:
                        mCondition = ASSET_DEPARTMENT;
                        mTvQueryContent.setText("");
                        break;
                    case R.id.rb_category_s:
                        mCondition = ASSET_CATEGORY;
                        mTvQueryContent.setText("");
                        break;
                    case R.id.rb_manager_s:
                        mCondition = ASSET_MANAGER;
                        mTvQueryContent.setText("");
                        break;
                    case R.id.rb_picture_s:
                        mCondition = ASSET_PICTURE;
                        mTvQueryContent.setText("");
                        break;
                    case R.id.rb_dipose_s:
                        mCondition = ASSET_NOT_NORMAL;
                        mTvQueryContent.setText("");
                        break;
                }
            }
        });
    }

    @OnClick({R.id.btn_select_content, R.id.btn_query_assets})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_select_content:
                switch (mCondition) {
                    case ASSET_LOCATION:
                        startActivity(SelectedTreeNodeActivity.SEARCH_LOCATION, false);
                        break;
                    case ASSET_DEPARTMENT:
                        startActivity(SelectedTreeNodeActivity.SEARCH_DEPARTMENT, false);
                        break;
                    case ASSET_CATEGORY:
                        startActivity(SelectedTreeNodeActivity.SEARCH_CATEGORY, false);
                        break;
                    case ASSET_MANAGER:
                        Intent intent = new Intent(QueryAssetsActivity.this, ManagerListActivity.class);
                        startActivityForResult(intent, REQUEST_SELECTE_MANAGER);
                        break;
                    case ASSET_PICTURE:
                        //TODO:
                        Intent intent1 = new Intent(this, SelectedTreeNodeActivity.class);
                        intent1.putExtra("type",SelectedTreeNodeActivity.SEARCH_CATEGORY);
                        intent1.putExtra("flag", 1);
                        startActivity(intent1);
                        break;
                    case ASSET_NOT_NORMAL:
                        //TODO:
                        toast("待开发中...");
                        break;
                    default:
                        toast("请选择查询条件！");
                        break;
                }
              break;
            case R.id.btn_query_assets:

                Bundle bundle = new Bundle();
                bundle.putInt("condition",mCondition);
                switch (mCondition) {
                    case ASSET_LOCATION:
                        bundle.putSerializable("content",mLocation);
                        break;
                    case ASSET_DEPARTMENT:
                        bundle.putSerializable("content",mDepartment);
                        break;
                    case ASSET_CATEGORY:
                        bundle.putSerializable("content",mCategory);
                        break;
                    case ASSET_MANAGER:
                        bundle.putSerializable("content",person);
                        break;
                }
                startActivity(AssetsListActivity.class,bundle,false);
                break;
        }
    }

    /**
     * 启动相应的查询条件活动，获取具体内容
     *
     * @param type
     * @param isPerson
     */
    private void startActivity(int type, boolean isPerson) {
        Intent intent = new Intent(QueryAssetsActivity.this, SelectedTreeNodeActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("person", isPerson);
        startActivityForResult(intent, type);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            switch (requestCode) {
                case SelectedTreeNodeActivity.SEARCH_LOCATION:
                    if (resultCode == SelectedTreeNodeActivity.SEARCH_RESULT_OK) {
                        mLocation = (Location) data.getSerializableExtra("node");
                        mTvQueryContent.setText(LocationNodeHelper.getSearchContentName(mLocation));
                    }
                    break;
                case SelectedTreeNodeActivity.SEARCH_CATEGORY:
                    if (resultCode == SelectedTreeNodeActivity.SEARCH_RESULT_OK) {
                        mCategory = (AssetCategory) data.getSerializableExtra("node");
                        mTvQueryContent.setText(CategoryNodeHelper.getSearchContentName(mCategory));
                    }
                    break;
                case SelectedTreeNodeActivity.SEARCH_DEPARTMENT:
                    if (resultCode == SelectedTreeNodeActivity.SEARCH_RESULT_OK) {
                        mDepartment = (Department) data.getSerializableExtra("node");
                        mTvQueryContent.setText(DepartmentNodeHelper.getSearchContentName(mDepartment));
                    }
                    break;
                case REQUEST_SELECTE_MANAGER:
                    if (resultCode == ManagerListActivity.SEARCH_OK) {
                        person = (Person) data.getSerializableExtra("manager");
                        mTvQueryContent.setText(person.getUsername());
                    }
                    break;

                default:
                    IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                    if (intentResult != null) {
                        if (intentResult.getContents() == null) {
                            Toast.makeText(this, "内容为空", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "扫描成功", Toast.LENGTH_LONG).show();
                            // ScanResult 为 获取到的字符串
                            String ScanResult = intentResult.getContents();
                            mTvQueryContent.setText("资产编号" + ScanResult);

                        }
                    } else {
                        super.onActivityResult(requestCode, resultCode, data);
                    }
            }
        }

    }
}
