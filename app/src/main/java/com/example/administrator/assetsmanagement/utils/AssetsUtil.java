package com.example.administrator.assetsmanagement.utils;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.administrator.assetsmanagement.bean.AssetInfo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2017/12/10 0010.
 */

public class AssetsUtil {
    public static final int SEARCH_ONE_ASSET = 1;
    public static int count = 0;//静态计数器，用于记录第几个500条数据

    /**
     * 分组后合并：因为同种资产的状态不一定一样，所以将资产列表中不同状态下同种资产合并为一项，
     * 并计算的数量
     *
     * @param list
     */
    public static List<AssetInfo> GroupAfterMerge(List<AssetInfo> list) {
        List<AssetInfo> result = new ArrayList<>();
        Map<Integer, List<AssetInfo>> groupedList = GroupAsset(list);
        for (Object object : groupedList.keySet()) {
            result.addAll(mergeAsset(groupedList.get(object)));
        }
        return result;
    }

    /**
     * 合并同类项
     * @param list
     * @return
     */
    @NonNull
    public static List<AssetInfo> mergeAsset(List<AssetInfo> list) {
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
     * AssetInfo按状态进行分组
     */
    public static Map<Integer,List<AssetInfo>> GroupAsset(List<AssetInfo> assets) {
        AssetInfo asset;
        Map<Integer, List<AssetInfo>> resultList = new HashMap<>();
        for(int i=0;i<assets.size();i++) {
            asset =assets.get(i);
            if (resultList.containsKey(asset.getStatus())) {
                resultList.get(asset.getStatus()).add(asset);
            } else {
                List<AssetInfo> list = new ArrayList<>();
                list.add(asset);
                resultList.put(asset.getStatus(), list);
            }
        }
        return resultList;
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
        ObjectOutputStream out;
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
            dest = (List<T>) (in != null ? in.readObject() : null);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return dest;
    }
    /**
     * 依据某一个参数，查询资产。因为Bmob查询一次最大数据量为500，所以采用了分页查询的原理，利用了递归
     * 调用方法。
     * 调用这个函数时，要先把静态变量清0
     * @param
     */
    public static void AndQueryAssets(final Context context, final String para, final Object value, final Handler handler
            , final List<AssetInfo> allList) {
        BmobQuery<AssetInfo> query = new BmobQuery<>();
        query.addWhereEqualTo(para, value);
        query.order("mLocation,mAssetsNum"); //排序
        query.setSkip(count*500);//跳过count次500条记录
        query.setLimit(500);
        query.include("mPicture,mOldManager,mLocation,mDepartment");
        query.findObjects(new FindListener<AssetInfo>() {
            @Override
            public void done(final List<AssetInfo> list, BmobException e) {
                if (e == null) {
//                    if (list == null || list.size() == 0) {//当查询条数为500的整数倍时，会出现这个
//                        Toast.makeText(context, "查询结束!", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
                    allList.addAll(list); //暂存入临时列表中，只有前500条
                    if (list.size() >= 500) {//因为可以查询出全部数据，但是一次只能上传500条
                        count++;//计数器加1，
                        AndQueryAssets(context, para, value, handler, allList);
                    } else {//当于500条，说明一次可以全部获取，结束递归
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Message msg = new Message();
                                msg.what = AssetsUtil.SEARCH_ONE_ASSET;
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("assets", (Serializable) allList);
                                msg.setData(bundle);
                                handler.sendMessage(msg);
                            }
                        }).start();
                    }

                } else {
                    {
                        Toast.makeText(context, "查询失败，请稍后再查！", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    /**
     * 依据某两个参数组合，查询资产
     *
     * @param
     */
    public static void AndQueryAssets(final Context context, final String para1, final Object value1,
                                      final String para2, final Object value2, final Handler handler,
                                      final List<AssetInfo> allList) {
        List<BmobQuery<AssetInfo>> and = new ArrayList<>();
        BmobQuery<AssetInfo> query1= new BmobQuery<>();
        query1.addWhereEqualTo(para1, value1);
        and.add(query1);
        BmobQuery<AssetInfo> query2= new BmobQuery<>();
        query1.addWhereEqualTo(para2, value2);
        and.add(query2);
        BmobQuery<AssetInfo> query= new BmobQuery<>();
        query.and(and);
        query.order("mLocation,mAssetsNum");
        query.setSkip(count*500);//跳过count次500条记录
        query.setLimit(500);
        query.include("mPicture,mOldManager,mLocation,mDepartment");
        query.findObjects(new FindListener<AssetInfo>() {
            @Override
            public void done(final List<AssetInfo> list, BmobException e) {
                if (e == null) {
//                    if (list == null || list.size() == 0) {//当查询条数为500的整数倍时，会出现这个
//                        Toast.makeText(context, "查询结束,没有更多结果!", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
                    allList.addAll(list); //暂存入临时列表中，只有前500条
                    if (list.size() >= 500) {//当数据条数超过
                        count++;//计数器加1，
                        AndQueryAssets(context,para1,value1, para2,value2, handler, allList);
                    } else {//当于500条，说明一次可以全部获取，结束递归
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Message msg = new Message();
                                msg.what = AssetsUtil.SEARCH_ONE_ASSET;
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("assets", (Serializable) allList);
                                msg.setData(bundle);
                                handler.sendMessage(msg);
                            }
                        }).start();
                    }

                } else {
                    {
                        Toast.makeText(context, "查询失败，请稍后再查！", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    /**
     * 依据某三个参数组合，进行查询资产
     *
     * @param
     */
    public static void AndQueryAssets(final Context context, final String para1, final Object value1,
                                      final String para2, final Object value2, final String para3, final Object value3,
                                      final Handler handler, final List<AssetInfo> allList) {
        List<BmobQuery<AssetInfo>> and = new ArrayList<>();
        BmobQuery<AssetInfo> query1= new BmobQuery<>();
        query1.addWhereEqualTo(para1, value1);
        and.add(query1);
        BmobQuery<AssetInfo> query2= new BmobQuery<>();
        query1.addWhereEqualTo(para2, value2);
        and.add(query2);
        BmobQuery<AssetInfo> query3= new BmobQuery<>();
        query1.addWhereEqualTo(para3, value3);
        and.add(query3);

        BmobQuery<AssetInfo> query= new BmobQuery<>();
        query.and(and);
        query.order("mLocation,mAssetsNum");
        query.setSkip(count * 500);
        query.setLimit(500);
        query.include("mPicture,mOldManager,mLocation,mDepartment");
        query.findObjects(new FindListener<AssetInfo>() {
            @Override
            public void done(final List<AssetInfo> list, BmobException e) {
                if (e == null) {
//                    if (list == null || list.size() == 0) {//当查询条数为500的整数倍时，会出现这个
//                        Toast.makeText(context, "查询结束!", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
                    allList.addAll(list); //暂存入临时列表中，只有前500条
                    if (list.size() >= 500) {//当数据条数超过
                        count++;//计数器加1，
                        AndQueryAssets(context,para1,value1, para2,value2,para3,value3, handler, allList);
                    } else {//当于500条，说明一次可以全部获取，结束递归
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Message msg = new Message();
                                msg.what = AssetsUtil.SEARCH_ONE_ASSET;
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("assets", (Serializable) allList);
                                msg.setData(bundle);
                                handler.sendMessage(msg);
                            }
                        }).start();
                    }

                } else {
                    {
                        Toast.makeText(context, "查询失败，请稍后再查！", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    /**
     * 三组参数，前两组先or，再和后一组and。进行查询
     * @param context
     * @param para1
     * @param value1
     * @param para2
     * @param value2
     * @param para3
     * @param value3
     * @param handler
     */
    public static void OrAndQueryAssets(final Context context, final String para1, final Object value1,
                                        final String para2, final Object value2, final String para3, final Object value3,
                                        final Handler handler, final List<AssetInfo> allList) {
        List<BmobQuery<AssetInfo>> or  = new ArrayList<>();
        //第一组or关系,状态为4 or 6
        BmobQuery<AssetInfo> query1 = new BmobQuery<>();
        query1.addWhereEqualTo(para1, value1);
        BmobQuery<AssetInfo> query2 = new BmobQuery<>();
        query2.addWhereEqualTo(para2, value2);
        or.add(query1);
        or.add(query2);
        BmobQuery<AssetInfo> first = new BmobQuery<>();
        first.or(or);
        //第二组 and关系，新管理 和第一组结果
        List<BmobQuery<AssetInfo>> and = new ArrayList<>();
        BmobQuery<AssetInfo> query3 = new BmobQuery<>();
        query3.addWhereEqualTo(para3, value3);
        and.add(query3);
        and.add(first);
        //最后结果
        BmobQuery<AssetInfo> query = new BmobQuery<>();
        query.and(and);
        query.order("mLocation,mAssetsNum");
        query.setSkip(count * 500);
        query.setLimit(500);
        query.include("mPicture,mOldManager,mLocation,mDepartment");
        query.findObjects(new FindListener<AssetInfo>() {
            @Override
            public void done(final List<AssetInfo> list, BmobException e) {
                if (e == null) {
//                    if (list == null || list.size() == 0) {//当查询条数为500的整数倍时，会出现这个
//                        Toast.makeText(context, "查询结束!", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
                    allList.addAll(list); //暂存入临时列表中，只有前500条
                    if (list.size() >= 500) {//当数据条数超过
                        count++;//计数器加1，
                        AndQueryAssets(context,para1,value1, para2,value2,para3,value3, handler, allList);
                    } else {//当于500条，说明一次可以全部获取，结束递归
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Message msg = new Message();
                                msg.what = AssetsUtil.SEARCH_ONE_ASSET;
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("assets", (Serializable) allList);
                                msg.setData(bundle);
                                handler.sendMessage(msg);
                            }
                        }).start();
                    }

                } else {
                    {
                        Toast.makeText(context, "查询失败，请稍后再查！", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    /**
     * 将修改好的资产列表保存入数据库
     */
    public static void updateBmobLibrary(final Context context, List<BmobObject> objects) {
        if (objects.size() <= 50) {//如果资产少于等于50时
            new BmobBatch().updateBatch(objects).doBatch(new QueryListListener<BatchResult>() {
                @Override
                public void done(List<BatchResult> list, BmobException e) {
                    if (e == null) {
                        Toast.makeText(context,"更新成功",Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context,"更新失败:" + e.toString(),Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {
            //TODO:如果资产大于50时分批处理
            batchUpdate(context,objects);
        }

    }

    /**
     * 分批处理修改,求出50的倍数和余数。先是倍数，如果余数，再处理余数的，最后处理最后一个。因为List
     * 的subList(fromIndex,toIndex)中不包含toIndex.
     */
    private static void batchUpdate(final Context context, List<BmobObject> objects) {
        int size = objects.size();
        int m = size / 50;//倍数
        int y = size % 50;//余数
        //整50的倍数量更新
        for (int i = 0; i < m; i++) {
            final int finalI = i + 1;
            int fromIndex = 50 * i;
            int toIndex = fromIndex + 49;
            new BmobBatch().updateBatch(objects.subList(fromIndex, toIndex)).doBatch(new QueryListListener<BatchResult>() {
                @Override
                public void done(List<BatchResult> list, BmobException e) {
                    if (e == null) {
                        Toast.makeText(context,"第" + finalI + "批量更新成功",Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context,"第" + finalI + "批量更新失败:" + e.toString()
                                ,Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
        //余数量批量更新
        if (y > 0) {
            new BmobBatch().updateBatch(objects.subList(50 * m - 1, size - 1)).doBatch(new QueryListListener<BatchResult>() {
                @Override
                public void done(List<BatchResult> list, BmobException e) {
                    if (e == null) {
                        Toast.makeText(context,"最后一批量更新成功",Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context,"最后一批量更新失败:" + e.toString(),Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
        //最后一个
        BmobObject object = objects.get(size - 1);
        String objectId = object.getObjectId();
        object.update(objectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Toast.makeText(context,"最后一个更新成功",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context,"最后一个更新失败:" + e.toString(),Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    /**
     * 将新增加的资产添加入数据库
     *
     * @param objects
     */
    public static void insertBmobLibrary(final Context context, List<BmobObject> objects) {
        if (objects.size() <= 50) {//如果资产少于等于50时
              new BmobBatch().insertBatch(objects).doBatch(new QueryListListener<BatchResult>() {
                @Override
                public void done(List<BatchResult> list, BmobException e) {
                    if (e == null) {
                        Toast.makeText(context,"移交更新成功",Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context,"移交更新失败:" + e.toString(),Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {
            //TODO:如果资产大于50时分批处理
            batchInsert(context,objects);
        }

    }

    /**
     * 当资产数量大于50时
     *
     * @param objects
     */
    private static void batchInsert(final Context context, List<BmobObject> objects) {
        int size = objects.size();
        int m = size / 50;//倍数
        int y = size % 50;//余数
        //整50的倍数量更新
        for (int i = 0; i < m; i++) {
            final int finalI = i + 1;
            int fromIndex = 50 * i;
            int toIndex = fromIndex + 49;
            new BmobBatch().insertBatch(objects.subList(fromIndex, toIndex)).doBatch(new QueryListListener<BatchResult>() {
                @Override
                public void done(List<BatchResult> list, BmobException e) {
                    if (e == null) {
                        Toast.makeText(context,"第" + finalI + "批量更新成功",Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context,"第" + finalI + "批量更新失败:" + e.toString(),Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
        //余数量批量更新
        if (y > 0) {
            new BmobBatch().insertBatch(objects.subList(50 * m - 1, size - 1)).doBatch(new QueryListListener<BatchResult>() {
                @Override
                public void done(List<BatchResult> list, BmobException e) {
                    if (e == null) {
                        Toast.makeText(context,"最后一批量更新成功",Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context,"最后一批量更新失败:" + e.toString(),Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
        //最后一个
        BmobObject object = objects.get(size - 1);
        object.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    Toast.makeText(context,"最后一个更新成功",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context,"最后一个更新失败:" + e.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
