package com.gawilive.common.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.gawilive.common.CommonAppContext;

import java.util.Map;

/**
 * Created by cxf on 2018/9/17.
 * SharedPreferences 封装
 */

public class SpUtil {

    public static final String LANGUAGE = "language";
    private static SpUtil sInstance;
    private final SharedPreferences mSharedPreferences;

    public static final String UID = "uid";
    public static final String TOKEN = "token";
    public static final String USER_INFO = "userInfo";
    public static final String CONFIG = "config";
    public static final String IM_LOGIN = "jimLogin";
    public static final String HAS_SYSTEM_MSG = "hasSystemMsg";
    public static final String LOCATION_LNG = "locationLng";
    public static final String LOCATION_LAT = "locationLat";
    public static final String LOCATION_PROVINCE = "locationProvince";
    public static final String LOCATION_CITY = "locationCity";
    public static final String LOCATION_DISTRICT = "locationDistrict";
    public static final String MH_BEAUTY_ENABLE = "mhBeautyEnable";
    public static final String BRIGHTNESS = "brightness";//亮度
    public static final String IM_MSG_RING = "imMsgRing";//私信音效
    public static final String DEVICE_ID = "deviceId";
    public static final String TEENAGER = "teenager";
    public static final String TEENAGER_SHOW = "teenagerShow";
    public static final String PIN = "pin";

    private SpUtil() {
        mSharedPreferences = CommonAppContext.getInstance().getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
    }

    public static SpUtil getInstance() {
        if (sInstance == null) {
            synchronized (SpUtil.class) {
                if (sInstance == null) {
                    sInstance = new SpUtil();
                }
            }
        }
        return sInstance;
    }


    private OnPreferenceChangeListener mListener;
    // Interface for preference change listener
    public interface OnPreferenceChangeListener {
        void onPreferenceChanged(String key, String newValue);
    }


    private final SharedPreferences.OnSharedPreferenceChangeListener internalListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    if (mListener != null) {
                        String newValue = sharedPreferences.getString(key, "");
                        mListener.onPreferenceChanged(key, newValue);
                    }
                }
            };


    public void removeOnPreferenceChangeListener() {
        mListener = null;
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(internalListener);
    }

    public void setOnPreferenceChangeListener(OnPreferenceChangeListener listener) {
        mListener = listener;
        // Register the internal listener
        mSharedPreferences.registerOnSharedPreferenceChangeListener(internalListener);
    }


    /**
     * 保存一个字符串
     */
    public void setStringValue(String key, String value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * 获取一个字符串
     */
    public String getStringValue(String key) {
        return mSharedPreferences.getString(key, "");
    }

    /**
     * 保存多个字符串
     */
    public void setMultiStringValue(Map<String, String> pairs) {
        if (pairs == null || pairs.size() == 0) {
            return;
        }
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        for (Map.Entry<String, String> entry : pairs.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                editor.putString(key, value);
            }
        }
        editor.apply();
    }

    /**
     * 获取多个字符串
     */
    public String[] getMultiStringValue(String... keys) {
        if (keys == null || keys.length == 0) {
            return null;
        }
        int length = keys.length;
        String[] result = new String[length];
        for (int i = 0; i < length; i++) {
            String temp = "";
            if (!TextUtils.isEmpty(keys[i])) {
                temp = mSharedPreferences.getString(keys[i], "");
            }
            result[i] = temp;
        }
        return result;
    }


    /**
     * 保存一个布尔值
     */
    public void setBooleanValue(String key, boolean value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }


    public void setDiamondBalance(String key, String value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getDiamondBalance(String key) {
        return mSharedPreferences.getString(key, ""); // Default to empty string if not found
    }

    /**
     * 获取一个布尔值
     */
    public boolean getBooleanValue(String key) {
        return mSharedPreferences.getBoolean(key, false);
    }

    /**
     * 保存多个布尔值
     */
    public void setMultiBooleanValue(Map<String, Boolean> pairs) {
        if (pairs == null || pairs.size() == 0) {
            return;
        }
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        for (Map.Entry<String, Boolean> entry : pairs.entrySet()) {
            String key = entry.getKey();
            Boolean value = entry.getValue();
            if (!TextUtils.isEmpty(key)) {
                editor.putBoolean(key, value);
            }
        }
        editor.apply();
    }

    /**
     * 获取多个布尔值
     */
    public boolean[] getMultiBooleanValue(String[] keys) {
        if (keys == null || keys.length == 0) {
            return null;
        }
        int length = keys.length;
        boolean[] result = new boolean[length];
        for (int i = 0; i < length; i++) {
            boolean temp = false;
            if (!TextUtils.isEmpty(keys[i])) {
                temp = mSharedPreferences.getBoolean(keys[i], false);
            }
            result[i] = temp;
        }
        return result;
    }


    public void removeValue(String... keys) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        for (String key : keys) {
            editor.remove(key);
        }
        editor.apply();
    }


    /**
     * 亮度
     */
    public void setBrightness(float brightness) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putFloat(BRIGHTNESS, brightness);
        editor.apply();
    }

    /**
     * 亮度
     */
    public float getBrightness() {
        return mSharedPreferences.getFloat(BRIGHTNESS, -1.0f);
    }


    /**
     * 私信音效
     */
    public void setImMsgRingOpen(boolean imMsgRingOpen) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(IM_MSG_RING, imMsgRingOpen);
        editor.apply();
    }

    /**
     * 私信音效
     */
    public boolean isImMsgRingOpen() {
        return mSharedPreferences.getBoolean(IM_MSG_RING, true);
    }


}