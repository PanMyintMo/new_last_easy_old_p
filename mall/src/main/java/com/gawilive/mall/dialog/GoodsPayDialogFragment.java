package com.gawilive.mall.dialog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.Constants;
import com.gawilive.common.HtmlConfig;
import com.gawilive.common.bean.ConfigBean;
import com.gawilive.common.bean.UserBean;
import com.gawilive.common.dialog.AbsDialogFragment;
import com.gawilive.common.http.CommonHttpConsts;
import com.gawilive.common.http.CommonHttpUtil;
import com.gawilive.common.http.HttpCallback;
import com.gawilive.common.interfaces.CommonCallback;
import com.gawilive.common.pay.PayCallback;
import com.gawilive.common.pay.PayPresenter;
import com.gawilive.common.utils.*;
import com.gawilive.mall.R;
import com.gawilive.mall.adapter.GoodsPayAdapter;
import com.gawilive.mall.bean.GoodsPayBean;
import com.gawilive.mall.bean.H5PayModel;
import com.gawilive.mall.bean.PayMallUserBean;
import com.gawilive.mall.http.MallHttpConsts;
import com.gawilive.mall.http.MallHttpUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.Request;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gawilive.common.CommonAppConfig.HOST2;

/**
 * 购买商品 付款弹窗
 */
