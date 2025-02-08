package com.gawilive.common.utils;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.hjq.toast.ToastUtils;
import com.gawilive.common.CommonAppContext;
import com.gawilive.common.R;

/**
 * Created by cxf on 2017/8/3.
 */

public class ToastUtil {

    private static final Toast sToast;
    private static long sLastTime;
    private static String sLastString;

    static {
        sToast = makeToast();
    }

    private static Toast makeToast() {
        Toast toast = new Toast(CommonAppContext.getInstance());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        View view = LayoutInflater.from(CommonAppContext.getInstance()).inflate(R.layout.view_toast, null);
        toast.setView(view);
        return toast;
    }


    public static void show(int res) {
        show(WordUtil.getString(res));
    }

    public static void show(String s) {
        if (TextUtils.isEmpty(s)) {
            return;
        }

        ToastUtils.show(s);

    }

    public static void show(CharSequence s) {
        if (TextUtils.isEmpty(s)) {
            return;
        }
//        long curTime = System.currentTimeMillis();
//        if (curTime - sLastTime > 2000) {
//            sLastTime = curTime;
//            sLastString = s.toString();
//            sToast.setText(s);
//            sToast.show();
//        } else {
//            if (!s.equals(sLastString)) {
//                sLastTime = curTime;
//                sLastString = s.toString();
//                sToast = makeToast();
//                sToast.setText(s);
//                sToast.show();
//            }
//        }

        ToastUtils.show(s);
    }

}
