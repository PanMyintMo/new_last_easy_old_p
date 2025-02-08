package com.gawilive.common.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.Locale;

public class MultiLanguageUtil {

    private static final String TAG = "multilanguage";
    private static MultiLanguageUtil instance;

    public static MultiLanguageUtil getInstance() {
        if (instance == null) {
            synchronized (MultiLanguageUtil.class) {
                if (instance == null) {
                    instance = new MultiLanguageUtil();
                }
            }
        }
        return instance;
    }

    private MultiLanguageUtil() {
    }

    /**
     * 如果不是英文、简体中文、繁体中文，默认返回简体中文
     *
     * @return
     */
    public Locale getLanguageLocale() {

        String langCode = SpUtil.getInstance().getStringValue(SpUtil.LANGUAGE);

        Log.e(TAG, "getLanguageLocale  " + "langCode:" + langCode);

        Locale locale = Locale.SIMPLIFIED_CHINESE;
//        if (langCode.equals("en-US")) {
//            locale = Locale.ENGLISH;
//        }else if (langCode.equals("zh-CN")) {
//            locale = Locale.SIMPLIFIED_CHINESE;
//        } else if (langCode.equals("vi-VN")) {
//            locale = new Locale("vi", "VN");
//        } else if (langCode.equals("pt-PT")) {
//            locale = new Locale("pt", "PT");
//        }else{
//            locale = Locale.ENGLISH;
//        }
        if (langCode.contains("zh")) {
            locale = Locale.SIMPLIFIED_CHINESE;
        } else if (langCode.contains("ar")) {
            locale = new Locale("ar");
        } else if (langCode.contains("th")) {
            locale = new Locale("th");
        }else if (langCode.contains("my")) {
            locale = new Locale("my");
        }else {
            locale = Locale.ENGLISH;
        }
        Log.e(TAG, "getLanguageLocale  " + getLanguage(locale));
        Log.e(TAG, "getLanguageLocale  " + locale);
        return locale;
    }

    public String getLanguage(Locale locale) {
        return locale.getLanguage() + "_" + locale.getCountry();
    }


    public static Context attachBaseContext(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return updateResources(context);
            } else {
                MultiLanguageUtil.getInstance().setConfiguration(context);
                return context;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getLanguageLocale  attachBaseContext Exception");
            return context;
        }

    }

    /**
     * 设置语言
     */
    public void setConfiguration(Context context) {
        try {
            updateLocal(context);
            updateLocal(context.getApplicationContext());

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getLanguageLocale  setConfiguration Exception");
        }

    }

    private void updateLocal(Context context) {
        if (context == null) {
            Log.e(TAG, "No context, MultiLanguageUtil will not work!");
            return;
        }

        Locale targetLocale = getLanguageLocale();
        Locale.setDefault(targetLocale);
        Configuration configuration = context.getResources().getConfiguration();

        configuration.setLocale(targetLocale);
        context.createConfigurationContext(configuration);

        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        resources.updateConfiguration(configuration, dm);//语言更换生效的代码!
    }


    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResources(Context context) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        Locale locale = getInstance().getLanguageLocale();
        LocaleList localeList = new LocaleList(locale);
        LocaleList.setDefault(localeList);
        configuration.setLocales(localeList);
        configuration.setLocale(locale);
        return context.createConfigurationContext(configuration);
    }

}
