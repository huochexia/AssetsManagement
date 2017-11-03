package com.example.administrator.assetsmanagement.base;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.assetsmanagement.R;

/**
 * Created by Administrator on 2017/11/3.
 */

public abstract class ParentWithNaviActivity extends BaseActivity {
    /**
     * 点击事件接口
     */
    public interface ToolbarListener {
        void clickLeft();

        void clickRight();
    }
    private ToolbarListener listener;
    private TextView title;
    private ImageView left;
    private ImageView right;

    /**
     * 必须实现的方法，设置标题内容
     * @return
     */
    public abstract String title();

    /**
     * 当需要在标题栏上显示左图标是实现该方法，返回具体图标
     * @return
     */
    public Object left() {
        return null;
    }
    /**
     * 当需要在标题栏上显示右图标是实现该方法，返回具体图标
     * @return
     */
    public Object right() {
        return null;
    }

    /**
     * 实现左右图标的点击事件时，需要改写些方法，返回实现具体处理方法的事件接口
     * @return
     */
    public ToolbarListener getToolbarListener() {
        return null;
    }

    protected <T extends View> T getView(int id) {
        return  (T) findViewById(id);
    }

    private void setToolbarListener(ToolbarListener listener) {
        this.listener = listener;
    }
    protected View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_left_navi:
                    if (listener != null)
                    listener.clickLeft();
                    break;
                case R.id.iv_right_navi:
                    if (listener != null)
                    listener.clickRight();
                    break;
            }
        }
    };
    public void initNaviView() {
        title = getView(R.id.tv_toolbar_title);
        left = getView(R.id.iv_left_navi);
        right = getView(R.id.iv_right_navi);
        setToolbarListener(getToolbarListener());
        left.setOnClickListener(clickListener);
        right.setOnClickListener(clickListener);
        title.setText(title());
        refreshTop();
    }

    protected void refreshTop() {
        setLeftView(left());
        setRightView(right());
        title.setText(title());
    }

    /**
     *设置左图标
     * @param object
     */
    private void setLeftView(Object object) {
        if (object != null && !object.equals("")) {
            left.setVisibility(View.VISIBLE);
            if (object instanceof Integer) {
                left.setImageResource(Integer.parseInt(object.toString()));
            } else {
                //默认值
                left.setImageResource(R.drawable.ic_left_navi);
            }
        } else {
            left.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 设置右图标
     * @param object
     */
    private void setRightView(Object object) {
        if (object != null && !object.equals("")) {
            right.setVisibility(View.VISIBLE);
            if (object instanceof Integer) {
                right.setImageResource(Integer.parseInt(object.toString()));
            } else {
                right.setImageResource(R.drawable.ic_search_db);
            }
        } else {
            right.setVisibility(View.INVISIBLE);
        }
    }
    /**
     * 获取当前用户
     */
//    public String getCurrentUid(){
//        return BmobUser.getCurrentUser(User.class).getObjectId();
//    }
}
