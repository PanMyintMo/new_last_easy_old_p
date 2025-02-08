package com.gawilive.common.utils;

import com.tencent.mmkv.MMKV;

public class MMKVUtils {

    public static void setKey(String key) {
        MMKV.defaultMMKV().encode("payKey", key);
    }

    public static String getKey() {
        return MMKV.defaultMMKV().decodeString("payKey", "");
    }

    public static void setPayPwd(String key) {
        MMKV.defaultMMKV().encode("payPwd", key);
    }

    public static String getPayPwd() {
        return MMKV.defaultMMKV().decodeString("payPwd", "");
    }

    public static void setPayUid(String uid) {
        MMKV.defaultMMKV().encode("payUid", uid);
    }

    public static String getPayUid() {
        return MMKV.defaultMMKV().decodeString("payUid", "");
    }

    public static void removeAll() {
        MMKV.defaultMMKV().clearAll();
    }
}
