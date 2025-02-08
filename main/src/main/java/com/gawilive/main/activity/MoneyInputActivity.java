package com.gawilive.main.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hjq.shape.view.ShapeTextView;
import com.lzj.pass.dialog.PayPassDialog;
import com.lzj.pass.dialog.PayPassView;
import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.activity.AbsActivity;
import com.gawilive.common.utils.MMKVUtils;
import com.gawilive.common.utils.OkHttp;
import com.gawilive.common.utils.ToastUtil;
import com.gawilive.common.utils.WordUtil;
import com.gawilive.main.R;
import com.gawilive.main.bean.ScanCodeOrderNo;
import okhttp3.Request;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// 金额输入页面
public class MoneyInputActivity extends AbsActivity {
    private EditText mEdMoney;
    private ShapeTextView mAddBank;

    private String merchant;

    public static void start(Context context, String json) {
        Intent intent = new Intent(context, MoneyInputActivity.class);
        intent.putExtra("merchant", json);
        context.startActivity(intent);
    }

    private void payDialog(final String money) {
        final PayPassDialog dialog = new PayPassDialog(this);
        dialog.getPayViewPass()
                .setRandomNumber(true)
                .setPayClickListener(new PayPassView.OnPayClickListener() {
                    @Override
                    public void onPassFinish(String passContent) {
                        //6位输入完成回调
                        pay(money, passContent);
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
                        startActivity(new Intent(MoneyInputActivity.this, PayPassActivity.class));
                    }
                });
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_money_input;
    }

    @Override
    protected void main() {
        super.main();
        merchant = getIntent().getStringExtra("merchant");
        initView();
    }

    private void initView() {
        setTitle(WordUtil.getString(R.string.string_syt));
        mEdMoney = findViewById(R.id.edMoney);
        mAddBank = findViewById(R.id.addBank);
        mAddBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String money = mEdMoney.getText().toString().trim();
                if (TextUtils.isEmpty(money)) {
                    ToastUtil.show(WordUtil.getString(R.string.string_input_pay));
                    return;
                }
                createOrder(money);

            }
        });
    }

    private void createOrder(String money) {
        JsonObject object = new JsonParser().parse(merchant).getAsJsonObject();
        Map<String, String> map = new HashMap<>();
        map.put("uid", MMKVUtils.getPayUid());
        map.put("payee", object.get("uid").getAsString());
        map.put("pay_type", "money");
        map.put("money", money);
        OkHttp.postAsync(CommonAppConfig.HOST2 + "?act=user_scan_business", map, new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                // {"code":0,"msg":"\u6210\u529f","data":{"insert_id":"121","money":"100","order_sn":"24052850515251"}}
                JsonObject object = new JsonParser().parse(result).getAsJsonObject();
                int code = object.get("code").getAsInt();
                if (code == 0) { // 进行信息同步
                    JsonObject dataObject = object.getAsJsonObject("data");
                    ScanCodeOrderNo scanCodeOrderNo = new Gson().fromJson(dataObject.toString(), ScanCodeOrderNo.class);
                    payDialog(scanCodeOrderNo.getOrder_sn());
                } else {
                    ToastUtil.show(object.get("msg").getAsString());
                }
            }

            @Override
            public void requestFailure(Request request, IOException e) {

            }
        });
    }

    private void pay(String order, String paypwd) {
        Map<String, String> map = new HashMap<>();
        map.put("order_sn", order);
        map.put("paypwd", paypwd);
        OkHttp.postAsync(CommonAppConfig.HOST2 + "?act=" +
                "" +
                "" +
                "" +
                "" +
                "", map, new OkHttp.DataCallBack() {

            @Override
            public void requestSuccess(String result) throws Exception {
                JsonObject object = new JsonParser().parse(result).getAsJsonObject();
                int code = object.get("code").getAsInt();
                if (code == 0) { // 进行信息同步
                    JsonObject data = object.get("data").getAsJsonObject();
                    PaySuccessActivity.start(MoneyInputActivity.this, data.toString());
                    finish();
                } else {
                    ToastUtil.show(object.get("msg").getAsString());
                }
            }

            @Override
            public void requestFailure(Request request, IOException e) {

            }
        });
    }
}
