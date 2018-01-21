package com.example.administrator.assetsmanagement.Interface;

/**
 * 人员列表中，导航栏被按下事件监听接口
 * Created by Administrator on 2018/1/21 0021.
 */

public interface onIndexPressedListener {
    void onIndexPressed(int index, String text);

    void onMotionEventEnd();
}
