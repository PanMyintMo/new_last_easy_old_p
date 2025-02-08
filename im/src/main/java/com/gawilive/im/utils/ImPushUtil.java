package com.gawilive.im.utils;

import android.content.Context;

import com.gawilive.common.CommonAppContext;
import com.gawilive.common.utils.L;


/**
 * Created by cxf on 2017/8/3.
 * 极光推送相关
 */

public class ImPushUtil {

    public static final String TAG = "极光推送";
    private static ImPushUtil sInstance;

    private ImPushUtil() {

    }

    public static ImPushUtil getInstance() {
        if (sInstance == null) {
            synchronized (ImPushUtil.class) {
                if (sInstance == null) {
                    sInstance = new ImPushUtil();
                }
            }
        }
        return sInstance;
    }

    public void init(Context context) {

    }


    public void logout() {
        stopPush();
    }

    public void resumePush() {

    }

    public void stopPush() {

    }



    /**
     * 获取极光推送 RegistrationID
     */
    public String getPushID() {
        return "";
    }
}
