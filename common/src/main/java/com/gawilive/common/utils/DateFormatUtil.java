package com.gawilive.common.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by cxf on 2018/7/19.
 */

public class DateFormatUtil {

    private static final SimpleDateFormat sFormat;
    private static final SimpleDateFormat sFormat2;
    private static final SimpleDateFormat sFormat3;

    static {
        sFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        sFormat2 = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");
        sFormat3 = new SimpleDateFormat("MM.dd-HH:mm:ss");
    }


    public static String getCurTimeString() {
        return sFormat.format(new Date());
    }

    public static String getVideoCurTimeString() {
        return sFormat2.format(new Date());
    }

    public static String getCurTimeString2() {
        return sFormat3.format(new Date());
    }
}
