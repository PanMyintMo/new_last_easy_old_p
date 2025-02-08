package com.gawilive.mall.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.alibaba.fastjson.JSON;
import com.gawilive.common.activity.AbsActivity;
import com.gawilive.common.http.HttpCallback;
import com.gawilive.common.utils.StringUtil;
import com.gawilive.common.utils.ToastUtil;
import com.gawilive.common.utils.WordUtil;
import com.gawilive.mall.R;
import com.gawilive.mall.bean.MerchantAddressModel;
import com.gawilive.mall.bean.SelectAreaModel;
import com.gawilive.mall.http.MallHttpUtil;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

/**
 * 编辑发货地址
 */
public class SellerAddressEditNewActivity extends AbsActivity {


    private LinearLayout mRefundGroup;
    private EditText mRefundName;
    private EditText mRefundPhoneNum;
    private FrameLayout mBtnRefundArea;
    private TextView mRefundArea;
    private EditText mRefundAddress;
    private TextView mBtnSubmit;

    private String sendProvince;

    private String sendCity;

    private String sendArea;

    public static void start(Activity context) {
        Intent intent = new Intent(context, SellerAddressEditNewActivity.class);
        context.startActivityForResult(intent, 789);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_seller_address_edit_new;
    }

    @Override
    protected void main() {
        super.main();
        setTitle(WordUtil.getString(R.string.str_009));
        initView();
    }

    private void initView() {
        mRefundGroup = findViewById(R.id.refund_group);
        mRefundName = findViewById(R.id.refund_name);
        mRefundPhoneNum = findViewById(R.id.refund_phone_num);
        mBtnRefundArea = findViewById(R.id.btn_refund_area);
        mRefundArea = findViewById(R.id.refund_area);
        mRefundAddress = findViewById(R.id.refund_address);
        mBtnSubmit = findViewById(R.id.btn_submit);

        getAddress();
        mBtnRefundArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseCity();
            }
        });
        // 提交数据
        mBtnSubmit.setOnClickListener(v -> submit());
    }

    private void submit() {
        if (TextUtils.isEmpty(mRefundArea.getText().toString().trim())) {
            ToastUtil.show(getString(R.string.a_005));
            return;
        }
        String name = mRefundName.getText().toString().trim();
        String address = mRefundAddress.getText().toString().trim();
        String phone = mRefundPhoneNum.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            ToastUtil.show(getString(R.string.mall_055));
            return;
        }

        if (TextUtils.isEmpty(address)) {
            ToastUtil.show(getString(R.string.mall_153));
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.show(getString(R.string.mall_159));
            return;
        }
        MallHttpUtil.updateMerchantAddress(sendProvince, sendCity, sendArea, address, phone, name, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    ToastUtil.show(msg);
                    setResult(RESULT_OK);
                    finish();
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    private void getAddress() {
        MallHttpUtil.getMerchantAddress(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    List<MerchantAddressModel> list = JSON.parseArray(Arrays.toString(info), MerchantAddressModel.class);
                    if (list.size() > 0) {
                        MerchantAddressModel model = list.get(0);
                        mRefundName.setText(model.getUsername());
                        mRefundPhoneNum.setText(model.getPhone());
                        mRefundArea.setText(StringUtil.contact(model.getProvince(), " ", model.getCity(), " ", model.getArea()));
                        mRefundAddress.setText(model.getAddress());
                        sendProvince = model.getProvince();
                        sendCity = model.getCity();
                        sendArea = model.getArea();
                    }
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }


    private String selectResult;

    /**
     * 选择地区
     */
    private void chooseCity() {
        AreaSelectActivity.start(this, selectResult);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 999) {
            selectResult = data.getStringExtra("selectResult");
            SelectAreaModel model = new Gson().fromJson(selectResult, SelectAreaModel.class);
            mRefundArea.setText(StringUtil.contact(model.getProvince(), " ", model.getCity(), " ", model.getArea()));
            sendProvince = model.getProvince();
            sendCity = model.getCity();
            sendArea = model.getArea();

        }
    }
}
