package com.example.administrator.assetsmanagement.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.example.administrator.assetsmanagement.Interface.ToolbarClickListener;
import com.example.administrator.assetsmanagement.R;
import com.example.administrator.assetsmanagement.base.ParentWithNaviActivity;
import com.example.administrator.assetsmanagement.bean.CategoryTree.AssetCategory;
import com.example.administrator.assetsmanagement.bean.CategoryTree.CategoryCheckboxNodeAdapter;
import com.example.administrator.assetsmanagement.bean.CategoryTree.CategoryNodeSelected;
import com.example.administrator.assetsmanagement.bean.DepartmentTree.Department;
import com.example.administrator.assetsmanagement.bean.DepartmentTree.DepartmentCheckboxNodeAdapter;
import com.example.administrator.assetsmanagement.bean.DepartmentTree.DepartmentNodeSelected;
import com.example.administrator.assetsmanagement.bean.LocationTree.Location;
import com.example.administrator.assetsmanagement.bean.LocationTree.LocationCheckboxNodeAdapter;
import com.example.administrator.assetsmanagement.bean.LocationTree.LocationNodeSelected;
import com.example.administrator.assetsmanagement.bean.Person;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * 根据要求从不同的表中获取数据集
 * Created by Administrator on 2017/11/10.
 */

public class SelectedTreeNodeActivity extends ParentWithNaviActivity {
    public static final int SEARCH_LOCATION = 1;
    public static final int SEARCH_DEPARTMENT = 2;
    public static final int SEARCH_MANAGER = 3;
    public static final int SEARCH_CATEGORY = 4;
    public static final int SEARCH_RESULT_OK = 100;
    protected List<BmobObject> treeNodeList = new ArrayList<>();
    List<Location> mLocations = new ArrayList<>();
    List<AssetCategory> mCategorys = new ArrayList<>();
    List<Department> mDepartments = new ArrayList<>();
    @BindView(R.id.lv_tree_structure)
    RecyclerView mLvTreeStructure;
    @BindView(R.id.loading_node_progress)
    ProgressBar loadingNodeProgress;
    private BmobObject mBaseNode;
    private int mPosition;
    //根据不同的类型，选择查询不同的内容
    private int type;
    //标志，因为查询的目的不同，正常0标志，是返回选择节点内容。进一步标志1，则不回返回结果，而是将选
    // 择的结果用于进行下一阶段查询
    private int flag = 0;


    @Override
    public String title() {
        switch (type) {
            case SEARCH_LOCATION:
                return "位置";
            case SEARCH_DEPARTMENT:
                return "部门";
            case SEARCH_MANAGER:
                return "管理员";
            case SEARCH_CATEGORY:
                return "资产类别";
            default:
                break;
        }
        return null;
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
        setContentView(R.layout.tree_structrue_search);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        type = intent.getIntExtra("type", 0);
        flag = intent.getIntExtra("flag", 0);
//        isPerson = intent.getBooleanExtra("person", false);
        initNaviView();
        LinearLayoutManager ll = new LinearLayoutManager(this);
        mLvTreeStructure.setLayoutManager(ll);
        switch (type) {
            case SEARCH_LOCATION:
                getLocationFromBmob();
                break;
            case SEARCH_DEPARTMENT:
                getDepartmentFromBmob();
                break;
            case SEARCH_MANAGER:
                getPersonFromBmob();
                break;
            case SEARCH_CATEGORY:
                getCategoryFromBmob();
                break;
            default:
                break;
        }


    }

    /**
     * 从服务器获取位置数据，并以此设置适配器
     */
    private void getLocationFromBmob() {
        BmobQuery<Location> query = new BmobQuery<>();
        query.setLimit(500);
        query.findObjects(new FindListener<Location>() {
            @Override
            public void done(List<Location> list, BmobException e) {
                if (e == null) {
                    createMessage((Serializable) list);
                }
            }

        });
    }

    /**
     * 从服务器获取部门数据，并以此设置适配器
     */
    private void getDepartmentFromBmob() {
        BmobQuery<Department> query = new BmobQuery<>();
        query.setLimit(500);
        query.findObjects(new FindListener<Department>() {
            @Override
            public void done(List<Department> list, BmobException e) {
                if (e == null) {
                    createMessage((Serializable) list);
                }

            }

        });
    }

    /**
     * 从服务器获取资产类别数据，并以此设置适配器
     */
    private void getCategoryFromBmob() {
        BmobQuery<AssetCategory> query = new BmobQuery<>();
        query.setLimit(500);
        query.findObjects(new FindListener<AssetCategory>() {
            @Override
            public void done(List<AssetCategory> list, BmobException e) {
                if (e == null) {
                    createMessage((Serializable) list);
                }
            }
        });
    }

