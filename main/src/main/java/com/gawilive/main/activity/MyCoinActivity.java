package com.gawilive.main.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;

import android.util.Log;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gawilive.mall.bean.H5PayModel;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;
import com.lzj.pass.dialog.PayPassDialog;
import com.lzj.pass.dialog.PayPassView;
import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.Constants;
import com.gawilive.common.HtmlConfig;
import com.gawilive.common.activity.AbsActivity;
import com.gawilive.common.activity.WebViewActivity;
import com.gawilive.common.bean.CoinBean;
import com.gawilive.common.bean.CoinPayBean;
import com.gawilive.common.bean.UserBean;
import com.gawilive.common.custom.ItemDecoration;
import com.gawilive.common.event.CoinChangeEvent;
import com.gawilive.common.event.FirstChargeEvent;
import com.gawilive.common.http.CommonHttpConsts;
import com.gawilive.common.http.CommonHttpUtil;
import com.gawilive.common.http.HttpCallback;
import com.gawilive.common.interfaces.CommonCallback;
import com.gawilive.common.interfaces.OnItemClickListener;
import com.gawilive.common.interfaces.PermissionCallback;
import com.gawilive.common.pay.PayCallback;
import com.gawilive.common.pay.PayPresenter;
import com.gawilive.common.utils.*;
import com.gawilive.main.R;
import com.gawilive.main.adapter.CoinAdapter;
import com.gawilive.main.adapter.CoinPayAdapter;
import com.gawilive.common.dialog.FirstChargeDialogFragment;

import com.gawilive.main.bean.BalanceOrderModel;
import com.gawilive.main.bean.PayUserBean;
import com.gawilive.main.bean.QrCodeModel;
import com.gawilive.main.http.MainHttpUtil;

import okhttp3.Request;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gawilive.common.CommonAppConfig.HOST2;

/**
 * Created by cxf on 2018/10/23.
 * 充值
 */
public class MyCoinActivity extends AbsActivity implements View.OnClickListener {

    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView mPayRecyclerView;
    private CoinAdapter mAdapter;
    private CoinPayAdapter mPayAdapter;
    private TextView mBalance;
    private long mBalanceValue;
    private boolean mFirstLoad = true;
    private PayPresenter mPayPresenter;
    private String mCoinName;
    //    private TextView mTip1;
//    private TextView mTip2;
    private TextView mCoin2;
    private TextView mBtnCharge;
    private boolean mIsPayPal;

    private RelativeLayout llPaymentCode;

    private TextView tvCZ, tvZZ, tvTX, tvZJJL, coin_3, tvZh;

    private RelativeLayout llScanCode;

    public static final int REQUEST_CODE_SCAN_ONE = 101;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_coin;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.wallet));
//        mTip1 = findViewById(R.id.tip_1);
//        mTip2 = findViewById(R.id.tip_2);

        tvCZ = findViewById(R.id.tvCZ);
        tvZZ = findViewById(R.id.tvZZ);
        tvTX = findViewById(R.id.tvTX);
        tvZJJL = findViewById(R.id.tvZJJL);
        tvZh = findViewById(R.id.tvZh);

        llScanCode = findViewById(R.id.llScanCode);
        coin_3 = findViewById(R.id.coin_3);

        mBtnCharge = findViewById(R.id.btn_charge);
        llPaymentCode = findViewById(R.id.llPaymentCode);
        mBtnCharge.setOnClickListener(this);
        findViewById(R.id.btn_charge_first).setOnClickListener(this);
        mCoin2 = findViewById(R.id.coin_2);
        mRefreshLayout = findViewById(R.id.refreshLayout);
        mRefreshLayout.setColorSchemeResources(R.color.blue3);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });
        mCoinName = CommonAppConfig.getInstance().getCoinName();
        mBalance = findViewById(R.id.coin);
        TextView coinNameTextView = findViewById(R.id.coin_name);
        coinNameTextView.setText(String.format(WordUtil.getString(R.string.wallet_coin_name), mCoinName));
        TextView scoreName = findViewById(R.id.score_name);
        scoreName.setText(String.format(WordUtil.getString(R.string.wallet_coin_name), CommonAppConfig.getInstance().getScoreName()));
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false));
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 5, 10);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRecyclerView.addItemDecoration(decoration);
        mAdapter = new CoinAdapter(mContext, mCoinName);
        mAdapter.setOnItemClickListener(new OnItemClickListener<CoinBean>() {
            @Override
            public void onItemClick(CoinBean bean, int position) {
                if (bean != null && mBtnCharge != null) {
                    String money = StringUtil.contact(mIsPayPal ? "$" : "Ks", bean.getMoney());
                    mBtnCharge.setText(String.format(WordUtil.getString(R.string.chat_charge_tip), money));
                }
            }
        });
