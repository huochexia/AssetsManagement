package com.example.administrator.assetsmanagement.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间工具
 * Created by Administrator on 2017/11/18 0018.
 */

public class TimeUtils {

    public final static String FORMAT_DATE= "yyyy-MM-dd";
    /**
     *   获取今天的时间，按指定时间格式
     */
    public static String getFormatToday(String dateFormat) {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        return formatter.format(currentTime);
    }
}
