package com.gawilive.common.pay;

import android.app.Dialog;
import androidx.fragment.app.FragmentActivity;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.Constants;
import com.gawilive.common.R;
import com.gawilive.common.bean.UserBean;
import com.gawilive.common.event.CoinChangeEvent;
import com.gawilive.common.http.CommonHttpUtil;
import com.gawilive.common.http.HttpCallback;
import com.gawilive.common.pay.ali.AliPayBuilder;
import com.gawilive.common.pay.wx.WxPayBuilder;
import com.gawilive.common.utils.StringUtil;
import com.gawilive.common.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;

/**
 * Created by cxf on 2019/4/22.
 */

public class PayPresenter {

    private String mAliPartner;// 支付宝商户ID
    private String mAliSellerId; // 支付宝商户收款账号
    private String mAliPrivateKey; // 支付宝商户私钥，pkcs8格式
    private String mAliCallbackUrl;//支付宝充值回调地址
    private String mWxAppID;//微信AppID
    private String mServiceNameAli;
    private String mServiceNameWx;
    private String mServiceNamePaypal;
    private long mBalanceValue;
    private FragmentActivity mActivity;
    private PayCallback mPayCallback;

    public PayPresenter(FragmentActivity activity) {
        mActivity = new WeakReference<>(activity).get();
    }

    public void setPayCallback(PayCallback callback) {
        mPayCallback = callback;
    }

    public long getBalanceValue() {
        return mBalanceValue;
    }

    public void setBalanceValue(long balanceValue) {
        mBalanceValue = balanceValue;
    }

    public void setAliPartner(String aliPartner) {
        mAliPartner = aliPartner;
    }

    public void setAliSellerId(String aliSellerId) {
        mAliSellerId = aliSellerId;
    }

    public void setAliPrivateKey(String aliPrivateKey) {
        mAliPrivateKey = aliPrivateKey;
    }

    public void setWxAppID(String wxAppID) {
        mWxAppID = wxAppID;
    }


    public void setServiceNameAli(String serviceNameAli) {
        mServiceNameAli = serviceNameAli;
    }

    public void setServiceNameWx(String serviceNameWx) {
        mServiceNameWx = serviceNameWx;
    }

    public void setServiceNamePaypal(String serviceNamePaypal) {
        mServiceNamePaypal = serviceNamePaypal;
    }

    public void setAliCallbackUrl(String aliCallbackUrl) {
        mAliCallbackUrl = aliCallbackUrl;
    }

    public void pay(String payType, String money, String goodsName, String orderParams) {
        if (TextUtils.isEmpty(payType)) {
            ToastUtil.show(R.string.wallet_tip_5);
            return;
        }
        switch (payType) {
            case Constants.PAY_TYPE_ALI://支付宝支付
                aliPay(money, goodsName, orderParams);
                break;
            case Constants.PAY_TYPE_WX://微信支付
                wxPay(orderParams);
                break;
//            case Constants.PAY_TYPE_PAYPAL://paypal支付
//                paypal(money, goodsName, orderParams);
//                break;
        }
    }

    /**
     * 支付宝支付
     */
    private void aliPay(String money, String goodsName, String orderParams) {
        if (mActivity == null || TextUtils.isEmpty(mServiceNameAli) || TextUtils.isEmpty(mAliCallbackUrl)) {
            return;
        }
//        if (!CommonAppConfig.isAppExist(Constants.PACKAGE_NAME_ALI)) {
//            ToastUtil.show(R.string.coin_ali_not_install);
//            return;
//        }
        if (TextUtils.isEmpty(mAliPartner) || TextUtils.isEmpty(mAliSellerId) || TextUtils.isEmpty(mAliPrivateKey)) {
            ToastUtil.show(R.string.pay_ali_not_enable);
            return;
        }
        AliPayBuilder builder = new AliPayBuilder(mActivity, mAliPartner, mAliSellerId, mAliPrivateKey);
        builder.setMoney(money);
        builder.setGoodsName(goodsName);
        builder.setCallbackUrl(mAliCallbackUrl);
        builder.setOrderParams(StringUtil.contact(mServiceNameAli, orderParams));
        builder.setPayCallback(mPayCallback);
        builder.pay();
    }

    /**
     * 微信支付
     */
    private void wxPay(String orderParams) {
        if (mActivity == null || TextUtils.isEmpty(mServiceNameWx)) {
            return;
        }
//        if (!CommonAppConfig.isAppExist(Constants.PACKAGE_NAME_WX)) {
//            ToastUtil.show(R.string.coin_wx_not_install);
//            return;
//        }
        if (TextUtils.isEmpty(mWxAppID)) {
            ToastUtil.show(R.string.pay_wx_not_enable);
            return;
        }
        WxPayBuilder builder = new WxPayBuilder(mActivity, mWxAppID);
        builder.setOrderParams(StringUtil.contact(mServiceNameWx, orderParams));
        builder.setPayCallback(mPayCallback);
        builder.pay();
    }


    /**
     * paypal支付
     */
//    private void paypal(final String money, final String goodsName, final String orderParams) {
//        CommonHttpUtil.getBraintreeToken(new HttpCallback() {
//            @Override
//            public void onSuccess(int code, String msg, String[] info) {
//                if (code == 0) {
//                    if (info.length > 0) {
//                        JSONObject obj = JSON.parseObject(info[0]);
//                      //  PaypalBuilder builder = new PaypalBuilder(mActivity);
////                        builder.setMoney(money);
////                        builder.setGoodsName(goodsName);
////                        builder.setBraintreeToken(obj.getString("braintreeToken"));
////                        builder.setOrderParams(StringUtil.contact(mServiceNamePaypal, orderParams));
////                        builder.setPayCallback(mPayCallback);
//                        if (Constants.PAY_BUY_COIN_PAYPAL.equals(mServiceNamePaypal)) {
//                           // builder.setBuyType("coin_charge");
//                        } else if (Constants.MALL_PAY_GOODS_ORDER.equals(mServiceNamePaypal)) {
//                           // builder.setBuyType("order_pay");
//                        } else if (Constants.MALL_PAY_CONTENT_PAYPAL.equals(mServiceNamePaypal)) {
//                           // builder.setBuyType("paidprogram_pay");
//                        }
//                       // builder.pay();
//                    }
//                } else {
//                    ToastUtil.show(msg);
//                }
//            }
//
//            @Override
//            public boolean showLoadingDialog() {
//                return true;
//            }
//
//            @Override
//            public Dialog createLoadingDialog() {
//                return DialogUitl.loadingDialog(mActivity);
//            }
//        });
//    }
//

    /**
     * 检查支付结果
     */
    public void checkPayResult() {
        CommonHttpUtil.getBalance(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    String coin = obj.getString("coin");
                    long balanceValue = Long.parseLong(coin);
                    if (balanceValue > mBalanceValue) {
                        mBalanceValue = balanceValue;
                        ToastUtil.show(R.string.coin_charge_success);
                        UserBean u = CommonAppConfig.getInstance().getUserBean();
                        if (u != null) {
                            u.setCoin(coin);
                        }
                        EventBus.getDefault().post(new CoinChangeEvent(coin, true));
                    }
                }
            }
        });
    }


    public void release() {
        mActivity = null;
        mPayCallback = null;
    }
}
