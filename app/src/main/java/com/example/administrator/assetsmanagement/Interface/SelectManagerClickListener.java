package com.example.administrator.assetsmanagement.Interface;

import com.example.administrator.assetsmanagement.bean.Manager.Person;

/**
 * Created by Administrator on 2017/12/14 0014.
 */

public interface SelectManagerClickListener {
    void select(Person person);

    void cancelSelect();
}