//        mAdapter.setContactView(findViewById(R.id.top));
        mRecyclerView.setAdapter(mAdapter);
        // findViewById(R.id.btn_tip).setOnClickListener(this);
//        View headView = mAdapter.getHeadView();
        mPayRecyclerView = findViewById(R.id.pay_recyclerView);
        ItemDecoration decoration2 = new ItemDecoration(mContext, 0x00000000, 14, 10);
        decoration2.setOnlySetItemOffsetsButNoDraw(true);
        mPayRecyclerView.addItemDecoration(decoration2);
        mPayRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mPayAdapter = new CoinPayAdapter(mContext);
        mPayAdapter.setOnItemClickListener(new OnItemClickListener<CoinPayBean>() {
            @Override
            public void onItemClick(CoinPayBean bean, int position) {
                boolean isPayPal = Constants.PAY_TYPE_PAYPAL.equals(bean.getId());
                if (mIsPayPal != isPayPal) {
                    if (mAdapter != null) {
                        CoinBean coinBean = mAdapter.getCheckedBean();
                        if (coinBean != null && mBtnCharge != null) {
                            String money = StringUtil.contact(isPayPal ? "$" : "Ks", coinBean.getMoney());
                            mBtnCharge.setText(String.format(WordUtil.getString(R.string.chat_charge_tip), money));
                        }
                    }
                }
                mIsPayPal = isPayPal;
                if (mAdapter != null) {
                    mAdapter.setIsPaypal(isPayPal);
                }
            }
        });
        mPayRecyclerView.setAdapter(mPayAdapter);
        mPayPresenter = new PayPresenter(this);
        mPayPresenter.setServiceNameAli(Constants.PAY_BUY_COIN_ALI);
        mPayPresenter.setServiceNameWx(Constants.PAY_BUY_COIN_WX);
        mPayPresenter.setServiceNamePaypal(Constants.PAY_BUY_COIN_PAYPAL);
        mPayPresenter.setAliCallbackUrl(HtmlConfig.ALI_PAY_COIN_URL);
        mPayPresenter.setPayCallback(new PayCallback() {
            @Override
            public void onSuccess() {
                if (mPayPresenter != null) {
                    mPayPresenter.checkPayResult();
                }
            }

            @Override
            public void onFailed() {

            }
        });
        EventBus.getDefault().register(this);

        llPaymentCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(idNo)) {

                } else {
                    if (TextUtils.isEmpty(MMKVUtils.getPayPwd())) {
                        ToastUtil.show(WordUtil.getString(R.string.string_pay_pass_tips));
                        startActivity(new Intent(MyCoinActivity.this, PayPassActivity.class));
                    } else {
                        startActivity(PaymentCodeActivity.class);
                    }
                }


            }
        });


        // top up
        tvCZ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRealName(1);

            }
        });

        tvZh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRealName(5);
            }
        });
        // transfer
        tvZZ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRealName(3);

            }
        });
        // Withdraw cash
        tvTX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRealName(4);
            }
        });
        // Funding records
        tvZJJL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(FinancialRecordsActivity.class);
            }
        });
        llScanCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionUtil.request(MyCoinActivity.this, new PermissionCallback() {
                            @Override
                            public void onAllGranted() {
                                isRealName(2);
                            }

                            @Override
                            public void onDenied(List<String> deniedPermissions) {

                            }
                        },
                        Manifest.permission.CAMERA);
            }
        });
    }

    private void scanCode() {
        HmsScanAnalyzerOptions options = new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.QRCODE_SCAN_TYPE, HmsScan.DATAMATRIX_SCAN_TYPE)
                .setViewType(1)
                .setErrorCheck(true).create();
        ScanUtil.startScan(MyCoinActivity.this, REQUEST_CODE_SCAN_ONE, options);

    }

    private String idNo;


    // Get payment information
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
                    PayUserBean bean = new Gson().fromJson(dataObject.toString(), PayUserBean.class);
                    coin_3.setText(bean.getMoney());
                    MMKVUtils.setPayUid(bean.getUid());
                    MMKVUtils.setKey(bean.getKey());
                    MMKVUtils.setPayPwd(bean.getPaypwd());
                    idNo = bean.getCertno();
                }
            }

            @Override
            public void requestFailure(Request request, IOException e) {

            }
        });
    }


    private void getUserInfo() {
        MainHttpUtil.getBaseInfo(mCallback);
    }

    private final CommonCallback<UserBean> mCallback = new CommonCallback<UserBean>() {
        @Override
        public void callback(UserBean bean) {
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
    };


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
    protected void onResume() {
        super.onResume();
        if (mFirstLoad) {
            mFirstLoad = false;
            loadData();
        }
        getPayData();
    }

    private void loadData() {
        CommonHttpUtil.getBalance(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    String coin = obj.getString("coin");
                    mBalanceValue = Long.parseLong(coin);
                    mBalance.setText(coin);
                    mCoin2.setText(obj.getString("score"));
                    List<CoinPayBean> payList = JSON.parseArray(obj.getString("paylist"), CoinPayBean.class);
                    if (mPayAdapter != null) {
                        mPayAdapter.setList(payList);
                    }
                    List<CoinBean> list = JSON.parseArray(obj.getString("rules"), CoinBean.class);
                    if (mAdapter != null) {
                        mAdapter.setList(list);
                        if (payList != null && payList.size() > 0) {
                            mIsPayPal = Constants.PAY_TYPE_PAYPAL.equals(payList.get(0).getId());
                            mAdapter.setIsPaypal(mIsPayPal);
                        }
                        CoinBean coinBean = mAdapter.getCheckedBean();
                        if (coinBean != null && mBtnCharge != null) {
                            String money = StringUtil.contact(mIsPayPal ? "$" : "Ks", coinBean.getMoney());
                            mBtnCharge.setText(String.format(WordUtil.getString(R.string.chat_charge_tip), money));
                        }
                    }
                    if (mPayPresenter != null) {
                        mPayPresenter.setBalanceValue(mBalanceValue);
                        mPayPresenter.setAliPartner(obj.getString("aliapp_partner"));
                        mPayPresenter.setAliSellerId(obj.getString("aliapp_seller_id"));
                        mPayPresenter.setAliPrivateKey(obj.getString("aliapp_key_android"));
                        mPayPresenter.setWxAppID(obj.getString("wx_appid"));
                    }
                }
            }

            @Override
            public void onFinish() {
                if (mRefreshLayout != null) {
                    mRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    /**
     * 充值
     */
    private void charge() {
        if (mAdapter == null) {
            return;
        }
        if (mPayPresenter == null) {
            return;
        }
        if (mPayAdapter == null) {
            return;
        }
        CoinBean bean = mAdapter.getCheckedBean();
        if (bean == null) {
            return;
        }
        CoinPayBean coinPayBean = mPayAdapter.getPayCoinPayBean();
        if (coinPayBean == null) {
            ToastUtil.show(R.string.wallet_tip_5);
            return;
        }
        String href = coinPayBean.getHref();
        String payType = coinPayBean.getId();
        if ("balance".equals(payType)) { // 判断是余额支付
            isRealName2(bean.getId(), bean.getCoin(), bean.getMoney());
        } else if ("98".equals(coinPayBean.getType()) && !TextUtils.isEmpty(href)) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(href + "&money=" + bean.getMoney()+"&qb=1"));
            startActivity(intent);
        } else if ("99".equals(coinPayBean.getType()) && !TextUtils.isEmpty(href)) {
            if (!TextUtils.isEmpty(MMKVUtils.getPayUid())) {
                String coin = Constants.PAY_TYPE_PAYPAL.equals(coinPayBean.getId()) ? bean.getCoinPaypal() : bean.getCoin();
                h5CreateOrder(href, bean.getMoney());
            } else {
                ToastUtil.show("请先进入个人中心同步钱包信息");
            }
        } else {
            if (TextUtils.isEmpty(href)) {
                String money = bean.getMoney();
                String coin = Constants.PAY_TYPE_PAYPAL.equals(coinPayBean.getId()) ? bean.getCoinPaypal() : bean.getCoin();
                String goodsName = StringUtil.contact(coin, mCoinName);
                String orderParams = StringUtil.contact(
                        "&uid=", CommonAppConfig.getInstance().getUid(),
                        "&token=", CommonAppConfig.getInstance().getToken(),
                        "&money=", money,
                        "&changeid=", bean.getId(),
                        "&coin=", coin);
                mPayPresenter.pay(coinPayBean.getId(), money, goodsName, orderParams);
            } else {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(href + "&coin=" + bean.getCoin() + "&changeid=" + bean.getId() + "&money=" + bean.getMoney()));
                mContext.startActivity(intent);
            }
        }

    }


    // H5 创建订单
    private void h5CreateOrder(String url, String money) {
        Map<String, String> map = new HashMap<>();
        map.put("uid", MMKVUtils.getPayUid());
        map.put("money", money);
        map.put("source", "android");
        map.put("pay_type", "kbzpay");
        map.put("type", "coin");
        OkHttp.postAsync(HOST2 + "?act=create_order", map, new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                JsonObject object = new JsonParser().parse(result).getAsJsonObject();
                int code = object.get("code").getAsInt();
                if (code == 0) {
                    JsonObject dataObject = object.getAsJsonObject("data");
                    H5PayModel model = new Gson().fromJson(dataObject.toString(), H5PayModel.class);
                    String str = url + "?tripartite_uid=" + model.getTripartite_uid() + "&money=" + model.getMoney() + "&order_sn=" + model.getOrder_sn() + "&type=coin&system=android";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(str));
                    startActivity(intent);
                } else {
                    String msg = object.get("msg").getAsString();
                    ToastUtil.show(TextUtils.isEmpty(msg) ? getResources().getString(com.gawilive.mall.R.string.TUIKitErrorSVRMsgNetError) : msg);
                }

            }

            @Override
            public void requestFailure(Request request, IOException e) {

            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCoinChangeEvent(CoinChangeEvent e) {
        if (mBalance != null) {
            mBalance.setText(e.getCoin());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFirstChargeEvent(FirstChargeEvent e) {
        loadData();
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
//        if (i == R.id.btn_tip) {
//            WebViewActivity.forward(mContext, "https://yszc.ezwel.live/");
//        } else

        if (i == R.id.btn_charge) {
            charge();
        } else if (i == R.id.btn_charge_first) {
            FirstChargeDialogFragment fragment = new FirstChargeDialogFragment();
            fragment.show(getSupportFragmentManager(), "FirstChargeDialogFragment");
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        CommonHttpUtil.cancel(CommonHttpConsts.GET_BALANCE);
        CommonHttpUtil.cancel(CommonHttpConsts.GET_ALI_ORDER);
        CommonHttpUtil.cancel(CommonHttpConsts.GET_WX_ORDER);
        if (mRefreshLayout != null) {
            mRefreshLayout.setOnRefreshListener(null);
        }
        mRefreshLayout = null;
        if (mPayPresenter != null) {
            mPayPresenter.release();
        }
        mPayPresenter = null;
        super.onDestroy();
    }

//    private void payDialog() {
//        final PayPassDialog dialog = new PayPassDialog(this);
//        dialog.getPayViewPass()
//                .setRandomNumber(true)
//                .setPayClickListener(new PayPassView.OnPayClickListener() {
//                    @Override
//                    public void onPassFinish(String passContent) {
//                        //6位输入完成回调
//                    }
//
//                    @Override
//                    public void onPayClose() {
//                        dialog.dismiss();
//                        //关闭弹框
//                    }
//
//                    @Override
//                    public void onPayForget() {
//                        dialog.dismiss();
//                        //点击忘记密码回调
//                    }
//                });
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SCAN_ONE && resultCode == RESULT_OK) {
            if (data != null) {
                //Import image scan return results
                int errorCode = data.getIntExtra(ScanUtil.RESULT_CODE, ScanUtil.SUCCESS);
                if (errorCode == ScanUtil.SUCCESS) {
                    Object obj = data.getParcelableExtra(ScanUtil.RESULT);
                    if (obj != null) {
                        JsonObject object = new JsonParser().parse(new Gson().toJson(obj)).getAsJsonObject();
                        String showResult = object.get("showResult").getAsString();
                        if (showResult.startsWith("http") || showResult.startsWith("https")) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(showResult + "&uid=" + MMKVUtils.getPayUid()));
                            startActivity(intent);
                        } else {
                            try {
                                QrCodeModel model = new Gson().fromJson(showResult, QrCodeModel.class);
                                if ("2".equals(model.getType())) {
                                    ScanMyTransferActivity.start(MyCoinActivity.this, showResult);
                                } else {
                                    MoneyInputActivity.start(this, object.get("showResult").getAsString());
                                }
                            } catch (Exception e) {
                                ToastUtil.show(WordUtil.getString(R.string.string_scan_code_exception));
                            }
                        }


                        // 展示扫码结果
                    }
                }
                if (errorCode == ScanUtil.ERROR_NO_READ_PERMISSION) {
                    // 无文件权限，请求文件权限

                }
            }

        }
    }


    // 实名认证
    private void isRealName(final int type) {
        OkHttp.getAsync(CommonAppConfig.HOST2 + "?act=findSm&uid=" + MMKVUtils.getPayUid(), new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                // {"code":0,"msg":"\u6210\u529f","data":false}
                JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                boolean data = jsonObject.get("data").getAsBoolean();
                if (data) {
                    switch (type) {
                        case 1: // 充值
                            startActivity(MyRechargeActivity.class);
                            break;
                        case 2: // 扫一扫
                            if (TextUtils.isEmpty(MMKVUtils.getPayPwd())) {
                                ToastUtil.show(WordUtil.getString(R.string.string_pay_pass_tips));
                                startActivity(new Intent(MyCoinActivity.this, PayPassActivity.class));
                            } else {
                                scanCode();
                            }
                            break;
                        case 3: // 转账
                            if (TextUtils.isEmpty(MMKVUtils.getPayPwd())) {
                                ToastUtil.show(WordUtil.getString(R.string.string_pay_pass_tips));
                                startActivity(new Intent(MyCoinActivity.this, PayPassActivity.class));
                            } else {
                                startActivity(MyTransferActivity.class);
                            }
                            break;
                        case 4:
                            if (TextUtils.isEmpty(MMKVUtils.getPayPwd())) {
                                ToastUtil.show(WordUtil.getString(R.string.string_pay_pass_tips));
                                startActivity(new Intent(MyCoinActivity.this, PayPassActivity.class));
                            } else {
                                startActivity(MyWithdrawalActivity.class);
                            }
                            break;
                        case 5: // 额度转换
                            startActivity(CreditConversionActivity.class);
                            break;
                    }
                } else {
                    ToastUtil.show(WordUtil.getString(R.string.string_smrz_tips));
                    startActivity(RealNameActivity.class);
                }
            }

            @Override
            public void requestFailure(Request request, IOException e) {

            }
        });
    }


    private void isRealName2(final String changeId, final String coin, final String money) {
        OkHttp.getAsync(CommonAppConfig.HOST2 + "?act=findSm&uid=" + MMKVUtils.getPayUid(), new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                // {"code":0,"msg":"\u6210\u529f","data":false}
                JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                boolean data = jsonObject.get("data").getAsBoolean();
                if (data) {
                    if (TextUtils.isEmpty(MMKVUtils.getPayPwd())) {
                        ToastUtil.show(WordUtil.getString(R.string.string_pay_pass_tips));
                        startActivity(new Intent(MyCoinActivity.this, PayPassActivity.class));
                    } else {
                        inputPass(changeId, coin, money);
                    }
                } else {
                    ToastUtil.show(WordUtil.getString(R.string.string_smrz_tips));
                    startActivity(RealNameActivity.class);
                }
            }

            @Override
            public void requestFailure(Request request, IOException e) {

            }
        });
    }


    private void inputPass(final String changeId, final String coin, final String money) {
        final PayPassDialog dialog = new PayPassDialog(this);
        dialog.getPayViewPass()
                .setRandomNumber(true)
                .setPayClickListener(new PayPassView.OnPayClickListener() {
                    @Override
                    public void onPassFinish(String passContent) {
                        //6位输入完成回调
                        getBalanceOrder(dialog, changeId, coin, money, passContent);
                    }

                    @Override
                    public void onPayClose() {
                        dialog.dismiss();
                        //关闭弹框
                    }

                    @Override
                    public void onPayForget() {
                        dialog.dismiss();
                        //点击忘记密码回调
                        startActivity(new Intent(MyCoinActivity.this, PayPassActivity.class));
                    }
                });
    }


    // 创建订单
    private void getBalanceOrder(final PayPassDialog dialog, String changeId, final String coin, final String money, final String password) {
        CommonHttpUtil.getBalanceOrder(changeId, coin, money, password, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {

                    BalanceOrderModel model = new Gson().fromJson(info[0], BalanceOrderModel.class);
                    balanceCallBack(model.getOrderid(), coin, money, password);
                }
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
    }

    // 回调
    private void balanceCallBack(String orderNo, String coin, String money, String password) {
        CommonHttpUtil.balanceCallback(orderNo, coin, money, password, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    loadData();
                    getPayData();
                }
                ToastUtil.show(msg);
            }
        });
    }
}
