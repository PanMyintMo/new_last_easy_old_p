package com.gawilive.common.http;

import android.app.Dialog;

import com.alibaba.fastjson.JSON;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.gawilive.common.R;
import com.gawilive.common.event.LoginInvalidEvent;
import com.gawilive.common.utils.RouteUtil;
import com.gawilive.common.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.net.UnknownServiceException;

/**
 * Created by cxf on 2017/8/7.
 */

public abstract class HttpCallback extends AbsCallback<JsonBean> {

    private Dialog mLoadingDialog;

    @Override
    public JsonBean convertResponse(okhttp3.Response response) throws Throwable {
        String str = response.body().string();
        if (!str.startsWith("{")) {
            str = str.substring(str.indexOf("{"));
        }
        return JSON.parseObject(str, JsonBean.class);
    }

    @Override
    public void onSuccess(Response<JsonBean> response) {
        JsonBean bean = response.body();
        if (bean != null) {
            if (200 == bean.getRet()) {
                Data data = bean.getData();
                if (data != null) {
                    if (700 == data.getCode()) {
                        //token过期，重新登录
                        if (isUseLoginInvalid()) {
                            onLoginInvalid();
                        } else {
                            EventBus.getDefault().post(new LoginInvalidEvent());
                            RouteUtil.forwardLoginInvalid(data.getMsg());
                        }
                    } else {
                        onSuccess(data.getCode(), data.getMsg(), data.getInfo());
                    }
                } else {
                    //L.e("Server return value exception--->ret: " + bean.getRet() + " msg: " + bean.getMsg());
                }
            } else {
             //   L.e("Server return value exception--->ret: " + bean.getRet() + " msg: " + bean.getMsg());
            }

        } else {
          //  L.e("Server return value exception--->bean = null");
        }
    }

    @Override
    public void onError(Response<JsonBean> response) {
        Throwable t = response.getException();
       // L.e("Network request error---->" + t.getClass() + " : " + t.getMessage());
        if (t instanceof SocketTimeoutException || t instanceof ConnectException || t instanceof UnknownHostException || t instanceof UnknownServiceException || t instanceof SocketException) {
            ToastUtil.show(R.string.load_failure);
        }
        if (showLoadingDialog() && mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
        onError();
    }

    public void onError() {

    }

    /**
     * Login expired
     */
    public void onLoginInvalid() {

    }


    public abstract void onSuccess(int code, String msg, String[] info);

    @Override
    public void onStart(Request<JsonBean, ? extends Request> request) {
        onStart();
    }

    public void onStart() {
        try {
            if (showLoadingDialog()) {
                if (mLoadingDialog == null) {
                    mLoadingDialog = createLoadingDialog();
                }
                mLoadingDialog.show();
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onFinish() {
        if (showLoadingDialog() && mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
    }

    public Dialog createLoadingDialog() {
        return null;
    }

    public boolean showLoadingDialog() {
        return false;
    }

    public boolean isUseLoginInvalid() {
        return false;
    }

}
