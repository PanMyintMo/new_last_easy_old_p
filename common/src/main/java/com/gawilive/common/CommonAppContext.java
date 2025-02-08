package com.gawilive.common;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.tencent.mmkv.MMKV;
import com.gawilive.common.event.AppLifecycleEvent;
import com.gawilive.common.interfaces.AppLifecycleUtil;
import com.gawilive.common.manager.ActivityManager;
import com.gawilive.common.utils.FloatWindowHelper;
import com.gawilive.common.utils.L;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.MessageDigest;


/**
 * Created by cxf on 2017/8/3.
 */

public class CommonAppContext extends MultiDexApplication {

    private static CommonAppContext sInstance;
    private static Handler sMainThreadHandler;
    private int mCount;
    private boolean mFront;//是否前台


    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        registerActivityLifecycleCallbacks();

        // MMKV 初始化
        MMKV.initialize(this);
        ActivityManager.getInstance().init(this);


    }

    @Override
    protected void attachBaseContext(Context base) {
        MultiDex.install(this);
        super.attachBaseContext(base);
    }

    public static CommonAppContext getInstance() {
        if (sInstance == null) {
            try {
                Class clazz = Class.forName("android.app.ActivityThread");
                Method method = clazz.getMethod("currentApplication");
                Object obj = method.invoke(null);
                if (obj != null && obj instanceof CommonAppContext) {
                    sInstance = (CommonAppContext) obj;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sInstance;
    }

    /**
     * 获取主线程的Handler
     */
    private static void getMainThreadHandler() {
        try {
            Class clazz = Class.forName("android.app.ActivityThread");
            Field field = clazz.getDeclaredField("sMainThreadHandler");
            field.setAccessible(true);
            Object obj = field.get(clazz);
            if (obj != null && obj instanceof Handler) {
                sMainThreadHandler = (Handler) obj;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void postDelayed(Runnable runnable, long delayMillis) {
        if (sMainThreadHandler == null) {
            getMainThreadHandler();
        }
        if (sMainThreadHandler != null) {
            sMainThreadHandler.postDelayed(runnable, delayMillis);
        }
    }

    public static void post(Runnable runnable) {
        if (sMainThreadHandler == null) {
            getMainThreadHandler();
        }
        if (sMainThreadHandler != null) {
            sMainThreadHandler.post(runnable);
        }
    }


    private void registerActivityLifecycleCallbacks() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                mCount++;
                if (!mFront) {
                    mFront = true;
                    //L.e("AppContext------->in the foreground");
                    EventBus.getDefault().post(new AppLifecycleEvent(false));
                    CommonAppConfig.getInstance().setFrontGround(true);
                    FloatWindowHelper.setFloatWindowVisible(true);
                    AppLifecycleUtil.onAppFrontGround();

                }
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                mCount--;
                if (mCount == 0) {
                    mFront = false;
                  //  L.e("AppContext------->in the background");
                    EventBus.getDefault().post(new AppLifecycleEvent(false));
                    CommonAppConfig.getInstance().setFrontGround(false);
                    FloatWindowHelper.setFloatWindowVisible(false);
                    AppLifecycleUtil.onAppBackGround();
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    /**
     * 获取App签名md5值
     */
    public String getAppSignature() {
        try {
            PackageInfo info =
                    this.getPackageManager().getPackageInfo(this.getPackageName(),
                            PackageManager.GET_SIGNATURES);
            if (info != null) {
                Signature[] signs = info.signatures;
                byte[] bytes = signs[0].toByteArray();
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(bytes);
                bytes = md.digest();
                StringBuilder stringBuilder = new StringBuilder(2 * bytes.length);
                for (int i = 0; ; i++) {
                    if (i >= bytes.length) {
                        return stringBuilder.toString();
                    }
                    String str = Integer.toString(0xFF & bytes[i], 16);
                    if (str.length() == 1) {
                        str = "0" + str;
                    }
                    stringBuilder.append(str);
                }
            }
        } catch (Exception e) {
           // e.printStackTrace();
        }
        return null;
    }

}
