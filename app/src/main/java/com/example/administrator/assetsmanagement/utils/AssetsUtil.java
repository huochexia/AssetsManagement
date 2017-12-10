package com.example.administrator.assetsmanagement.utils;

import com.example.administrator.assetsmanagement.bean.AssetInfo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/12/10 0010.
 */

public class AssetsUtil {
    /**
     * 将资产列表中同种的资产合并为一项，并计算同种资产的数量
     * @param list
     */
    public static List<AssetInfo> mergeAndSum(List<AssetInfo> list) {
        Map<String,AssetInfo> map = new HashMap();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            AssetInfo asset = (AssetInfo) it.next();
            String key = asset.getPicture();
            if (map.get(key) == null) {
                map.put(key, asset);
            } else {
                AssetInfo asset1 =  map.get(key);
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
}
