package com.gawilive.common.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.R;
import com.gawilive.common.activity.ErrorActivity;
import com.gawilive.common.bean.ConfigBean;
import com.gawilive.common.event.FollowEvent;
import com.gawilive.common.interfaces.CommonCallback;
import com.gawilive.common.utils.L;
import com.gawilive.common.utils.MD5Util;
import com.gawilive.common.utils.SpUtil;
import com.gawilive.common.utils.StringUtil;
import com.gawilive.common.utils.ToastUtil;
import com.gawilive.common.utils.WordFilterUtil;
import com.gawilive.common.utils.WordUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by cxf on 2018/9/17.
 */

public class CommonHttpUtil {

    public static final String SALT = "76576076c1f5f657b634e966c8836a06";

    /**
     * 取消网络请求
     */
    public static void cancel(String tag) {
        HttpClient.getInstance().cancel(tag);
    }

    /**
     * 使用腾讯定位sdk获取 位置信息
     *
     * @param lng 经度
     * @param lat 纬度
     * @param poi 是否要查询POI
     */
    public static void getAddressInfoByTxLocaitonSdk(final double lng, final double lat, final int poi, int pageIndex, String tag, final HttpCallback commonCallback) {
        String txMapAppKey = CommonAppConfig.getInstance().getTxMapAppKey();
        String s = "/ws/geocoder/v1/?get_poi=" + poi + "&key=" + txMapAppKey + "&location=" + lat + "," + lng
                + "&poi_options=address_format=short;radius=1000;page_size=20;page_index=" + pageIndex + ";policy=5" + CommonAppConfig.getInstance().getTxMapAppSecret();
        String sign = MD5Util.getMD5(s);
        OkGo.<String>get("https://apis.map.qq.com/ws/geocoder/v1/")
                .params("location", lat + "," + lng)
                .params("get_poi", poi)
                .params("poi_options", "address_format=short;radius=1000;page_size=20;page_index=" + pageIndex + ";policy=5")
                .params("key", txMapAppKey)
                .params("sig", sign)
                .tag(tag)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        JSONObject obj = JSON.parseObject(response.body());
                        if (obj != null && commonCallback != null) {
                            commonCallback.onSuccess(obj.getIntValue("status"), "", new String[]{obj.getString("result")});
                        }
                    }


                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        if (commonCallback != null) {
                            commonCallback.onError();
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        if (commonCallback != null) {
                            commonCallback.onFinish();
                        }
                    }
                });
    }

    /**
     * 使用腾讯地图API进行搜索
     *
     * @param lng 经度
     * @param lat 纬度
     */
    public static void searchAddressInfoByTxLocaitonSdk(final double lng, final double lat, String keyword, int pageIndex, final HttpCallback commonCallback) {

        String txMapAppKey = CommonAppConfig.getInstance().getTxMapAppKey();
        String s = "/ws/place/v1/search?boundary=nearby(" + lat + "," + lng + ",1000)&key=" + txMapAppKey + "&keyword=" + keyword + "&orderby=_distance&page_index=" + pageIndex +
                "&page_size=20" + CommonAppConfig.getInstance().getTxMapAppSecret();
        String sign = MD5Util.getMD5(s);
        OkGo.<String>get("https://apis.map.qq.com/ws/place/v1/search")
                .params("keyword", keyword)
                .params("boundary", "nearby(" + lat + "," + lng + ",1000)&orderby=_distance&page_size=20&page_index=" + pageIndex)
                .params("key", txMapAppKey)
                .params("sig", sign)
                .tag(CommonHttpConsts.GET_MAP_SEARCH)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        JSONObject obj = JSON.parseObject(response.body());
                        if (obj != null && commonCallback != null) {
                            commonCallback.onSuccess(obj.getIntValue("status"), "", new String[]{obj.getString("data")});
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        if (commonCallback != null) {
                            commonCallback.onError();
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        if (commonCallback != null) {
                            commonCallback.onFinish();
                        }
                    }
                });
    }


    /**
     * 获取config
     */
    public static void getConfig(final CommonCallback<ConfigBean> commonCallback) {
        HttpClient.getInstance().get("Home.getConfig", CommonHttpConsts.GET_CONFIG)
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0 && info.length > 0) {
                            try {
                                JSONObject obj = JSON.parseObject(info[0]);
                                ConfigBean bean = JSON.toJavaObject(obj, ConfigBean.class);
                                CommonAppConfig.getInstance().setConfig(bean);
                                CommonAppConfig.getInstance().setLevel(obj.getString("level"));
                                CommonAppConfig.getInstance().setAnchorLevel(obj.getString("levelanchor"));
                                SpUtil.getInstance().setStringValue(SpUtil.CONFIG, info[0]);
                                WordFilterUtil.getInstance().initWordMap(JSON.parseArray(obj.getString("sensitive_words"), String.class));
                                if (commonCallback != null) {
                                    commonCallback.callback(bean);
                                }
                            } catch (Exception e) {
                                String error = "info[0]:" + info[0] + "\n\n\n" + "Exception:" + e.getClass() + "---message--->" + e.getMessage();
                                ErrorActivity.forward("GetConfig接口返回数据异常", error);
                            }
                        }
                    }

                });
    }


    /**
     * QQ登录的时候 获取unionID 与PC端互通的时候用
     */
    public static void getQQLoginUnionID(String accessToken, final CommonCallback<String> commonCallback) {
        OkGo.<String>get("https://graph.qq.com/oauth2.0/me?access_token=" + accessToken + "&unionid=1")
                .tag(CommonHttpConsts.GET_QQ_LOGIN_UNION_ID)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (commonCallback != null) {
                            String data = response.body();
                            data = data.substring(data.indexOf("{"), data.lastIndexOf("}") + 1);
                            //L.e("getQQLoginUnionID------>" + data);
                            JSONObject obj = JSON.parseObject(data);
                            commonCallback.callback(obj.getString("unionid"));
                        }
                    }
                });
    }


    /**
     * Interface to follow others or unfollow others
     */
    public static void setAttention(String touid, CommonCallback<Integer> callback) {
        setAttention(CommonHttpConsts.SET_ATTENTION, touid, callback);
    }

    /**
     * Interface to follow others or unfollow others
     */
    public static void setAttention(String tag, final String touid, final CommonCallback<Integer> callback) {
        if (touid.equals(CommonAppConfig.getInstance().getUid())) {
            ToastUtil.show(WordUtil.getString(R.string.cannot_follow_self));
            return;
        }
        HttpClient.getInstance().get("User.setAttent", tag)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("touid", touid)
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0 && info.length > 0) {
                            int isAttention = JSON.parseObject(info[0]).getIntValue("isattent");//1是 关注  0是未关注
                            EventBus.getDefault().post(new FollowEvent(touid, isAttention));
                            if (callback != null) {
                                callback.callback(isAttention);
                            }
                        }
                    }
                });
    }

    /**
     * Recharge page, my diamonds
     */
    public static void getBalance(HttpCallback callback) {
        HttpClient.getInstance().get("User.getBalance", CommonHttpConsts.GET_BALANCE)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("type", 0)
                .execute(callback);
    }

    /**
     * 用支付宝充值 的时候在服务端生成订单号
     *
     * @param callback
     */
    public static void getAliOrder(String parmas, HttpCallback callback) {
        HttpClient.getInstance().get(parmas, CommonHttpConsts.GET_ALI_ORDER)
                .execute(callback);
    }

    /**
     * 用微信支付充值 的时候在服务端生成订单号
     *
     * @param callback
     */
    public static void getWxOrder(String parmas, HttpCallback callback) {
        HttpClient.getInstance().get(parmas, CommonHttpConsts.GET_WX_ORDER)
                .execute(callback);
    }


    /**
     * 用Paypal支付充值 的时候在服务端生成订单号
     *
     * @param callback
     */
    public static void getPaypalOrder(String parmas, HttpCallback callback) {
        HttpClient.getInstance().get(parmas, CommonHttpConsts.GET_PAYPAL_ORDER)
                .execute(callback);
    }


    /**
     * 检查token是否失效
     */
    public static void checkTokenInvalid() {
        HttpClient.getInstance().get("User.ifToken", CommonHttpConsts.CHECK_TOKEN_INVALID)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(NO_CALLBACK);
    }

    //不做任何操作的HttpCallback
    public static final HttpCallback NO_CALLBACK = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {

        }
    };


    /**
     * Upload file and obtain Qiniu Cloud token interface
     */

    public static void getUploadQiNiuToken(HttpCallback callback) {
        HttpClient.getInstance().get("Video.getQiniuToken", CommonHttpConsts.GET_UPLOAD_QI_NIU_TOKEN)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * Update Jiguang Push ID
     */
    public static void updatePushId(String pushId) {
        HttpClient.getInstance().get("Login.upUserPush", "updatePushId")
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("pushid", pushId)
                .execute(CommonHttpUtil.NO_CALLBACK);
    }


    /**
     * Determine whether a product has been removed from the shelves or deleted
     */
    public static void checkGoodsExist(String goodsId, HttpCallback callback) {
        cancel("CHECK_GOODS_EXIST");
        HttpClient.getInstance().get("Shop.getGoodExistence", "CHECK_GOODS_EXIST")
                .params("goodsid", goodsId)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }


    /**
     * Get beauty value
     */
    public static void getBeautyValue(HttpCallback callback) {
        HttpClient.getInstance().get("User.getBeautyParams", CommonHttpConsts.GET_BEAUTY_VALUE)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }


    /**
     * Set beauty value
     */
    public static void setBeautyValue(String jsonStr) {
        HttpClient.getInstance().get("User.setBeautyParams", CommonHttpConsts.SET_BEAUTY_VALUE)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("params", jsonStr)
                .execute(NO_CALLBACK);
    }

    /**
     * Get upload information
     */
    public static void getUploadInfo(HttpCallback callback) {
        HttpClient.getInstance().get("Upload.getCosInfo", CommonHttpConsts.GET_UPLOAD_INFO)
                .execute(callback);
    }

    /**
     * get BraintreeToken
     */
    public static void getBraintreeToken(HttpCallback callback) {
        HttpClient.getInstance().get("User.getBraintreeToken", CommonHttpConsts.GET_BRAINTREE_TOKEN)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }


    /**
     * BraintreePayback
     */
    public static void braintreeCallback(String orderId, String buyType, String nonce, String money, HttpCallback callback) {
        String time = String.valueOf(System.currentTimeMillis() / 1000);
        CommonAppConfig appConfig = CommonAppConfig.getInstance();
        String uid = appConfig.getUid();
        String token = appConfig.getToken();
        String sign = MD5Util.getMD5(StringUtil.contact("nonce=", nonce, "&orderno=", orderId, "&ordertype=", buyType, "&time=", time, "&uid=", uid, "&", CommonHttpUtil.SALT));
        HttpClient.getInstance().get("User.BraintreeCallback", CommonHttpConsts.BRAINTREE_CALLBACK)
                .params("uid", uid)
                .params("token", token)
                .params("orderno", orderId)
                .params("ordertype", buyType)
                .params("nonce", nonce)
                .params("money", money)
                .params("time", time)
                .params("sign", sign)
                .execute(callback);
    }


    /**
     * Get product details
     */
    public static void getGoodsInfo(String goodsId, HttpCallback callback) {
        HttpClient.getInstance().get("Shop.getGoodsInfo", CommonHttpConsts.GET_GOODS_INFO)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("goodsid", goodsId)
                .execute(callback);
    }

    /**
     * Get the first recharge rules list
     */
    public static void getFirstChargeRules(HttpCallback callback) {
        HttpClient.getInstance().get("Charge.getFirstChargeRules", CommonHttpConsts.GET_FIRST_CHARGE_RULES)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }

    // Create order
    public static void getBalanceOrder(String changeId,String coin, String money, String password, HttpCallback callback) {
        HttpClient.getInstance().get("Charge.getBalanceOrder", CommonHttpConsts.GET_BALANCE_ORDER)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("changeid", changeId)
                .params("coin", coin)
                .params("money", money)
                .params("password", password)
                .execute(callback);
    }

    // Balance Payment Callback
    public static void balanceCallback(String orderNo,String coin, String money, String password, HttpCallback callback){
        HttpClient.getInstance().get("User.BalanceCallback", CommonHttpConsts.BALANCE_CALL_BACK)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("orderno", orderNo)
                .params("coin", coin)
                .params("money", money)
                .params("password", password)
                .execute(callback);
    }


    /**
     * 修改直播状态
     * @param callback
     */
    public static void updateIsLive(HttpCallback callback){
        HttpClient.getInstance().get("Live.updateIsLive", CommonHttpConsts.updateIsLive)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);

    }
}




