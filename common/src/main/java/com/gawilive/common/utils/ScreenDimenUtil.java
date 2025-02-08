package com.gawilive.common.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.gawilive.common.CommonAppContext;

/**
 * Created by cxf on 2017/10/30.
 * 获取屏幕尺寸
 */

public class ScreenDimenUtil {

    private int mStatusBarHeight;//状态栏高度
    private int mContentHeight;
    private final int mScreenWidth;
    private final int mScreenHeight;
    private final int mScreenRealHeight;


    private static ScreenDimenUtil sInstance;

    private ScreenDimenUtil() {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) CommonAppContext.getInstance().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        display.getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        display.getRealMetrics(dm);
        mScreenRealHeight = dm.heightPixels;

        int resourceId = CommonAppContext.getInstance().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            mStatusBarHeight = CommonAppContext.getInstance().getResources().getDimensionPixelSize(resourceId);
            mContentHeight = mScreenHeight - mStatusBarHeight;
        }
    }

    public static ScreenDimenUtil getInstance() {
        if (sInstance == null) {
            synchronized (ScreenDimenUtil.class) {
                if (sInstance == null) {
                    sInstance = new ScreenDimenUtil();
                }
            }
        }
        return sInstance;
    }

    /**
     * 获取屏幕的宽度
     *
     * @return
     */
    public int getScreenWdith() {
        return mScreenWidth;
    }

    /**
     * 获取屏幕的高度
     *
     * @return
     */
    public int getScreenHeight() {
        return mScreenHeight;
    }


    /**
     * 获取ContentView的高度
     *
     * @return
     */
    public int getContentHeight() {
        return mContentHeight;
    }


    public int getScreenRealHeight(){
        return mScreenRealHeight;
    }

    /**
     * 获取状态栏的高度
     *
     * @return
     */
    public int getStatusBarHeight() {
        return mStatusBarHeight;
    }

}
