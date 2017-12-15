package com.example.administrator.assetsmanagement.utils;

import android.content.Context;
import android.widget.Toast;

import com.example.administrator.assetsmanagement.bean.AssetInfo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2017/12/10 0010.
 */

public class AssetsUtil {
    /**
     * 将资产列表中同种的资产合并为一项，并计算同种资产的数量
     *
     * @param list
     */
    public static List<AssetInfo> mergeAndSum(List<AssetInfo> list) {
        Map<String, AssetInfo> map = new HashMap();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            AssetInfo asset = (AssetInfo) it.next();
            String key = asset.getPicture().getImageNum();
            if (map.get(key) == null) {
                map.put(key, asset);
            } else {
                AssetInfo asset1 = map.get(key);
                asset1.setQuantity(asset1.getQuantity() + 1);
                map.put(key, asset1);
            }

        }
        list.clear();//最后清空后遍历map，存入List中
        for (Object obj : map.keySet()) {
            list.add(map.get(obj));
        }
        return list;
    }

    /**
     * 改变单一资产的状态
     *
     * @param asset
     * @param status
     */
    public static void changeAssetStatus(final Context context, AssetInfo asset, int status) {
        asset.setStatus(status);
        asset.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Toast.makeText(context, "工作完成！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "网络出现异常，稍后再操作！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * List深复制
     */
    public static <T> List<T> deepCopy(List<T> src) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(byteOut);
            out.writeObject(src);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(byteIn);
        } catch (IOException e) {
            e.printStackTrace();
        }
        @SuppressWarnings("unchecked")
        List<T> dest = null;
        try {
            dest = (List<T>) in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return dest;
    }
}