public class GoodsPayDialogFragment extends AbsDialogFragment implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private GoodsPayAdapter mAdapter;
    private String mOrderId;
    private double mMoneyVal;
    private String mGoodsNameVal;
    private ActionListener mActionListener;
    private PayPresenter mPayPresenter;
    private boolean mPaySuccess;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_goods_pay;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        mOrderId = bundle.getString(Constants.MALL_ORDER_ID);
        mMoneyVal = bundle.getDouble(Constants.MALL_ORDER_MONEY, 0);
        mGoodsNameVal = bundle.getString(Constants.MALL_GOODS_NAME);
        TextView payName = findViewById(R.id.pay_name);
        String shopName = null;
        ConfigBean configBean = CommonAppConfig.getInstance().getConfig();
        if (configBean != null) {
            shopName = configBean.getShopSystemName();
        } else {
            shopName = WordUtil.getString(R.string.mall_001);
        }
        payName.setText(StringUtil.contact(shopName, WordUtil.getString(R.string.mall_191)));
        TextView money = findViewById(R.id.money);
        money.setText(String.valueOf(mMoneyVal));
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

        findViewById(R.id.btn_close).setOnClickListener(this);
        findViewById(R.id.btn_pay).setOnClickListener(this);

        MallHttpUtil.getBuyerPayList(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);

                    List<GoodsPayBean> payList = JSON.parseArray(obj.getString("paylist"), GoodsPayBean.class);
                    if (payList != null && payList.size() > 0) {
                        payList.get(0).setChecked(true);
                        mAdapter = new GoodsPayAdapter(mContext, payList, obj.getString("balance"));
                        if (mRecyclerView != null) {
                            mRecyclerView.setAdapter(mAdapter);
                        }
                        mPayPresenter = new PayPresenter((FragmentActivity) mContext);
                        mPayPresenter.setServiceNameAli(Constants.MALL_PAY_GOODS_ORDER);
                        mPayPresenter.setServiceNameWx(Constants.MALL_PAY_GOODS_ORDER);
                        mPayPresenter.setServiceNamePaypal(Constants.MALL_PAY_GOODS_ORDER);
                        mPayPresenter.setAliCallbackUrl(HtmlConfig.ALI_PAY_MALL_ORDER);
                        mPayPresenter.setAliPartner(obj.getString("aliapp_partner"));
                        mPayPresenter.setAliSellerId(obj.getString("aliapp_seller_id"));
                        mPayPresenter.setAliPrivateKey(obj.getString("aliapp_key_android"));
                        mPayPresenter.setWxAppID(obj.getString("wx_appid"));
                        mPayPresenter.setPayCallback(new PayCallback() {
                            @Override
                            public void onSuccess() {
                                mPaySuccess = true;
                                dismiss();
                            }

                            @Override
                            public void onFailed() {
                                ToastUtil.show(R.string.mall_367);
                            }
                        });
                    }
                }
            }
        });

        if (TextUtils.isEmpty(MMKVUtils.getPayUid())) {
            getPayData();
        }
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    private void getPayData() {
        OkHttp.getAsync(CommonAppConfig.HOST2 + "?act=findUser&phone=" + CommonAppConfig.getInstance().getUserBean().getMobile(), new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {

                JsonObject object = new JsonParser().parse(result).getAsJsonObject();
                int code = object.get("code").getAsInt();
                if (code == -3) { // 进行信息同步
                    getUserInfo();
                } else {
                    JsonObject dataObject = object.getAsJsonObject("data");
                    PayMallUserBean bean = new Gson().fromJson(dataObject.toString(), PayMallUserBean.class);
                    MMKVUtils.setPayUid(bean.getUid());
                    MMKVUtils.setKey(bean.getKey());
                    MMKVUtils.setPayPwd(bean.getPaypwd());
                }
            }

            @Override
            public void requestFailure(Request request, IOException e) {

            }
        });
    }

    private void getUserInfo() {
        UserBean bean = CommonAppConfig.getInstance().getUserBean();
        Map<String, String> map = new HashMap<>();
        map.put("phone", bean.getMobile());
        map.put("email", TextUtils.isEmpty(bean.getUser_email()) ? bean.getMobile() : bean.getUser_email());
        map.put("pwd", bean.getUser_pass());
        map.put("account", bean.getMobile());
        map.put("username", bean.getUserNiceName());
        map.put("avatar", bean.getAvatarThumb());
        map.put("live_user_id", bean.getId());
        syncData(map);
    }


    // Sync information
    private void syncData(Map<String, String> map) {
        OkHttp.postAsync(CommonAppConfig.HOST2 + "?act=addUser", map, new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                getPayData();
            }

            @Override
            public void requestFailure(Request request, IOException e) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_close) {
            dismiss();
        } else if (id == R.id.btn_pay) {
            pay();
        }
    }

    private void pay() {
        if (TextUtils.isEmpty(mOrderId) || mContext == null || mAdapter == null) {
            return;
        }
        GoodsPayBean bean = mAdapter.getCheckedPayType();
        if (bean == null) {
            return;
        }
        String type = bean.getType();
        if (Constants.PAY_TYPE_BALANCE.equals(bean.getId())) {//余额支付
            balancePay(type);
        } else if ("98".equals(type) && !TextUtils.isEmpty(bean.getHref())) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(bean.getHref() + "&money=" + mMoneyVal + "&qb=3"));
            startActivity(intent);
        } else if ("99".equals(type) && !TextUtils.isEmpty(bean.getHref())) {
            if (!TextUtils.isEmpty(MMKVUtils.getPayUid())) {
                h5CreateOrder(bean.getHref());
            } else {
                ToastUtil.show("请先进入个人中心同步钱包信息");
            }

        } else {//支付宝和微信支付
            if (mPayPresenter == null) {
                return;
            }
            String time = String.valueOf(System.currentTimeMillis() / 1000);
            CommonAppConfig appConfig = CommonAppConfig.getInstance();
            String uid = appConfig.getUid();
            String token = appConfig.getToken();
            String sign = MD5Util.getMD5(StringUtil.contact("orderid=", mOrderId, "&time=", time,
                    "&token=", token, "&type=", type, "&uid=", uid, "&", CommonHttpUtil.SALT));
            String orderParams = StringUtil.contact(
                    "&uid=", uid,
                    "&token=", token,
                    "&time=", time,
                    "&sign=", sign,
                    "&orderid=", mOrderId,
                    "&type=", type);
            mPayPresenter.pay(bean.getId(), String.valueOf(mMoneyVal), mGoodsNameVal, orderParams);
        }
    }


    // H5 创建订单
    private void h5CreateOrder(String url) {
        Map<String, String> map = new HashMap<>();
        map.put("tripartite_uid", CommonAppConfig.getInstance().getUid());
        map.put("money", String.valueOf(mMoneyVal));
        map.put("tripartite_order_id", mOrderId);
        map.put("goodname", mGoodsNameVal);
        map.put("source", "Android_直播平台");
        OkHttp.postAsync(HOST2 + "?act=create_tripartite_order", map, new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                JsonObject object = new JsonParser().parse(result).getAsJsonObject();
                int code = object.get("code").getAsInt();
                if (code == 0) {
                    JsonObject dataObject = object.getAsJsonObject("data");
                    H5PayModel model = new Gson().fromJson(dataObject.toString(), H5PayModel.class);
                    String uid = TextUtils.isEmpty(model.getTripartite_uid()) ? CommonAppConfig.getInstance().getUid() : model.getTripartite_uid();
                    String str = url + "?tripartite_uid=" + uid + "&money=" + model.getMoney() + "&order_sn=" + model.getOrder_sn() + "&type=shop&system=android";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(str));
                    startActivity(intent);
                } else {
                    String msg = object.get("msg").getAsString();
                    ToastUtil.show(TextUtils.isEmpty(msg) ? getResources().getString(R.string.TUIKitErrorSVRMsgNetError) : msg);
                }

            }

            @Override
            public void requestFailure(Request request, IOException e) {

            }
        });
    }

    /**
     * 余额支付
     */
    private void balancePay(String payType) {
        MallHttpUtil.buyerPayOrder(mOrderId, payType, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    mPaySuccess = true;
                    dismiss();
                }
                ToastUtil.show(msg);
            }
        });
    }


    @Override
    public void onDestroy() {
        MallHttpUtil.cancel(MallHttpConsts.GET_BUYER_PAY_LIST);
        MallHttpUtil.cancel(MallHttpConsts.BUYER_PAY_ORDER);
        MallHttpUtil.cancel(CommonHttpConsts.GET_ALI_ORDER);
        MallHttpUtil.cancel(CommonHttpConsts.GET_WX_ORDER);
        if (mPayPresenter != null) {
            mPayPresenter.release();
        }
        mPayPresenter = null;
        mAdapter = null;
        mContext = null;
        if (mActionListener != null) {
            mActionListener.onPayResult(mPaySuccess);
        }
        mActionListener = null;
        super.onDestroy();
    }


    public interface ActionListener {
        void onPayResult(boolean paySuccess);
    }


}
