package com.gawilive.main.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.hjq.shape.view.ShapeTextView;
import com.gawilive.common.activity.AbsActivity;
import com.gawilive.main.R;
import com.gawilive.main.bean.PaySuccessModel;

public class PaySuccessActivity extends AbsActivity {

    private String payment;
    private FrameLayout mFlTop;
    private TextView mTitleView;
    private ImageView mBtnBack;
    private TextView mTvMoney;
    private TextView mTvBankCard;
    private TextView mTvBankNo;
    private TextView mTvRealName;
    private TextView mTvSkRealName;
    private ShapeTextView mTvLijCz;


    public static void start(Context context, String json) {
        Intent intent = new Intent(context, PaySuccessActivity.class);
        intent.putExtra("payment", json);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_pay_success;
    }

    @Override
    protected void main() {
        super.main();
        payment = getIntent().getStringExtra("payment");
        initView();
    }

    private void initView() {
        mFlTop = findViewById(R.id.fl_top);
        mTitleView = findViewById(R.id.titleView);
        mBtnBack = findViewById(R.id.btn_back);
        mTvMoney = findViewById(R.id.tvMoney);
        mTvBankCard = findViewById(R.id.tvBankCard);
        mTvBankNo = findViewById(R.id.tvBankNo);
        mTvRealName = findViewById(R.id.tvRealName);
        mTvSkRealName = findViewById(R.id.tvSkRealName);
        mTvLijCz = findViewById(R.id.tvLijCz);
        mTvLijCz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        PaySuccessModel model = new Gson().fromJson(payment, PaySuccessModel.class);
        mTvMoney.setText("Ks" + model.getMoney());
        mTvBankNo.setText(model.getPaytime());
        mTvRealName.setText(model.getOrder_sn());
        mTvSkRealName.setText(model.getSkr());
    }
}