    /**
     * 从服务器获取人员数据，并以此设置适配器
     */
    private void getPersonFromBmob() {
        BmobQuery<Person> query = new BmobQuery<>();
        query.setLimit(500);
        query.findObjects(new FindListener<Person>() {
            @Override
            public void done(List<Person> list, BmobException e) {
                if (e == null) {
                    createMessage((Serializable) list);
                }
            }
        });
    }


    /**
     * 创建适配器
     *
     * @param list
     */
    private void createAdapter(List<BmobObject> list) {
        treeNodeList = list;
        switch (type) {
            case SEARCH_LOCATION:
                for (BmobObject location : list) {
                    mLocations.add((Location) location);
                }
                LocationCheckboxNodeAdapter ladapter = new LocationCheckboxNodeAdapter(this,
                        mLocations, 0, R.mipmap.expand, R.mipmap.collapse);
                mLvTreeStructure.setAdapter(ladapter);
                ladapter.setCheckBoxSelectedListener(new LocationNodeSelected() {
                    @Override
                    public void checked(Location node, int position) {
                        mBaseNode = node;
                        mPosition = position;
                    }

                    @Override
                    public void cancelCheck(Location node, int position) {
                        mBaseNode = null;
                        mPosition = 0;
                    }
                });
                break;
            case SEARCH_DEPARTMENT:
                for (BmobObject dept : list) {
                    mDepartments.add((Department) dept);
                }
                DepartmentCheckboxNodeAdapter dadapter = new DepartmentCheckboxNodeAdapter(this,
                        mDepartments, 0, R.mipmap.expand, R.mipmap.collapse);
                mLvTreeStructure.setAdapter(dadapter);
                dadapter.setCheckBoxSelectedListener(new DepartmentNodeSelected() {
                    @Override
                    public void checked(Department node, int position) {
                        mBaseNode = node;
                        mPosition = position;
                    }

                    @Override
                    public void cancelCheck(Department node, int position) {
                        mBaseNode = null;
                        mPosition = 0;
                    }
                });
                break;
            case SEARCH_MANAGER:
                getPersonFromBmob();
                break;
            case SEARCH_CATEGORY:
                for (BmobObject category : list) {
                    mCategorys.add((AssetCategory) category);
                }
                CategoryCheckboxNodeAdapter cadapter = new CategoryCheckboxNodeAdapter(this,
                        mCategorys, 0, R.mipmap.expand, R.mipmap.collapse);
                mLvTreeStructure.setAdapter(cadapter);
                cadapter.setCheckBoxSelectedListener(new CategoryNodeSelected() {
                    @Override
                    public void checked(AssetCategory node, int position) {
                        mBaseNode = node;
                        mPosition = position;
                    }

                    @Override
                    public void cancelCheck(AssetCategory node, int position) {
                        mBaseNode = null;
                        mPosition = 0;
                    }
                });
                break;
            default:
                break;
        }
        loadingNodeProgress.setVisibility(View.GONE);
    }

    /**
     * 生成异步传递信息
     *
     * @param list
     */
    private void createMessage(Serializable list) {
        Message msg = new Message();
        msg.what = 0;
        Bundle bundle = new Bundle();
        bundle.putSerializable("list", list);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    /**
     * 异步处理
     */
    public myHander handler = new myHander();

    class myHander extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    List<BmobObject> list = (List<BmobObject>) msg.getData().getSerializable("list");
                    createAdapter(list);
                    break;
            }

        }
    }

    @OnClick(R.id.btn_tree_search_node_ok)
    public void onViewClicked() {
        if (mBaseNode != null) {
            switch (flag) {
                case 0://返回选择的结果
                    sendNodeInfo(mBaseNode);
                    finish();
                    break;
                case 1://利用选择结果，继续查询
                    Intent intent = new Intent(this,SelectAssetsPhotoActivity.class);
                    intent.putExtra("isRegister", true);
                    intent.putExtra("category", mBaseNode);
                    intent.putExtra("category_name", ((AssetCategory) mBaseNode).getCategoryName());
                    startActivity(intent);
                    finish();
                    break;
            }
        } else {
            toast("请选择要查询的内容！");
        }

    }

    /**
     * 返回选择节点
     *
     * @param node
     */
    private void sendNodeInfo(BmobObject node) {
        Intent intent = new Intent();
        intent.putExtra("node", node);
        setResult(SEARCH_RESULT_OK, intent);
    }

}
