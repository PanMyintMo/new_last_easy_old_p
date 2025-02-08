package com.gawilive.main.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import android.widget.EditText;

import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gawilive.common.http.HttpCallback;
import com.gawilive.main.http.MainHttpUtil;
import com.gawilive.mall.bean.H5PayModel;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.google.gson.reflect.TypeToken;
import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.activity.AbsActivity;
import com.gawilive.common.activity.WebViewActivity;
import com.gawilive.common.adapter.BaseAdapter;
import com.gawilive.common.utils.*;
import com.gawilive.main.R;
import com.gawilive.main.adapter.PayTypeAdapter;
import com.gawilive.main.bean.CreateOrderBean;
import com.gawilive.main.bean.PayTypeModel;
import com.gawilive.main.bean.PayUserBean;

import okhttp3.Request;

// 充值
public class MyRechargeActivity extends AbsActivity {
    private android.widget.FrameLayout flTop;
    private android.widget.TextView titleView;
    private android.widget.ImageView btnBack;
    private android.widget.TextView tvMoney;
    private androidx.recyclerview.widget.RecyclerView recyclerView;
    private com.hjq.shape.view.ShapeTextView tvLijCz;

    private PayTypeAdapter adapter;

    private EditText edMoney;

    private int checkPosition = 0;

