package com.kedong.ieduflscommon.util;

import java.text.SimpleDateFormat;

/**
 * 时间处理工具类
 * 2022年12月27日15:46:00
 * mr_qin
 */
public class TimeTool {
    //年月时间转换格式
    public static final String FORMIT_MONTH = "yyyyMM";

    public static final String FORMIT_TIME = "yyyyMMddHHmmss";
    /**
     * 获取时间字符串
     * @param cal 毫秒
     * @param formit 时间格式
     * @return
     */
    public static String getTimeStr(long cal,String formit) {
        SimpleDateFormat sdf=new SimpleDateFormat(formit);
        String time  = sdf.format(cal).toString();
        return time;
    }
}
