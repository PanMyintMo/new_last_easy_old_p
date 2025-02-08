package com.gawilive.main.http;

import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.http.HttpCallback;
import com.gawilive.common.http.HttpClients;

/**
 * Created by cxf on 2018/9/17.
 */

public class MainHttpUtils {

    private static final String DEVICE = "android";

    /**
     * 取消网络请求
     */
    public static void cancel(String tag) {
        HttpClients.getInstance().cancel(tag);
    }


    /**
     * 设置支付密码
     *
     * @param payPass
     * @param callback
     */
    public static void setPayPass(String payPass, HttpCallback callback) {
        HttpClients.getInstance().post("paypwd", MainHttpConsts.SETUP_PAY_PASS)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("paypwd", payPass)
                .execute(callback);
    }


}






