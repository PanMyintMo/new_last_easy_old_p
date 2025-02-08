package com.gawilive.main.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hjq.shape.view.ShapeTextView;
import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.activity.AbsActivity;
import com.gawilive.common.utils.*;
import com.gawilive.main.R;
import com.gawilive.main.bean.BankModel;
import com.gawilive.main.bean.PayUserBean;
import okhttp3.Request;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//提现
public class MyWithdrawalActivity extends AbsActivity {
    private FrameLayout mFlTop;
    private TextView mTitleView;
    private ImageView mBtnBack;
    private TextView mTvMoney;
    private RelativeLayout mLlBankCardList;
    private TextView mTvBankCard;
    private RelativeLayout mLlBankCardList2;
    private TextView mTvBankNo;
    private TextView mTvRealName;
    private EditText mEdMoney;
    private ShapeTextView mTvLijCz;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_withdrawal;
    }

    @Override
    protected void main() {
        super.main();
        initView();
    }

    private void initView() {
        setTitle(WordUtil.getString(R.string.string_tx));
        mFlTop = findViewById(R.id.fl_top);
        mTitleView = findViewById(R.id.titleView);
        mBtnBack = findViewById(R.id.btn_back);
        mTvMoney = findViewById(R.id.tvMoney);
        mLlBankCardList = findViewById(R.id.llBankCardList);
        mTvBankCard = findViewById(R.id.tvBankCard);
        mLlBankCardList2 = findViewById(R.id.llBankCardList2);
        mTvBankNo = findViewById(R.id.tvBankNo);
        mTvRealName = findViewById(R.id.tvRealName);
        mEdMoney = findViewById(R.id.edMoney);
        mTvLijCz = findViewById(R.id.tvLijCz);
        mEdMoney.addTextChangedListener(new MoneyWatcher());
        getPayData();
        mLlBankCardList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyWithdrawalActivity.this, MyBankListActivity.class);
                startActivityForResult(intent, 888);
            }
        });

        mLlBankCardList2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyWithdrawalActivity.this, MyBankListActivity.class);
                startActivityForResult(intent, 888);
            }
        });
        // 发起提现
        mTvLijCz.setOnClickListener(v -> withdraw());

    }

    private void withdraw() {
        String money = mEdMoney.getText().toString().trim();
        if (TextUtils.isEmpty(money)) {
            ToastUtil.show(mEdMoney.getHint());
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("uid", MMKVUtils.getPayUid());
        map.put("money", money);
        map.put("bank_id", bankModel.getId());
        OkHttp.postAsync(CommonAppConfig.HOST2 + "?act=userSetCash", map, new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                JsonObject object = new JsonParser().parse(result).getAsJsonObject();
                int code = object.get("code").getAsInt();
                if (code == 0) {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.TransferSuccess");
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.putExtra("title", "提现申请中");
                    startActivity(intent);
                    finish();
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
                    mTvMoney.setText(bean.getMoney());
                }
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                Log.d("查询用户信息返回数据", "出错了");
            }
        });
    }

    private BankModel bankModel;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 888 && resultCode == RESULT_OK) {
            String json = data.getStringExtra("bankData");
            if (!TextUtils.isEmpty(json)) {
                bankModel = new Gson().fromJson(json, BankModel.class);
                mTvBankCard.setText(bankModel.getName());
                mTvBankNo.setText(bankModel.getCard_no());
                mTvRealName.setText(bankModel.getRealname());
            }
        }
    }
}