    private int second = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_recharge;
    }

    private void initView() {
        setTitle(WordUtil.getString(R.string.string_cz));
        flTop = findViewById(R.id.fl_top);
        titleView = findViewById(R.id.titleView);
        btnBack = findViewById(R.id.btn_back);
        tvMoney = findViewById(R.id.tvMoney);
        recyclerView = findViewById(R.id.recyclerView);
        tvLijCz = findViewById(R.id.tvLijCz);
        edMoney = findViewById(R.id.edMoney);
        edMoney.addTextChangedListener(new MoneyWatcher());
        adapter = new PayTypeAdapter(this);
        adapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
                for (int i = 0; i < adapter.getCount(); i++) {
                    if (i == position) {
                        adapter.getItem(i).setSelect(true);
                        checkPosition = position;
                    } else {
                        adapter.getItem(i).setSelect(false);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
        recyclerView.setAdapter(adapter);
        getPayData();
        payType();
    }

    private String href;

    @Override
    protected void main() {
        super.main();
        initView();
        tvLijCz.setOnClickListener(v -> {
            String money = edMoney.getText().toString().trim();
            if (TextUtils.isEmpty(money)) {
                ToastUtil.show(edMoney.getHint());
                return;
            }
            tvLijCz.setEnabled(false);
            edMoney.setEnabled(false);
            if (!TextUtils.isEmpty(adapter.getItem(checkPosition).getHref())) {
                getPublicConfig(money, adapter.getItem(checkPosition).getHref());
            } else {
                createOrder(money);
            }

        });

    }

    private void getPublicConfig(String money, String href) {

        MainHttpUtil.getPublicConfig(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    String public_collection = obj.getString("public_collection");
                    String url = href + "?uid=" + CommonAppConfig.getInstance().getUid() + "&system=android&public_collection=" + public_collection + "&money=" + money + "&qb=2";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                }else {
                    ToastUtil.show(msg);
                }
            }
        });
    }


    // 支付方式
    private void payType() {
        OkHttp.getAsync(CommonAppConfig.HOST2 + "?act=paytype", new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                JsonObject object = new JsonParser().parse(result).getAsJsonObject();
                int code = object.get("code").getAsInt();
                if (code == 1) {
                    JsonArray array = object.getAsJsonArray("data");
                    List<PayTypeModel> list = new Gson().fromJson(array.toString(), new TypeToken<List<PayTypeModel>>() {
                    }.getType());
                    for (int i = 0; i < list.size(); i++) {
                        list.get(i).setSelect(i == 0);
                    }
                    adapter.setData(list);

                }
            }

            @Override
            public void requestFailure(Request request, IOException e) {

            }
        });
    }

    // 获取支付信息
    private void getPayData() {
        OkHttp.getAsync(CommonAppConfig.HOST2 + "?act=findUser&phone=" + CommonAppConfig.getInstance().getUserBean().getMobile(), new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                Log.d("查询用户信息返回数据", result);
                JsonObject object = new JsonParser().parse(result).getAsJsonObject();
                int code = object.get("code").getAsInt();
                if (code == 0) { // 进行信息同步
                    JsonObject dataObject = object.getAsJsonObject("data");
                    PayUserBean bean = new Gson().fromJson(dataObject.toString(), PayUserBean.class);
                    tvMoney.setText(bean.getMoney());
                }
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                Log.d("查询用户信息返回数据", "出错了");
            }
        });
    }

    private String orderNo;

    // 创建订单
    private void createOrder(String money) {
        Map<String, String> map = new HashMap<>();
        map.put("uid", MMKVUtils.getPayUid());
        map.put("money", money);
        map.put("pay_type", adapter.getItem(checkPosition).getName());
        map.put("type", "balance");
        map.put("source", "android");
        OkHttp.postAsync(CommonAppConfig.HOST2 + "?act=create_order", map, new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                //   {"code":0,"msg":"\u6210\u529f","data":{"insert_id":"32","order_sn":"24052757985350"}}
                JsonObject object = new JsonParser().parse(result).getAsJsonObject();
                int code = object.get("code").getAsInt();
                if (code == 0) {
                    JsonObject dataObject = object.getAsJsonObject("data");
                    H5PayModel model = new Gson().fromJson(dataObject.toString(), H5PayModel.class);
                    String str = adapter.getItem(checkPosition).getHref() + "?tripartite_uid=" + model.getTripartite_uid() + "&money=" + model.getMoney() + "&order_sn=" + model.getOrder_sn() + "&type=coin&system=android";
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

    private void recharge(final String orderNo) {
        Map<String, String> map = new HashMap<>();
        map.put("order_sn", orderNo);
        map.put("uid", MMKVUtils.getPayUid());
        OkHttp.postAsync(CommonAppConfig.HOST2 + "?act=recharge", map, new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                Log.d("返回数据", result);
                JsonObject object = new JsonParser().parse(result).getAsJsonObject();
                int code = object.get("code").getAsInt();
                if (code == 0) {
                    handler.removeCallbacks(pollingRunnable);
                    tvLijCz.setEnabled(true);
                    edMoney.setEnabled(true);
                    JsonObject object1 = object.getAsJsonObject("data").getAsJsonObject();
                    String url = object1.get("url").getAsString();
                    String money = object1.get("money").getAsString();

                    String path = CommonAppConfig.PAY_H5_URL + "money=" + money + "&url=" + url + "&orderNo=" + orderNo + "&system=android";
                    WebViewActivity.loadingPayCode(MyRechargeActivity.this, path, false, true);
                    finish();

                }// 进行信息同步
            }

            @Override
            public void requestFailure(Request request, IOException e) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void getOrder() {
        Map<String, String> map = new HashMap<>();
        map.put("order_sn", orderNo);
        OkHttp.postAsync(CommonAppConfig.HOST2 + "?act=find_order", map, new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                JsonObject object = new JsonParser().parse(result).getAsJsonObject();
                int code = object.get("code").getAsInt();
                if (code == 0) {
                    // 移除定时任务
                    handler.removeCallbacks(pollingRunnable);
                    TransferSuccessfulActivity.start(MyRechargeActivity.this, "充值成功");
                    finish();
                }
            }

            @Override
            public void requestFailure(Request request, IOException e) {

            }
        });
    }

    private final Handler handler = new Handler();
    private final Runnable pollingRunnable = new Runnable() {
        @Override
        public void run() {
            recharge(orderNo);
            handler.postDelayed(this, 3000); // 每5秒执行一次
            second += 3;
            if (second == 12) {
                ToastUtil.show("订单超时，请重新下单");
                handler.removeCallbacks(pollingRunnable);
                tvLijCz.setEnabled(true);
                edMoney.setEnabled(true);
                second = 0;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 取消轮询任务
        handler.removeCallbacks(pollingRunnable);
    }
}
