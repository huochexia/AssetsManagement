package com.example.administrator.assetsmanagement.bean;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * 因为Bmob对用户的修改需要登录，所以只能增加这个类，用于保存用户的权限。四个角色：1、登记员，可以进
 * 行资产登记；2、管理员，可以对所有资产进行查询；3、系统管理员，进行基础设置；4、审批员，可以对本部门
 * 资产报废和处置进行审批。
 * Created by Administrator on 2017/12/26.
 */

public class Role extends BmobObject{

    private Person user;
    private List<String> rights ;

    public Role() {
    }

    public Person getUser() {
        return user;
    }

    public void setUser(Person user) {
        this.user = user;
    }

    public List<String> getRights() {
        return rights;
    }

    public void setRights(List<String> rights) {
        this.rights = rights;
    }
}
